package de.markherrmann.famkidmem.ccms.settings;

public class Settings {

    private String backendUrl;
    private String backendFilesDir;
    private String apiKey;
    private String masterKey;

    public String getBackendUrl() {
        return backendUrl;
    }

    public void setBackendUrl(String backendUrl) {
        this.backendUrl = backendUrl;
    }

    public String getBackendFilesDir() {
        return backendFilesDir;
    }

    public void setBackendFilesDir(String backendFilesDir) {
        this.backendFilesDir = backendFilesDir;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getMasterKey() {
        return masterKey;
    }

    public void setMasterKey(String masterKey) {
        this.masterKey = masterKey;
    }
}
