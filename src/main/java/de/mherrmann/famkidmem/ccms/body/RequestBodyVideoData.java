package de.mherrmann.famkidmem.ccms.body;

import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

public class RequestBodyVideoData {
    private String title;
    private String description;
    private String key;
    private String iv;
    private boolean recordedInCologne;
    private boolean recordedInGardelegen;
    private List<Integer> years;
    private List<String> persons;
    private String thumbnailKey;
    private String thumbnailIv;
    private int showDateValues;
    private long timestamp;

    public RequestBodyVideoData(){}

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
        if( !(other instanceof RequestBodyVideoData)){
            return false;
        }
        RequestBodyVideoData otherRequest = (RequestBodyVideoData) other;
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
