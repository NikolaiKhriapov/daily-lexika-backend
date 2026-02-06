package my.project.admin.dailylexika.service;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import my.project.library.dailylexika.dtos.log.LogDto;
import my.project.library.util.api.BaseUrlConfig;
import my.project.library.util.pageable.CustomPageImpl;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.web.reactive.function.client.WebClient;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Service
@RequiredArgsConstructor
public class DailyLexikaLogService {

    private final WebClient webClient;
    private final BaseUrlConfig baseUrlConfig;
    private final HttpServletRequest request;

    private static final String ENDPOINT_LOGS = "/api/v1/logs";

    public Page<LogDto> getAllLogs(int page, int size, String sortDirection) {
        String url = UriComponentsBuilder.fromHttpUrl(baseUrlConfig.getDailyLexika())
                .path(ENDPOINT_LOGS)
                .queryParam("page", page)
                .queryParam("size", size)
                .queryParam("sort", sortDirection)
                .toUriString();
        return webClient.get()
                .uri(url)
                .header(AUTHORIZATION, request.getHeader(AUTHORIZATION))
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<CustomPageImpl<LogDto>>() {})
                .block();
    }
}
