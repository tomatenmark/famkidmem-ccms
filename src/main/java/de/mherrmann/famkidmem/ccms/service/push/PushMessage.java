package de.mherrmann.famkidmem.ccms.service.push;

import de.mherrmann.famkidmem.ccms.item.FileEntity;

public class PushMessage {

    private String message;
    private String details;
    private String logLine;
    private boolean override;
    private FileEntity file;
    private int tsFiles;
    private int progress;

    private PushMessage(String message, String logLine, boolean override, int progress) {
        this.message = message;
        this.logLine = logLine;
        this.override = override;
        this.progress = progress;
    }

    private PushMessage(String message, FileEntity file) {
        this.message = message;
        this.file = file;
    }

    private PushMessage(String message, String details) {
        this.message = message;
        this.details = details;
    }

    private PushMessage(String message, FileEntity file, int tsfiles) {
        this.message = message;
        this.file = file;
        this.tsFiles = tsfiles;
    }

    private PushMessage(String message, int progress) {
        this.message = message;
        this.progress = progress;
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

    public static PushMessage thumbnailEncryptionProgress(int progress){
        return new PushMessage("thumbnailEncryptionProgress", progress);
    }

    public static PushMessage videoEncryptionProgress(String logLine, boolean override, int progress){
        return new PushMessage("videoEncryptionProgress", logLine, override, progress);
    }

    public static PushMessage videoEncryptionError(String details){
        return new PushMessage("videoEncryptionError", details);
    }

    public static PushMessage finishedWithThumbnail(FileEntity file){
        return new PushMessage("finishedWithThumbnail", file);
    }

    public static PushMessage finishedWithVideo(FileEntity file, int tsFiles){
        return new PushMessage("finishedWithVideo", file, tsFiles);
    }

    public String getMessage() {
        return message;
    }

    public String getDetails() {
        return details;
    }

    public String getLogLine() {
        return logLine;
    }

    public boolean isOverride() {
        return override;
    }

    public FileEntity getFile() {
        return file;
    }

    public int getTsFiles() {
        return tsFiles;
    }

    public int getProgress() {
        return progress;
    }
}
