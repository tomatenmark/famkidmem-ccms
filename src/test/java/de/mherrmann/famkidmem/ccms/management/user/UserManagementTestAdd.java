package de.mherrmann.famkidmem.ccms.management.user;

import de.mherrmann.famkidmem.ccms.Application;
import de.mherrmann.famkidmem.ccms.TestUtil;
import de.mherrmann.famkidmem.ccms.body.RequestBodyAddUser;
import de.mherrmann.famkidmem.ccms.body.ResponseBody;
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

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
@SpringBootTest
public class UserManagementTestAdd {

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
    public void shouldAddUser() throws Exception {
        RequestBodyAddUser addUserRequest = testUtil.createAddUserRequest();
        given(restTemplate.exchange(Application.getSettings().getBackendUrl()+"/ccms/admin/user/add", HttpMethod.POST, testUtil.createTestHttpEntityGivenBody(addUserRequest), ResponseBody.class))
                .willReturn(testUtil.createTestResponseEntityStatusOk());

        this.mockMvc.perform(post("/user/add")
                .param("displayName", addUserRequest.getDisplayName())
                .param("username", addUserRequest.getUsername())
                .param("loginHash", addUserRequest.getLoginHash())
                .param("passwordKeySalt", addUserRequest.getPasswordKeySalt())
                .param("userKey", addUserRequest.getMasterKey())
                .param("link", "link"))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(model().attribute("success", Matchers.equalTo(true)))
                .andExpect(model().attribute("post", Matchers.equalTo(true)))
                .andExpect(model().attribute("initLink", Matchers.equalTo("link")));
    }

    @Test
    public void shouldFailAddUserCausedByBadRequestResponse() throws Exception {
        RequestBodyAddUser addUserRequest = testUtil.createAddUserRequest();
        given(restTemplate.exchange(Application.getSettings().getBackendUrl()+"/ccms/admin/user/add", HttpMethod.POST, testUtil.createTestHttpEntityGivenBody(addUserRequest), ResponseBody.class))
                .willReturn(testUtil.createTestResponseEntityStatusBadRequest());

        this.mockMvc.perform(post("/user/add")
                .param("displayName", addUserRequest.getDisplayName())
                .param("username", addUserRequest.getUsername())
                .param("loginHash", addUserRequest.getLoginHash())
                .param("passwordKeySalt", addUserRequest.getPasswordKeySalt())
                .param("userKey", addUserRequest.getMasterKey())
                .param("link", "link"))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(model().attribute("success", Matchers.equalTo(false)))
                .andExpect(model().attribute("post", Matchers.equalTo(true)))
                .andExpect(model().attribute("exception", Matchers.equalTo("RuntimeException")))
                .andExpect(model().attribute("details", Matchers.equalTo("testErrorDetails")));
    }

    @Test
    public void shouldFailAddUserCausedByConnectionFailure() throws Exception {
        RequestBodyAddUser addUserRequest = testUtil.createAddUserRequest();
        given(restTemplate.exchange(Application.getSettings().getBackendUrl()+"/ccms/admin/user/add", HttpMethod.POST, testUtil.createTestHttpEntityGivenBody(addUserRequest), ResponseBody.class))
                .willThrow(new RuntimeException());

        this.mockMvc.perform(post("/user/add")
                .param("displayName", addUserRequest.getDisplayName())
                .param("username", addUserRequest.getUsername())
                .param("loginHash", addUserRequest.getLoginHash())
                .param("passwordKeySalt", addUserRequest.getPasswordKeySalt())
                .param("userKey", addUserRequest.getMasterKey())
                .param("link", "link"))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(model().attribute("success", Matchers.equalTo(false)))
                .andExpect(model().attribute("post", Matchers.equalTo(true)))
                .andExpect(model().attribute("exception", Matchers.equalTo("java.lang.RuntimeException")))
                .andExpect(model().attribute("details", Matchers.equalTo("connection failure")));
    }

    @Test
    public void shouldLoadAddUserView() throws Exception {
        String testRandomCredential = "testRandomCredential";
        given(cryptoUtil.generateSecureRandomCredential()).willReturn(testRandomCredential);

        this.mockMvc.perform(get("/user/add"))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(model().attribute("frontendUrl", Matchers.equalTo(Application.getSettings().getFrontendUrl())))
                .andExpect(model().attribute("masterKey", Matchers.equalTo(Application.getSettings().getMasterKey())))
                .andExpect(model().attribute("username", Matchers.equalTo(testRandomCredential)))
                .andExpect(model().attribute("password", Matchers.equalTo(testRandomCredential)))
                .andExpect(model().attribute("post", Matchers.equalTo(false)));
    }

}
