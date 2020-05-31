package de.markherrmann.famkidmem.ccms;

import de.markherrmann.famkidmem.ccms.settings.Settings;
import de.markherrmann.famkidmem.ccms.settings.SettingsReader;

public class Application {

    private static Settings settings;

    public static void main(String[] args) {
        settings = SettingsReader.readSettings();

    }

    public static Settings getSettings(){
        return settings;
    }
}
