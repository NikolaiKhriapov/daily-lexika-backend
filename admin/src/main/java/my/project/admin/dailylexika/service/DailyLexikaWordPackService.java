package my.project.admin.dailylexika.service;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import my.project.library.dailylexika.dtos.flashcards.admin.WordPackCreateDto;
import my.project.library.dailylexika.dtos.flashcards.admin.WordPackDto;
import my.project.library.dailylexika.dtos.flashcards.admin.WordPackUpdateDto;
import my.project.library.dailylexika.enumerations.Platform;
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
public class DailyLexikaWordPackService {

    private final WebClient webClient;
    private final BaseUrlConfig baseUrlConfig;
    private final HttpServletRequest request;

    private static final String ENDPOINT_WORD_PACKS = "/api/v1/flashcards/word-packs";

    public Page<WordPackDto> getWordPackPage(Platform platform, int page, int size) {
        String url = UriComponentsBuilder.fromHttpUrl(baseUrlConfig.getDailyLexika())
                .path(ENDPOINT_WORD_PACKS)
                .path("/search/admin")
                .queryParam("platform", platform)
                .queryParam("page", page)
                .queryParam("size", size)
                .toUriString();
        return webClient.get()
                .uri(url)
                .header(AUTHORIZATION, request.getHeader(AUTHORIZATION))
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<CustomPageImpl<WordPackDto>>() {})
                .block();
    }

    public WordPackDto getWordPack(Long wordPackId) {
        String url = UriComponentsBuilder.fromHttpUrl(baseUrlConfig.getDailyLexika())
                .path(ENDPOINT_WORD_PACKS)
                .pathSegment(wordPackId.toString())
                .toUriString();
        return webClient.get()
                .uri(url)
                .header(AUTHORIZATION, request.getHeader(AUTHORIZATION))
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(WordPackDto.class)
                .block();
    }

    public WordPackDto createWordPack(WordPackCreateDto createDto) {
        String url = UriComponentsBuilder.fromHttpUrl(baseUrlConfig.getDailyLexika())
                .path(ENDPOINT_WORD_PACKS)
                .toUriString();
        return webClient.post()
                .uri(url)
                .header(AUTHORIZATION, request.getHeader(AUTHORIZATION))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(createDto)
                .retrieve()
                .bodyToMono(WordPackDto.class)
                .block();
    }

    public WordPackDto updateWordPack(Long wordPackId, WordPackUpdateDto patchDto) {
        String url = UriComponentsBuilder.fromHttpUrl(baseUrlConfig.getDailyLexika())
                .path(ENDPOINT_WORD_PACKS)
                .pathSegment(wordPackId.toString())
                .toUriString();
        return webClient.patch()
                .uri(url)
                .header(AUTHORIZATION, request.getHeader(AUTHORIZATION))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(patchDto)
                .retrieve()
                .bodyToMono(WordPackDto.class)
                .block();
    }

    public void deleteWordPack(Long wordPackId) {
        String url = UriComponentsBuilder.fromHttpUrl(baseUrlConfig.getDailyLexika())
                .path(ENDPOINT_WORD_PACKS)
                .pathSegment(wordPackId.toString())
                .toUriString();
        webClient.delete()
                .uri(url)
                .header(AUTHORIZATION, request.getHeader(AUTHORIZATION))
                .retrieve()
                .bodyToMono(Void.class)
                .block();
    }
}
