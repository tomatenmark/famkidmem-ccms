package de.mherrmann.famkidmem.ccms.utils;

import org.springframework.http.HttpStatus;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;

public class ErrorResponseUtil {

    public static HttpStatus getErrorStatus(HttpServletRequest request){
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        Integer statusCode = 500;
        if(status != null) {
            statusCode = Integer.valueOf(status.toString());
        }
        return HttpStatus.resolve(statusCode);
    }
}
