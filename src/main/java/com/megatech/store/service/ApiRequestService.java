package com.megatech.store.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ApiRequestService {

    private final RestTemplate restTemplate;

    public ApiRequestService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public <T> T getApiResponse(String url, Class<T> responseType) {
        return restTemplate.getForObject(url, responseType);
    }
}
