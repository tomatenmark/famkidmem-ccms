package de.mherrmann.famkidmem.ccms.item;

public class FileEntity {

    private Key key;
    private String filename;

    private FileEntity(){}

    public FileEntity(String filename, Key key){
        this.filename = filename;
        this.key = key;
    }

    public Key getKey() {
        return key;
    }

    public void setKey(Key key) {
        this.key = key;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

}
