package de.mherrmann.famkidmem.ccms.body;

public class RequestBodyUpdateVideo extends RequestBodyVideoData {

    private String designator;

    public String getDesignator() {
        return designator;
    }

    public void setDesignator(String designator) {
        this.designator = designator;
    }

    @Override
    public boolean equals(Object other){
        return super.equals(other);
    }
}
