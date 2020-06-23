package de.mherrmann.famkidmem.ccms.service.video;

import de.mherrmann.famkidmem.ccms.body.ResponseBodyGetVideos;
import de.mherrmann.famkidmem.ccms.exception.WebBackendException;
import de.mherrmann.famkidmem.ccms.item.Video;
import de.mherrmann.famkidmem.ccms.service.ConnectionService;
import de.mherrmann.famkidmem.ccms.utils.ExceptionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.TimeZone;

@Service
public class VideoIndexService {

    private final ConnectionService connectionService;
    private final ExceptionUtil exceptionUtil;

    private static final Logger LOGGER = LoggerFactory.getLogger(VideoIndexService.class);

    @Autowired
    public VideoIndexService(ConnectionService connectionService, ExceptionUtil exceptionUtil) {
        this.connectionService = connectionService;
        this.exceptionUtil = exceptionUtil;
    }

    public void fillIndexModel(Model model){
        try {
            model.addAttribute("videos", getVideos());
            model.addAttribute("success", true);
        } catch(Exception ex){
            exceptionUtil.handleException(ex, model, LOGGER);
            model.addAttribute("users", Collections.emptyList());
        }
    }

    public String getFormattedDate(Video video){
        LocalDateTime date =
                LocalDateTime.ofInstant(Instant.ofEpochMilli(video.getTimestamp().getTime()),
                        TimeZone.getDefault().toZoneId());
        String dateFormatted = String.valueOf(date.getYear());
        if(video.getShowDateValues() >= 6){
            dateFormatted += "-" + String.valueOf(date.getMonth());
        }
        if(video.getShowDateValues() == 7){
            dateFormatted += "-" + String.valueOf(date.getDayOfMonth());
        }
        return dateFormatted;
    }

    public String getShortDescription(String description){
        description = description.substring(0,10);
        description = description.replaceAll("\\s+[^\\s]*$", "");
        description = description.trim();
        return description + "...";
    }

    @SuppressWarnings("unchecked") //we know, the assignment will work
    private List<Video> getVideos() throws Exception {
        ResponseEntity<ResponseBodyGetVideos> response = connectionService.doGetVideosRequest();
        if(response.getStatusCode().is2xxSuccessful()){
            return response.getBody().getVideos();
        }
        throw new WebBackendException(response.getBody());
    }
}
