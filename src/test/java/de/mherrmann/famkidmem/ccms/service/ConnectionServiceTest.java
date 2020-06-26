package de.mherrmann.famkidmem.ccms.service;

import de.mherrmann.famkidmem.ccms.Application;
import de.mherrmann.famkidmem.ccms.TestUtil;
import de.mherrmann.famkidmem.ccms.body.ResponseBody;
import de.mherrmann.famkidmem.ccms.body.ResponseBodyContentFileBase64;
import de.mherrmann.famkidmem.ccms.body.ResponseBodyGetUsers;
import de.mherrmann.famkidmem.ccms.body.ResponseBodyGetVideos;
import de.mherrmann.famkidmem.ccms.item.User;
import de.mherrmann.famkidmem.ccms.item.Video;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ConnectionServiceTest {

    @MockBean
    private RestTemplate restTemplate;

    @Autowired
    private ConnectionService connectionService;

    @Autowired
    private TestUtil testUtil;

    @Before
    public void setUp(){
        testUtil.createTestSettings();
    }

    @Test
    public void shouldReturnResponseBodyGetUsers(){
        List<User> testList = new ArrayList<>();
        User user = new User();
        user.setUsername("testUser");
        testList.add(user);
        given(
                this.restTemplate.exchange(Application.getSettings().getBackendUrl()+"/ccms/admin/user/get", HttpMethod.GET, testUtil.createTestHttpEntityNoBody(), ResponseBodyGetUsers.class))
                .willReturn(testUtil.createTestResponseEntityGetUsers(testList));

        ResponseEntity<ResponseBodyGetUsers> body = connectionService.doGetUsersRequest();

        assertThat(body.getBody().getDetails()).isEqualTo("Successfully get users");
        assertThat(body.getBody().getUsers()).isEqualTo(testList);
    }

    @Test
    public void shouldReturnResponseBodyGetVideos(){
        List<Video> testList = new ArrayList<>();
        Video video = new Video();
        video.setTitle("testVideo");
        testList.add(video);
        given(
                this.restTemplate.exchange(Application.getSettings().getBackendUrl()+"/ccms/edit/video/get", HttpMethod.GET, testUtil.createTestHttpEntityNoBody(), ResponseBodyGetVideos.class))
                .willReturn(testUtil.createTestResponseEntityGetVideos(testList));

        ResponseEntity<ResponseBodyGetVideos> body = connectionService.doGetVideosRequest();

        assertThat(body.getBody().getDetails()).isEqualTo("Successfully get videos");
        assertThat(body.getBody().getVideos()).isEqualTo(testList);
    }

    @Test
    public void shouldReturnResponseBodyContentFileBase64(){
        String testBase64 = "YmFzZTY0";
        given(
                this.restTemplate.exchange(Application.getSettings().getBackendUrl()+"/ccms/edit/video/base64/filename", HttpMethod.GET, testUtil.createTestHttpEntityNoBody(), ResponseBodyContentFileBase64.class))
                .willReturn(testUtil.createTestResponseEntityContentFileBase64(testBase64));

        ResponseEntity<ResponseBodyContentFileBase64> body = connectionService.doGetBase64("filename");

        assertThat(body.getBody().getDetails()).isEqualTo("Successfully got content");
        assertThat(body.getBody().getBase64()).isEqualTo(testBase64);
    }

    @Test
    public void shouldReturnResponseBodyPost(){
        given(
                this.restTemplate.exchange(Application.getSettings().getBackendUrl(), HttpMethod.POST, testUtil.createTestHttpEntityTestBody(), ResponseBody.class))
                .willReturn(testUtil.createTestResponseEntityStatusOk());

        ResponseEntity<ResponseBody> body = connectionService.doPostRequest("", "", MediaType.APPLICATION_JSON);

        assertThat(body.getBody().getDetails()).isEqualTo("testDetails");
    }

    @Test
    public void shouldReturnResponseBodyDelete(){
        given(
                this.restTemplate.exchange(Application.getSettings().getBackendUrl(), HttpMethod.DELETE, testUtil.createTestHttpEntityTestBody(), ResponseBody.class))
                .willReturn(testUtil.createTestResponseEntityStatusOk());

        ResponseEntity<ResponseBody> body = connectionService.doDeleteRequest("", "", MediaType.APPLICATION_JSON);

        assertThat(body.getBody().getDetails()).isEqualTo("testDetails");
    }

    @Test
    public void shouldReturnResponseBodyUpload(){
        given(
                this.restTemplate.exchange(eq(Application.getSettings().getBackendUrl()+"/ccms/upload"), eq(HttpMethod.POST), any(), eq(String.class)))
                .willReturn(ResponseEntity.ok("ok"));

        ResponseEntity<ResponseBody> body = connectionService.doUploadRequest(null);

        assertThat(body.getBody()).isEqualTo("ok");
    }

}
