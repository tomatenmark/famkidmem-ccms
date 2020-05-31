package de.markherrmann.famkidmem.ccms.settings;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.markherrmann.famkidmem.ccms.CcmsLogger;


import javax.swing.*;
import java.io.File;
import java.nio.file.Files;

public class SettingsReader {

    private static final CcmsLogger LOGGER = new CcmsLogger(SettingsReader.class);

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
            showAndLogError();
            System.exit(1);
            return "";
        }
    }

    private static Settings jsonToSettings(final String json) {
        try {
            return new ObjectMapper().readValue(json, Settings.class);
        } catch (Exception ex) {
            showAndLogError();
            System.exit(1);
            return null;
        }
    }

    private static void showAndLogError(){
        String message = "Error while reading settings file settings.json: File not found or invalid. Shutting down";
        LOGGER.error(message);
        JOptionPane.showMessageDialog(null, message, "Error!", JOptionPane.ERROR_MESSAGE);
    }
}
