package de.mherrmann.famkidmem.ccms.management.video;

import de.mherrmann.famkidmem.ccms.Application;
import de.mherrmann.famkidmem.ccms.TestUtil;
import de.mherrmann.famkidmem.ccms.body.RequestBodyAddVideo;
import de.mherrmann.famkidmem.ccms.body.ResponseBody;
import de.mherrmann.famkidmem.ccms.item.Key;
import de.mherrmann.famkidmem.ccms.service.FfmpegService;
import de.mherrmann.famkidmem.ccms.service.VideoService;
import de.mherrmann.famkidmem.ccms.utils.CryptoUtil;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.*;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
@SpringBootTest
public class VideoManagementTestAdd {

    private static final String TEST_CONTENT = "Content";
    private static final String TEST_NAME = "test.txt";
    private static final String TEST_DIRECTORY = "./files/";
    private static final String RANDOM_NAME = "randomName";

    @MockBean
    private RestTemplate restTemplate;

    @MockBean
    private CryptoUtil cryptoUtil;

    @MockBean
    private FfmpegService ffmpegService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TestUtil testUtil;

    @Autowired
    private VideoService videoService;

    @Before
    public void setUp(){
        testUtil.createTestSettings();
        testUtil.createFilesDirectory();
    }

    @After
    public void tearDown(){
        testUtil.deleteFilesDirectory();
    }

    @Test
    public void shouldLoadAddVideoView() throws Exception {
        this.mockMvc.perform(get("/video/add"))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(model().attribute("post", Matchers.equalTo(false)));
    }

    @Test
    public void shouldUploadThumbnail() throws Exception {
        MockMultipartFile multipartFile = new MockMultipartFile("file", TEST_NAME,
                "text/plain", TEST_CONTENT.getBytes());

        MvcResult mvcResult = this.mockMvc.perform(multipart("/video/upload-thumbnail").file(multipartFile))
                .andExpect(status().isOk())
                .andReturn();

        assertThat(new File(TEST_DIRECTORY + "thumbnail.png").exists()).isTrue();
        assertThat(mvcResult.getResponse().getContentAsString()).isEqualTo("ok");
    }

    @Test
    public void shouldUploadVideo() throws Exception {
        MockMultipartFile multipartFile = new MockMultipartFile("file", TEST_NAME,
                "text/plain", TEST_CONTENT.getBytes());

        MvcResult mvcResult = this.mockMvc.perform(multipart("/video/upload-video").file(multipartFile))
                .andExpect(status().isOk())
                .andReturn();

        assertThat(new File(TEST_DIRECTORY + "video.mp4").exists()).isTrue();
        assertThat(mvcResult.getResponse().getContentAsString()).isEqualTo("ok");
    }

    @Test
    public void shouldFailUpload() throws Exception {
        MockMultipartFile multipartFile = new MockMultipartFile("file", TEST_NAME,
                "text/plain", TEST_CONTENT.getBytes());
        testUtil.deleteFilesDirectory();

        MvcResult mvcResult = this.mockMvc.perform(multipart("/video/upload-video").file(multipartFile))
                .andExpect(status().isBadRequest())
                .andReturn();

        assertThat(mvcResult.getResponse().getContentAsString()).isEqualTo("error: Could not save file. I/O Error");
    }

    @Test
    public void shouldEncrypt() throws Exception {
        createMediaFiles();
        byte[] expected = new byte[]{1,2,3,4,5,6,7,8};
        given(cryptoUtil.encrypt(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any()))
                .willReturn(expected);
        given(ffmpegService.encryptVideo(ArgumentMatchers.any())).willReturn(FfmpegService.getDummyState());

        this.mockMvc.perform(post("/video/encrypt"))
                .andExpect(status().isOk());

        String thumbnailPath = "";
        String m3u8Path = "";
        for(String file : new File("./files/").list()){
            if(!file.contains("thumbnail") && file.contains(".png")){
                thumbnailPath = file;
            }
            if(!file.contains("index") && file.contains(".m3u8")){
                m3u8Path = file;
            }
        }
        byte[] encryptedBytesThumbnail = Files.readAllBytes(Paths.get("./files/"+thumbnailPath));
        byte[] encryptedBytesM3u8 = Files.readAllBytes(Paths.get("./files/"+m3u8Path));
        assertThat(encryptedBytesThumbnail).isEqualTo(expected);
        assertThat(encryptedBytesM3u8).isEqualTo(expected);
    }

