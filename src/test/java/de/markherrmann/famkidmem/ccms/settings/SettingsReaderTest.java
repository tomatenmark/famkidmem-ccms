package de.markherrmann.famkidmem.ccms.settings;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.After;
import org.junit.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class SettingsReaderTest {

    @After
    public void tearDown(){
        new File("./settings.json").delete();
    }

    @Test
    public void shouldReadSettings() throws IOException {
        Settings reference = createTestSettings();

        Settings actual = SettingsReader.readSettings();

        assertToBe(actual, reference);
    }

    private Settings createTestSettings() throws IOException {
        Settings settings = new Settings();
        settings.setApiKey("apiKey");
        settings.setBackendFilesDir("/opt/dir/");
        settings.setBackendUrl("https://ccms.example.de");
        settings.setMasterKey("key");
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

    private void assertToBe(Settings actual, Settings reference){
        assertEquals(actual.getApiKey(), reference.getApiKey());
        assertEquals(actual.getMasterKey(), reference.getMasterKey());
        assertEquals(actual.getBackendFilesDir(), reference.getBackendFilesDir());
        assertEquals(actual.getBackendUrl(), reference.getBackendUrl());
    }

    private static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
