package de.mherrmann.famkidmem.ccms.management.video;

import de.mherrmann.famkidmem.ccms.TestUtil;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.client.RestTemplate;

import java.io.File;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
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

    //TODO: add tests: shouldAddVideo, shouldFailAddVideoCausedByBadRequestResponse, shouldFailAddVideoCausedByConnectionFailure, (maybe) shouldFailAddVideoCausedByInvalidForm
}