    @Test
    public void shouldUploadToWeb() throws Exception {
        videoService.getState().randomName = RANDOM_NAME;
        videoService.getState().tsFiles = 3;
        createEncryptedMediaFiles();
        given(restTemplate.exchange(eq(Application.getSettings().getBackendUrl()+"/ccms/upload"), eq(HttpMethod.POST), ArgumentMatchers.any(), eq(String.class)))
                .willReturn(ResponseEntity.ok("ok"));

        this.mockMvc.perform(post("/video/upload-web"))
                .andExpect(status().isOk());

        assertThat(new File("./files/").list().length).isEqualTo(0);
    }

    @Test
    public void shouldFailUploadToWebAndRollback() throws Exception {
        videoService.getState().randomName = RANDOM_NAME;
        videoService.getState().tsFiles = 3;
        createEncryptedMediaFiles();
        new File("./files/"+RANDOM_NAME+".2.ts").delete();
        given(restTemplate.exchange(eq(Application.getSettings().getBackendUrl()+"/ccms/upload"), eq(HttpMethod.POST), ArgumentMatchers.any(), eq(String.class)))
                .willReturn(ResponseEntity.ok("ok"));
        given(restTemplate.exchange(eq(Application.getSettings().getBackendUrl()+"/ccms/delete/"+RANDOM_NAME+".png"), eq(HttpMethod.DELETE), ArgumentMatchers.any(), eq(ResponseBody.class)))
                .willThrow(new RuntimeException("thrown while rollback"));

        MvcResult result = this.mockMvc.perform(post("/video/upload-web"))
                .andExpect(status().isBadRequest())
                .andReturn();

        assertThat(result.getResponse().getContentAsString()).isEqualTo("error: thrown while rollback");
    }

