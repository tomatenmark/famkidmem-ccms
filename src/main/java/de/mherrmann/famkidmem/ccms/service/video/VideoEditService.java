package de.mherrmann.famkidmem.ccms.service.video;

import de.mherrmann.famkidmem.ccms.Application;
import de.mherrmann.famkidmem.ccms.body.RequestBodyUpdateVideo;
import de.mherrmann.famkidmem.ccms.body.ResponseBody;
import de.mherrmann.famkidmem.ccms.body.ResponseBodyGetVideos;
import de.mherrmann.famkidmem.ccms.exception.EncryptionException;
import de.mherrmann.famkidmem.ccms.exception.WebBackendException;
import de.mherrmann.famkidmem.ccms.item.Key;
import de.mherrmann.famkidmem.ccms.item.Person;
import de.mherrmann.famkidmem.ccms.item.Video;
import de.mherrmann.famkidmem.ccms.item.Year;
import de.mherrmann.famkidmem.ccms.service.ConnectionService;
import de.mherrmann.famkidmem.ccms.utils.CryptoUtil;
import de.mherrmann.famkidmem.ccms.utils.ExceptionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

@Service
public class VideoEditService {

    private final VideoDataService videoDataService;
    private final ConnectionService connectionService;
    private final ExceptionUtil exceptionUtil;
    private final CryptoUtil cryptoUtil;

    private static final Logger LOGGER = LoggerFactory.getLogger(VideoEditService.class);

    public VideoEditService(VideoDataService videoDataService, ConnectionService connectionService, ExceptionUtil exceptionUtil, CryptoUtil cryptoUtil){
        this.videoDataService = videoDataService;
        this.connectionService = connectionService;
        this.exceptionUtil = exceptionUtil;
        this.cryptoUtil = cryptoUtil;
    }

    public void fillEditDataModel(Model model, String title){
        model.addAttribute("post", false);
        model.addAttribute("titleUrlBase64", title);
        try {
            Video video = getVideo(title);
            model.addAttribute("video", video);
            model.addAttribute("persons", getPersonsStringifiedList(video.getPersons()));
            model.addAttribute("years", getYearsStringifiedList(video.getYears()));
            model.addAttribute("year", getYearFromTimestamp(video.getTimestamp()));
            model.addAttribute("month", getMonthFromTimestamp(video.getTimestamp(), video.getShowDateValues()));
            model.addAttribute("day", getDayFromTimestamp(video.getTimestamp(), video.getShowDateValues()));
            model.addAttribute("masterKey", Application.getSettings().getMasterKey());
            model.addAttribute("success", true);
        } catch (Exception ex){
            exceptionUtil.handleException(ex, model, LOGGER);
        }
    }

    public void editData(Model model, HttpServletRequest request, String designator){
        designator = designator.replace('-', '+').replace('_', '/');
        model.addAttribute("post", true);
        try {
            RequestBodyUpdateVideo updateVideoRequest = buildUpdateVideoRequest(request);
            updateVideoRequest.setDesignator(designator);
            updateVideo(updateVideoRequest);
            model.addAttribute("success", true);
            model.addAttribute("updateVideoRequest", updateVideoRequest);
            LOGGER.info("Successfully updated video. Encrypted title: {}.", updateVideoRequest.getTitle());
        } catch(WebBackendException | GeneralSecurityException | UnsupportedEncodingException ex){
            LOGGER.error("Error. Could not update video", ex);
            exceptionUtil.handleException(ex, model, LOGGER);
        }
    }

    public void fillReplaceThumbnailModel(Model model, String designator){
        model.addAttribute("post", false);
        model.addAttribute("title", designator);
    }

    @SuppressWarnings("unchecked") //we know, the assignment will work
    public void replaceThumbnail(MultipartFile file, Model model, String designator){
        model.addAttribute("post", true);
        model.addAttribute("title", designator);
        try {
            Video video = getVideo(designator);
            Key key = updateThumbnail(file, video.getThumbnail().getFilename());
            RequestBodyUpdateVideo updateVideoRequest = buildUpdateVideoRequest(video, key);
            updateVideo(updateVideoRequest);
            model.addAttribute("success", true);
            model.addAttribute("updateVideoRequest", updateVideoRequest);
            LOGGER.info("Successfully updated video (new thumbnail). Encrypted title: {}.", updateVideoRequest.getTitle());
        } catch(Exception ex){
            LOGGER.error("Error. Could not replace thumbnail", ex);
            exceptionUtil.handleException(ex, model, LOGGER);
        }
    }

    @SuppressWarnings("unchecked") //we know, the assignment will work
    private void updateVideo(RequestBodyUpdateVideo updateVideoRequest) throws WebBackendException {
        ResponseEntity<ResponseBody> response = connectionService.doPostRequest(updateVideoRequest, "/ccms/edit/video/update", MediaType.APPLICATION_JSON);
        if(!response.getStatusCode().is2xxSuccessful()){
            throw new WebBackendException(response.getBody());
        }
    }

    @SuppressWarnings("unchecked") //we know, the assignment will work
    private Video getVideo(String designator) throws Exception {
        ResponseEntity<ResponseBodyGetVideos> response = connectionService.doGetSingleVideoRequest(designator);
        if(!response.getStatusCode().is2xxSuccessful()){
            throw new WebBackendException(response.getBody());
        }
        return response.getBody().getVideos().get(0);
    }

