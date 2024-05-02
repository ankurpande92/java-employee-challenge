package com.example.rqchallenge.util;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.util.Collections;
import java.util.Map;

public class HttpClientHelper {

    /**
     * @return HttpEntity with only headers
     */
    public static  HttpEntity getHttpEntity() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        return new HttpEntity(headers);
    }

    /**
     * @param employeeInput
     * @return HttpEntity with header and input payload body
     */
    public static HttpEntity getHttpEntity(Map<String, Object> employeeInput) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        return new HttpEntity(employeeInput, headers);
    }
}
