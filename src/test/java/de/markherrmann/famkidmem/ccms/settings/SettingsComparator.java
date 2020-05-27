package de.markherrmann.famkidmem.ccms.settings;

import java.util.Comparator;

import static java.util.Comparator.*;

public class SettingsComparator implements Comparator<Settings> {

    @Override
    public int compare(Settings s1, Settings s2){
        return comparing(Settings::getApiKey)
                .thenComparing(Settings::getBackendFilesDir)
                .thenComparing(Settings::getBackendHost)
                .thenComparing(Settings::getMasterKey)
                .compare(s1, s2);
    }

}
