package de.markherrmann.famkidmem.ccms.settings;

public class Settings {

    private String backendHost;
    private String backendFilesDir;
    private String apiKey;
    private String masterKey;

    public String getBackendHost() {
        return backendHost;
    }

    public void setBackendHost(String backendHost) {
        this.backendHost = backendHost;
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
