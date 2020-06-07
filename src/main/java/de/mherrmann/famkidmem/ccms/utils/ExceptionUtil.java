package de.mherrmann.famkidmem.ccms.utils;

import de.mherrmann.famkidmem.ccms.exception.WebBackendException;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

@Service
public class ExceptionUtil {

    public void handleException(Exception ex, Model model, Logger logger){
        if(ex instanceof WebBackendException){
            fillWebBackendExceptionValues((WebBackendException) ex, model);
            logger.error("Error", ex);
        } else {
            fillExceptionValues(ex, model);
        }
    }

    private void fillExceptionValues(Exception ex, Model model){
        model.addAttribute("success", false);
        model.addAttribute("exception", ex.toString());
        model.addAttribute("details", "connection failure");
    }

    private void fillWebBackendExceptionValues(WebBackendException ex, Model model){
        model.addAttribute("success", false);
        model.addAttribute("details", ex.getDetails());
        model.addAttribute("exception", ex.getException());
    }

}
