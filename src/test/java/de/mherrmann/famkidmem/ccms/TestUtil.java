package de.mherrmann.famkidmem.ccms;

import de.mherrmann.famkidmem.ccms.body.*;
import de.mherrmann.famkidmem.ccms.item.User;
import de.mherrmann.famkidmem.ccms.settings.Settings;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TestUtil {

    public ResponseEntity<ResponseBody> createTestResponseEntityStatusOk(){
        return ResponseEntity.ok(createTestResponseBodyOk());
    }

    public ResponseEntity<ResponseBody> createTestResponseEntityStatusBadRequest(){
        return ResponseEntity.badRequest().body(createTestResponseBodyError());
    }

    public ResponseEntity<ResponseBody> createTestResponseEntityGetUsers(List<User> users){
        return ResponseEntity.ok(createTestResponseBodyGetUsers(users));
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
        settings.setMasterKey("");
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

    public List<User> createUsersList(boolean empty){
        List<User> users = new ArrayList<>();
        if(!empty){
            User user1 = new User();
            User user2 = new User();
            user1.setUsername("user1");
            user2.setUsername("user2");
            users.add(user1);
            users.add(user2);
        }
        return users;
    }

    private ResponseBody createTestResponseBodyOk(){
        return new ResponseBody("ok", "testDetails");
    }

    private ResponseBody createTestResponseBodyError(){
        return new ResponseBody("error", "testErrorDetails", new RuntimeException());
    }

    private ResponseBody createTestResponseBodyGetUsers(List<User> users){
        return new ResponseBodyGetUsers(users);
    }

    private ResponseBody createTestResponseBodyGetUsersError(){
        return new ResponseBodyGetUsers(new RuntimeException("testException"));
    }
}
