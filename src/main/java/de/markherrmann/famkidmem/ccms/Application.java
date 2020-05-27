package de.markherrmann.famkidmem.ccms;

import de.markherrmann.famkidmem.ccms.settings.Settings;
import de.markherrmann.famkidmem.ccms.settings.SettingsReader;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application {

    private static Settings settings;

    public static void main(String[] args) {
        settings = SettingsReader.readSettings();
        SpringApplication.run(Application.class, args);
    }

    public static Settings getSettings(){
        return settings;
    }
}