package de.mherrmann.famkidmem.ccms.body;

import de.mherrmann.famkidmem.ccms.item.Video;

import java.util.List;

public class ResponseBodyGetVideos extends ResponseBody {

    private List<Video> videos;

    public ResponseBodyGetVideos(){}

    public ResponseBodyGetVideos(List<Video> videos){
        super("ok", "Successfully get videos");
        this.setVideos(videos);
    }

    public ResponseBodyGetVideos(Exception ex){
        super("error", ex.getMessage(), ex);
    }

    public List<Video> getVideos() {
        return videos;
    }

    public void setVideos(List<Video> videos) {
        this.videos = videos;
    }
}
