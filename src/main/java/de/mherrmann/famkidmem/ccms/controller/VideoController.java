package de.mherrmann.famkidmem.ccms.controller;

import de.mherrmann.famkidmem.ccms.service.VideoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;

@Controller
public class VideoController {

    private final VideoService videoService;

    @Autowired
    public VideoController(VideoService videoService) {
        this.videoService = videoService;
    }

    @GetMapping(value = "/video/index")
    public String loadIndexView(Model model){
        videoService.fillIndexModel(model);
        return "video/index";
    }

    @GetMapping(value = "/video/add")
    public String loadAddVideoView(Model model){
        model.addAttribute("post", false);
        return "video/index";
    }

    @GetMapping(value = "/video/edit-data")
    public String loadEditDataView(Model model){
        videoService.fillEditDataModel(model);
        return "video/index";
    }

    @GetMapping(value = "/video/replace-thumbnail")
    public String loadReplaceThumbnailView(Model model){
        videoService.fillReplaceThumbnailModel(model);
        return "video/index";
    }

    @GetMapping(value = "/video/remove")
    public String loadRemoveVideoView(Model model){
        videoService.fillRemoveVideoModel(model);
        return "video/index";
    }

    @PostMapping(value = "/video/add")
    public String addVideo(HttpServletRequest request, Model model){
        videoService.addVideo(request, model);
        return "video/add";
    }

    @PostMapping(value = "/video/edit-data/{title}")
    public String editData(HttpServletRequest request, Model model, @PathVariable String title){
        videoService.editData(request, model, title);
        return "video/edit-data";
    }

    @PostMapping(value = "/video/replace-thumbnail/{title}")
    public String replaceThumbnail(MultipartFile file, Model model, @PathVariable String title){
        videoService.replaceThumbnail(file, model, title);
        return "video/replace-thumbnail";
    }

    @PostMapping(value = "/video/remove/{title}")
    public String deleteVideo(Model model, @PathVariable String title){
        videoService.deleteVideo(model, title);
        return "video/remove";
    }
}
