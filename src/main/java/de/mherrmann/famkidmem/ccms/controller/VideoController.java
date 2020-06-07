package de.mherrmann.famkidmem.ccms.controller;

import de.mherrmann.famkidmem.ccms.service.VideoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

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
}
