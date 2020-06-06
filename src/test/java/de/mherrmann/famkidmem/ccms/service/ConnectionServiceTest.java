package de.mherrmann.famkidmem.ccms.service;

import de.mherrmann.famkidmem.ccms.Application;
import de.mherrmann.famkidmem.ccms.TestUtil;
import de.mherrmann.famkidmem.ccms.body.ResponseBody;
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

import static org.assertj.core.api.Assertions.assertThat;
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
    public void shouldReturnResponseBodyGet(){
        given(
                this.restTemplate.exchange(Application.getSettings().getBackendUrl(), HttpMethod.GET, testUtil.createTestHttpEntityNoBody(), ResponseBody.class))
                .willReturn(testUtil.createTestResponseEntityStatusOk());

        ResponseEntity<ResponseBody> body = connectionService.doGetRequest("");

        assertThat(body.getBody().getDetails()).isEqualTo("testDetails");
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

}
