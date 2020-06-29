package de.mherrmann.famkidmem.ccms.service;

import de.mherrmann.famkidmem.ccms.Application;
import de.mherrmann.famkidmem.ccms.body.ResponseBody;
import de.mherrmann.famkidmem.ccms.body.ResponseBodyContentFileBase64;
import de.mherrmann.famkidmem.ccms.body.ResponseBodyGetUsers;
import de.mherrmann.famkidmem.ccms.body.ResponseBodyGetVideos;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Service
public class ConnectionService {

    private final RestTemplate restTemplate;

    @Autowired
    public ConnectionService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public ResponseEntity doGetUsersRequest() throws RestClientException {
        return doRequest(HttpMethod.GET, null, "/ccms/admin/user/get", null, ResponseBodyGetUsers.class);
    }

    public ResponseEntity doGetVideosRequest() throws RestClientException {
        return doRequest(HttpMethod.GET, null, "/ccms/edit/video/get", null, ResponseBodyGetVideos.class);
    }

    public ResponseEntity doGetSingleVideoRequest(String title) throws RestClientException {
        return doRequest(HttpMethod.GET, null, "/ccms/edit/video/get/"+title, null, ResponseBodyGetVideos.class);
    }

    public ResponseEntity doGetBase64(String filename) throws RestClientException {
        return doRequest(HttpMethod.GET, null, "/ccms/edit/video/base64/"+filename, null, ResponseBodyContentFileBase64.class);
    }

    public ResponseEntity doGetTs(String filename) throws RestClientException {
        return doRequest(HttpMethod.GET, null, "/ccms/edit/video/ts/"+filename, null, ByteArrayResource.class);
    }

    public ResponseEntity doPostRequest(Object body, String path, MediaType mediaType) throws RestClientException {
        return doRequest(HttpMethod.POST, body, path, mediaType, ResponseBody.class);
    }

    public ResponseEntity doUploadRequest(Object body) throws RestClientException {
        return doRequest(HttpMethod.POST, body, "/ccms/upload", MediaType.MULTIPART_FORM_DATA, String.class);
    }

    ResponseEntity doDeleteUserRequest(Object body) throws RestClientException {
        return doRequest(HttpMethod.DELETE, body, "/ccms/admin/user/delete",  MediaType.APPLICATION_JSON, ResponseBody.class);
    }

    public ResponseEntity doDeleteVideoRequest(String designator) throws RestClientException {
        return doRequest(HttpMethod.DELETE, null, "/ccms/edit/video/delete/"+designator, null, ResponseBody.class);
    }

    public ResponseEntity doDeleteFileRequest(String filename) throws RestClientException {
        return doRequest(HttpMethod.DELETE, null, "/ccms/delete/"+filename, null, String.class);
    }

    private ResponseEntity doRequest(HttpMethod method, Object body, String path, MediaType mediaType, Class responseClass) throws RestClientException {
        HttpEntity httpEntity = getHttpEntity(body, mediaType);
        String url = Application.getSettings().getBackendUrl() + path;
        return restTemplate.exchange(url, method, httpEntity, responseClass);
    }

    private HttpEntity getHttpEntity(Object body, MediaType mediaType){
        return new HttpEntity<>(body, getHeaders(mediaType));
    }

    private HttpHeaders getHeaders(MediaType mediaType){
        HttpHeaders headers = new HttpHeaders();
        if(mediaType != null){
            headers.setContentType(mediaType);
        }
        headers.add("CCMS_AUTH_TOKEN", Application.getSettings().getApiKey());
        return headers;
    }

}
