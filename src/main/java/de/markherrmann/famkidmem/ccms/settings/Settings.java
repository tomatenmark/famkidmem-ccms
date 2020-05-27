package de.markherrmann.famkidmem.ccms.settings;

public class Settings {

    private String backendHost;
    private String backendPort;
    private String backendProtocol;
    private String backendFilesDir;
    private String apiKey;
    private String sshHost;
    private String sshPort;
    private String sshKeyPath;
    private String sshTunnelLocalPort;

    public String getBackendHost() {
        return backendHost;
    }

    public void setBackendHost(String backendHost) {
        this.backendHost = backendHost;
    }

    public String getBackendPort() {
        return backendPort;
    }

    public void setBackendPort(String backendPort) {
        this.backendPort = backendPort;
    }

    public String getBackendProtocol() {
        return backendProtocol;
    }

    public void setBackendProtocol(String backendProtocol) {
        this.backendProtocol = backendProtocol;
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

    public String getSshHost() {
        return sshHost;
    }

    public void setSshHost(String sshHost) {
        this.sshHost = sshHost;
    }

    public String getSshPort() {
        return sshPort;
    }

    public void setSshPort(String sshPort) {
        this.sshPort = sshPort;
    }

    public String getSshKeyPath() {
        return sshKeyPath;
    }

    public void setSshKeyPath(String sshKey) {
        this.sshKeyPath = sshKey;
    }

    public String getSshTunnelLocalPort() {
        return sshTunnelLocalPort;
    }

    public void setSshTunnelLocalPort(String sshTunnelLocalPort) {
        this.sshTunnelLocalPort = sshTunnelLocalPort;
    }
}
