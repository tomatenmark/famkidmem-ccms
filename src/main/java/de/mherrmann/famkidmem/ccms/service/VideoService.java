package de.mherrmann.famkidmem.ccms.service;

import de.mherrmann.famkidmem.ccms.body.ResponseBodyGetVideos;
import de.mherrmann.famkidmem.ccms.exception.WebBackendException;
import de.mherrmann.famkidmem.ccms.item.Video;
import de.mherrmann.famkidmem.ccms.utils.ExceptionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.util.Collections;
import java.util.List;

@Service
public class VideoService {

    private final ConnectionService connectionService;
    private final ExceptionUtil exceptionUtil;

    private static final Logger LOGGER = LoggerFactory.getLogger(VideoService.class);

    @Autowired
    public VideoService(ConnectionService connectionService, ExceptionUtil exceptionUtil) {
        this.connectionService = connectionService;
        this.exceptionUtil = exceptionUtil;
    }

    public void fillIndexModel(Model model){
        try {
            model.addAttribute("users", getVideos());
            model.addAttribute("success", true);
        } catch(Exception ex){
            exceptionUtil.handleException(ex, model, LOGGER);
            model.addAttribute("users", Collections.emptyList());
        }
    }

    public void fillEditDataModel(Model model){
        /* TODO: fill:
            * video
            * post=false
            * comma-separated stringified list of years
            * comma-separated stringified list of persons
            * year, read from timestamp
            * month, read from timestamp (0, if video.showDataValues < 2 )
            * day, read from timestamp   (0, if video.showDataValues < 3 )
         */
    }

    public void fillReplaceThumbnailModel(Model model){
        /* TODO: fill:
         * post=false
         * title (video.title)
         */
    }

    public void fillRemoveVideoModel(Model model){
        /* TODO: fill:
         * post=false
         * title (video.title)
         * m3u8 (base64 content of m3u8 file from server)
         * m3u8 filename
         * thumbnail filename
         */
    }

    @SuppressWarnings("unchecked") //we know, the assignment will work
    private List<Video> getVideos() throws Exception {
        ResponseEntity<ResponseBodyGetVideos> response = connectionService.doGetRequest( "/ccms/edit/video/get/");
        if(response.getStatusCode().is2xxSuccessful()){
            return response.getBody().getVideos();
        }
        throw new WebBackendException(response.getBody());
    }
}
