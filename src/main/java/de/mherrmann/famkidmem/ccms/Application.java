package de.mherrmann.famkidmem.ccms;

import de.mherrmann.famkidmem.ccms.settings.Settings;
import de.mherrmann.famkidmem.ccms.settings.SettingsReader;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class Application {

    private static Settings settings;

    public static void main(String[] args) {
        setSettings(SettingsReader.readSettings());
        SpringApplication.run(Application.class, args);
    }

    public static void setSettings(Settings settings){
        Application.settings = settings;
    }

    public static Settings getSettings(){
        return settings;
    }


    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

}


