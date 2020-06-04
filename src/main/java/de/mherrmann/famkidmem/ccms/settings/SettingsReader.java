package de.mherrmann.famkidmem.ccms.settings;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.file.Files;

public class SettingsReader {

    private static final Logger LOGGER = LoggerFactory.getLogger(SettingsReader.class);

    public static Settings readSettings() {
        String json = getStringFromFile();
        if(json.isEmpty()){
            return null;
        }
        return jsonToSettings(json);
    }

    private static String getStringFromFile(){
        try {
            File settingsFile = new File("./settings.json");
            return new String(Files.readAllBytes(settingsFile.toPath()));
        } catch (Exception ex){
            LOGGER.error("Error while reading settings file settings.json: File not found or invalid. Shutting down");
            System.exit(1);
            return "";
        }
    }

    private static Settings jsonToSettings(final String json) {
        try {
            return new ObjectMapper().readValue(json, Settings.class);
        } catch (Exception ex) {
            LOGGER.error("Error while reading settings file settings.json: File not found or invalid. Shutting down");
            System.exit(1);
            return null;
        }
    }
}
