package de.mherrmann.famkidmem.ccms.management.video;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.mherrmann.famkidmem.ccms.Application;
import de.mherrmann.famkidmem.ccms.TestUtil;
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
public class VideoManagementTestIndex {

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
    public void shouldLoadIndexView() throws Exception {
        List<Video> videos = testUtil.createVideosList();
        given(restTemplate.exchange(Application.getSettings().getBackendUrl()+"/ccms/edit/video/get", HttpMethod.GET, testUtil.createTestHttpEntityNoBody(), ResponseBodyGetVideos.class))
                .willReturn(testUtil.createTestResponseEntityGetVideos(videos));


        this.mockMvc.perform(get("/video/index"))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(model().attribute("videosJson", Matchers.equalTo(asJsonString(videos))))
                .andExpect(model().attribute("videosCount", Matchers.equalTo(2)))
                .andExpect(model().attribute("success", Matchers.equalTo(true)));
    }

    private static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
