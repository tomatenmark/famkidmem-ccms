package de.mherrmann.famkidmem.ccms.service.video;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.mherrmann.famkidmem.ccms.body.ResponseBodyGetVideos;
import de.mherrmann.famkidmem.ccms.exception.WebBackendException;
import de.mherrmann.famkidmem.ccms.item.Video;
import de.mherrmann.famkidmem.ccms.service.ConnectionService;
import de.mherrmann.famkidmem.ccms.utils.CryptoUtil;
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
public class VideoIndexService {

    private final ConnectionService connectionService;
    private final ExceptionUtil exceptionUtil;
    private final CryptoUtil cryptoUtil;

    private static final Logger LOGGER = LoggerFactory.getLogger(VideoIndexService.class);

    @Autowired
    public VideoIndexService(ConnectionService connectionService, ExceptionUtil exceptionUtil, CryptoUtil cryptoUtil) {
        this.connectionService = connectionService;
        this.exceptionUtil = exceptionUtil;
        this.cryptoUtil = cryptoUtil;
    }

    public void fillIndexModel(Model model){
        try {
            List<Video> videos = getVideos();
            model.addAttribute("videosJson", asJsonString(videos));
            model.addAttribute("videosCount", videos.size());
            model.addAttribute("success", true);
        } catch(Exception ex){
            exceptionUtil.handleException(ex, model, LOGGER);
            model.addAttribute("users", Collections.emptyList());
        }
    }

    @SuppressWarnings("unchecked") //we know, the assignment will work
    private List<Video> getVideos() throws Exception {
        ResponseEntity<ResponseBodyGetVideos> response = connectionService.doGetVideosRequest();
        if(!response.getStatusCode().is2xxSuccessful()){
            throw new WebBackendException(response.getBody());
        }
        return response.getBody().getVideos();
    }

    private static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
