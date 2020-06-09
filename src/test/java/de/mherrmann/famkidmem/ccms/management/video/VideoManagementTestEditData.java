package de.mherrmann.famkidmem.ccms.management.video;

import de.mherrmann.famkidmem.ccms.TestUtil;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.RestTemplate;

@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
@SpringBootTest
public class VideoManagementTestEditData {

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
    public void shouldLoadEditDataView() throws Exception {
        //TODO: implement test (see VideoManagementTestIndex.shouldLoadIndexView)
    }

    //TODO: add tests: shouldEditDataVideo, shouldFailEditDataCausedByBadRequestResponse, shouldFailEditDataCausedByConnectionFailure, (maybe) shouldFailEditDataCausedByInvalidForm
}
