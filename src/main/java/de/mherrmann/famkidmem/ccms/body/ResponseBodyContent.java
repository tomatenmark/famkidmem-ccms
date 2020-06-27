package de.mherrmann.famkidmem.ccms.body;

public abstract class ResponseBodyContent extends ResponseBody {

    private String masterKey;

    protected ResponseBodyContent(){}

    public ResponseBodyContent(String masterKey){
        super("ok", "Successfully got content");
        this.masterKey = masterKey;
    }

    public ResponseBodyContent(Exception ex){
        super("error", ex.getMessage(), ex);
    }

    public String getMasterKey() {
        return masterKey;
    }

    public void setMasterKey(String masterKey) {
        this.masterKey = masterKey;
    }
}
