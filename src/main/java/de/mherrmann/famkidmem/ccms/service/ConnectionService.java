package de.mherrmann.famkidmem.ccms.service;

import de.mherrmann.famkidmem.ccms.Application;
import de.mherrmann.famkidmem.ccms.body.ResponseBody;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ConnectionService {

    private final RestTemplate restTemplate;

    @Autowired
    public ConnectionService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    private ResponseEntity doRequest(HttpMethod method, Object body, String path, MediaType mediaType){
        HttpEntity httpEntity = getHttpEntity(body, mediaType);
        String url = Application.getSettings().getBackendUrl() + path;
        return restTemplate.exchange(url, method, httpEntity, ResponseBody.class);
    }

    ResponseEntity doGetRequest(String path){
        return doRequest(HttpMethod.GET, null, path, null);
    }

    ResponseEntity doPostRequest(Object body, String path, MediaType mediaType){
        return doRequest(HttpMethod.POST, body, path, mediaType);
    }

    ResponseEntity doDeleteRequest(Object body, String path, MediaType mediaType){
        return doRequest(HttpMethod.DELETE, body, path, mediaType);
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