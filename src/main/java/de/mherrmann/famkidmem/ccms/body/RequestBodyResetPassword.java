package de.mherrmann.famkidmem.ccms.body;

public class RequestBodyResetPassword {

    private String username;
    private String loginHash;
    private String passwordKeySalt;
    private String masterKey;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getLoginHash() {
        return loginHash;
    }

    public void setLoginHash(String loginHash) {
        this.loginHash = loginHash;
    }

    public String getPasswordKeySalt() {
        return passwordKeySalt;
    }

    public void setPasswordKeySalt(String passwordKeySalt) {
        this.passwordKeySalt = passwordKeySalt;
    }

    public String getMasterKey() {
        return masterKey;
    }

    public void setMasterKey(String masterKey) {
        this.masterKey = masterKey;
    }

    @Override
    public boolean equals(Object other){
        if( !(other instanceof RequestBodyResetPassword)){
            return false;
        }
        if(this.username == null){
            return ((RequestBodyResetPassword) other).username == null;
        }
        return this.username.equals(((RequestBodyResetPassword) other).username);
    }
}
