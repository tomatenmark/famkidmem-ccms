package de.mherrmann.famkidmem.ccms.controller;

import de.mherrmann.famkidmem.ccms.service.video.VideoAddService;
import de.mherrmann.famkidmem.ccms.service.video.VideoIndexService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;

@Controller
public class VideoController {

    private final VideoAddService videoAddService;
    private final VideoIndexService videoIndexService;

    @Autowired
    public VideoController(VideoAddService videoAddService, VideoIndexService videoIndexService) {
        this.videoAddService = videoAddService;
        this.videoIndexService = videoIndexService;
    }

    @GetMapping(value = "/video/index")
    public String loadIndexView(Model model){
        videoIndexService.fillIndexModel(model);
        return "video/index";
    }

    @GetMapping(value = "/video/file/base64/{filename}")
    public ResponseEntity<String> getBase64FromFile(@PathVariable String filename){
        try {
            videoIndexService.getBase64FromFile(filename);
            return ResponseEntity.ok("ok");
        } catch(Exception ex){
            return ResponseEntity.badRequest().body("error: " + ex.getMessage());
        }
    }

    @GetMapping(value = "/video/add")
    public String loadAddVideoView(Model model){
        model.addAttribute("post", false);
        return "video/add";
    }

    @GetMapping(value = "/video/edit-data")
    public String loadEditDataView(Model model){
        //videoAddService.fillEditDataModel(model); TODO: fix to VideoEditService
        return "video/index";
    }

    @GetMapping(value = "/video/replace-thumbnail")
    public String loadReplaceThumbnailView(Model model){
        //videoAddService.fillReplaceThumbnailModel(model); TODO: fix to VideoEditService
        return "video/index";
    }

    @GetMapping(value = "/video/remove")
    public String loadRemoveVideoView(Model model){
        //videoAddService.fillRemoveVideoModel(model); TODO: fix to VideoDeleteService
        return "video/index";
    }

    @PostMapping(value = "/video/add")
    public String addVideo(HttpServletRequest request, Model model){
        videoAddService.addVideo(request, model);
        return "video/add";
    }

    @PostMapping(value = "/video/edit-data/{title}")
    public String editData(HttpServletRequest request, Model model, @PathVariable String title){
        //videoAddService.editData(request, model, title); TODO: fix to VideoEditService
        return "video/edit-data";
    }

    @PostMapping(value = "/video/replace-thumbnail/{title}")
    public String replaceThumbnail(MultipartFile file, Model model, @PathVariable String title){
        //videoAddService.replaceThumbnail(file, model, title); TODO: fix to VideoEditService
        return "video/replace-thumbnail";
    }

    @PostMapping(value = "/video/remove/{title}")
    public String deleteVideo(Model model, @PathVariable String title){
        //videoAddService.deleteVideo(model, title); TODO: fix to VideoDeleteService
        return "video/remove";
    }

    @PostMapping(value = "/video/upload-video")
    public ResponseEntity<String> updateVideo(MultipartFile file){
        try {
            videoAddService.uploadVideo(file);
            return ResponseEntity.ok("ok");
        } catch(Exception ex){
            return ResponseEntity.badRequest().body("error: " + ex.getMessage());
        }
    }

    @PostMapping(value = "/video/upload-thumbnail")
    public ResponseEntity<String> updateThumbnail(MultipartFile file){
        try {
            videoAddService.uploadThumbnail(file);
            return ResponseEntity.ok("ok");
        } catch(Exception ex){
            return ResponseEntity.badRequest().body("error: " + ex.getMessage());
        }
    }

    @PostMapping(value = "/video/encrypt")
    public ResponseEntity<String> encrypt(){
        try {
            videoAddService.encrypt();
            return ResponseEntity.ok("ok");
        } catch(Exception ex){
            return ResponseEntity.badRequest().body("error: " + ex.getMessage());
        }
    }

    @PostMapping(value = "/video/upload-web")
    public ResponseEntity<String> uploadToWebBackend(){
        try {
            videoAddService.uploadToWebBackend();
            return ResponseEntity.ok("ok");
        } catch(Exception ex){
            return ResponseEntity.badRequest().body("error: " + ex.getMessage());
        }
    }
}
