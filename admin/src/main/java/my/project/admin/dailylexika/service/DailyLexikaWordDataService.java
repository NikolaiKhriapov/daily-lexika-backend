package my.project.admin.dailylexika.service;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import my.project.library.dailylexika.dtos.flashcards.WordDataDto;
import my.project.library.dailylexika.dtos.flashcards.admin.WordDataCreateDto;
import my.project.library.dailylexika.dtos.flashcards.admin.WordDataUpdateDto;
import my.project.library.dailylexika.enumerations.Platform;
import my.project.library.util.api.BaseUrlConfig;
import my.project.library.util.pageable.CustomPageImpl;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Optional;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Service
@RequiredArgsConstructor
public class DailyLexikaWordDataService {

    private final WebClient webClient;
    private final BaseUrlConfig baseUrlConfig;
    private final HttpServletRequest request;

    private static final String ENDPOINT_WORD_DATA = "/api/v1/flashcards/word-data";

    public Page<WordDataDto> getWordDataPage(Platform platform, int page, int size, String query) {
        String url = UriComponentsBuilder.fromHttpUrl(baseUrlConfig.getDailyLexika())
                .path(ENDPOINT_WORD_DATA)
                .path("/search/admin")
                .queryParam("platform", platform)
                .queryParam("page", page)
                .queryParam("size", size)
                .queryParamIfPresent("query", Optional.ofNullable(query).filter(value -> !value.isBlank()))
                .toUriString();
        return webClient.get()
                .uri(url)
                .header(AUTHORIZATION, request.getHeader(AUTHORIZATION))
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<CustomPageImpl<WordDataDto>>() {})
                .block();
    }

    public WordDataDto getWordData(Integer id) {
        String url = UriComponentsBuilder.fromHttpUrl(baseUrlConfig.getDailyLexika())
                .path(ENDPOINT_WORD_DATA)
                .pathSegment(String.valueOf(id))
                .toUriString();
        return webClient.get()
                .uri(url)
                .header(AUTHORIZATION, request.getHeader(AUTHORIZATION))
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(WordDataDto.class)
                .block();
    }

    public WordDataDto createWordData(WordDataCreateDto createDto) {
        String url = UriComponentsBuilder.fromHttpUrl(baseUrlConfig.getDailyLexika())
                .path(ENDPOINT_WORD_DATA)
                .toUriString();
        return webClient.post()
                .uri(url)
                .header(AUTHORIZATION, request.getHeader(AUTHORIZATION))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(createDto)
                .retrieve()
                .bodyToMono(WordDataDto.class)
                .block();
    }

    public WordDataDto updateWordData(Integer id, WordDataUpdateDto patchDto) {
        String url = UriComponentsBuilder.fromHttpUrl(baseUrlConfig.getDailyLexika())
                .path(ENDPOINT_WORD_DATA)
                .pathSegment(String.valueOf(id))
                .toUriString();
        return webClient.patch()
                .uri(url)
                .header(AUTHORIZATION, request.getHeader(AUTHORIZATION))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(patchDto)
                .retrieve()
                .bodyToMono(WordDataDto.class)
                .block();
    }

    public void deleteWordData(Integer id) {
        String url = UriComponentsBuilder.fromHttpUrl(baseUrlConfig.getDailyLexika())
                .path(ENDPOINT_WORD_DATA)
                .pathSegment(String.valueOf(id))
                .toUriString();
        webClient.delete()
                .uri(url)
                .header(AUTHORIZATION, request.getHeader(AUTHORIZATION))
                .retrieve()
                .bodyToMono(Void.class)
                .block();
    }
}
