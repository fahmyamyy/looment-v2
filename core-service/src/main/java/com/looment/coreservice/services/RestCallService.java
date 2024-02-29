package com.looment.coreservice.services;

import com.looment.coreservice.dtos.UploadRequest;
import com.looment.coreservice.dtos.UploadResponse;
import com.looment.coreservice.exceptions.FailedRestCall;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class RestCallService {
    private final WebClient webClient;
    private final Environment environment;
    public String getToHost(String url) {
        try {
            return webClient.get()
                    .uri(url)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
        } catch (Exception e) {
            throw new FailedRestCall();
        }
    }

    public String deleteToHost(String url) {
        try {
            return webClient.delete()
                    .uri(url)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
        } catch (Exception e) {
            throw new FailedRestCall();
        }
    }

    public UploadResponse callApiUpload(UploadRequest uploadRequest) {
        try {
            return webClient
                    .post()
                    .uri(Objects.requireNonNull(environment.getProperty("port.upload")))
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .body(BodyInserters.fromMultipartData(createMultipartData(uploadRequest)))
                    .retrieve()
                    .bodyToMono(UploadResponse.class)
                    .block();
        } catch (Exception e) {
            throw new FailedRestCall();
        }
    }

    private MultiValueMap<String, HttpEntity<?>> createMultipartData(UploadRequest uploadRequest) {
        MultipartBodyBuilder builder = new MultipartBodyBuilder();
        builder.part("file", uploadRequest.getFile().getResource());
        builder.part("parent", uploadRequest.getParent());
        builder.part("uploadedBy", uploadRequest.getUploadedBy());

        return builder.build();
    }
}
