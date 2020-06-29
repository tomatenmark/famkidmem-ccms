package de.mherrmann.famkidmem.ccms.service.video;

import de.mherrmann.famkidmem.ccms.Application;
import de.mherrmann.famkidmem.ccms.body.ResponseBodyContentFileBase64;
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

@Service
public class VideoRemoveService {

    private final ConnectionService connectionService;
    private final ExceptionUtil exceptionUtil;

    private static final Logger LOGGER = LoggerFactory.getLogger(VideoRemoveService.class);


    @Autowired
    public VideoRemoveService(ConnectionService connectionService, ExceptionUtil exceptionUtil) {
        this.connectionService = connectionService;
        this.exceptionUtil = exceptionUtil;
    }

    public void fillRemoveVideoModel(Model model, String designator){
        model.addAttribute("post", false);
        model.addAttribute("title", designator);
        try {
            Video video = getVideo(designator);
            model.addAttribute("thumbnailFilename", video.getThumbnail().getFilename());
            model.addAttribute("m3u8Filename", video.getM3u8().getFilename());
            model.addAttribute("m3u8", getM3u8(video.getM3u8().getFilename()));
            model.addAttribute("m3u8", getM3u8(video.getM3u8().getFilename()));
            model.addAttribute("key", video.getM3u8().getKey().getKey());
            model.addAttribute("iv", video.getM3u8().getKey().getIv());
            model.addAttribute("masterKey", Application.getSettings().getMasterKey());
            model.addAttribute("success", true);
        } catch(Exception ex){
            LOGGER.error("Error. Could not replace thumbnail", ex);
            exceptionUtil.handleException(ex, model, LOGGER);
        }
    }

    public void deleteVideo(Model model, String title){
        //TODO: delete video (get names of files to delete from request)
    }

    @SuppressWarnings("unchecked") //we know, the assignment will work
    private Video getVideo(String designator) throws Exception {
        designator = designator.replace('_', '/').replace('-', '+');
        ResponseEntity<ResponseBodyGetVideos> response = connectionService.doGetSingleVideoRequest(designator);
        if(!response.getStatusCode().is2xxSuccessful()){
            throw new WebBackendException(response.getBody());
        }
        return response.getBody().getVideos().get(0);
    }

    @SuppressWarnings("unchecked") //we know, the assignment will work
    private String getM3u8(String filename) throws Exception {
        try {
            ResponseEntity<ResponseBodyContentFileBase64> response = connectionService.doGetBase64(filename);
            if(!response.getStatusCode().is2xxSuccessful()){
                throw new WebBackendException(response.getBody());
            }
            LOGGER.info("successfully get base64 of file {}", filename);
            return response.getBody().getBase64();
        } catch (Exception ex){
            LOGGER.error("could not get base64 of file {}", filename, ex);
            throw ex;
        }
    }

}
