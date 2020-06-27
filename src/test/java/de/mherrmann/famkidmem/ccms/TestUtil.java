package de.mherrmann.famkidmem.ccms;

import de.mherrmann.famkidmem.ccms.body.*;
import de.mherrmann.famkidmem.ccms.item.*;
import de.mherrmann.famkidmem.ccms.settings.Settings;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.yaml.snakeyaml.tokens.Token.ID.Key;

@Service
public class TestUtil {

    public ResponseEntity<ResponseBody> createTestResponseEntityStatusOk(){
        return ResponseEntity.ok(createTestResponseBodyOk());
    }

    public ResponseEntity<ResponseBody> createTestResponseEntityStatusBadRequest(){
        return ResponseEntity.badRequest().body(createTestResponseBodyError());
    }

    public ResponseEntity<ResponseBodyGetUsers> createTestResponseEntityGetUsers(List<User> users){
        return ResponseEntity.ok(createTestResponseBodyGetUsers(users));
    }

    public ResponseEntity<ResponseBodyGetVideos> createTestResponseEntityGetVideos(List<Video> videos){
        return ResponseEntity.ok(createTestResponseBodyGetVideos(videos));
    }

    public ResponseEntity<ResponseBodyContentFileBase64> createTestResponseEntityContentFileBase64(String base64){
        return ResponseEntity.ok(createTestResponseBodyContentFileBase64(base64));
    }

    public ResponseEntity<ResponseBody> createTestResponseEntityGetUsersError(){
        return ResponseEntity.badRequest().body(createTestResponseBodyGetUsersError());
    }

    public HttpEntity createTestHttpEntityNoBody(){
        HttpHeaders headers = new HttpHeaders();
        headers.add("CCMS_AUTH_TOKEN", Application.getSettings().getApiKey());
        return new HttpEntity<>(null, headers);
    }

    public HttpEntity createTestHttpEntityTestBody(){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("CCMS_AUTH_TOKEN", Application.getSettings().getApiKey());
        return new HttpEntity<>("", headers);
    }

    public HttpEntity createTestHttpEntityGivenBody(Object body){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("CCMS_AUTH_TOKEN", Application.getSettings().getApiKey());
        return new HttpEntity<>(body, headers);
    }

    public void createTestSettings(){
        Settings settings = new Settings();
        settings.setBackendUrl("");
        settings.setFrontendUrl("");
        settings.setApiKey("");
        settings.setMasterKey("QUJDREVGR0hJSktMTU5PUA==");
        Application.setSettings(settings);
    }

    public RequestBodyAddUser createAddUserRequest(){
        RequestBodyAddUser addUserRequest = new RequestBodyAddUser();
        addUserRequest.setDisplayName("test");
        addUserRequest.setUsername("testUserName");
        addUserRequest.setLoginHash("hash");
        addUserRequest.setPasswordKeySalt("salt");
        addUserRequest.setMasterKey("key");
        return addUserRequest;
    }

    public RequestBodyResetPassword createResetPasswordRequest(){
        RequestBodyResetPassword resetPasswordRequest = new RequestBodyResetPassword();
        resetPasswordRequest.setUsername("testUserName");
        resetPasswordRequest.setLoginHash("hash");
        resetPasswordRequest.setPasswordKeySalt("salt");
        resetPasswordRequest.setMasterKey("key");
        return resetPasswordRequest;
    }

    public RequestBodyDeleteUser createRemoveUserRequest(){
        RequestBodyDeleteUser removeUserRequest = new RequestBodyDeleteUser();
        removeUserRequest.setUsername("testUserName");
        return removeUserRequest;
    }

    public RequestBodyAddVideo createAddVideoRequestTwoPersons1999March2Cologne() {
        return createAddVideoRequest(
                Arrays.asList("person1", "person2"),
                Collections.singletonList(1999),
                true,
                false,
                getTimestamp(1999, 3, 2),
                7
                );
    }

    public RequestBodyAddVideo createAddVideoRequestTwoPersons1999December31Gardelegen() {
        return createAddVideoRequest(
                Arrays.asList("person1", "person2"),
                Collections.singletonList(1999),
                false,
                true,
                getTimestamp(1999, 12, 31),
                7
        );
    }

    public RequestBodyAddVideo createAddVideoRequestOnePerson1999December31Silvester() {
        return createAddVideoRequest(
                Collections.singletonList("person1"),
                Arrays.asList(1999,2000),
                false,
                false,
                getTimestamp(1999, 12, 31),
                7
        );
    }

    public RequestBodyAddVideo createAddVideoRequestOnePerson2000January1Silvester() {
        return createAddVideoRequest(
                Collections.singletonList("person1"),
                Arrays.asList(1999,2000),
                false,
                false,
                getTimestamp(2000, 1, 1),
                7
        );
    }

