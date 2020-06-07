package de.mherrmann.famkidmem.ccms.body;

import de.mherrmann.famkidmem.ccms.item.Video;

import java.util.List;

public class ResponseBodyContentIndex {

    private List<Video> videos;
    private List<String> persons;
    private List<Integer> years;
    private String masterKey;

    private ResponseBodyContentIndex(){}

    public List<Video> getVideos() {
        return videos;
    }

    public void setVideos(List<Video> videos) {
        this.videos = videos;
    }

    public List<String> getPersons() {
        return persons;
    }

    public void setPersons(List<String> persons) {
        this.persons = persons;
    }

    public List<Integer> getYears() {
        return years;
    }

    public void setYears(List<Integer> years) {
        this.years = years;
    }

    public String getMasterKey() {
        return masterKey;
    }

    public void setMasterKey(String masterKey) {
        this.masterKey = masterKey;
    }
}
