package ru.practicum;

import org.springframework.http.*;
import org.springframework.lang.Nullable;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

public class BaseClient {

    protected final RestTemplate restTemplate;

    public BaseClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    protected ResponseEntity<Object> get(String path,
                                         @Nullable Map<String, Object> parameters) {
        return makeRequest(HttpMethod.GET, path, parameters, null);
    }

    protected <T> ResponseEntity<Object> post(String path,
                                              T body) {
        return makeRequest(HttpMethod.POST, path, null, body);
    }

    private <T> ResponseEntity<Object> makeRequest(HttpMethod method,
                                                   String path,
                                                   @Nullable Map<String, Object> parameters,
                                                   @Nullable T body) {
        HttpEntity<T> requestEntity = body != null
                ? new HttpEntity<>(body, defaultHeaders())
                : new HttpEntity<>(defaultHeaders());

        try {
            if (parameters != null) {
                return restTemplate.exchange(path, method, requestEntity, Object.class, parameters);
            } else {
                return restTemplate.exchange(path, method, requestEntity, Object.class);
            }
        } catch (HttpStatusCodeException e) {
            return ResponseEntity
                    .status(e.getStatusCode())
                    .body(e.getResponseBodyAsByteArray());
        }
    }

    private HttpHeaders defaultHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }
}
