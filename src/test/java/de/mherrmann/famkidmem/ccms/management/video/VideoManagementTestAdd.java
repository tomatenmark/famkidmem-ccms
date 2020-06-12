package de.mherrmann.famkidmem.ccms.management.video;

import de.mherrmann.famkidmem.ccms.Application;
import de.mherrmann.famkidmem.ccms.TestUtil;
import de.mherrmann.famkidmem.ccms.body.ResponseBody;
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
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;
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

    @MockBean
    private RestTemplate restTemplate;

    @MockBean
    private CryptoUtil cryptoUtil;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TestUtil testUtil;

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
    public void shouldEncryptCheckThumbnail() throws Exception {
        new File("./files/thumbnail.png").createNewFile();
        byte[] expected = new byte[]{1,2,3,4,5,6,7,8};
        given(cryptoUtil.encrypt(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any()))
                .willReturn(expected);

        this.mockMvc.perform(post("/video/encrypt"))
                .andExpect(status().isOk());

        String path = "";
        for(String file : new File("./files/").list()){
            if(!file.contains("thumbnail")){
                path = file;
            }
        }
        byte[] encryptedBytes = Files.readAllBytes(Paths.get("./files/"+path));
        assertThat(encryptedBytes).isEqualTo(expected);
    }

    //TODO: add tests: shouldAddVideo, shouldFailAddVideoCausedByBadRequestResponse, shouldFailAddVideoCausedByConnectionFailure, (maybe) shouldFailAddVideoCausedByInvalidForm

    private static String byteToHex(byte num) {
        char[] hexDigits = new char[2];
        hexDigits[0] = Character.forDigit((num >> 4) & 0xF, 16);
        hexDigits[1] = Character.forDigit((num & 0xF), 16);
        return new String(hexDigits);
    }

}
