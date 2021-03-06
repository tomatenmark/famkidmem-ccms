package de.mherrmann.famkidmem.ccms.exception;

import de.mherrmann.famkidmem.ccms.body.ResponseBody;

public class WebBackendException extends Exception {

    private String details;
    private String exception;

    public WebBackendException(ResponseBody body){
        this.details = body.getDetails();
        this.exception = body.getException();
    }

    public WebBackendException(){
        this.details = "unknown. see logs of web-backend";
        this.exception = "unknown. see logs of web-backend";
    }

    public String getDetails() {
        return details;
    }

    public String getException() {
        return exception;
    }
}
