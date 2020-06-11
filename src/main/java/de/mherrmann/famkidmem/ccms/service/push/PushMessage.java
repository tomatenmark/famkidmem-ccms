package de.mherrmann.famkidmem.ccms.service.push;

import de.mherrmann.famkidmem.ccms.item.FileEntity;

import java.util.List;

public class PushMessage {

    private String message;
    private String details;
    private FileEntity file;
    private List<String> tsFilenames;
    private int progress;

    private PushMessage(String message, String details, int progress) {
        this.message = message;
        this.details = details;
        this.progress = progress;
    }

    private PushMessage(String message, FileEntity file) {
        this.message = message;
        this.file = file;
    }

    private PushMessage(String message, FileEntity file, List<String> tsFilenames) {
        this.message = message;
        this.file = file;
        this.tsFilenames = tsFilenames;
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

    public static PushMessage videoEncryptionProgress(String details, int progress){
        return new PushMessage("videoEncryptionProgress", details, progress);
    }

    public static PushMessage finishedWithThumbnail(FileEntity file){
        return new PushMessage("finishedWithThumbnail", file);
    }

    public static PushMessage finishedWithVideo(FileEntity file, List<String> tsFilenames){
        return new PushMessage("finishedWithVideo", file, tsFilenames);
    }

    public String getMessage() {
        return message;
    }

    public String getDetails() {
        return details;
    }

    public FileEntity getFile() {
        return file;
    }

    public List<String> getTsFilenames() {
        return tsFilenames;
    }

    public int getProgress() {
        return progress;
    }
}