    @Test
    public void shouldAddVideoTwoPersons1999MarchSecondCologne() throws Exception {
        prepareAddVideoTest();
        RequestBodyAddVideo addVideoRequest = testUtil.createAddVideoRequestTwoPersons1999March2Cologne();

        this.mockMvc.perform(post("/video/add")
                .param("title", "title")
                .param("description", "description")
                .param("persons", "person1,person2")
                .param("recordedInCologne", "cologne")
                .param("year", "1999")
                .param("month", "3")
                .param("day", "2"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("success", Matchers.equalTo(true)))
                .andExpect(model().attribute("post", Matchers.equalTo(true)))
                .andExpect(model().attribute("addVideoRequest", Matchers.equalTo(addVideoRequest)));
    }

    @Test
    public void shouldAddVideoTwoPersons1999December31Gardelegen() throws Exception {
        prepareAddVideoTest();
        RequestBodyAddVideo addVideoRequest = testUtil.createAddVideoRequestTwoPersons1999December31Gardelegen();

        this.mockMvc.perform(post("/video/add")
                .param("title", "title")
                .param("description", "description")
                .param("persons", "person1,person2")
                .param("recordedInGardelegen", "gardelegen")
                .param("year", "1999")
                .param("month", "12")
                .param("day", "31"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("success", Matchers.equalTo(true)))
                .andExpect(model().attribute("post", Matchers.equalTo(true)))
                .andExpect(model().attribute("addVideoRequest", Matchers.equalTo(addVideoRequest)));
    }

    @Test
    public void shouldAddVideoOnePerson1999December31Silvester() throws Exception {
        prepareAddVideoTest();
        RequestBodyAddVideo addVideoRequest = testUtil.createAddVideoRequestOnePerson1999December31Silvester();

        this.mockMvc.perform(post("/video/add")
                .param("title", "title")
                .param("description", "description")
                .param("persons", "person1")
                .param("year", "1999")
                .param("month", "12")
                .param("day", "31")
                .param("silvester", "silvester"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("success", Matchers.equalTo(true)))
                .andExpect(model().attribute("post", Matchers.equalTo(true)))
                .andExpect(model().attribute("addVideoRequest", Matchers.equalTo(addVideoRequest)));
    }

    @Test
    public void shouldAddVideoOnePerson2000January11Silvester() throws Exception {
        prepareAddVideoTest();
        RequestBodyAddVideo addVideoRequest = testUtil.createAddVideoRequestOnePerson2000January1Silvester();

        this.mockMvc.perform(post("/video/add")
                .param("title", "title")
                .param("description", "description")
                .param("persons", "person1")
                .param("year", "2000")
                .param("month", "1")
                .param("day", "1")
                .param("silvester", "silvester"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("success", Matchers.equalTo(true)))
                .andExpect(model().attribute("post", Matchers.equalTo(true)))
                .andExpect(model().attribute("addVideoRequest", Matchers.equalTo(addVideoRequest)));
    }

    @Test
    public void shouldAddVideoOnePerson2000January11() throws Exception {
        prepareAddVideoTest();
        RequestBodyAddVideo addVideoRequest = testUtil.createAddVideoRequestOnePerson2000January1();

        this.mockMvc.perform(post("/video/add")
                .param("title", "title")
                .param("description", "description")
                .param("persons", "person1")
                .param("year", "2000")
                .param("month", "1")
                .param("day", "1"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("success", Matchers.equalTo(true)))
                .andExpect(model().attribute("post", Matchers.equalTo(true)))
                .andExpect(model().attribute("addVideoRequest", Matchers.equalTo(addVideoRequest)));
    }

    @Test
    public void shouldAddVideoOnePerson2003May() throws Exception {
        prepareAddVideoTest();
        RequestBodyAddVideo addVideoRequest = testUtil.createAddVideoRequestOnePerson2003May();

        this.mockMvc.perform(post("/video/add")
                .param("title", "title")
                .param("description", "description")
                .param("persons", "person1")
                .param("year", "2003")
                .param("month", "5")
                .param("day", "0"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("success", Matchers.equalTo(true)))
                .andExpect(model().attribute("post", Matchers.equalTo(true)))
                .andExpect(model().attribute("addVideoRequest", Matchers.equalTo(addVideoRequest)));
    }

    @Test
    public void shouldAddVideoOnePerson2005CologneAndGardelegen() throws Exception {
        prepareAddVideoTest();
        RequestBodyAddVideo addVideoRequest = testUtil.createAddVideoRequestOnePerson2005CologneAndGardelegen();

        this.mockMvc.perform(post("/video/add")
                .param("title", "title")
                .param("description", "description")
                .param("persons", "person1")
                .param("recordedInCologne", "cologne")
                .param("recordedInGardelegen", "gardelegen")
                .param("year", "2005")
                .param("month", "0")
                .param("day", "0"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("success", Matchers.equalTo(true)))
                .andExpect(model().attribute("post", Matchers.equalTo(true)))
                .andExpect(model().attribute("addVideoRequest", Matchers.equalTo(addVideoRequest)));
    }

    private void prepareAddVideoTest() throws Exception {
        byte[] keyDummy = new byte[]{0,0,0,0,0,0,0,0};
        byte[] encryptedTitleDummy = new byte[]{1,1,1,1,1,1,1,1};
        byte[] encryptedDescriptionDummy = new byte[]{2,2,2,2,2,2,2,2};
        videoService.getState().randomName = RANDOM_NAME;
        videoService.getState().tsFiles = 3;
        videoService.getState().seconds = 24;
        videoService.getState().thumbnailKey = new Key("thumbnailKey", "thumbnailIv");
        videoService.getState().m3u8Key = new Key("videoKey", "videoIv");
        given(restTemplate.exchange(eq(Application.getSettings().getBackendUrl()+"/ccms/edit/video/add"), eq(HttpMethod.POST), ArgumentMatchers.any(), eq(ResponseBody.class)))
                .willReturn(testUtil.createTestResponseEntityStatusOk());
        given(cryptoUtil.generateSecureRandomKeyParam())
                .willReturn(keyDummy);
        given(cryptoUtil.encrypt(ArgumentMatchers.eq("title".getBytes("UTF-8")), ArgumentMatchers.any(), ArgumentMatchers.any()))
                .willReturn(encryptedTitleDummy);
        given(cryptoUtil.encrypt(ArgumentMatchers.eq("description".getBytes("UTF-8")), ArgumentMatchers.any(), ArgumentMatchers.any()))
                .willReturn(encryptedDescriptionDummy);
        given(cryptoUtil.toBase64(ArgumentMatchers.eq(encryptedTitleDummy)))
                .willReturn("titleEncrypted");
        given(cryptoUtil.toBase64(ArgumentMatchers.eq(encryptedDescriptionDummy)))
                .willReturn("descriptionEncrypted");
        given(cryptoUtil.toBase64(ArgumentMatchers.eq(keyDummy)))
                .willReturn("iv");
        given(cryptoUtil.encryptKey(ArgumentMatchers.eq(keyDummy)))
                .willReturn("keyEncrypted");
    }

    private void createMediaFiles() throws IOException {
        new File("./files/thumbnail.png").createNewFile();
        new File("./files/index.m3u8").createNewFile();
    }

    private void createEncryptedMediaFiles() throws IOException {
        new File("./files/"+RANDOM_NAME+".png").createNewFile();
        new File("./files/"+RANDOM_NAME+".m3u8").createNewFile();
        new File("./files/"+RANDOM_NAME+".0.ts").createNewFile();
        new File("./files/"+RANDOM_NAME+".1.ts").createNewFile();
        new File("./files/"+RANDOM_NAME+".2.ts").createNewFile();
    }

}
