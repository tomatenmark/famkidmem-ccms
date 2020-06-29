package de.mherrmann.famkidmem.ccms.management.video;

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
public class VideoManagementTestReplaceThumbnail {

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

    //TODO: add tests: shouldReplaceThumbnail, shouldFailReplaceThumbnailCausedByBadRequestResponse, shouldFailReplaceThumbnailCausedByConnectionFailure
}
