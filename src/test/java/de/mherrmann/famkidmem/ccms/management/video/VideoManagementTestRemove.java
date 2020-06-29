package de.mherrmann.famkidmem.ccms.management.video;

import de.mherrmann.famkidmem.ccms.Application;
import de.mherrmann.famkidmem.ccms.TestUtil;
import de.mherrmann.famkidmem.ccms.body.ResponseBody;
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
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
                .andExpect(model().attribute("m3u8Key", Matchers.equalTo("m3u8Key")))
                .andExpect(model().attribute("m3u8Iv", Matchers.equalTo("m3u8Iv")))
                .andExpect(model().attribute("key", Matchers.equalTo("key")))
                .andExpect(model().attribute("iv", Matchers.equalTo("iv")))
                .andExpect(model().attribute("masterKey", Matchers.equalTo(Application.getSettings().getMasterKey())))
                .andExpect(model().attribute("post", Matchers.equalTo(false)))
                .andExpect(model().attribute("success", Matchers.equalTo(true)));
    }

    @Test
    public void shouldRemoveVideo() throws Exception {
        given(restTemplate.exchange(Application.getSettings().getBackendUrl()+"/ccms/delete/thumbnail", HttpMethod.DELETE, testUtil.createTestHttpEntityNoBody(), String.class))
                .willReturn(ResponseEntity.ok("ok"));
        given(restTemplate.exchange(Application.getSettings().getBackendUrl()+"/ccms/delete/m3u8", HttpMethod.DELETE, testUtil.createTestHttpEntityNoBody(), String.class))
                .willReturn(ResponseEntity.ok("ok"));
        given(restTemplate.exchange(Application.getSettings().getBackendUrl()+"/ccms/delete/sequence.0.ts", HttpMethod.DELETE, testUtil.createTestHttpEntityNoBody(), String.class))
                .willReturn(ResponseEntity.ok("ok"));
        given(restTemplate.exchange(Application.getSettings().getBackendUrl()+"/ccms/delete/sequence.1.ts", HttpMethod.DELETE, testUtil.createTestHttpEntityNoBody(), String.class))
                .willReturn(ResponseEntity.ok("ok"));
        given(restTemplate.exchange(Application.getSettings().getBackendUrl()+"/ccms/edit/video/delete/video1", HttpMethod.DELETE, testUtil.createTestHttpEntityNoBody(), ResponseBody.class))
                .willReturn(testUtil.createTestResponseEntityStatusOk());

        this.mockMvc.perform(post("/video/remove/video1")
                .param("thumbnailFilename", "thumbnail")
                .param("m3u8Filename", "m3u8")
                .param("tsFilesName", "sequence.%d.ts")
                .param("tsFilesCount", "2"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("success", Matchers.equalTo(true)))
                .andExpect(model().attribute("post", Matchers.equalTo(true)));
    }

    @Test
    public void checkCallRemoveVideo() throws Exception {
        given(restTemplate.exchange(Application.getSettings().getBackendUrl()+"/ccms/delete/thumbnail", HttpMethod.DELETE, testUtil.createTestHttpEntityNoBody(), String.class))
                .willReturn(ResponseEntity.ok("ok"));
        given(restTemplate.exchange(Application.getSettings().getBackendUrl()+"/ccms/delete/m3u8", HttpMethod.DELETE, testUtil.createTestHttpEntityNoBody(), String.class))
                .willReturn(ResponseEntity.ok("ok"));
        given(restTemplate.exchange(Application.getSettings().getBackendUrl()+"/ccms/delete/sequence.0.ts", HttpMethod.DELETE, testUtil.createTestHttpEntityNoBody(), String.class))
                .willReturn(ResponseEntity.ok("ok"));
        given(restTemplate.exchange(Application.getSettings().getBackendUrl()+"/ccms/delete/sequence.1.ts", HttpMethod.DELETE, testUtil.createTestHttpEntityNoBody(), String.class))
                .willReturn(ResponseEntity.ok("ok"));
        given(restTemplate.exchange(Application.getSettings().getBackendUrl()+"/ccms/edit/video/delete/video1", HttpMethod.DELETE, testUtil.createTestHttpEntityNoBody(), ResponseBody.class))
                .willThrow(new RuntimeException("was called"));

        this.mockMvc.perform(post("/video/remove/video1")
                .param("thumbnailFilename", "thumbnail")
                .param("m3u8Filename", "m3u8")
                .param("tsFilesName", "sequence.%d.ts")
                .param("tsFilesCount", "2"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("success", Matchers.equalTo(false)))
                .andExpect(model().attribute("exception", Matchers.equalTo("java.lang.RuntimeException: was called")))
                .andExpect(model().attribute("post", Matchers.equalTo(true)));
    }

    @Test
    public void checkCallDeleteFile() throws Exception {
        given(restTemplate.exchange(Application.getSettings().getBackendUrl()+"/ccms/delete/thumbnail", HttpMethod.DELETE, testUtil.createTestHttpEntityNoBody(), String.class))
                .willThrow(new RuntimeException("was called"));

        this.mockMvc.perform(post("/video/remove/video1")
                .param("thumbnailFilename", "thumbnail")
                .param("m3u8Filename", "m3u8")
                .param("tsFilesName", "sequence.%d.ts")
                .param("tsFilesCount", "2"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("success", Matchers.equalTo(false)))
                .andExpect(model().attribute("exception", Matchers.equalTo("java.lang.RuntimeException: was called")))
                .andExpect(model().attribute("post", Matchers.equalTo(true)));
    }
}
