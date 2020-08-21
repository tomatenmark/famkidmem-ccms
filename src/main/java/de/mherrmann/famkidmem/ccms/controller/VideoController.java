package de.mherrmann.famkidmem.ccms.controller;

import de.mherrmann.famkidmem.ccms.service.video.VideoAddService;
import de.mherrmann.famkidmem.ccms.service.video.VideoEditService;
import de.mherrmann.famkidmem.ccms.service.video.VideoIndexService;
import de.mherrmann.famkidmem.ccms.service.video.VideoRemoveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServletRequest;

@Controller
@MultipartConfig
public class VideoController {

    private final VideoAddService videoAddService;
    private final VideoIndexService videoIndexService;
    private final VideoEditService videoEditService;
    private final VideoRemoveService videoRemoveService;

    @Autowired
    public VideoController(VideoAddService videoAddService, VideoIndexService videoIndexService,
                           VideoEditService videoEditService, VideoRemoveService videoRemoveService) {
        this.videoAddService = videoAddService;
        this.videoIndexService = videoIndexService;
        this.videoEditService = videoEditService;
        this.videoRemoveService = videoRemoveService;
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

    @GetMapping(value = "/video/file/ts/{filename}")
    public ResponseEntity<ByteArrayResource> getTsFile(@PathVariable String filename){
        try {
            return videoIndexService.getTsFile(filename);
        } catch(Exception ex){
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping(value = "/video/add")
    public String loadAddVideoView(Model model){
        model.addAttribute("post", false);
        return "video/add";
    }

    @GetMapping(value = "/video/edit-data/{title}")
    public String loadEditDataView(Model model, @PathVariable String title){
        videoEditService.fillEditDataModel(model, title);
        return "video/edit-data";
    }

    @GetMapping(value = "/video/replace-thumbnail/{designator}")
    public String loadReplaceThumbnailView(Model model, @PathVariable String designator){
        videoEditService.fillReplaceThumbnailModel(model, designator);
        return "video/replace-thumbnail";
    }

    @GetMapping(value = "/video/remove/{designator}")
    public String loadRemoveVideoView(Model model, @PathVariable String designator){
        videoRemoveService.fillRemoveVideoModel(model, designator);
        return "video/remove";
    }

    @PostMapping(value = "/video/add")
    public String addVideo(HttpServletRequest request, Model model){
        videoAddService.addVideo(request, model);
        return "video/add";
    }

    @PostMapping(value = "/video/edit-data/{designator}")
    public String editData(HttpServletRequest request, Model model, @PathVariable String designator){
        videoEditService.editData(model, request, designator);
        return "video/edit-data";
    }

    @PostMapping(value = "/video/replace-thumbnail/{designator}")
    public String replaceThumbnail(MultipartFile file, Model model, @PathVariable String designator){
        videoEditService.replaceThumbnail(file, model, designator);
        return "video/replace-thumbnail";
    }

    @PostMapping(value = "/video/remove/{designator}")
    public String deleteVideo(Model model, HttpServletRequest request, @PathVariable String designator){
        videoRemoveService.deleteVideo(model, request, designator);
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
