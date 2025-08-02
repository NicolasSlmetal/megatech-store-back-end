package com.megatech.store.configuration;

import com.megatech.store.exceptions.BaseException;
import com.megatech.store.exceptions.ErrorType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.io.InputStream;

@Configuration
public class RestTemplateConfig {

    @Bean
    public RestTemplate restTemplate(ResponseErrorHandler responseErrorHandler) {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setErrorHandler(responseErrorHandler);
        return restTemplate;
    }

    @Bean
    public ResponseErrorHandler responseErrorHandler() {
        return new ResponseErrorHandler() {
            @Override
            public boolean hasError(ClientHttpResponse response) throws IOException {
                return response.getStatusCode().is4xxClientError() || response.getStatusCode().is5xxServerError();
            }

            @Override
            public void handleError(ClientHttpResponse response) throws IOException {

                String statusText = response.getStatusText();
                if (response.getStatusCode().is4xxClientError()) {
                    throw new BaseException("Client error occurred - %s".formatted(statusText), ErrorType.INVALID_REQUEST, HttpStatus.valueOf(response.getStatusCode().value()));
                }

                throw new BaseException("Internal server error occurred - %s".formatted(statusText), ErrorType.UNKNOWN_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        };
    }
}
