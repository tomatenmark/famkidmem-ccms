package de.mherrmann.famkidmem.ccms.management.video;

import de.mherrmann.famkidmem.ccms.Application;
import de.mherrmann.famkidmem.ccms.TestUtil;
import de.mherrmann.famkidmem.ccms.body.ResponseBodyContentFileBase64;
import de.mherrmann.famkidmem.ccms.body.ResponseBodyGetVideos;
import de.mherrmann.famkidmem.ccms.item.Video;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
@SpringBootTest
public class VideoManagementTestRemove {

    private static final String M3U8_BASE64 = "bTN1OA==";

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
    public void shouldLoadRemoveVideoView() throws Exception {
        List<Video> videos = testUtil.createVideosList();
        given(restTemplate.exchange(Application.getSettings().getBackendUrl()+"/ccms/edit/video/get/video1", HttpMethod.GET, testUtil.createTestHttpEntityNoBody(), ResponseBodyGetVideos.class))
                .willReturn(testUtil.createTestResponseEntityGetVideos(videos));
        given(restTemplate.exchange(Application.getSettings().getBackendUrl()+"/ccms/edit/video/base64/m3u8", HttpMethod.GET, testUtil.createTestHttpEntityNoBody(), ResponseBodyContentFileBase64.class))
                .willReturn(testUtil.createTestResponseEntityContentFileBase64(M3U8_BASE64));


        this.mockMvc.perform(get("/video/remove/video1"))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(model().attribute("title", Matchers.equalTo("video1")))
                .andExpect(model().attribute("thumbnailFilename", Matchers.equalTo("thumbnail")))
                .andExpect(model().attribute("m3u8Filename", Matchers.equalTo("m3u8")))
                .andExpect(model().attribute("m3u8", Matchers.equalTo(M3U8_BASE64)))
                .andExpect(model().attribute("post", Matchers.equalTo(false)))
                .andExpect(model().attribute("success", Matchers.equalTo(true)));
    }

    //TODO: add tests: shouldRemoveVideoVideo, shouldFailRemoveVideoCausedByBadRequestResponse, shouldFailRemoveVideoCausedByConnectionFailure
}
