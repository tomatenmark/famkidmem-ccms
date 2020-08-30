package de.mherrmann.famkidmem.ccms.service.push;

public class PushMessage {

    private String message;
    private String details;
    private int value;

    private PushMessage(String message, String details) {
        this.message = message;
        this.details = details;
    }

    private PushMessage(String message, String details, int value) {
        this.message = message;
        this.details = details;
        this.value = value;
    }

    private PushMessage(String message) {
        this.message = message;
    }

    public static PushMessage thumbnailUploadComplete(){
        return new PushMessage("thumbnailUploadComplete");
    }

    public static PushMessage videoUploadComplete(){
        return new PushMessage("videoUploadComplete");
    }

    public static PushMessage videoEncryptionProgress(String logLine, int progress){
        return new PushMessage("videoEncryptionProgress", logLine, progress);
    }

    public static PushMessage videoEncryptionError(String details){
        return new PushMessage("videoEncryptionError", details);
    }

    public static PushMessage finishedWithThumbnail(){
        return new PushMessage("finishedWithThumbnail");
    }

    public static PushMessage finishedWithVideo(){
        return new PushMessage("finishedWithVideo");
    }

    public static PushMessage finishedWithWebUpload(){
        return new PushMessage("finishedWithWebUpload");
    }

    public static PushMessage error(String details){
        return new PushMessage("error", details);
    }

    public static PushMessage webBackendUploadProgress(int current, int total){
        int value = (int) Math.round(100.0 / total * current);
        String details = String.format("File %d/%d", current, total);
        return new PushMessage("webBackendUploadProgress", details, value);
    }

    public static PushMessage base64(String filename, String base64){
        return new PushMessage(filename, base64);
    }

    public String getMessage() {
        return message;
    }

    public String getDetails() {
        return details;
    }

    public int getValue() {
        return value;
    }
}
