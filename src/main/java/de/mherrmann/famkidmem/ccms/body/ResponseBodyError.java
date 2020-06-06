package de.mherrmann.famkidmem.ccms.body;

public class ResponseBodyError extends ResponseBody {
    private ResponseBodyError(){}

    public ResponseBodyError(Exception exception){
        super("error", exception.getMessage(), exception);
    }

    public ResponseBodyError(String error){
        super("error", "failure: " + error);
    }
}
