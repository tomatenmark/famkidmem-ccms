package de.markherrmann.famkidmem.ccms.settings;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SettingsReaderTest {

    @Before
    public void setUp(){
        SettingsReader.test = true;
    }

    @After
    public void tearDown(){
        new File("./settings.json").delete();
        SettingsReader.test = false;
    }

    @Test
    public void shouldReadSettings() throws IOException {
        Settings testSettings = createTestSettings();

        Settings actualSettings = SettingsReader.readSettings();

        assertThat(actualSettings).usingComparator(new SettingsComparator()).isEqualTo(testSettings);
    }

    @Test
    public void shouldFailReadSettingsCausedByFileNotFound() {
        Settings actualSettings = SettingsReader.readSettings();

        assertThat(actualSettings).isNull();
    }

    @Test
    public void shouldFailReadSettingsCausedByInvalidFile() throws IOException {
        createInvalidSettingsFile();

        Settings actualSettings = SettingsReader.readSettings();

        assertThat(actualSettings).isNull();
    }

    private Settings createTestSettings() throws IOException {
        Settings settings = new Settings();
        settings.setApiKey("apiKey");
        settings.setBackendFilesDir("/opt/dir/");
        settings.setBackendHost("example.com");
        settings.setBackendPort("4242");
        settings.setBackendProtocol("https");
        settings.setSshHost("ssh.example.com");
        settings.setSshPort("22");
        settings.setSshKeyPath("./key");
        settings.setSshTunnelLocalPort("14242");
        createTestSettingsFile(settings);
        return settings;
    }

    private void createTestSettingsFile(Settings settings) throws IOException {
        String json = asJsonString(settings);
        File file = new File("./settings.json");
        file.createNewFile();
        FileWriter fileWriter = new FileWriter(file.getPath());
        fileWriter.write(json);
        fileWriter.close();
    }

    private void createInvalidSettingsFile() throws IOException {
        File file = new File("./settings.json");
        file.createNewFile();
        FileWriter fileWriter = new FileWriter(file.getPath());
        fileWriter.write("invalid");
        fileWriter.close();
    }

    private static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
