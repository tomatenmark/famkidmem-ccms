package de.mherrmann.famkidmem.ccms.service.push;

public class PushMessage {

    private String message;
    private String details;
    private boolean override;
    private int value;

    private PushMessage(String message, String details, boolean override, int value) {
        this.message = message;
        this.details = details;
        this.value = value;
        this.override = override;
    }

    private PushMessage(String message, String details) {
        this.message = message;
        this.details = details;
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

    public static PushMessage videoEncryptionProgress(String logLine, boolean override, int progress){
        return new PushMessage("videoEncryptionProgress", logLine, override, progress);
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

    public String getMessage() {
        return message;
    }

    public String getDetails() {
        return details;
    }

    public boolean isOverride() {
        return override;
    }

    public int getValue() {
        return value;
    }
}
