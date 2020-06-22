package de.mherrmann.famkidmem.ccms.item;

public class Key {

    private String key;
    private String iv;

    private Key(){}

    public Key(String key, String iv){
        this.key = key;
        this.iv = iv;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getIv() {
        return iv;
    }

    public void setIv(String iv) {
        this.iv = iv;
    }

}
