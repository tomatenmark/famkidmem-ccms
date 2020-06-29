package de.mherrmann.famkidmem.ccms;

import de.mherrmann.famkidmem.ccms.body.*;
import de.mherrmann.famkidmem.ccms.item.*;
import de.mherrmann.famkidmem.ccms.settings.Settings;
import de.mherrmann.famkidmem.ccms.utils.CryptoUtil;
import org.mockito.ArgumentMatchers;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

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

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
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

    public ResponseEntity<ResponseBodyGetUsers> createTestResponseEntityGetUsersError(){
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

    public RequestBodyUpdateVideo createUpdateVideoRequestTwoPersons1999March2Cologne() {
        return createUpdateVideoRequest(
                Arrays.asList("person1", "person2"),
                Collections.singletonList(1999),
                true,
                false,
                getTimestamp(1999, 3, 2),
                7
        );
    }

    public RequestBodyUpdateVideo createUpdateVideoRequestTwoPersons1999December31Gardelegen() {
        return createUpdateVideoRequest(
                Arrays.asList("person1", "person2"),
                Collections.singletonList(1999),
                false,
                true,
                getTimestamp(1999, 12, 31),
                7
        );
    }

    public RequestBodyUpdateVideo createUpdateVideoRequestOnePerson1999December31Silvester() {
        return createUpdateVideoRequest(
                Collections.singletonList("person1"),
                Arrays.asList(1999,2000),
                false,
                false,
                getTimestamp(1999, 12, 31),
                7
        );
    }

    public RequestBodyUpdateVideo createUpdateVideoRequestOnePerson2000January1Silvester() {
        return createUpdateVideoRequest(
                Collections.singletonList("person1"),
                Arrays.asList(1999,2000),
                false,
                false,
                getTimestamp(2000, 1, 1),
                7
        );
    }

    public RequestBodyUpdateVideo createUpdateVideoRequestOnePerson2000January1() {
        return createUpdateVideoRequest(
                Collections.singletonList("person1"),
                Collections.singletonList(2000),
                false,
                false,
                getTimestamp(2000, 1, 1),
                7
        );
    }

    public RequestBodyUpdateVideo createUpdateVideoRequestOnePerson2003May() {
        return createUpdateVideoRequest(
                Collections.singletonList("person1"),
                Collections.singletonList(2003),
                false,
                false,
                getTimestamp(2003, 5, 1),
                6
        );
    }

    public RequestBodyUpdateVideo createUpdateVideoRequestOnePerson2005CologneAndGardelegen() {
        return createUpdateVideoRequest(
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

    private RequestBodyUpdateVideo createUpdateVideoRequest(List<String> persons, List<Integer> years,
                                                      boolean recordedInCologne, boolean recordedInGardelegen, long time, int showDateValues) {

        RequestBodyUpdateVideo updateVideoRequest = new RequestBodyUpdateVideo();
        updateVideoRequest.setDesignator("title");
        updateVideoRequest.setTitle("titleEncrypted");
        updateVideoRequest.setDescription("descriptionEncrypted");
        updateVideoRequest.setKey("keyEncrypted");
        updateVideoRequest.setIv("iv");
        updateVideoRequest.setThumbnailKey("thumbnailKey");
        updateVideoRequest.setThumbnailIv("thumbnailIv");
        updateVideoRequest.setPersons(persons);
        updateVideoRequest.setYears(years);
        updateVideoRequest.setRecordedInCologne(recordedInCologne);
        updateVideoRequest.setRecordedInGardelegen(recordedInGardelegen);
        updateVideoRequest.setTimestamp(time);
        updateVideoRequest.setShowDateValues(showDateValues);
        return updateVideoRequest;
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
        videos.add(createFullDataVideo("video1"));
        videos.add(createFullDataVideo("video2"));
        return videos;
    }

    private Video createFullDataVideo(String title){
        Video video = new Video();
        video.setTitle(title);
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
        Key m3u8Key = new Key("m3u8Key", "m3u8Iv");
        video.setM3u8(new FileEntity("m3u8", m3u8Key));
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

    private ResponseBodyGetUsers createTestResponseBodyGetUsersError(){
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

    public void prepareUpdateVideoTest(CryptoUtil cryptoUtil, RestTemplate restTemplate) throws Exception {
        byte[] keyDummy = new byte[]{0,0,0,0,0,0,0,0};
        byte[] encryptedTitleDummy = new byte[]{1,1,1,1,1,1,1,1};
        byte[] encryptedDescriptionDummy = new byte[]{2,2,2,2,2,2,2,2};
        given(restTemplate.exchange(eq(Application.getSettings().getBackendUrl()+"/ccms/edit/video/update"), eq(HttpMethod.POST), ArgumentMatchers.any(), eq(ResponseBody.class)))
                .willReturn(createTestResponseEntityStatusOk());
        given(cryptoUtil.generateSecureRandomKeyParam())
                .willReturn(keyDummy);
        given(cryptoUtil.encrypt(ArgumentMatchers.eq("title".getBytes("UTF-8")), ArgumentMatchers.any(), ArgumentMatchers.any()))
                .willReturn(encryptedTitleDummy);
        given(cryptoUtil.encrypt(ArgumentMatchers.eq("description".getBytes("UTF-8")), ArgumentMatchers.any(), ArgumentMatchers.any()))
                .willReturn(encryptedDescriptionDummy);
        given(cryptoUtil.toBase64(ArgumentMatchers.eq(encryptedTitleDummy)))
                .willReturn("titleEncrypted");
        given(cryptoUtil.toBase64(ArgumentMatchers.eq(encryptedDescriptionDummy)))
                .willReturn("descriptionEncrypted");
        given(cryptoUtil.toBase64(ArgumentMatchers.eq(keyDummy)))
                .willReturn("iv");
        given(cryptoUtil.encryptKey(ArgumentMatchers.eq(keyDummy)))
                .willReturn("keyEncrypted");
    }
}
