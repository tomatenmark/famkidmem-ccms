package de.mherrmann.famkidmem.ccms.item;

import java.sql.Timestamp;
import java.util.List;

public class Video {

    private String title;
    private String description;
    private int durationInSeconds;
    private boolean recordedInCologne;
    private boolean recordedInGardelegen;
    private int showDateValues;
    private Timestamp timestamp;
    private List<Year> years;
    private List<Person> persons;
    private Key key;
    private FileEntity thumbnail;
    private FileEntity m3u8;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getDurationInSeconds() {
        return durationInSeconds;
    }

    public void setDurationInSeconds(int durationInSeconds) {
        this.durationInSeconds = durationInSeconds;
    }

    public boolean isRecordedInCologne() {
        return recordedInCologne;
    }

    public void setRecordedInCologne(boolean recordedInCologne) {
        this.recordedInCologne = recordedInCologne;
    }

    public boolean isRecordedInGardelegen() {
        return recordedInGardelegen;
    }

    public void setRecordedInGardelegen(boolean recordedInGardelegen) {
        this.recordedInGardelegen = recordedInGardelegen;
    }

    public int getShowDateValues() {
        return showDateValues;
    }

    public void setShowDateValues(int showDateValues) {
        this.showDateValues = showDateValues;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public List<Year> getYears() {
        return years;
    }

    public void setYears(List<Year> years) {
        this.years = years;
    }

    public List<Person> getPersons() {
        return persons;
    }

    public void setPersons(List<Person> persons) {
        this.persons = persons;
    }

    public Key getKey() {
        return key;
    }

    public void setKey(Key key) {
        this.key = key;
    }

    public FileEntity getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(FileEntity thumbnail) {
        this.thumbnail = thumbnail;
    }

    public FileEntity getM3u8() {
        return m3u8;
    }

    public void setM3u8(FileEntity m3u8) {
        this.m3u8 = m3u8;
    }

    @Override
    public boolean equals(Object other){
        if( !(other instanceof Video)){
            return false;
        }
        if(this.title == null){
            return ((Video) other).title == null;
        }
        return this.title.equals(((Video) other).title);
    }
}
