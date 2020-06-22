package de.mherrmann.famkidmem.ccms.body;

import java.util.List;

public class RequestBodyAddVideo {

    private String title;
    private String description;
    private int durationInSeconds;
    private boolean recordedInCologne;
    private boolean recordedInGardelegen;
    private List<Integer> years;
    private List<String> persons;
    private String key;
    private String iv;
    private String thumbnailFilename;
    private String thumbnailKey;
    private String thumbnailIv;
    private String m3u8Filename;
    private String m3u8Key;
    private String m3u8Iv;
    private int showDateValues;
    private long timestamp;

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

    public List<Integer> getYears() {
        return years;
    }

    public void setYears(List<Integer> years) {
        this.years = years;
    }

    public List<String> getPersons() {
        return persons;
    }

    public void setPersons(List<String> persons) {
        this.persons = persons;
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

    public String getThumbnailFilename() {
        return thumbnailFilename;
    }

    public void setThumbnailFilename(String thumbnailFilename) {
        this.thumbnailFilename = thumbnailFilename;
    }

    public String getThumbnailKey() {
        return thumbnailKey;
    }

    public void setThumbnailKey(String thumbnailKey) {
        this.thumbnailKey = thumbnailKey;
    }

    public String getThumbnailIv() {
        return thumbnailIv;
    }

    public void setThumbnailIv(String thumbnailIv) {
        this.thumbnailIv = thumbnailIv;
    }

    public String getM3u8Filename() {
        return m3u8Filename;
    }

    public void setM3u8Filename(String m3u8Filename) {
        this.m3u8Filename = m3u8Filename;
    }

    public String getM3u8Key() {
        return m3u8Key;
    }

    public void setM3u8Key(String m3u8Key) {
        this.m3u8Key = m3u8Key;
    }

    public String getM3u8Iv() {
        return m3u8Iv;
    }

    public void setM3u8Iv(String m3u8Iv) {
        this.m3u8Iv = m3u8Iv;
    }

    public int getShowDateValues() {
        return showDateValues;
    }

    public void setShowDateValues(int showDateValues) {
        this.showDateValues = showDateValues;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public boolean equals(Object other){
        if( !(other instanceof RequestBodyAddVideo)){
            return false;
        }
        RequestBodyAddVideo otherRequest = (RequestBodyAddVideo) other;
        return
                otherRequest.title.equals(this.title)
                && otherRequest.description.equals(this.description)
                && otherRequest.key.equals(this.key)
                && otherRequest.iv.equals(this.iv)
                && listEqualsList(otherRequest.persons, this.persons)
                && listEqualsList(otherRequest.years, this.years)
                && otherRequest.timestamp == this.timestamp
                && otherRequest.showDateValues == this.showDateValues;
    }

    private boolean listEqualsList(List list1, List list2){
        if(list1.size() != list2.size()){
            return false;
        }
        for(int i = 0; i < list1.size(); i++){
            if(!list1.get(i).equals(list2.get(i))){
                return false;
            }
        }
        return true;
    }
}
