package de.mherrmann.famkidmem.ccms.service.video;

import de.mherrmann.famkidmem.ccms.body.RequestBodyAddVideo;
import de.mherrmann.famkidmem.ccms.body.ResponseBody;
import de.mherrmann.famkidmem.ccms.exception.EncryptionException;
import de.mherrmann.famkidmem.ccms.exception.FileUploadException;
import de.mherrmann.famkidmem.ccms.exception.WebBackendException;
import de.mherrmann.famkidmem.ccms.item.Key;
import de.mherrmann.famkidmem.ccms.service.ConnectionService;
import de.mherrmann.famkidmem.ccms.service.FfmpegService;
import de.mherrmann.famkidmem.ccms.service.push.PushMessage;
import de.mherrmann.famkidmem.ccms.service.push.PushService;
import de.mherrmann.famkidmem.ccms.utils.CryptoUtil;
import de.mherrmann.famkidmem.ccms.utils.ExceptionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.GeneralSecurityException;
import java.util.*;

@Service
public class VideoAddService {

    private final VideoDataService videoDataService;
    private final ConnectionService connectionService;
    private final ExceptionUtil exceptionUtil;
    private final PushService pushService;
    private final CryptoUtil cryptoUtil;
    private final FfmpegService ffmpegService;

    private State state = new State();

    private static final Logger LOGGER = LoggerFactory.getLogger(VideoAddService.class);

    @Autowired
    public VideoAddService(VideoDataService videoDataService, ConnectionService connectionService, ExceptionUtil exceptionUtil, PushService pushService,
                           CryptoUtil cryptoUtil, FfmpegService ffmpegService) {
        this.videoDataService = videoDataService;
        this.connectionService = connectionService;
        this.exceptionUtil = exceptionUtil;
        this.pushService = pushService;
        this.cryptoUtil = cryptoUtil;
        this.ffmpegService = ffmpegService;
    }

    public void uploadThumbnail(MultipartFile file) throws FileUploadException {
        uploadFile(file, "thumbnail.png");
        pushService.push(PushMessage.thumbnailUploadComplete());
        LOGGER.info("Successfully uploaded thumbnail to ccms.");
    }

    public void uploadVideo(MultipartFile file) throws FileUploadException {
        uploadFile(file, "video.mp4");
        pushService.push(PushMessage.videoUploadComplete());
        LOGGER.info("Successfully uploaded video to ccms.");
    }

    public void uploadToWebBackend(){
        int files = 0;
        List<String> filesList = getFilesToUploadList();
        try {
            for(String path : filesList){
                uploadFileToWebBackend(path);
                int percentage = (int) Math.round(100.0 / filesList.size() * ++files);
                pushService.push(PushMessage.webBackendUploadProgress(percentage));
            }
            pushService.push(PushMessage.finishedWithWebUpload());
            LOGGER.info("Successfully uploaded video files to web-backend.");
            cleanUpFilesDirectory();
        } catch(IOException | RestClientException | WebBackendException ex){
            LOGGER.error("Error during file upload to web-backend.", ex);
            if(files > 0){
                rollbackWebBackendUpload(filesList.subList(0, files-1));
            }
            String errorMessage = "Error during file upload to web-backend. See logs for details";
            pushService.push(PushMessage.error(errorMessage));
            throw new FileUploadException(errorMessage);
        }
    }

    @SuppressWarnings("unchecked") //we know, the assignment will work
    public void addVideo(HttpServletRequest request, Model model){
        model.addAttribute("post", true);
        try {
            RequestBodyAddVideo addVideoRequest = buildAddVideoRequest(request);
            ResponseEntity<ResponseBody> response = connectionService.doPostRequest(addVideoRequest, "/ccms/edit/video/add", MediaType.APPLICATION_JSON);
            if(!response.getStatusCode().is2xxSuccessful()){
                throw new WebBackendException(response.getBody());
            }
            model.addAttribute("success", true);
            model.addAttribute("addVideoRequest", addVideoRequest);
            LOGGER.info("Successfully added video. Encrypted title: {}.", addVideoRequest.getTitle());
        } catch(WebBackendException | GeneralSecurityException | UnsupportedEncodingException ex){
            LOGGER.error("Error. Could not save video", ex);
            exceptionUtil.handleException(ex, model, LOGGER);
        }
    }

