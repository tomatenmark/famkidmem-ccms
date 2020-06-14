package de.mherrmann.famkidmem.ccms.service.push;

import de.mherrmann.famkidmem.ccms.item.Key;

public class PushMessage {

    private String message;
    private String details;
    private boolean override;
    private Key key;
    private int value;

    private PushMessage(String message, String details, boolean override, int value) {
        this.message = message;
        this.details = details;
        this.value = value;
        this.override = override;
    }

    private PushMessage(String message, Key key) {
        this.message = message;
        this.key = key;
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

    public static PushMessage finishedWithThumbnail(Key key){
        return new PushMessage("finishedWithThumbnail", key);
    }

    public static PushMessage finishedWithVideo(Key key){
        return new PushMessage("finishedWithVideo", key);
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

    public Key getKey() {
        return key;
    }

    public int getValue() {
        return value;
    }
}
