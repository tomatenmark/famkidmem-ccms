package de.mherrmann.famkidmem.ccms.controller;

import de.mherrmann.famkidmem.ccms.utils.ErrorResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
public class CustomErrorController implements ErrorController {

    private static final Logger LOGGER = LoggerFactory.getLogger(CustomErrorController.class);

    @RequestMapping("/error")
    public String handleError(HttpServletRequest request, Model model, HttpServletResponse response) {
        HttpStatus httpStatus = ErrorResponseUtil.getErrorStatus(request);
        model.addAttribute("status", httpStatus.value());
        model.addAttribute("statusDetail", httpStatus.getReasonPhrase());
        LOGGER.error("Request error: status:{}; message:{}", httpStatus.value(), httpStatus.getReasonPhrase());
        response.setStatus(httpStatus.value());
        return "error";
    }

    @Override
    @Deprecated
    public String getErrorPath() {
        return "/error";
    }
}
