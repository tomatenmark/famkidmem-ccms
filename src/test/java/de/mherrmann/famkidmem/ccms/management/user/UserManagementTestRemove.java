package de.mherrmann.famkidmem.ccms.management.user;

import de.mherrmann.famkidmem.ccms.Application;
import de.mherrmann.famkidmem.ccms.TestUtil;
import de.mherrmann.famkidmem.ccms.body.RequestBodyDeleteUser;
import de.mherrmann.famkidmem.ccms.body.ResponseBody;
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

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
@SpringBootTest
public class UserManagementTestRemove {

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
    public void shouldRemoveUser() throws Exception {
        RequestBodyDeleteUser removeUserRequest = testUtil.createRemoveUserRequest();
        given(restTemplate.exchange(Application.getSettings().getBackendUrl()+"/ccms/admin/user/delete", HttpMethod.DELETE, testUtil.createTestHttpEntityGivenBody(removeUserRequest), ResponseBody.class))
                .willReturn(testUtil.createTestResponseEntityStatusOk());

        this.mockMvc.perform(post("/user/remove/{username}", removeUserRequest.getUsername())
                .param("username", removeUserRequest.getUsername()))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(model().attribute("success", Matchers.equalTo(true)))
                .andExpect(model().attribute("post", Matchers.equalTo(true)));
    }

    @Test
    public void shouldFailRemoveUserCausedByBadRequestResponse() throws Exception {
        RequestBodyDeleteUser removeUserRequest = testUtil.createRemoveUserRequest();
        given(restTemplate.exchange(Application.getSettings().getBackendUrl()+"/ccms/admin/user/delete", HttpMethod.DELETE, testUtil.createTestHttpEntityGivenBody(removeUserRequest), ResponseBody.class))
                .willReturn(testUtil.createTestResponseEntityStatusBadRequest());

        this.mockMvc.perform(post("/user/remove/{username}", removeUserRequest.getUsername())
                .param("username", removeUserRequest.getUsername()))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(model().attribute("success", Matchers.equalTo(false)))
                .andExpect(model().attribute("post", Matchers.equalTo(true)))
                .andExpect(model().attribute("exception", Matchers.equalTo("RuntimeException")))
                .andExpect(model().attribute("details", Matchers.equalTo("testErrorDetails")));
    }

    @Test
    public void shouldFailRemoveUserCausedByConnectionFailure() throws Exception {
        RequestBodyDeleteUser removeUserRequest = testUtil.createRemoveUserRequest();
        given(restTemplate.exchange(Application.getSettings().getBackendUrl()+"/ccms/admin/user/delete", HttpMethod.DELETE, testUtil.createTestHttpEntityGivenBody(removeUserRequest), ResponseBody.class))
                .willThrow(new RuntimeException());

        this.mockMvc.perform(post("/user/remove/{username}", removeUserRequest.getUsername())
                .param("username", removeUserRequest.getUsername()))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(model().attribute("success", Matchers.equalTo(false)))
                .andExpect(model().attribute("post", Matchers.equalTo(true)))
                .andExpect(model().attribute("exception", Matchers.equalTo("java.lang.RuntimeException")))
                .andExpect(model().attribute("details", Matchers.equalTo("connection failure")));
    }

    @Test
    public void shouldLoadRemoveUserView() throws Exception {
        RequestBodyDeleteUser removeUserRequest = testUtil.createRemoveUserRequest();

        this.mockMvc.perform(get("/user/remove/{username}", removeUserRequest.getUsername()))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(model().attribute("post", Matchers.equalTo(false)));
    }

}
