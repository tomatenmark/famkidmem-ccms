package de.mherrmann.famkidmem.ccms.management.user;

import de.mherrmann.famkidmem.ccms.Application;
import de.mherrmann.famkidmem.ccms.TestUtil;
import de.mherrmann.famkidmem.ccms.body.ResponseBody;
import de.mherrmann.famkidmem.ccms.item.User;
import org.hamcrest.Matchers;
import org.junit.After;
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

import java.util.Collections;
import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
@SpringBootTest
public class UserManagementTestIndex {

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
        List<User> users = testUtil.createUsersList();
        given(restTemplate.exchange(Application.getSettings().getBackendUrl()+"/ccms/admin/user/get", HttpMethod.GET, testUtil.createTestHttpEntityNoBody(), ResponseBody.class))
                .willReturn(testUtil.createTestResponseEntityGetUsers(users));


        this.mockMvc.perform(get("/user/index"))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(model().attribute("users", Matchers.equalTo(users)))
                .andExpect(model().attribute("success", Matchers.equalTo(true)));
    }

    @Test
    public void shouldLoadIndexViewEmpty() throws Exception {
        List<User> users = Collections.emptyList();
        given(restTemplate.exchange(Application.getSettings().getBackendUrl()+"/ccms/admin/user/get", HttpMethod.GET, testUtil.createTestHttpEntityNoBody(), ResponseBody.class))
                .willReturn(testUtil.createTestResponseEntityGetUsers(users));


        this.mockMvc.perform(get("/user/index"))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(model().attribute("users", Matchers.equalTo(users)))
                .andExpect(model().attribute("success", Matchers.equalTo(true)));
    }

    @Test
    public void shouldFailLoadIndexViewCausedByBadRequestResponse() throws Exception {
        given(restTemplate.exchange(Application.getSettings().getBackendUrl()+"/ccms/admin/user/get", HttpMethod.GET, testUtil.createTestHttpEntityNoBody(), ResponseBody.class))
                .willReturn(testUtil.createTestResponseEntityGetUsersError());


        this.mockMvc.perform(get("/user/index"))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(model().attribute("users", Matchers.hasSize(0)))
                .andExpect(model().attribute("exception", Matchers.equalTo("RuntimeException")))
                .andExpect(model().attribute("details", Matchers.equalTo("testException")))
                .andExpect(model().attribute("success", Matchers.equalTo(false)));
    }

    @Test
    public void shouldFailLoadIndexViewCausedByConnectionFailure() throws Exception {
        given(restTemplate.exchange(Application.getSettings().getBackendUrl()+"/ccms/admin/user/get", HttpMethod.GET, testUtil.createTestHttpEntityNoBody(), ResponseBody.class))
                .willThrow(new RuntimeException());


        this.mockMvc.perform(get("/user/index"))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(model().attribute("users", Matchers.hasSize(0)))
                .andExpect(model().attribute("exception", Matchers.equalTo("java.lang.RuntimeException")))
                .andExpect(model().attribute("details", Matchers.equalTo("connection failure")))
                .andExpect(model().attribute("success", Matchers.equalTo(false)));
    }

}