    private Integer getYearFromTimestamp(Timestamp timestamp){
        long time = timestamp.getTime();
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(time);
        return cal.get(Calendar.YEAR);
    }

    private Integer getMonthFromTimestamp(Timestamp timestamp, int showDateValues){
        if(showDateValues < 6){
            return 0;
        }
        long time = timestamp.getTime();
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(time);
        return cal.get(Calendar.MONTH)+1; //month is 0 based
    }

    private Integer getDayFromTimestamp(Timestamp timestamp, int showDateValues){
        if(showDateValues < 7){
            return 0;
        }
        long time = timestamp.getTime();
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(time);
        return cal.get(Calendar.DAY_OF_MONTH);
    }

    private String getPersonsStringifiedList(List<Person> persons){
        StringBuilder sB = new StringBuilder();
        for(Person person : persons){
            sB.append(",").append(person.getName());
        }
        return sB.toString().substring(1);
    }

    private String getYearsStringifiedList(List<Year> years){
        StringBuilder sB = new StringBuilder();
        for(Year year : years){
            sB.append(",").append(year.getValue());
        }
        return sB.toString().substring(1);
    }

    private RequestBodyUpdateVideo buildUpdateVideoRequest(HttpServletRequest request) throws GeneralSecurityException, UnsupportedEncodingException {
        RequestBodyUpdateVideo updateVideoRequest = new RequestBodyUpdateVideo();
        videoDataService.addEncryptedVideoMeta(updateVideoRequest, request);
        updateVideoRequest.setPersons(videoDataService.getPersonsList(request.getParameter("persons")));
        updateVideoRequest.setYears(videoDataService.getYearsList(request));
        updateVideoRequest.setTimestamp(videoDataService.getTimestamp(request));
        updateVideoRequest.setShowDateValues(videoDataService.getShowDateValues(request));
        updateVideoRequest.setRecordedInCologne("cologne".equals(request.getParameter("recordedInCologne")));
        updateVideoRequest.setRecordedInGardelegen("gardelegen".equals(request.getParameter("recordedInGardelegen")));
        updateVideoRequest.setThumbnailKey(request.getParameter("thumbnailKey"));
        updateVideoRequest.setThumbnailIv(request.getParameter("thumbnailIv"));
        return updateVideoRequest;
    }

    private RequestBodyUpdateVideo buildUpdateVideoRequest(Video current, Key key) {
        RequestBodyUpdateVideo updateVideoRequest = new RequestBodyUpdateVideo();
        updateVideoRequest.setDesignator(current.getTitle());
        updateVideoRequest.setTitle(current.getTitle());
        updateVideoRequest.setDescription(current.getDescription());
        updateVideoRequest.setKey(current.getKey().getKey());
        updateVideoRequest.setIv(current.getKey().getIv());
        updateVideoRequest.setPersons(getPersonStringList(current.getPersons()));
        updateVideoRequest.setYears(getYearsIntegerList(current.getYears()));
        updateVideoRequest.setTimestamp(current.getTimestamp().getTime());
        updateVideoRequest.setShowDateValues(current.getShowDateValues());
        updateVideoRequest.setRecordedInCologne(current.isRecordedInCologne());
        updateVideoRequest.setRecordedInCologne(current.isRecordedInGardelegen());
        updateVideoRequest.setThumbnailKey(key.getKey());
        updateVideoRequest.setThumbnailIv(key.getIv());
        return updateVideoRequest;
    }

    private List<String> getPersonStringList(List<Person> persons){
        List<String> personsStringList = new ArrayList<>();
        for(Person person : persons){
            personsStringList.add(person.getName());
        }
        return personsStringList;
    }

    private List<Integer> getYearsIntegerList(List<Year> years){
        List<Integer> yearsIntegerList = new ArrayList<>();
        for(Year year : years){
            yearsIntegerList.add(year.getValue());
        }
        return yearsIntegerList;
    }

    private Key updateThumbnail(MultipartFile file, String filename) throws WebBackendException, EncryptionException {
        try {
            byte[] plain = file.getBytes();
            byte[] key = cryptoUtil.generateSecureRandomKeyParam();
            byte[] iv = cryptoUtil.generateSecureRandomKeyParam();
            Key keySpec = new Key(cryptoUtil.encryptKey(key), cryptoUtil.toBase64(iv));
            byte[] encrypted = cryptoUtil.encrypt(plain, key, iv);
            uploadThumbnailToWebBackend(encrypted, filename);
            return keySpec;
        } catch(GeneralSecurityException | IOException ex){
            throw new EncryptionException("Exception during encryption: "+ex);
        }
    }

    @SuppressWarnings("unchecked") //we know, the assignment will work
    private void uploadThumbnailToWebBackend(byte[] bytes, String filename) throws IOException, WebBackendException, RestClientException {
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        ByteArrayResource resource = buildByteArrayResource(bytes, filename);
        body.add("file", resource);
        ResponseEntity<ResponseBody> response = connectionService.doUploadRequest(body);
        if(!response.getStatusCode().is2xxSuccessful()){
            throw new WebBackendException(response.getBody());
        }
    }

    private ByteArrayResource buildByteArrayResource(byte[] bytes, String filename) throws IOException {
        return new ByteArrayResource(bytes) {
            @Override
            public String getFilename() {
                return filename;
            }
        };
    }
}
