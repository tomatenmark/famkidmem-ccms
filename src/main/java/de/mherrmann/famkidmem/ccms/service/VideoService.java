package de.mherrmann.famkidmem.ccms.service;

import de.mherrmann.famkidmem.ccms.body.ResponseBodyGetVideos;
import de.mherrmann.famkidmem.ccms.exception.EncryptionException;
import de.mherrmann.famkidmem.ccms.exception.FileUploadException;
import de.mherrmann.famkidmem.ccms.exception.WebBackendException;
import de.mherrmann.famkidmem.ccms.item.Key;
import de.mherrmann.famkidmem.ccms.item.Video;
import de.mherrmann.famkidmem.ccms.service.push.PushMessage;
import de.mherrmann.famkidmem.ccms.service.push.PushService;
import de.mherrmann.famkidmem.ccms.utils.CryptoUtil;
import de.mherrmann.famkidmem.ccms.utils.ExceptionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
public class VideoService {

    private final ConnectionService connectionService;
    private final ExceptionUtil exceptionUtil;
    private final PushService pushService;
    private final CryptoUtil cryptoUtil;
    private final FfmpegService ffmpegService;

    private static final Logger LOGGER = LoggerFactory.getLogger(VideoService.class);

    @Autowired
    public VideoService(ConnectionService connectionService, ExceptionUtil exceptionUtil, PushService pushService,
                        CryptoUtil cryptoUtil, FfmpegService ffmpegService) {
        this.connectionService = connectionService;
        this.exceptionUtil = exceptionUtil;
        this.pushService = pushService;
        this.cryptoUtil = cryptoUtil;
        this.ffmpegService = ffmpegService;
    }

    public void fillIndexModel(Model model){
        try {
            model.addAttribute("users", getVideos());
            model.addAttribute("success", true);
        } catch(Exception ex){
            exceptionUtil.handleException(ex, model, LOGGER);
            model.addAttribute("users", Collections.emptyList());
        }
    }

    public void fillEditDataModel(Model model){
        /* TODO: fill:
            * video
            * post=false
            * comma-separated stringified list of years
            * comma-separated stringified list of persons
            * year, read from timestamp
            * month, read from timestamp (0, if video.showDataValues < 2 )
            * day, read from timestamp   (0, if video.showDataValues < 3 )
         */
    }

    public void fillReplaceThumbnailModel(Model model){
        /* TODO: fill:
         * post=false
         * title (video.title)
         */
    }

    public void fillRemoveVideoModel(Model model){
        /* TODO: fill:
         * post=false
         * title (video.title)
         * m3u8 (base64 content of m3u8 file from server)
         * m3u8 filename
         * thumbnail filename
         */
    }

    public void addVideo(HttpServletRequest request, Model model){
        //TODO: save video attributes including keys ivs and filenames (included in request)
    }

    public void replaceThumbnail(MultipartFile file, Model model, String title){
        //TODO:  save file to files folder locally, encrypt it and then upload it to web backend
    }

    public void editData(HttpServletRequest request, Model model, String title){
        //TODO: save new video attributes (maybe get old via web-backend get video request)
    }

    public void deleteVideo(Model model, String title){
        //TODO: delete video (get names of files to delete from request)
    }

    public void uploadThumbnail(MultipartFile file) throws FileUploadException {
        uploadFile(file, "thumbnail.png");
        pushService.push(PushMessage.thumbnailUploadComplete());
    }

    public void uploadVideo(MultipartFile file) throws FileUploadException {
        uploadFile(file, "video.mp4");
        pushService.push(PushMessage.videoUploadComplete());
    }

    public void encrypt() throws EncryptionException {
        String randomName = UUID.randomUUID().toString();
        encryptThumbnail(randomName);
        encryptVideo(randomName);
    }

    private void encryptThumbnail(String name) throws EncryptionException {
        byte[] key = cryptoUtil.generateSecureRandomKeyParam();
        byte[] iv = cryptoUtil.generateSecureRandomKeyParam();
        encryptFile(name, "thumbnail.png", "png", key, iv);
        Key keySpec = new Key(cryptoUtil.toBase64(key), cryptoUtil.toBase64(iv));
        pushService.push(PushMessage.finishedWithThumbnail(keySpec));
    }

    private void encryptVideo(String name) throws EncryptionException {
        ffmpegService.encryptVideo(name);
        Key key= encryptM3u8(name);
        pushService.push(PushMessage.finishedWithVideo(key));
    }

    private Key encryptM3u8(String name){
        byte[] key = cryptoUtil.generateSecureRandomKeyParam();
        byte[] iv = cryptoUtil.generateSecureRandomKeyParam();
        encryptFile(name, "index.m3u8", "m3u8", key, iv);
        return new Key(cryptoUtil.toBase64(key), cryptoUtil.toBase64(iv));
    }

    private void encryptFile(String randomName, String filename, String extension, byte[] key, byte[] iv) throws EncryptionException {
        try {
            byte[] plain = Files.readAllBytes(Paths.get("./files/"+filename));
            byte[] encrypted = cryptoUtil.encrypt(plain, key, iv);
            FileOutputStream stream = new FileOutputStream("./files/"+randomName+"."+extension);
            stream.write(encrypted);
            stream.close();
        } catch(GeneralSecurityException | IOException ex){
            throw new EncryptionException("Exception during encryption: "+ex);
        }
    }

    private void uploadFile(MultipartFile file, String filename) throws FileUploadException {
        try {
            File destinationFile = new File("./files/" + filename);
            InputStream inputStream = file.getInputStream();
            Files.copy(inputStream, destinationFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            LOGGER.info("Successfully saved file {}", filename);
        } catch(IOException ex){
            LOGGER.error("Could not save file {}. I/O Error", filename, ex);
            throw new FileUploadException("Could not save file. I/O Error");
        }
    }

    @SuppressWarnings("unchecked") //we know, the assignment will work
    private List<Video> getVideos() throws Exception {
        ResponseEntity<ResponseBodyGetVideos> response = connectionService.doGetRequest( "/ccms/edit/video/get/");
        if(response.getStatusCode().is2xxSuccessful()){
            return response.getBody().getVideos();
        }
        throw new WebBackendException(response.getBody());
    }
}
