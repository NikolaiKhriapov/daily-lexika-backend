package my.project.library.util.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import my.project.library.util.exception.InternalServerErrorException;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@AllArgsConstructor
@Component
public class RestTemplateService {

    private final ObjectMapper objectMapper;
    private final RestTemplate restTemplate;

    public <T> ResponseEntity<T> post(String url, HttpHeaders headers, Object body, Class<T> tClass) {
        try {
            HttpEntity<Object> request = new HttpEntity<>(objectMapper.writeValueAsString(body), headers);
            return restTemplate.exchange(url, HttpMethod.POST, request, tClass);
        } catch (JsonProcessingException e) {
            throw new InternalServerErrorException(e.getMessage());
        }
    }

    public <T> T post(String url, Object body, Class<T> tClass) {
        try {
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Object> request = new HttpEntity<>(objectMapper.writeValueAsString(body), httpHeaders);
            return restTemplate.postForObject(url, request, tClass);
        } catch (JsonProcessingException e) {
            throw new InternalServerErrorException(e.getMessage());
        }
    }

    public <T> T post(String url, HttpEntity<MultiValueMap<String, String>> entity , Class<T> tClass)  {
        return restTemplate.exchange(url, HttpMethod.POST, entity, tClass).getBody();
    }

    public <T> T patch(String url, HttpHeaders headers, Object body, Class<T> tClass) {
        try {
            HttpEntity<Object> request = new HttpEntity<>(objectMapper.writeValueAsString(body), headers);
            return restTemplate.patchForObject(url, request, tClass);
        } catch (JsonProcessingException e) {
            throw new InternalServerErrorException(e.getMessage());
        }
    }

    public <T> ResponseEntity<T> get(String url, HttpHeaders httpHeaders, Class<T> tClass) {
        HttpEntity<Void> requestEntity = new HttpEntity<>(httpHeaders);
        return restTemplate.exchange(url, HttpMethod.GET, requestEntity,  tClass, new HashMap<String, String>());
    }

    public <T> ResponseEntity<T> get(String url, HttpHeaders httpHeaders, ParameterizedTypeReference<T> responseType) {
        HttpEntity<Void> requestEntity = new HttpEntity<>(httpHeaders);
        return restTemplate.exchange(url, HttpMethod.GET, requestEntity,  responseType, new HashMap<String, String>());
    }

    public <T> ResponseEntity<T> get(String url, ParameterizedTypeReference<T> responseType) {
        return restTemplate.exchange(url, HttpMethod.GET, null,  responseType, new HashMap<String, String>());
    }

    public <T> ResponseEntity<T> delete(String url, HttpHeaders headers, Class<T> tClass) {
        HttpEntity<Void> request = new HttpEntity<>(headers);
        return restTemplate.exchange(url, HttpMethod.DELETE, request, tClass);
    }

    public <T> ResponseEntity<T> exchange(String url, HttpHeaders headers, Object body, HttpMethod httpMethod, Class<T> tClass) {
        try{
            HttpEntity<Object> request = new HttpEntity<>(objectMapper.writeValueAsString(body), headers);
            return restTemplate.exchange(url, httpMethod, request, tClass);
        } catch (JsonProcessingException e) {
            throw new InternalServerErrorException(e.getMessage());
        }
    }

    public HttpHeaders getHeadersWithJsonContentTypeAndJwtToken(HttpServletRequest request) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set(AUTHORIZATION, request.getHeader(AUTHORIZATION));
        return headers;
    }
}
