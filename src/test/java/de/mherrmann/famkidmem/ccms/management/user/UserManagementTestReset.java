package de.mherrmann.famkidmem.ccms.management.user;

import de.mherrmann.famkidmem.ccms.Application;
import de.mherrmann.famkidmem.ccms.TestUtil;
import de.mherrmann.famkidmem.ccms.body.RequestBodyResetPassword;
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
public class UserManagementTestReset {

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
    public void shouldResetPassword() throws Exception {
        RequestBodyResetPassword resetPasswordRequest = testUtil.createResetPasswordRequest();
        given(restTemplate.exchange(Application.getSettings().getBackendUrl()+"/ccms/admin/user/reset", HttpMethod.POST, testUtil.createTestHttpEntityGivenBody(resetPasswordRequest), ResponseBody.class))
                .willReturn(testUtil.createTestResponseEntityStatusOk());

        this.mockMvc.perform(post("/user/reset/{username}", resetPasswordRequest.getUsername())
                .param("username", resetPasswordRequest.getUsername())
                .param("loginHash", resetPasswordRequest.getLoginHash())
                .param("passwordKeySalt", resetPasswordRequest.getPasswordKeySalt())
                .param("userKey", resetPasswordRequest.getMasterKey())
                .param("link", "link"))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(model().attribute("success", Matchers.equalTo(true)))
                .andExpect(model().attribute("post", Matchers.equalTo(true)))
                .andExpect(model().attribute("resetLink", Matchers.equalTo("link")));
    }

    @Test
    public void shouldFailResetPasswordCausedByBadRequestResponse() throws Exception {
        RequestBodyResetPassword resetPasswordRequest = testUtil.createResetPasswordRequest();
        given(restTemplate.exchange(Application.getSettings().getBackendUrl()+"/ccms/admin/user/reset", HttpMethod.POST, testUtil.createTestHttpEntityGivenBody(resetPasswordRequest), ResponseBody.class))
                .willReturn(testUtil.createTestResponseEntityStatusBadRequest());

        this.mockMvc.perform(post("/user/reset/{username}", resetPasswordRequest.getUsername())
                .param("username", resetPasswordRequest.getUsername())
                .param("loginHash", resetPasswordRequest.getLoginHash())
                .param("passwordKeySalt", resetPasswordRequest.getPasswordKeySalt())
                .param("userKey", resetPasswordRequest.getMasterKey())
                .param("link", "link"))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(model().attribute("success", Matchers.equalTo(false)))
                .andExpect(model().attribute("post", Matchers.equalTo(true)))
                .andExpect(model().attribute("exception", Matchers.equalTo("RuntimeException")))
                .andExpect(model().attribute("details", Matchers.equalTo("testErrorDetails")));
    }

    @Test
    public void shouldFailResetPasswordCausedByConnectionFailure() throws Exception {
        RequestBodyResetPassword resetPasswordRequest = testUtil.createResetPasswordRequest();
        given(restTemplate.exchange(Application.getSettings().getBackendUrl()+"/ccms/admin/user/reset", HttpMethod.POST, testUtil.createTestHttpEntityGivenBody(resetPasswordRequest), ResponseBody.class))
                .willThrow(new RuntimeException());

        this.mockMvc.perform(post("/user/reset/{username}", resetPasswordRequest.getUsername())
                .param("username", resetPasswordRequest.getUsername())
                .param("loginHash", resetPasswordRequest.getLoginHash())
                .param("passwordKeySalt", resetPasswordRequest.getPasswordKeySalt())
                .param("userKey", resetPasswordRequest.getMasterKey())
                .param("link", "link"))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(model().attribute("success", Matchers.equalTo(false)))
                .andExpect(model().attribute("post", Matchers.equalTo(true)))
                .andExpect(model().attribute("exception", Matchers.equalTo("java.lang.RuntimeException")))
                .andExpect(model().attribute("details", Matchers.equalTo("connection failure")));
    }

    @Test
    public void shouldLoadResetPasswordView() throws Exception {
        RequestBodyResetPassword resetPasswordRequest = testUtil.createResetPasswordRequest();
        String testRandomCredential = "testRandomCredential";
        given(cryptoUtil.generateSecureRandomCredential()).willReturn(testRandomCredential);

        this.mockMvc.perform(get("/user/reset/{username}", resetPasswordRequest.getUsername()))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(model().attribute("frontendUrl", Matchers.equalTo(Application.getSettings().getFrontendUrl())))
                .andExpect(model().attribute("masterKey", Matchers.equalTo(Application.getSettings().getMasterKey())))
                .andExpect(model().attribute("username", Matchers.equalTo(resetPasswordRequest.getUsername())))
                .andExpect(model().attribute("password", Matchers.equalTo(testRandomCredential)))
                .andExpect(model().attribute("post", Matchers.equalTo(false)));
    }

}