    private RequestBodyAddVideo buildAddVideoRequest(HttpServletRequest request) throws GeneralSecurityException, UnsupportedEncodingException {
        RequestBodyAddVideo addVideoRequest = new RequestBodyAddVideo();
        videoDataService.addEncryptedVideoMeta(addVideoRequest, request);
        addVideoRequest.setDurationInSeconds(state.seconds);
        addVideoRequest.setPersons(videoDataService.getPersonsList(request.getParameter("persons")));
        addVideoRequest.setYears(videoDataService.getYearsList(request));
        addVideoRequest.setTimestamp(videoDataService.getTimestamp(request));
        addVideoRequest.setShowDateValues(videoDataService.getShowDateValues(request));
        addVideoRequest.setM3u8Filename(state.randomName+".m3u8");
        addVideoRequest.setM3u8Key(state.m3u8Key.getKey());
        addVideoRequest.setM3u8Iv(state.m3u8Key.getIv());
        addVideoRequest.setThumbnailFilename(state.randomName+".png");
        addVideoRequest.setThumbnailKey(state.thumbnailKey.getKey());
        addVideoRequest.setThumbnailIv(state.thumbnailKey.getIv());
        addVideoRequest.setRecordedInCologne("cologne".equals(request.getParameter("recordedInCologne")));
        addVideoRequest.setRecordedInGardelegen("gardelegen".equals(request.getParameter("recordedInGardelegen")));
        return addVideoRequest;
    }

    private void cleanUpFilesDirectory(){
        File directory = new File("./files");
        if(!directory.exists()){
            return;
        }
        for (File file : directory.listFiles()) {
            file.delete();
        }
    }

    @SuppressWarnings("unchecked") //we know, the assignment will work
    private void rollbackWebBackendUpload(List<String> filesList){
        for(String path : filesList){
            String filename = new File(path).getName();
            ResponseEntity<String> response = connectionService.doDeleteFileRequest(filename);
            if(!response.getStatusCode().is2xxSuccessful()){
                LOGGER.error("Fatal Error: Could not rollback web-backend upload for file {}. Try to delete manually.", filename);
            }
        }
    }

    private List<String> getFilesToUploadList(){
        String dirPath = "./files";
        List<String> filesList = new ArrayList<>();
        filesList.add(String.format("%s/%s.png", dirPath, state.randomName));
        filesList.add(String.format("%s/%s.m3u8", dirPath, state.randomName));
        for(int i = 0; i < state.tsFiles; i++){
            filesList.add(String.format("%s/%s.%s.ts", dirPath, state.randomName, i));
        }
        return filesList;
    }

    @SuppressWarnings("unchecked") //we know, the assignment will work
    private void uploadFileToWebBackend(String path) throws IOException, WebBackendException, RestClientException {
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        ByteArrayResource resource = buildByteArrayResource(path);
        body.add("file", resource);
        ResponseEntity<ResponseBody> response = connectionService.doUploadRequest(body);
        if(!response.getStatusCode().is2xxSuccessful()){
            throw new WebBackendException(response.getBody());
        }
    }

    private ByteArrayResource buildByteArrayResource(String path) throws IOException {
        return new ByteArrayResource(Files.readAllBytes(Paths.get(path))) {
            @Override
            public String getFilename() {
                return new File(path).getName();
            }
        };
    }

    public void encrypt() throws EncryptionException, GeneralSecurityException {
        try {
            String randomName = UUID.randomUUID().toString();
            state.randomName = randomName;
            encryptThumbnail(randomName);
            encryptVideo(randomName);
        } catch(EncryptionException | GeneralSecurityException ex){
            LOGGER.error("Error during encryption.", ex);
            pushService.push(PushMessage.error(ex.getMessage()));
            throw ex;
        }
    }

    private void encryptThumbnail(String name) throws EncryptionException, GeneralSecurityException {
        byte[] key = cryptoUtil.generateSecureRandomKeyParam();
        byte[] iv = cryptoUtil.generateSecureRandomKeyParam();
        encryptFile(name, "thumbnail.png", "png", key, iv);
        state.thumbnailKey = new Key(cryptoUtil.encryptKey(key), cryptoUtil.toBase64(iv));
        pushService.push(PushMessage.finishedWithThumbnail());
        LOGGER.info("Successfully encrypted thumbnail.");
    }

    private void encryptVideo(String name) throws EncryptionException, GeneralSecurityException {
        FfmpegService.State ffmpegState = ffmpegService.encryptVideo(name);
        state.m3u8Key = encryptM3u8(name);
        state.tsFiles = ffmpegState.tsFiles;
        state.seconds = (int) Math.floor(ffmpegState.seconds);
        pushService.push(PushMessage.finishedWithVideo());
        LOGGER.info("Successfully encrypted video.");
    }

    private Key encryptM3u8(String name) throws GeneralSecurityException {
        byte[] key = cryptoUtil.generateSecureRandomKeyParam();
        byte[] iv = cryptoUtil.generateSecureRandomKeyParam();
        encryptFile(name, "index.m3u8", "m3u8", key, iv);
        return new Key(cryptoUtil.encryptKey(key), cryptoUtil.toBase64(iv));
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

    public State getState(){
        return state;
    }

    public class State {
        public String randomName;
        public Key thumbnailKey;
        public Key m3u8Key;
        public int tsFiles;
        public int seconds;
    }
}
