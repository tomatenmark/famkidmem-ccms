package de.mherrmann.famkidmem.ccms.service;

import de.mherrmann.famkidmem.ccms.Application;
import de.mherrmann.famkidmem.ccms.body.*;
import de.mherrmann.famkidmem.ccms.exception.WebBackendException;
import de.mherrmann.famkidmem.ccms.item.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import javax.servlet.http.HttpServletRequest;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Collections;
import java.util.List;

@Service
public class UserService {

    private final ConnectionService connectionService;

    private static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);

    @Autowired
    public UserService(ConnectionService connectionService) {
        this.connectionService = connectionService;
    }

    @SuppressWarnings("unchecked") //we know, the assignment will work
    public void addUser(HttpServletRequest request, Model model){
        fillAddUserModel(model, true);
        try {
            RequestBodyAddUser addUserRequest = createAddUserRequest(request);
            ResponseEntity<ResponseBody> response = connectionService.doPostRequest(addUserRequest, "/ccms/admin/user/add", MediaType.APPLICATION_JSON);
            if(!response.getStatusCode().is2xxSuccessful()){
                throw new WebBackendException(response.getBody());
            }
            model.addAttribute("success", true);
            model.addAttribute("initLink", request.getParameter("link"));
            LOGGER.info("Successfully added user. Display name: {}.", addUserRequest.getDisplayName());
        } catch(Exception ex){
            handleException(ex, model);
        }
    }

    @SuppressWarnings("unchecked") //we know, the assignment will work
    public void resetPassword(HttpServletRequest request, Model model, String username){
        fillResetPasswordModel(model, username, true);
        try {
            RequestBodyResetPassword resetPasswordRequest = createResetPasswordRequest(request, username);
            ResponseEntity<ResponseBody> response = connectionService.doPostRequest(resetPasswordRequest, "/ccms/admin/user/reset", MediaType.APPLICATION_JSON);
            if(!response.getStatusCode().is2xxSuccessful()){
                throw new WebBackendException(response.getBody());
            }
            model.addAttribute("success", true);
            model.addAttribute("resetLink", request.getParameter("link"));
            LOGGER.info("Successfully reset password for user {}.", username);
        } catch(Exception ex){
            handleException(ex, model);
        }
    }

    @SuppressWarnings("unchecked") //we know, the assignment will work
    public void deleteUser(Model model, String username){
        fillResetPasswordModel(model, username, true);
        try {
            RequestBodyDeleteUser deleteUserRequest = createDeleteUserRequest(username);
            ResponseEntity<ResponseBody> response = connectionService.doDeleteRequest(deleteUserRequest, "/ccms/admin/user/delete", MediaType.APPLICATION_JSON);
            if(!response.getStatusCode().is2xxSuccessful()){
                throw new WebBackendException(response.getBody());
            }
            model.addAttribute("success", true);
            LOGGER.info("Successfully removed user {}.", username);
        } catch(Exception ex){
            handleException(ex, model);
        }
    }

    public void fillAddUserModel(Model model, boolean post){
        fillUserModel(model, generateSecureRandomToken(), post);
    }

    public void fillResetPasswordModel(Model model, String username, boolean post){
        fillUserModel(model, username, post);
    }

    public void fillIndexModel(Model model){
        try {
            model.addAttribute("users", getUsers());
            model.addAttribute("success", true);
        } catch(Exception ex){
            handleException(ex, model);
            model.addAttribute("users", Collections.emptyList());
        }
    }

    private void handleException(Exception ex, Model model){
        if(ex instanceof WebBackendException){
            fillWebBackendExceptionValues((WebBackendException) ex, model);
            LOGGER.error("Error", ex);
        } else {
            fillExceptionValues(ex, model);
        }
    }

    private void fillUserModel(Model model, String username, boolean post){
        model.addAttribute("frontendUrl", Application.getSettings().getFrontendUrl());
        model.addAttribute("masterKey", Application.getSettings().getMasterKey());
        model.addAttribute("username", username);
        model.addAttribute("password", generateSecureRandomToken());
        model.addAttribute("post", post);
    }

    private void fillExceptionValues(Exception ex, Model model){
        model.addAttribute("success", false);
        model.addAttribute("exception", ex.toString());
        model.addAttribute("details", "connection failure");
    }

    private void fillWebBackendExceptionValues(WebBackendException ex, Model model){
        model.addAttribute("success", false);
        model.addAttribute("details", ex.getDetails());
        model.addAttribute("exception", ex.getException());
    }

    @SuppressWarnings("unchecked") //we know, the assignment will work
    private List<User> getUsers() throws Exception {
        ResponseEntity<ResponseBodyGetUsers> response = connectionService.doGetRequest( "/ccms/admin/user/get");
        if(response.getStatusCode().is2xxSuccessful()){
            return response.getBody().getUsers();
        }
        throw new WebBackendException(response.getBody());
    }

    //https://stackoverflow.com/questions/46261055/how-to-generate-a-securerandom-string-of-length-n-in-java
    private String generateSecureRandomToken() {
        SecureRandom random = new SecureRandom();
        byte bytes[] = new byte[20];
        random.nextBytes(bytes);
        Base64.Encoder encoder = Base64.getUrlEncoder().withoutPadding();
        return encoder.encodeToString(bytes);
    }

    private RequestBodyAddUser createAddUserRequest(HttpServletRequest request){
        RequestBodyAddUser addUserRequest = new RequestBodyAddUser();
        addUserRequest.setDisplayName(request.getParameter("displayName"));
        addUserRequest.setUsername(request.getParameter("username"));
        addUserRequest.setLoginHash(request.getParameter("loginHash"));
        addUserRequest.setPasswordKeySalt(request.getParameter("passwordKeySalt"));
        addUserRequest.setMasterKey(request.getParameter("userKey"));
        return addUserRequest;
    }

    private RequestBodyResetPassword createResetPasswordRequest(HttpServletRequest request, String username){
        RequestBodyResetPassword resetPasswordRequest = new RequestBodyResetPassword();
        resetPasswordRequest.setUsername(username);
        resetPasswordRequest.setLoginHash(request.getParameter("loginHash"));
        resetPasswordRequest.setPasswordKeySalt(request.getParameter("passwordKeySalt"));
        resetPasswordRequest.setMasterKey(request.getParameter("userKey"));
        return resetPasswordRequest;
    }

    private RequestBodyDeleteUser createDeleteUserRequest(String username){
        RequestBodyDeleteUser deleteUserRequest = new RequestBodyDeleteUser();
        deleteUserRequest.setUsername(username);
        return deleteUserRequest;
    }

}
