package de.mherrmann.famkidmem.ccms.management.video;

import de.mherrmann.famkidmem.ccms.Application;
import de.mherrmann.famkidmem.ccms.TestUtil;
import de.mherrmann.famkidmem.ccms.body.RequestBodyUpdateVideo;
import de.mherrmann.famkidmem.ccms.body.ResponseBodyGetVideos;
import de.mherrmann.famkidmem.ccms.item.Person;
import de.mherrmann.famkidmem.ccms.item.Video;
import de.mherrmann.famkidmem.ccms.item.Year;
import de.mherrmann.famkidmem.ccms.utils.CryptoUtil;
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

import java.sql.Timestamp;
import java.util.Collections;
import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
@SpringBootTest
public class VideoManagementTestEditData {

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
    }

    @Test
    public void shouldLoadEditDataViewSilvester1994December31() throws Exception {
        List<Video> videos = testUtil.createVideosList();
        given(restTemplate.exchange(Application.getSettings().getBackendUrl()+"/ccms/edit/video/get/video1", HttpMethod.GET, testUtil.createTestHttpEntityNoBody(), ResponseBodyGetVideos.class))
                .willReturn(testUtil.createTestResponseEntityGetVideos(videos));


        this.mockMvc.perform(get("/video/edit-data/video1"))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(model().attribute("video", Matchers.equalTo(videos.get(0))))
                .andExpect(model().attribute("persons", Matchers.equalTo("person1,person2")))
                .andExpect(model().attribute("years", Matchers.equalTo("1994,1995")))
                .andExpect(model().attribute("year", Matchers.equalTo(1994)))
                .andExpect(model().attribute("month", Matchers.equalTo(12)))
                .andExpect(model().attribute("day", Matchers.equalTo(31)))
                .andExpect(model().attribute("masterKey", Matchers.equalTo(Application.getSettings().getMasterKey())))
                .andExpect(model().attribute("success", Matchers.equalTo(true)));
    }

    @Test
    public void shouldLoadEditDataView1995May() throws Exception {
        List<Video> videos = testUtil.createVideosList();
        Year year = new Year();
        year.setValue(1995);
        videos.get(0).setYears(Collections.singletonList(year));
        videos.get(0).setShowDateValues(6);
        videos.get(0).setTimestamp(new Timestamp(799347600000L));
        given(restTemplate.exchange(Application.getSettings().getBackendUrl()+"/ccms/edit/video/get/video1", HttpMethod.GET, testUtil.createTestHttpEntityNoBody(), ResponseBodyGetVideos.class))
                .willReturn(testUtil.createTestResponseEntityGetVideos(videos));


        this.mockMvc.perform(get("/video/edit-data/video1"))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(model().attribute("video", Matchers.equalTo(videos.get(0))))
                .andExpect(model().attribute("persons", Matchers.equalTo("person1,person2")))
                .andExpect(model().attribute("years", Matchers.equalTo("1995")))
                .andExpect(model().attribute("year", Matchers.equalTo(1995)))
                .andExpect(model().attribute("month", Matchers.equalTo(5)))
                .andExpect(model().attribute("day", Matchers.equalTo(0)))
                .andExpect(model().attribute("masterKey", Matchers.equalTo(Application.getSettings().getMasterKey())))
                .andExpect(model().attribute("success", Matchers.equalTo(true)));
    }

    @Test
    public void shouldLoadEditDataView2000OnePerson() throws Exception {
        List<Video> videos = testUtil.createVideosList();
        Year year = new Year();
        year.setValue(2000);
        Person person = new Person();
        person.setName("Max");
        videos.get(0).setYears(Collections.singletonList(year));
        videos.get(0).setPersons(Collections.singletonList(person));
        videos.get(0).setShowDateValues(4);
        videos.get(0).setTimestamp(new Timestamp(946749600000L));
        given(restTemplate.exchange(Application.getSettings().getBackendUrl()+"/ccms/edit/video/get/video1", HttpMethod.GET, testUtil.createTestHttpEntityNoBody(), ResponseBodyGetVideos.class))
                .willReturn(testUtil.createTestResponseEntityGetVideos(videos));


        this.mockMvc.perform(get("/video/edit-data/video1"))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(model().attribute("video", Matchers.equalTo(videos.get(0))))
                .andExpect(model().attribute("persons", Matchers.equalTo("Max")))
                .andExpect(model().attribute("years", Matchers.equalTo("2000")))
                .andExpect(model().attribute("year", Matchers.equalTo(2000)))
                .andExpect(model().attribute("month", Matchers.equalTo(0)))
                .andExpect(model().attribute("day", Matchers.equalTo(0)))
                .andExpect(model().attribute("masterKey", Matchers.equalTo(Application.getSettings().getMasterKey())))
                .andExpect(model().attribute("success", Matchers.equalTo(true)));
    }

    @Test
    public void shouldUpdateVideoTwoPersons1999MarchSecondCologne() throws Exception {
        testUtil.prepareUpdateVideoTest(cryptoUtil, restTemplate);
        RequestBodyUpdateVideo updateVideoRequest = testUtil.createUpdateVideoRequestTwoPersons1999March2Cologne();

        this.mockMvc.perform(post("/video/edit-data/title")
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
                .andExpect(model().attribute("updateVideoRequest", Matchers.equalTo(updateVideoRequest)));
    }

    @Test
    public void shouldUpdateVideoTwoPersons1999December31Gardelegen() throws Exception {
        testUtil.prepareUpdateVideoTest(cryptoUtil, restTemplate);
        RequestBodyUpdateVideo updateVideoRequest = testUtil.createUpdateVideoRequestTwoPersons1999December31Gardelegen();

        this.mockMvc.perform(post("/video/edit-data/title")
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
                .andExpect(model().attribute("updateVideoRequest", Matchers.equalTo(updateVideoRequest)));
    }

    @Test
    public void shouldUpdateVideoOnePerson1999December31Silvester() throws Exception {
        testUtil.prepareUpdateVideoTest(cryptoUtil, restTemplate);
        RequestBodyUpdateVideo updateVideoRequest = testUtil.createUpdateVideoRequestOnePerson1999December31Silvester();

        this.mockMvc.perform(post("/video/edit-data/title")
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
                .andExpect(model().attribute("updateVideoRequest", Matchers.equalTo(updateVideoRequest)));
    }

    @Test
    public void shouldUpdateVideoOnePerson2000January11Silvester() throws Exception {
        testUtil.prepareUpdateVideoTest(cryptoUtil, restTemplate);
        RequestBodyUpdateVideo updateVideoRequest = testUtil.createUpdateVideoRequestOnePerson2000January1Silvester();

        this.mockMvc.perform(post("/video/edit-data/title")
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
                .andExpect(model().attribute("updateVideoRequest", Matchers.equalTo(updateVideoRequest)));
    }

    @Test
    public void shouldUpdateVideoOnePerson2000January11() throws Exception {
        testUtil.prepareUpdateVideoTest(cryptoUtil, restTemplate);
        RequestBodyUpdateVideo updateVideoRequest = testUtil.createUpdateVideoRequestOnePerson2000January1();

        this.mockMvc.perform(post("/video/edit-data/title")
                .param("title", "title")
                .param("description", "description")
                .param("persons", "person1")
                .param("year", "2000")
                .param("month", "1")
                .param("day", "1"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("success", Matchers.equalTo(true)))
                .andExpect(model().attribute("post", Matchers.equalTo(true)))
                .andExpect(model().attribute("updateVideoRequest", Matchers.equalTo(updateVideoRequest)));
    }

    @Test
    public void shouldUpdateVideoOnePerson2003May() throws Exception {
        testUtil.prepareUpdateVideoTest(cryptoUtil, restTemplate);
        RequestBodyUpdateVideo updateVideoRequest = testUtil.createUpdateVideoRequestOnePerson2003May();

        this.mockMvc.perform(post("/video/edit-data/title")
                .param("title", "title")
                .param("description", "description")
                .param("persons", "person1")
                .param("year", "2003")
                .param("month", "5")
                .param("day", "0"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("success", Matchers.equalTo(true)))
                .andExpect(model().attribute("post", Matchers.equalTo(true)))
                .andExpect(model().attribute("updateVideoRequest", Matchers.equalTo(updateVideoRequest)));
    }

    @Test
    public void shouldUpdateVideoOnePerson2005CologneAndGardelegen() throws Exception {
        testUtil.prepareUpdateVideoTest(cryptoUtil, restTemplate);
        RequestBodyUpdateVideo updateVideoRequest = testUtil.createUpdateVideoRequestOnePerson2005CologneAndGardelegen();

        this.mockMvc.perform(post("/video/edit-data/title")
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
                .andExpect(model().attribute("updateVideoRequest", Matchers.equalTo(updateVideoRequest)));
    }

}
