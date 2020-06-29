package de.mherrmann.famkidmem.ccms.management.video;

import de.mherrmann.famkidmem.ccms.Application;
import de.mherrmann.famkidmem.ccms.TestUtil;
import de.mherrmann.famkidmem.ccms.body.RequestBodyUpdateVideo;
import de.mherrmann.famkidmem.ccms.body.ResponseBodyGetVideos;
import de.mherrmann.famkidmem.ccms.item.Video;
import de.mherrmann.famkidmem.ccms.utils.CryptoUtil;
import org.hamcrest.Matchers;
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
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
@SpringBootTest
public class VideoManagementTestReplaceThumbnail {

    @MockBean
    private CryptoUtil cryptoUtil;

    @MockBean
    private RestTemplate restTemplate;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TestUtil testUtil;

    @Before
    public void setUp(){
        testUtil.createTestSettings();
    }

    @Test
    public void shouldLoadReplaceThumbnailView() throws Exception {
        this.mockMvc.perform(get("/video/replace-thumbnail/video1"))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(model().attribute("post", Matchers.equalTo(false)))
                .andExpect(model().attribute("title", Matchers.equalTo("video1")));
    }

    @Test
    public void shouldReplaceThumbnail() throws Exception {
        MockMultipartFile multipartFile = new MockMultipartFile("file", "thumbnail",
                "text/plain", "thumbnail".getBytes());
        RequestBodyUpdateVideo updateVideoRequest = prepareReplaceThumbnailTest();

        this.mockMvc.perform(multipart("/video/replace-thumbnail/video1").file(multipartFile))
                .andExpect(status().isOk())
                .andExpect(model().attribute("success", Matchers.equalTo(true)))
                .andExpect(model().attribute("post", Matchers.equalTo(true)))
                .andExpect(model().attribute("updateVideoRequest", Matchers.equalTo(updateVideoRequest)));
    }

    @Test
    public void checkUploadToWebBackendCall() throws Exception {
        MockMultipartFile multipartFile = new MockMultipartFile("file", "thumbnail",
                "text/plain", "thumbnail".getBytes());
        prepareReplaceThumbnailTest();
        given(restTemplate.exchange(eq(Application.getSettings().getBackendUrl()+"/ccms/upload"), eq(HttpMethod.POST), ArgumentMatchers.any(), eq(String.class)))
                .willThrow(new RuntimeException("was called"));

        this.mockMvc.perform(multipart("/video/replace-thumbnail/video1").file(multipartFile))
                .andExpect(status().isOk())
                .andExpect(model().attribute("success", Matchers.equalTo(false)))
                .andExpect(model().attribute("post", Matchers.equalTo(true)))
                .andExpect(model().attribute("exception", Matchers.equalTo("java.lang.RuntimeException: was called")));
    }

    private RequestBodyUpdateVideo prepareReplaceThumbnailTest() throws Exception {
        testUtil.prepareUpdateVideoTest(cryptoUtil, restTemplate);
        RequestBodyUpdateVideo updateVideoRequest = testUtil.createUpdateVideoRequestTwoPersons1999March2Cologne();
        updateVideoRequest.setTitle("title");
        updateVideoRequest.setDescription("Description");
        updateVideoRequest.setKey("key");
        updateVideoRequest.setTitle("title");
        List<Integer> years = new ArrayList<>();
        years.add(1994);
        years.add(1995);
        updateVideoRequest.setYears(years);
        updateVideoRequest.setTimestamp(788896800000L);
        List<Video> videos = testUtil.createVideosList();
        videos.get(0).setTitle("title");
        given(restTemplate.exchange(Application.getSettings().getBackendUrl()+"/ccms/edit/video/get/video1", HttpMethod.GET, testUtil.createTestHttpEntityNoBody(), ResponseBodyGetVideos.class))
                .willReturn(testUtil.createTestResponseEntityGetVideos(videos));
        given(restTemplate.exchange(eq(Application.getSettings().getBackendUrl()+"/ccms/upload"), eq(HttpMethod.POST), ArgumentMatchers.any(), eq(String.class)))
                .willReturn(ResponseEntity.ok("ok"));
        given(cryptoUtil.encrypt(ArgumentMatchers.eq("thumbnail".getBytes("UTF-8")), ArgumentMatchers.any(), ArgumentMatchers.any()))
                .willReturn(new byte[]{8,8,8,8,8,8,8,8});

        return updateVideoRequest;
    }
}
