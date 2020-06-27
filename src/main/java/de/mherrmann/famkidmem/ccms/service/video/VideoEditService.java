package de.mherrmann.famkidmem.ccms.service.video;

import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;

@Service
public class VideoEditService {

    

    //TODO: move to VideoEditService
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

    //TODO: move to VideoEditService
    public void fillReplaceThumbnailModel(Model model){
        /* TODO: fill:
         * post=false
         * title (video.title)
         */
    }

    public void replaceThumbnail(MultipartFile file, Model model, String title){
        //TODO:  save file to files folder locally, encrypt it and then upload it to web backend
    }

    public void editData(HttpServletRequest request, Model model, String title){
        //TODO: save new video attributes (maybe get old via web-backend get video request)
    }

}
