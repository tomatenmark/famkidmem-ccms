package de.mherrmann.famkidmem.ccms.controller;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
@SpringBootTest
public class ErrorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void shouldHandle() throws Exception {
        // Calls /error path directly. This causes internal server error, which we can test
        MvcResult mvcResult = this.mockMvc.perform(get("/error"))
                .andExpect(status().is(HttpStatus.INTERNAL_SERVER_ERROR.value()))
                .andReturn();

        String response = mvcResult.getResponse().getContentAsString();
        assertThat(response).contains("Status: 500 Internal Server Error");
    }
}