    public RequestBodyAddVideo createAddVideoRequestOnePerson2000January1() {
        return createAddVideoRequest(
                Collections.singletonList("person1"),
                Collections.singletonList(2000),
                false,
                false,
                getTimestamp(2000, 1, 1),
                7
        );
    }

    public RequestBodyAddVideo createAddVideoRequestOnePerson2003May() {
        return createAddVideoRequest(
                Collections.singletonList("person1"),
                Collections.singletonList(2003),
                false,
                false,
                getTimestamp(2003, 5, 1),
                6
        );
    }

    public RequestBodyAddVideo createAddVideoRequestOnePerson2005CologneAndGardelegen() {
        return createAddVideoRequest(
                Collections.singletonList("person1"),
                Collections.singletonList(2005),
                true,
                true,
                getTimestamp(2005, 1, 1),
                4
        );
    }

    private RequestBodyAddVideo createAddVideoRequest(List<String> persons, List<Integer> years,
                            boolean recordedInCologne, boolean recordedInGardelegen, long time, int showDateValues) {

        RequestBodyAddVideo addVideoRequest = new RequestBodyAddVideo();
        addVideoRequest.setTitle("titleEncrypted");
        addVideoRequest.setDescription("descriptionEncrypted");
        addVideoRequest.setKey("keyEncrypted");
        addVideoRequest.setIv("iv");
        addVideoRequest.setM3u8Filename("m3u8");
        addVideoRequest.setM3u8Key("m3u8Key");
        addVideoRequest.setM3u8Iv("m3u8Iv");
        addVideoRequest.setThumbnailFilename("thumbnail");
        addVideoRequest.setThumbnailKey("thumbnailKey");
        addVideoRequest.setThumbnailIv("thumbnailIv");
        addVideoRequest.setPersons(persons);
        addVideoRequest.setYears(years);
        addVideoRequest.setRecordedInCologne(recordedInCologne);
        addVideoRequest.setRecordedInGardelegen(recordedInGardelegen);
        addVideoRequest.setTimestamp(time);
        addVideoRequest.setShowDateValues(showDateValues);
        return addVideoRequest;
    }

    private long getTimestamp(int year, int month, int day){
        LocalDateTime date = LocalDateTime.of(year, month, day, 0, 0);
        ZonedDateTime zonedDateTime = date.atZone(ZoneId.of("Europe/Paris"));
        return zonedDateTime.toInstant().toEpochMilli();
    }

    public List<User> createUsersList(){
        List<User> users = new ArrayList<>();
            User user1 = new User();
            User user2 = new User();
            user1.setUsername("user1");
            user2.setUsername("user2");
            users.add(user1);
            users.add(user2);
        return users;
    }

    public List<Video> createVideosList(){
        List<Video> videos = new ArrayList<>();
        Video video1 = createFullDataVideo();
        Video video2 = new Video();
        video2.setTitle("video2");
        videos.add(video1);
        videos.add(video2);
        return videos;
    }

    private Video createFullDataVideo(){
        Video video = new Video();
        video.setTitle("video1");
        video.setDescription("Description");
        Person person1 = new Person();
        Person person2 = new Person();
        person1.setName("person1");
        person2.setName("person2");
        video.setPersons(Arrays.asList(person1, person2));
        Year year1 = new Year();
        Year year2 = new Year();
        year1.setValue(1994);
        year2.setValue(1995);
        video.setYears(Arrays.asList(year1, year2));
        video.setTimestamp(new Timestamp(788896800000L));
        video.setShowDateValues(7);
        Key key = new Key("key", "iv");
        Key thumbnailKey = new Key("thumbnailKey", "thumbnailIv");
        video.setThumbnail(new FileEntity("thumbnail", thumbnailKey));
        video.setKey(key);
        return video;
    }

    private ResponseBody createTestResponseBodyOk(){
        return new ResponseBody("ok", "testDetails");
    }

    private ResponseBody createTestResponseBodyError(){
        return new ResponseBody("error", "testErrorDetails", new RuntimeException());
    }

    private ResponseBodyGetUsers createTestResponseBodyGetUsers(List<User> users){
        return new ResponseBodyGetUsers(users);
    }

    private ResponseBodyGetVideos createTestResponseBodyGetVideos(List<Video> videos){
        return new ResponseBodyGetVideos(videos);
    }

    private ResponseBodyContentFileBase64 createTestResponseBodyContentFileBase64(String base64){
        return new ResponseBodyContentFileBase64(null, base64);
    }

    private ResponseBody createTestResponseBodyGetUsersError(){
        return new ResponseBodyGetUsers(new RuntimeException("testException"));
    }

    public void createFilesDirectory(){
        File dir = new File("./files");
        if(!dir.exists()){
            dir.mkdir();
        }
    }

    public void deleteFilesDirectory() {
        File directory = new File("./files");
        if(!directory.exists()){
            return;
        }
        for (File file : directory.listFiles()) {
            file.delete();
        }
        directory.delete();
    }
}
