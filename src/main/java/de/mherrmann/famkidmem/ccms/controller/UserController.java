package de.mherrmann.famkidmem.ccms.controller;

import de.mherrmann.famkidmem.ccms.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpServletRequest;

@Controller
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping(value = "/user/index")
    public String loadIndex(Model model){
        userService.fillIndexModel(model);
        return "user/index";
    }

    @GetMapping(value = "/user/add")
    public String loadAdd(Model model){
        userService.fillAddUserModel(model, false);
        return "user/add";
    }

    @GetMapping(value = "/user/reset/{username}")
    public String loadReset(Model model, @PathVariable String username){
        userService.fillResetPasswordModel(model, username, false);
        return "user/reset";
    }

    @PostMapping(value = "/user/add")
    public String addUser(HttpServletRequest request, Model model){
        userService.addUser(request, model);
        return "user/add";
    }

    @PostMapping(value = "/user/reset/{username}")
    public String resetPassword(HttpServletRequest request, Model model, @PathVariable String username){
        userService.resetPassword(request, model, username);
        return "user/reset";
    }
}
