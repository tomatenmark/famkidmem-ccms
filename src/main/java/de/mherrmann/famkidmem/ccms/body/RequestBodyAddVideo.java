package de.mherrmann.famkidmem.ccms.body;

import java.util.List;

public class RequestBodyAddVideo extends RequestBodyVideoData {

    private int durationInSeconds;
    private String thumbnailFilename;
    private String m3u8Filename;
    private String m3u8Key;
    private String m3u8Iv;

    public int getDurationInSeconds() {
        return durationInSeconds;
    }

    public void setDurationInSeconds(int durationInSeconds) {
        this.durationInSeconds = durationInSeconds;
    }

    public String getThumbnailFilename() {
        return thumbnailFilename;
    }

    public void setThumbnailFilename(String thumbnailFilename) {
        this.thumbnailFilename = thumbnailFilename;
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

    @Override
    public boolean equals(Object other){
        return super.equals(other);
    }
}
