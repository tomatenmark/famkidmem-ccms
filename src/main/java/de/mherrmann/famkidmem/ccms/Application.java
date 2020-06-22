package de.mherrmann.famkidmem.ccms;

import de.mherrmann.famkidmem.ccms.settings.Settings;
import de.mherrmann.famkidmem.ccms.settings.SettingsReader;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

import java.io.File;

@SpringBootApplication
public class Application {

    private static Settings settings;

    public static void main(String[] args) {
        setSettings(SettingsReader.readSettings());
        createFilesDir();
        SpringApplication.run(Application.class, args);
    }

    public static void setSettings(Settings settings){
        Application.settings = settings;
    }

    public static Settings getSettings(){
        return settings;
    }

    private static void createFilesDir(){
        File dir = new File("./files");
        if(!dir.exists()){
            dir.mkdir();
        }
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

}


