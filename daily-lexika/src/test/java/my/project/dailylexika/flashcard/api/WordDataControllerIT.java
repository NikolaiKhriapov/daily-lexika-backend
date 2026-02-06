package my.project.dailylexika.flashcard.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import my.project.dailylexika.config.AbstractIntegrationTest;
import my.project.dailylexika.flashcard.model.entities.WordData;
import my.project.dailylexika.flashcard.model.entities.WordPack;
import my.project.dailylexika.flashcard.persistence.WordDataRepository;
import my.project.library.dailylexika.dtos.flashcards.WordDataDto;
import my.project.library.dailylexika.dtos.flashcards.WordPackCustomCreateDto;
import my.project.library.dailylexika.dtos.flashcards.admin.WordDataCreateDto;
import my.project.library.dailylexika.dtos.flashcards.admin.WordDataUpdateDto;
import my.project.library.dailylexika.dtos.flashcards.admin.WordPackCreateDto;
import my.project.library.dailylexika.dtos.flashcards.admin.WordPackDto;
import my.project.library.dailylexika.dtos.user.AuthenticationResponse;
import my.project.library.dailylexika.dtos.user.RegistrationRequest;
import my.project.library.dailylexika.enumerations.Category;
import my.project.library.dailylexika.enumerations.Platform;
import my.project.library.util.exception.ApiErrorDTO;
import my.project.library.util.pageable.CustomPageImpl;
import my.project.library.util.security.JwtService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Stream;

import static my.project.dailylexika.util.TestDataFactory.*;
import static my.project.library.dailylexika.enumerations.Platform.CHINESE;
import static my.project.library.dailylexika.enumerations.Platform.ENGLISH;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class WordDataControllerIT extends AbstractIntegrationTest {

    private static final String URI_REGISTER = "/api/v1/auth/register";
    private static final String URI_CREATE_WORD_PACK = "/api/v1/flashcards/word-packs";
    private static final String URI_CREATE_CUSTOM_WORD_PACK = "/api/v1/flashcards/word-packs/custom";

    private static final String URI_WORD_DATA = "/api/v1/flashcards/word-data";
    private static final String URI_SEARCH_WORD_DATA = URI_WORD_DATA + "/search";
    private static final String URI_SEARCH_WORD_DATA_ADMIN = URI_WORD_DATA + "/search/admin";
    private static final String URI_GET_WORD_DATA = URI_WORD_DATA + "/{id}";
    private static final String URI_CREATE_WORD_DATA = URI_WORD_DATA;
    private static final String URI_UPDATE_WORD_DATA = URI_WORD_DATA + "/{id}";
    private static final String URI_DELETE_WORD_DATA = URI_WORD_DATA + "/{id}";
    private static final String URI_ADD_CUSTOM_WORD_PACK_TO_WORD_DATA = URI_WORD_DATA + "/{id}/add-word-pack/{wordPackId}";
    private static final String URI_ADD_CUSTOM_WORD_PACK_TO_WORD_DATA_BY_WORD_PACK_ID = URI_WORD_DATA + "/{wordPackIdOriginal}/add-word-pack-to-word-data/{wordPackIdToBeAdded}";
    private static final String URI_REMOVE_CUSTOM_WORD_PACK_FROM_WORD_DATA = URI_WORD_DATA + "/{id}/remove-word-pack/{wordPackId}";

    private static final String DEFAULT_NAME = "Test User";
    private static final String DEFAULT_PASSWORD = "Pass123!";

    private static final String VALID_NAME_ENGLISH = "itxhello9";
    private static final String VALID_NAME_ENGLISH_2 = "itxgoodbye9";
    private static final String VALID_NAME_CHINESE = "龘龘";
    private static final String VALID_NAME_CHINESE_2 = "鱻鱻";
    private static final String VALID_NAME_RUSSIAN = "привет";
    private static final String VALID_ENGLISH_TRANSCRIPTION = "/hello/";
    private static final String VALID_ENGLISH_TRANSCRIPTION_2 = "/goodbye/";
    private static final String VALID_CHINESE_TRANSCRIPTION = "ni hao";
    private static final String VALID_CHINESE_TRANSCRIPTION_2 = "zai jian";
    private static final String VALID_ENGLISH_DEFINITION = "Definition.";
    private static final String VALID_CHINESE_DEFINITION = "定义。";

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private WordDataRepository wordDataRepository;

    @Test
    void searchWordData_unauthenticated_unauthorized() throws Exception {
        // WordDataController.searchWordData
        mockMvc.perform(get(URI_SEARCH_WORD_DATA)
                        .param("query", "alpha")
                        .param("limit", "5"))
                .andExpect(status().isUnauthorized());
    }

    @ParameterizedTest
    @MethodSource("my.project.dailylexika.flashcard.api.WordDataControllerIT$TestDataSource#platforms")
    void searchWordData_missingQueryOrLimit_badRequest(Platform platform) throws Exception {
        // AuthenticationController.register
        AuthenticationResponse registerResponse = performAuthenticationControllerRegister(platform);

        // WordDataController.searchWordData
        mockMvc.perform(get(URI_SEARCH_WORD_DATA)
                        .param("limit", "5")
                        .header(HttpHeaders.AUTHORIZATION, buildBearerToken(registerResponse.token())))
                .andExpect(status().isBadRequest());

        // WordDataController.searchWordData
        mockMvc.perform(get(URI_SEARCH_WORD_DATA)
                        .param("query", "alpha")
                        .header(HttpHeaders.AUTHORIZATION, buildBearerToken(registerResponse.token())))
                .andExpect(status().isBadRequest());
    }

    @ParameterizedTest
    @MethodSource("my.project.dailylexika.flashcard.api.WordDataControllerIT$TestDataSource#platforms")
    void searchWordData_authenticated_returnsLimitedList(Platform platform) throws Exception {
        // AuthenticationController.register
        AuthenticationResponse registerResponse = performAuthenticationControllerRegister(platform);

        // WordDataController.createWordData
        WordDataDto wordData = performWordDataControllerCreateWordData(platform);

        // WordDataController.createWordData
        performWordDataControllerCreateWordData(platform, buildUniqueWordEnglish(), buildUniqueWordChinese(), platform == ENGLISH ? VALID_ENGLISH_TRANSCRIPTION_2 : VALID_CHINESE_TRANSCRIPTION_2, new ArrayList<>());

        // WordDataController.searchWordData
        String query = platform == ENGLISH ? wordData.nameEnglish() : wordData.nameChinese();
        MvcResult result = mockMvc.perform(get(URI_SEARCH_WORD_DATA)
                        .param("query", query)
                        .param("limit", "1")
                        .header(HttpHeaders.AUTHORIZATION, buildBearerToken(registerResponse.token())))
                .andExpect(status().isOk())
                .andReturn();
        List<WordDataDto> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {});

        // Assertions
        assertThat(response).hasSizeLessThanOrEqualTo(1);
        assertThat(response).extracting(WordDataDto::id).containsOnly(wordData.id());
    }

    @ParameterizedTest
    @MethodSource("my.project.dailylexika.flashcard.api.WordDataControllerIT$TestDataSource#platforms")
    void searchWordDataAdmin_unauthenticated_unauthorized(Platform platform) throws Exception {
        // WordDataController.searchWordDataAdmin
        mockMvc.perform(get(URI_SEARCH_WORD_DATA_ADMIN)
                                .param("platform", platform.name())
                                .param("page", "0")
                                .param("size", "5"))
                .andExpect(status().isUnauthorized());
    }

    @ParameterizedTest
    @MethodSource("my.project.dailylexika.flashcard.api.WordDataControllerIT$TestDataSource#platforms")
    void searchWordDataAdmin_nonAdmin_forbidden(Platform platform) throws Exception {
        // AuthenticationController.register
        AuthenticationResponse registerResponse = performAuthenticationControllerRegister(platform);

        // WordDataController.searchWordDataAdmin
        mockMvc.perform(get(URI_SEARCH_WORD_DATA_ADMIN)
                        .param("platform", platform.name())
                        .param("page", "0")
                        .param("size", "5")
                        .header(HttpHeaders.AUTHORIZATION, buildBearerToken(registerResponse.token())))
                .andExpect(status().isForbidden());
    }

    @ParameterizedTest
    @MethodSource("my.project.dailylexika.flashcard.api.WordDataControllerIT$TestDataSource#platforms")
    void searchWordDataAdmin_missingParams_badRequest(Platform platform) throws Exception {
        // WordDataController.searchWordDataAdmin
        mockMvc.perform(get(URI_SEARCH_WORD_DATA_ADMIN)
                        .param("page", "0")
                        .param("size", "5")
                        .header(HttpHeaders.AUTHORIZATION, buildAdminBearerToken(jwtService)))
                .andExpect(status().isBadRequest());

        // WordDataController.searchWordDataAdmin
        mockMvc.perform(get(URI_SEARCH_WORD_DATA_ADMIN)
                        .param("platform", platform.name())
                        .param("size", "5")
                        .header(HttpHeaders.AUTHORIZATION, buildAdminBearerToken(jwtService)))
                .andExpect(status().isBadRequest());

        // WordDataController.searchWordDataAdmin
        mockMvc.perform(get(URI_SEARCH_WORD_DATA_ADMIN)
                        .param("platform", platform.name())
                        .param("page", "0")
                        .header(HttpHeaders.AUTHORIZATION, buildAdminBearerToken(jwtService)))
                .andExpect(status().isBadRequest());
    }

    @ParameterizedTest
    @MethodSource("my.project.dailylexika.flashcard.api.WordDataControllerIT$TestDataSource#platforms")
    void searchWordDataAdmin_superAdmin_returnsPage(Platform platform) throws Exception {
        // WordDataController.createWordData
        WordDataDto wordData = performWordDataControllerCreateWordData(platform, "test" + buildUniqueWordEnglish(), "龘" + buildUniqueWordChinese(), platform == ENGLISH ? VALID_ENGLISH_TRANSCRIPTION : "da " + VALID_CHINESE_TRANSCRIPTION, new ArrayList<>());

        // WordDataController.searchWordDataAdmin
        String query = platform == ENGLISH ? "test" : "龘";
        MvcResult result = mockMvc.perform(get(URI_SEARCH_WORD_DATA_ADMIN)
                        .param("platform", platform.name())
                        .param("page", "0")
                        .param("size", "50")
                        .param("query", query)
                        .header(HttpHeaders.AUTHORIZATION, buildAdminBearerToken(jwtService)))
                .andExpect(status().isOk())
                .andReturn();
        CustomPageImpl<WordDataDto> page = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {});

        // Assertions
        assertThat(page.getContent()).isNotEmpty();
        assertThat(page.getContent()).extracting(WordDataDto::id).contains(wordData.id());
    }

    @ParameterizedTest
    @MethodSource("my.project.dailylexika.flashcard.api.WordDataControllerIT$TestDataSource#platforms")
    void searchWordDataAdmin_blankQuery_sortsByPlatform(Platform platform) throws Exception {
        // WordDataController.createWordData
        performWordDataControllerCreateWordData(platform, "0001", "01", platform == ENGLISH ? VALID_ENGLISH_TRANSCRIPTION : VALID_CHINESE_TRANSCRIPTION, new ArrayList<>());

        // WordDataController.createWordData
        performWordDataControllerCreateWordData(platform, "0000", "00", platform == ENGLISH ? VALID_ENGLISH_TRANSCRIPTION : VALID_CHINESE_TRANSCRIPTION, new ArrayList<>());

        // WordDataController.searchWordDataAdmin
        MvcResult result = mockMvc.perform(get(URI_SEARCH_WORD_DATA_ADMIN)
                        .param("platform", platform.name())
                        .param("page", "0")
                        .param("size", "200")
                        .header(HttpHeaders.AUTHORIZATION, buildAdminBearerToken(jwtService)))
                .andExpect(status().isOk())
                .andReturn();
        CustomPageImpl<WordDataDto> page = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {});

        // Assertions
        List<String> values = page.getContent().stream()
                .map(dto -> platform == ENGLISH ? dto.nameEnglish() : dto.nameChinese())
                .filter(value -> value != null && (value.startsWith("00") || value.startsWith("01")))
                .toList();

        int indexA = values.indexOf("0000");
        int indexB = values.indexOf("0001");
        if (platform == CHINESE) {
            indexA = values.indexOf("00");
            indexB = values.indexOf("01");
        }

        assertThat(indexA).isGreaterThanOrEqualTo(0);
        assertThat(indexB).isGreaterThanOrEqualTo(0);
        assertThat(indexA).isLessThan(indexB);
    }

    @ParameterizedTest
    @MethodSource("my.project.dailylexika.flashcard.api.WordDataControllerIT$TestDataSource#platforms")
    void getWordData_unauthenticated_unauthorized(Platform platform) throws Exception {
        // WordDataController.getWordData
        mockMvc.perform(get(URI_GET_WORD_DATA, missingWordDataId()))
                .andExpect(status().isUnauthorized());
    }

    @ParameterizedTest
    @MethodSource("my.project.dailylexika.flashcard.api.WordDataControllerIT$TestDataSource#platforms")
    void getWordData_nonAdmin_forbidden(Platform platform) throws Exception {
        // AuthenticationController.register
        AuthenticationResponse registerResponse = performAuthenticationControllerRegister(platform);

        // WordDataController.getWordData
        mockMvc.perform(get(URI_GET_WORD_DATA, missingWordDataId())
                        .header(HttpHeaders.AUTHORIZATION, buildBearerToken(registerResponse.token())))
                .andExpect(status().isForbidden());
    }

    @ParameterizedTest
    @MethodSource("my.project.dailylexika.flashcard.api.WordDataControllerIT$TestDataSource#platforms")
    void getWordData_missing_notFound(Platform platform) throws Exception {
        // WordDataController.getWordData
        mockMvc.perform(get(URI_GET_WORD_DATA, missingWordDataId())
                        .header(HttpHeaders.AUTHORIZATION, buildAdminBearerToken(jwtService)))
                .andExpect(status().isNotFound());
    }

    @ParameterizedTest
    @MethodSource("my.project.dailylexika.flashcard.api.WordDataControllerIT$TestDataSource#platforms")
    void getWordData_superAdmin_returnsDto(Platform platform) throws Exception {
        // WordDataController.createWordData
        WordDataDto wordData = performWordDataControllerCreateWordData(platform);

        // WordDataController.getWordData
        MvcResult result = mockMvc
                .perform(get(URI_GET_WORD_DATA, wordData.id())
                        .header(HttpHeaders.AUTHORIZATION, buildAdminBearerToken(jwtService)))
                .andExpect(status().isOk())
                .andReturn();
        WordDataDto response = objectMapper.readValue(result.getResponse().getContentAsString(), WordDataDto.class);

        // Assertions
        assertThat(response.id()).isEqualTo(wordData.id());
        assertThat(response.nameEnglish()).isEqualTo(wordData.nameEnglish());
    }

    @ParameterizedTest
    @MethodSource("my.project.dailylexika.flashcard.api.WordDataControllerIT$TestDataSource#platforms")
    void createWordData_unauthenticated_unauthorized(Platform platform) throws Exception {
        // WordDataController.createWordData
        WordDataCreateDto request = buildValidWordDataCreateDto(platform, List.of(missingWordPackId()));
        mockMvc.perform(post(URI_CREATE_WORD_DATA)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @ParameterizedTest
    @MethodSource("my.project.dailylexika.flashcard.api.WordDataControllerIT$TestDataSource#platforms")
    void createWordData_nonAdmin_forbidden(Platform platform) throws Exception {
        // AuthenticationController.register
        AuthenticationResponse registerResponse = performAuthenticationControllerRegister(platform);

        // WordPackController.createWordPack
        WordPackDto wordPack = performWordPackControllerCreateWordPack(platform);

        // WordPackController.createWordData
        WordDataCreateDto request = buildValidWordDataCreateDto(platform, List.of(wordPack.id()));
        mockMvc.perform(post(URI_CREATE_WORD_DATA)
                        .header(HttpHeaders.AUTHORIZATION, buildBearerToken(registerResponse.token()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @ParameterizedTest
    @MethodSource("my.project.dailylexika.flashcard.api.WordDataControllerIT$TestDataSource#platforms")
    void createWordData_superAdmin_creates(Platform platform) throws Exception {
        // WordPackController.createWordPack
        WordPackDto wordPack = performWordPackControllerCreateWordPack(platform);

        // WordDataController.createWordData
        WordDataCreateDto request = buildValidWordDataCreateDto(platform, List.of(wordPack.id()));
        MvcResult result = mockMvc
                .perform(post(URI_CREATE_WORD_DATA)
                        .header(HttpHeaders.AUTHORIZATION, buildAdminBearerToken(jwtService))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn();
        WordDataDto response = objectMapper.readValue(result.getResponse().getContentAsString(), WordDataDto.class);

        // Assertions
        assertThat(response.id()).isNotNull();
        assertThat(response.platform()).isEqualTo(platform);
        assertThat(response.listOfWordPackIds()).containsOnly(wordPack.id());
        assertThat(wordDataRepository.existsById(response.id())).isTrue();
    }

    @ParameterizedTest
    @MethodSource("my.project.dailylexika.flashcard.api.WordDataControllerIT$TestDataSource#invalidCreateWordDataRequests")
    void createWordData_validation_missingRequired_badRequest(Platform platform, WordDataCreateDto invalidTemplate) throws Exception {
        // WordPackController.createWordPack
        WordPackDto wordPack = performWordPackControllerCreateWordPack(platform);

        // WordDataController.createWordData
        List<Long> wordPackIds = invalidTemplate.listOfWordPackIds() == null ? null : List.of(wordPack.id());
        WordDataCreateDto request = new WordDataCreateDto(
                invalidTemplate.nameChinese(),
                invalidTemplate.transcription(),
                invalidTemplate.nameEnglish(),
                invalidTemplate.nameRussian(),
                invalidTemplate.definition(),
                invalidTemplate.examples(),
                wordPackIds,
                invalidTemplate.platform()
        );

        MvcResult result = mockMvc
                .perform(post(URI_CREATE_WORD_DATA)
                        .header(HttpHeaders.AUTHORIZATION, buildAdminBearerToken(jwtService))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andReturn();
        ApiErrorDTO error = objectMapper.readValue(result.getResponse().getContentAsString(), ApiErrorDTO.class);

        // Assertions
        assertThat(error.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(error.path()).isEqualTo(URI_WORD_DATA);
    }

    @ParameterizedTest
    @MethodSource("my.project.dailylexika.flashcard.api.WordDataControllerIT$TestDataSource#platforms")
    void createWordData_wordPackCustom_badRequest(Platform platform) throws Exception {
        // AuthenticationController.register
        AuthenticationResponse registerResponse = performAuthenticationControllerRegister(platform);

        // WordPackController.createCustomWordPack
        WordPackDto customPack = performWordPackControllerCreateCustomWordPack(registerResponse.token());

        // WordDataController.createWordData
        WordDataCreateDto request = buildValidWordDataCreateDto(platform, List.of(customPack.id()));
        mockMvc.perform(post(URI_CREATE_WORD_DATA)
                        .header(HttpHeaders.AUTHORIZATION, buildAdminBearerToken(jwtService))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @ParameterizedTest
    @MethodSource("my.project.dailylexika.flashcard.api.WordDataControllerIT$TestDataSource#platforms")
    void createWordData_wordPackMissing_notFound(Platform platform) throws Exception {
        // WordDataController.createWordData
        WordDataCreateDto request = buildValidWordDataCreateDto(platform, List.of(missingWordPackId()));
        mockMvc.perform(post(URI_CREATE_WORD_DATA)
                        .header(HttpHeaders.AUTHORIZATION, buildAdminBearerToken(jwtService))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @ParameterizedTest
    @MethodSource("my.project.dailylexika.flashcard.api.WordDataControllerIT$TestDataSource#platforms")
    void updateWordData_unauthenticated_unauthorized(Platform platform) throws Exception {
        // WordDataController.updateWordData
        WordDataUpdateDto request = new WordDataUpdateDto(VALID_NAME_CHINESE_2, null, VALID_NAME_ENGLISH_2, null, null, null, null);
        mockMvc.perform(patch(URI_UPDATE_WORD_DATA, missingWordDataId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @ParameterizedTest
    @MethodSource("my.project.dailylexika.flashcard.api.WordDataControllerIT$TestDataSource#platforms")
    void updateWordData_nonAdmin_forbidden(Platform platform) throws Exception {
        // AuthenticationController.register
        AuthenticationResponse registerResponse = performAuthenticationControllerRegister(platform);

        // WordDataController.updateWordData
        WordDataUpdateDto request = new WordDataUpdateDto(VALID_NAME_CHINESE_2, null, VALID_NAME_ENGLISH_2, null, null, null, null);
        mockMvc.perform(patch(URI_UPDATE_WORD_DATA, missingWordDataId())
                        .header(HttpHeaders.AUTHORIZATION, buildBearerToken(registerResponse.token()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @ParameterizedTest
    @MethodSource("my.project.dailylexika.flashcard.api.WordDataControllerIT$TestDataSource#platforms")
    void updateWordData_superAdmin_updatesPartial(Platform platform) throws Exception {
        // WordDataController.createWordData
        WordDataDto wordData = performWordDataControllerCreateWordData(platform);

        // WordDataController.updateWordData
        WordDataUpdateDto request = new WordDataUpdateDto(null, null, VALID_NAME_ENGLISH_2, null, null, null, null);
        MvcResult result = mockMvc
                .perform(patch(URI_UPDATE_WORD_DATA, wordData.id())
                        .header(HttpHeaders.AUTHORIZATION, buildAdminBearerToken(jwtService))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn();
        WordDataDto response = objectMapper.readValue(result.getResponse().getContentAsString(), WordDataDto.class);

        // Assertions
        assertThat(response.nameEnglish()).isEqualTo(VALID_NAME_ENGLISH_2);

        WordData refreshed = wordDataRepository.findById(wordData.id()).orElseThrow();
        assertThat(refreshed.getId()).isEqualTo(wordData.id());
        assertThat(refreshed.getNameChinese()).isEqualTo(wordData.nameChinese());
        assertThat(refreshed.getTranscription()).isEqualTo(wordData.transcription());
        assertThat(refreshed.getNameEnglish()).isEqualTo(VALID_NAME_ENGLISH_2);
        assertThat(refreshed.getNameRussian()).isEqualTo(wordData.nameRussian());
        assertThat(refreshed.getDefinition()).isEqualTo(wordData.definition());
        assertThat(refreshed.getWordOfTheDayDate()).isEqualTo(wordData.wordOfTheDayDate());
        assertThat(refreshed.getPlatform()).isEqualTo(wordData.platform());
    }

    @ParameterizedTest
    @MethodSource("my.project.dailylexika.flashcard.api.WordDataControllerIT$TestDataSource#platforms")
    void updateWordData_emptyPatch_noopOk(Platform platform) throws Exception {
        // WordDataController.createWordData
        WordDataDto wordData = performWordDataControllerCreateWordData(platform);

        // WordDataController.updateWordData
        WordDataUpdateDto request = new WordDataUpdateDto(null, null, null, null, null, null, null);
        mockMvc.perform(patch(URI_UPDATE_WORD_DATA, wordData.id())
                        .header(HttpHeaders.AUTHORIZATION, buildAdminBearerToken(jwtService))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn();

        // Assertions
        WordData refreshed = wordDataRepository.findById(wordData.id()).orElseThrow();
        assertThat(refreshed.getId()).isEqualTo(wordData.id());
        assertThat(refreshed.getNameChinese()).isEqualTo(wordData.nameChinese());
        assertThat(refreshed.getTranscription()).isEqualTo(wordData.transcription());
        assertThat(refreshed.getNameEnglish()).isEqualTo(wordData.nameEnglish());
        assertThat(refreshed.getNameRussian()).isEqualTo(wordData.nameRussian());
        assertThat(refreshed.getDefinition()).isEqualTo(wordData.definition());
        assertThat(refreshed.getWordOfTheDayDate()).isEqualTo(wordData.wordOfTheDayDate());
        assertThat(refreshed.getPlatform()).isEqualTo(wordData.platform());
    }

    @ParameterizedTest
    @MethodSource("my.project.dailylexika.flashcard.api.WordDataControllerIT$TestDataSource#platforms")
    void updateWordData_missing_notFound(Platform platform) throws Exception {
        // WordDataController.updateWordData
        WordDataUpdateDto request = new WordDataUpdateDto(null, null, VALID_NAME_ENGLISH_2, null, null, null, null);
        mockMvc.perform(patch(URI_UPDATE_WORD_DATA, missingWordDataId())
                        .header(HttpHeaders.AUTHORIZATION, buildAdminBearerToken(jwtService))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @ParameterizedTest
    @MethodSource("my.project.dailylexika.flashcard.api.WordDataControllerIT$TestDataSource#platforms")
    void updateWordData_wordPackCustom_badRequest(Platform platform) throws Exception {
        // AuthenticationController.register
        AuthenticationResponse registerResponse = performAuthenticationControllerRegister(platform);

        // WordDataController.createWordData
        WordDataDto wordData = performWordDataControllerCreateWordData(platform);

        // WordPackController.createCustomWordPack
        WordPackDto customPack = performWordPackControllerCreateCustomWordPack(registerResponse.token());

        // WordDataController.updateWordData
        WordDataUpdateDto request = new WordDataUpdateDto(null, null, null, null, null, null, List.of(customPack.id()));
        mockMvc.perform(patch(URI_UPDATE_WORD_DATA, wordData.id())
                        .header(HttpHeaders.AUTHORIZATION, buildAdminBearerToken(jwtService))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @ParameterizedTest
    @MethodSource("my.project.dailylexika.flashcard.api.WordDataControllerIT$TestDataSource#platforms")
    void updateWordData_wordPackMissing_notFound(Platform platform) throws Exception {
        // WordDataController.createWordData
        WordDataDto wordData = performWordDataControllerCreateWordData(platform);

        // WordDataController.updateWordData
        WordDataUpdateDto request = new WordDataUpdateDto(null, null, null, null, null, null, List.of(missingWordPackId()));
        mockMvc.perform(patch(URI_UPDATE_WORD_DATA, wordData.id())
                        .header(HttpHeaders.AUTHORIZATION, buildAdminBearerToken(jwtService))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @ParameterizedTest
    @MethodSource("my.project.dailylexika.flashcard.api.WordDataControllerIT$TestDataSource#platforms")
    void deleteWordData_unauthenticated_unauthorized(Platform platform) throws Exception {
        // WordDataController.deleteWordData
        mockMvc.perform(delete(URI_DELETE_WORD_DATA, missingWordDataId()))
                .andExpect(status().isUnauthorized());
    }

    @ParameterizedTest
    @MethodSource("my.project.dailylexika.flashcard.api.WordDataControllerIT$TestDataSource#platforms")
    void deleteWordData_nonAdmin_forbidden(Platform platform) throws Exception {
        // AuthenticationController.register
        AuthenticationResponse registerResponse = performAuthenticationControllerRegister(platform);

        // WordDataController.deleteWordData
        mockMvc.perform(delete(URI_DELETE_WORD_DATA, missingWordDataId())
                        .header(HttpHeaders.AUTHORIZATION, buildBearerToken(registerResponse.token())))
                .andExpect(status().isForbidden());
    }

    @ParameterizedTest
    @MethodSource("my.project.dailylexika.flashcard.api.WordDataControllerIT$TestDataSource#platforms")
    void deleteWordData_missing_notFound(Platform platform) throws Exception {
        // WordDataController.deleteWordData
        mockMvc.perform(delete(URI_DELETE_WORD_DATA, missingWordDataId())
                        .header(HttpHeaders.AUTHORIZATION, buildAdminBearerToken(jwtService)))
                .andExpect(status().isNotFound());
    }

    @ParameterizedTest
    @MethodSource("my.project.dailylexika.flashcard.api.WordDataControllerIT$TestDataSource#platforms")
    void deleteWordData_superAdmin_deletes(Platform platform) throws Exception {
        // WordDataController.createWordData
        WordDataDto wordData = performWordDataControllerCreateWordData(platform);

        // WordDataController.deleteWordData
        mockMvc.perform(delete(URI_DELETE_WORD_DATA, wordData.id())
                        .header(HttpHeaders.AUTHORIZATION, buildAdminBearerToken(jwtService)))
                .andExpect(status().isNoContent());

        // Assertions
        assertThat(wordDataRepository.existsById(wordData.id())).isFalse();
    }

    @ParameterizedTest
    @MethodSource("my.project.dailylexika.flashcard.api.WordDataControllerIT$TestDataSource#platforms")
    void addCustomWordPackToWordData_unauthenticated_unauthorized(Platform platform) throws Exception {
        // WordDataController.addCustomWordPackToWordData
        Integer wordDataId = missingWordDataId();
        Long wordPackId = missingWordPackId();
        mockMvc.perform(get(URI_ADD_CUSTOM_WORD_PACK_TO_WORD_DATA, wordDataId, wordPackId))
                .andExpect(status().isUnauthorized());
    }

    @ParameterizedTest
    @MethodSource("my.project.dailylexika.flashcard.api.WordDataControllerIT$TestDataSource#platforms")
    void addCustomWordPackToWordData_addsCustomWordPack(Platform platform) throws Exception {
        // AuthenticationController.register
        AuthenticationResponse registerResponse = performAuthenticationControllerRegister(platform);

        // WordPackController.createCustomWordPack
        WordPackDto customPack = performWordPackControllerCreateCustomWordPack(registerResponse.token());

        // WordDataController.createWordData
        WordDataDto wordData = performWordDataControllerCreateWordData(platform);

        // WordDataController.addCustomWordPackToWordData
        MvcResult result = mockMvc
                .perform(get(URI_ADD_CUSTOM_WORD_PACK_TO_WORD_DATA, wordData.id(), customPack.id())
                        .header(HttpHeaders.AUTHORIZATION, buildBearerToken(registerResponse.token())))
                .andExpect(status().isOk())
                .andReturn();
        WordDataDto response = objectMapper.readValue(result.getResponse().getContentAsString(), WordDataDto.class);

        // Assertions
        assertThat(response.listOfWordPackIds()).containsOnly(customPack.id());
    }

    @ParameterizedTest
    @MethodSource("my.project.dailylexika.flashcard.api.WordDataControllerIT$TestDataSource#platforms")
    void addCustomWordPackToWordData_alreadyLinked_badRequest(Platform platform) throws Exception {
        // AuthenticationController.register
        AuthenticationResponse registerResponse = performAuthenticationControllerRegister(platform);

        // WordPackController.createCustomWordPack
        WordPackDto customPack = performWordPackControllerCreateCustomWordPack(registerResponse.token());

        // WordDataController.createWordData
        WordDataDto wordData = performWordDataControllerCreateWordData(platform);

        // WordDataController.addCustomWordPackToWordData
        mockMvc.perform(get(URI_ADD_CUSTOM_WORD_PACK_TO_WORD_DATA, wordData.id(), customPack.id())
                        .header(HttpHeaders.AUTHORIZATION, buildBearerToken(registerResponse.token())))
                .andExpect(status().isOk());

        // WordDataController.addCustomWordPackToWordData
        mockMvc.perform(get(URI_ADD_CUSTOM_WORD_PACK_TO_WORD_DATA, wordData.id(), customPack.id())
                        .header(HttpHeaders.AUTHORIZATION, buildBearerToken(registerResponse.token())))
                .andExpect(status().isBadRequest());
    }

    @ParameterizedTest
    @MethodSource("my.project.dailylexika.flashcard.api.WordDataControllerIT$TestDataSource#platforms")
    void addCustomWordPackToWordData_nonOwner_badRequest(Platform platform) throws Exception {
        // AuthenticationController.register
        AuthenticationResponse registerResponse1 = performAuthenticationControllerRegister(platform);

        // AuthenticationController.register
        AuthenticationResponse registerResponse2 = performAuthenticationControllerRegister(platform);

        // WordPackController.createCustomWordPack
        WordPackDto customPack = performWordPackControllerCreateCustomWordPack(registerResponse2.token());

        // WordDataController.createWordData
        WordDataDto wordData = performWordDataControllerCreateWordData(platform);

        // WordDataController.addCustomWordPackToWordData
        mockMvc.perform(get(URI_ADD_CUSTOM_WORD_PACK_TO_WORD_DATA, wordData.id(), customPack.id())
                        .header(HttpHeaders.AUTHORIZATION, buildBearerToken(registerResponse1.token())))
                .andExpect(status().isBadRequest());
    }

    @ParameterizedTest
    @MethodSource("my.project.dailylexika.flashcard.api.WordDataControllerIT$TestDataSource#platforms")
    void addCustomWordPackToWordData_wordPackMissing_notFound(Platform platform) throws Exception {
        // AuthenticationController.register
        AuthenticationResponse registerResponse = performAuthenticationControllerRegister(platform);

        // WordDataController.createWordData
        WordDataDto wordData = performWordDataControllerCreateWordData(platform);

        // WordDataController.addCustomWordPackToWordData
        mockMvc.perform(get(URI_ADD_CUSTOM_WORD_PACK_TO_WORD_DATA, wordData.id(), missingWordPackId())
                        .header(HttpHeaders.AUTHORIZATION, buildBearerToken(registerResponse.token())))
                .andExpect(status().isNotFound());
    }

    @Test
    void addCustomWordPackToWordDataByWordPackId_unauthenticated_unauthorized() throws Exception {
        // WordDataController.addCustomWordPackToWordDataByWordPackId
        Long wordPackIdOriginal = missingWordPackId();
        Long wordPackIdToBeAdded = missingWordPackId();
        mockMvc.perform(get(URI_ADD_CUSTOM_WORD_PACK_TO_WORD_DATA_BY_WORD_PACK_ID, wordPackIdOriginal, wordPackIdToBeAdded))
                .andExpect(status().isUnauthorized());
    }

    @ParameterizedTest
    @MethodSource("my.project.dailylexika.flashcard.api.WordDataControllerIT$TestDataSource#platforms")
    void addCustomWordPackToWordDataByWordPackId_addsToAllWordData(Platform platform) throws Exception {
        // AuthenticationController.register
        AuthenticationResponse registerResponse = performAuthenticationControllerRegister(platform);

        // WordPackController.createWordPack
        WordPackDto wordPackOriginal = performWordPackControllerCreateWordPack(platform);

        // WordPackController.createCustomWordPack
        WordPackDto wordPackToBeAdded = performWordPackControllerCreateCustomWordPack(registerResponse.token());

        // WordDataController.createWordData
        WordDataDto wordData1 = performWordDataControllerCreateWordData(platform, buildUniqueWordEnglish(), buildUniqueWordChinese(), platform == ENGLISH ? VALID_ENGLISH_TRANSCRIPTION : VALID_CHINESE_TRANSCRIPTION, List.of(wordPackOriginal.id()));

        // WordDataController.createWordData
        WordDataDto wordData2 = performWordDataControllerCreateWordData(platform, buildUniqueWordEnglish(), buildUniqueWordChinese(), platform == ENGLISH ? VALID_ENGLISH_TRANSCRIPTION_2 : VALID_CHINESE_TRANSCRIPTION_2, List.of(wordPackOriginal.id()));

        // WordDataController.addCustomWordPackToWordDataByWordPackId
        mockMvc.perform(get(URI_ADD_CUSTOM_WORD_PACK_TO_WORD_DATA_BY_WORD_PACK_ID, wordPackOriginal.id(), wordPackToBeAdded.id())
                        .header(HttpHeaders.AUTHORIZATION, buildBearerToken(registerResponse.token())))
                .andExpect(status().isOk());

        // Assertions
        WordData updated1 = wordDataRepository.findWithWordPacksById(wordData1.id()).orElseThrow();
        WordData updated2 = wordDataRepository.findWithWordPacksById(wordData2.id()).orElseThrow();

        List<Long> wordPackIds1 = updated1.getListOfWordPacks().stream()
                .map(WordPack::getId)
                .toList();
        List<Long> wordPackIds2 = updated2.getListOfWordPacks().stream()
                .map(WordPack::getId)
                .toList();

        assertThat(wordPackIds1).contains(wordPackToBeAdded.id());
        assertThat(wordPackIds2).contains(wordPackToBeAdded.id());
    }

    @ParameterizedTest
    @MethodSource("my.project.dailylexika.flashcard.api.WordDataControllerIT$TestDataSource#platforms")
    void addCustomWordPackToWordDataByWordPackId_nonOwner_badRequest(Platform platform) throws Exception {
        // AuthenticationController.register
        AuthenticationResponse registerResponse1 = performAuthenticationControllerRegister(platform);

        // AuthenticationController.register
        AuthenticationResponse registerResponse2 = performAuthenticationControllerRegister(platform);

        // WordPackController.createWordPack
        WordPackDto wordPackOriginal = performWordPackControllerCreateWordPack(platform);

        // WordPackController.createCustomWordPack
        WordPackDto wordPackToBeAdded = performWordPackControllerCreateCustomWordPack(registerResponse2.token());

        // WordDataController.createWordData
        performWordDataControllerCreateWordData(platform, buildUniqueWordEnglish(), buildUniqueWordChinese(), platform == ENGLISH ? VALID_ENGLISH_TRANSCRIPTION : VALID_CHINESE_TRANSCRIPTION, List.of(wordPackOriginal.id()));

        // WordDataController.addCustomWordPackToWordDataByWordPackId
        mockMvc.perform(get(URI_ADD_CUSTOM_WORD_PACK_TO_WORD_DATA_BY_WORD_PACK_ID, wordPackOriginal.id(), wordPackToBeAdded.id())
                        .header(HttpHeaders.AUTHORIZATION, buildBearerToken(registerResponse1.token())))
                .andExpect(status().isBadRequest());
    }

    @ParameterizedTest
    @MethodSource("my.project.dailylexika.flashcard.api.WordDataControllerIT$TestDataSource#platforms")
    void addCustomWordPackToWordDataByWordPackId_wordPackMissing_notFound(Platform platform) throws Exception {
        // AuthenticationController.register
        AuthenticationResponse registerResponse = performAuthenticationControllerRegister(platform);

        // WordDataController.addCustomWordPackToWordDataByWordPackId
        Long wordPackIdOriginal = missingWordPackId();
        Long wordPackIdToBeAdded = missingWordPackId();
        mockMvc.perform(get(URI_ADD_CUSTOM_WORD_PACK_TO_WORD_DATA_BY_WORD_PACK_ID, wordPackIdOriginal, wordPackIdToBeAdded)
                        .header(HttpHeaders.AUTHORIZATION, buildBearerToken(registerResponse.token())))
                .andExpect(status().isNotFound());
    }

    @ParameterizedTest
    @MethodSource("my.project.dailylexika.flashcard.api.WordDataControllerIT$TestDataSource#platforms")
    void removeCustomWordPackFromWordData_unauthenticated_unauthorized(Platform platform) throws Exception {
        // WordDataController.removeCustomWordPackFromWordData
        Integer wordDataId = missingWordDataId();
        Long wordPackId = missingWordPackId();
        mockMvc.perform(get(URI_REMOVE_CUSTOM_WORD_PACK_FROM_WORD_DATA, wordDataId, wordPackId))
                .andExpect(status().isUnauthorized());
    }

    @ParameterizedTest
    @MethodSource("my.project.dailylexika.flashcard.api.WordDataControllerIT$TestDataSource#platforms")
    void removeCustomWordPackFromWordData_removesCustomWordPack(Platform platform) throws Exception {
        // AuthenticationController.register
        AuthenticationResponse registerResponse = performAuthenticationControllerRegister(platform);

        // WordDataController.createWordData
        WordDataDto wordData = performWordDataControllerCreateWordData(platform);

        // WordPackController.createCustomWordPack
        WordPackDto customPack = performWordPackControllerCreateCustomWordPack(registerResponse.token());

        // WordDataController.addCustomWordPackToWordData
        performWordDataControllerAddCustomWordPackToWordData(registerResponse.token(), wordData.id(), customPack.id());

        // WordDataController.removeCustomWordPackFromWordData
        MvcResult result = mockMvc
                .perform(get(URI_REMOVE_CUSTOM_WORD_PACK_FROM_WORD_DATA, wordData.id(), customPack.id())
                        .header(HttpHeaders.AUTHORIZATION, buildBearerToken(registerResponse.token())))
                .andExpect(status().isOk())
                .andReturn();
        WordDataDto response = objectMapper.readValue(result.getResponse().getContentAsString(), WordDataDto.class);

        // Assertions
        assertThat(response.listOfWordPackIds()).doesNotContain(customPack.id());
    }

    @ParameterizedTest
    @MethodSource("my.project.dailylexika.flashcard.api.WordDataControllerIT$TestDataSource#platforms")
    void removeCustomWordPackFromWordData_notLinked_badRequest(Platform platform) throws Exception {
        // AuthenticationController.register
        AuthenticationResponse registerResponse = performAuthenticationControllerRegister(platform);

        // WordDataController.createWordData
        WordDataDto wordData = performWordDataControllerCreateWordData(platform);

        // WordPackController.createCustomWordPack
        WordPackDto customPack = performWordPackControllerCreateCustomWordPack(registerResponse.token());

        // WordDataController.removeCustomWordPackFromWordData
        mockMvc.perform(get(URI_REMOVE_CUSTOM_WORD_PACK_FROM_WORD_DATA, wordData.id(), customPack.id())
                        .header(HttpHeaders.AUTHORIZATION, buildBearerToken(registerResponse.token())))
                .andExpect(status().isBadRequest());
    }

    @ParameterizedTest
    @MethodSource("my.project.dailylexika.flashcard.api.WordDataControllerIT$TestDataSource#platforms")
    void removeCustomWordPackFromWordData_nonOwner_badRequest(Platform platform) throws Exception {
        // AuthenticationController.register
        AuthenticationResponse registerResponse1 = performAuthenticationControllerRegister(platform);

        // AuthenticationController.register
        AuthenticationResponse registerResponse2 = performAuthenticationControllerRegister(platform);

        // WordDataController.createWordData
        WordDataDto wordData = performWordDataControllerCreateWordData(platform);

        // WordPackController.createCustomWordPack
        WordPackDto customPack = performWordPackControllerCreateCustomWordPack(registerResponse2.token());

        // WordDataController.addCustomWordPackToWordData
        performWordDataControllerAddCustomWordPackToWordData(registerResponse2.token(), wordData.id(), customPack.id());

        // WordDataController.removeCustomWordPackFromWordData
        mockMvc.perform(get(URI_REMOVE_CUSTOM_WORD_PACK_FROM_WORD_DATA, wordData.id(), customPack.id())
                        .header(HttpHeaders.AUTHORIZATION, buildBearerToken(registerResponse1.token())))
                .andExpect(status().isBadRequest());
    }

    @ParameterizedTest
    @MethodSource("my.project.dailylexika.flashcard.api.WordDataControllerIT$TestDataSource#platforms")
    void removeCustomWordPackFromWordData_wordPackMissing_notFound(Platform platform) throws Exception {
        // AuthenticationController.register
        AuthenticationResponse registerResponse = performAuthenticationControllerRegister(platform);

        // WordDataController.createWordData
        WordDataDto wordData = performWordDataControllerCreateWordData(platform);

        // WordDataController.removeCustomWordPackFromWordData
        mockMvc.perform(get(URI_REMOVE_CUSTOM_WORD_PACK_FROM_WORD_DATA, wordData.id(), missingWordPackId())
                        .header(HttpHeaders.AUTHORIZATION, buildBearerToken(registerResponse.token())))
                .andExpect(status().isNotFound());
    }

    private AuthenticationResponse performAuthenticationControllerRegister(Platform platform) throws Exception {
        String email = buildUniqueEmail();
        RegistrationRequest request = new RegistrationRequest(DEFAULT_NAME, email, DEFAULT_PASSWORD, platform);
        MvcResult result = mockMvc
                .perform(
                        post(URI_REGISTER)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isCreated())
                .andReturn();
        return objectMapper.readValue(result.getResponse().getContentAsString(), AuthenticationResponse.class);
    }

    private WordDataDto performWordDataControllerCreateWordData(Platform platform) throws Exception {
        String transcription = switch (platform) {
            case ENGLISH -> VALID_ENGLISH_TRANSCRIPTION;
            case CHINESE -> VALID_CHINESE_TRANSCRIPTION;
        };
        return performWordDataControllerCreateWordData(platform, buildUniqueWordEnglish(), buildUniqueWordChinese(), transcription, new ArrayList<>());
    }

    private WordDataDto performWordDataControllerCreateWordData(Platform platform, String nameEnglish, String nameChinese, String transcription, List<Long> wordPackIds) throws Exception {
        String definition = switch (platform) {
            case ENGLISH -> VALID_ENGLISH_DEFINITION;
            case CHINESE -> VALID_CHINESE_DEFINITION;
        };
        List<Map<String, String>> examples = switch (platform) {
            case ENGLISH -> buildValidEnglishExamples();
            case CHINESE -> buildValidChineseExamples(nameChinese);
        };

        WordDataCreateDto request = new WordDataCreateDto(
                nameChinese,
                transcription,
                nameEnglish,
                VALID_NAME_RUSSIAN,
                definition,
                examples,
                wordPackIds,
                platform
        );

        MvcResult result = mockMvc.perform(post(URI_CREATE_WORD_DATA)
                        .header(HttpHeaders.AUTHORIZATION, buildAdminBearerToken(jwtService))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn();
        return objectMapper.readValue(result.getResponse().getContentAsString(StandardCharsets.UTF_8), WordDataDto.class);
    }

    private WordDataDto performWordDataControllerAddCustomWordPackToWordData(String jwtToken, Integer wordDataId, Long customWordPackId) throws Exception {
        MvcResult result = mockMvc
                .perform(get(URI_ADD_CUSTOM_WORD_PACK_TO_WORD_DATA, wordDataId, customWordPackId)
                        .header(HttpHeaders.AUTHORIZATION, buildBearerToken(jwtToken)))
                .andExpect(status().isOk())
                .andReturn();
        return objectMapper.readValue(result.getResponse().getContentAsString(), WordDataDto.class);
    }

    private WordPackDto performWordPackControllerCreateWordPack(Platform platform) throws Exception {
        WordPackCreateDto request = new WordPackCreateDto(
                "Pack-" + UUID.randomUUID(),
                "Description",
                Category.OTHER,
                platform
        );
        MvcResult result = mockMvc
                .perform(
                        post(URI_CREATE_WORD_PACK)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                                .header(HttpHeaders.AUTHORIZATION, buildAdminBearerToken(jwtService))
                )
                .andExpect(status().isCreated())
                .andReturn();
        return objectMapper.readValue(result.getResponse().getContentAsString(), WordPackDto.class);
    }

    private WordPackDto performWordPackControllerCreateCustomWordPack(String jwtToken) throws Exception {
        WordPackCustomCreateDto request = new WordPackCustomCreateDto(
                "Pack-" + UUID.randomUUID(),
                "Description"
        );
        MvcResult result = mockMvc.perform(
                        post(URI_CREATE_CUSTOM_WORD_PACK)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                                .header(HttpHeaders.AUTHORIZATION, buildBearerToken(jwtToken))
                )
                .andExpect(status().isCreated())
                .andReturn();
        return objectMapper.readValue(result.getResponse().getContentAsString(), WordPackDto.class);
    }

    private static WordDataCreateDto buildValidWordDataCreateDto(Platform platform, List<Long> wordPackIds) {
        return switch (platform) {
            case ENGLISH -> new WordDataCreateDto(
                    VALID_NAME_CHINESE,
                    VALID_ENGLISH_TRANSCRIPTION,
                    VALID_NAME_ENGLISH,
                    VALID_NAME_RUSSIAN,
                    VALID_ENGLISH_DEFINITION,
                    buildValidEnglishExamples(),
                    wordPackIds,
                    platform
            );
            case CHINESE -> new WordDataCreateDto(
                    VALID_NAME_CHINESE,
                    VALID_CHINESE_TRANSCRIPTION,
                    VALID_NAME_ENGLISH,
                    VALID_NAME_RUSSIAN,
                    VALID_CHINESE_DEFINITION,
                    buildValidChineseExamples(VALID_NAME_CHINESE),
                    wordPackIds,
                    platform
            );
        };
    }

    private static List<Map<String, String>> buildValidEnglishExamples() {
        Map<String, String> validEnglishExample = Map.of(
                "ch", VALID_NAME_CHINESE + "：例句。",
                "en", "Example sentence.",
                "ru", "Пример предложения."
        );
        return List.of(validEnglishExample, validEnglishExample, validEnglishExample, validEnglishExample, validEnglishExample);
    }

    private static List<Map<String, String>> buildValidChineseExamples(String nameChinese) {
        Map<String, String> validChineseExample = Map.of(
                "ch", nameChinese + "：例句。",
                "pinyin", "ni hao: li ju",
                "en", "Example sentence.",
                "ru", "Пример предложения."
        );
        return List.of(validChineseExample, validChineseExample, validChineseExample, validChineseExample, validChineseExample);
    }

    static class TestDataSource {
        static Stream<Platform> platforms() {
            return Stream.of(ENGLISH, CHINESE);
        }

        static Stream<Arguments> invalidCreateWordDataRequests() {
            List<Long> placeholderWordPackIds = List.of(1L);
            return platforms()
                    .flatMap(platform -> {
                        WordDataCreateDto base = buildValidWordDataCreateDto(platform, placeholderWordPackIds);
                        List<WordDataCreateDto> invalid = new ArrayList<>();

                        invalid.add(new WordDataCreateDto(null, base.transcription(), base.nameEnglish(), base.nameRussian(), base.definition(), base.examples(), base.listOfWordPackIds(), platform));
                        invalid.add(new WordDataCreateDto("", base.transcription(), base.nameEnglish(), base.nameRussian(), base.definition(), base.examples(), base.listOfWordPackIds(), platform));
                        invalid.add(new WordDataCreateDto(" ", base.transcription(), base.nameEnglish(), base.nameRussian(), base.definition(), base.examples(), base.listOfWordPackIds(), platform));

                        invalid.add(new WordDataCreateDto(base.nameChinese(), null, base.nameEnglish(), base.nameRussian(), base.definition(), base.examples(), base.listOfWordPackIds(), platform));
                        invalid.add(new WordDataCreateDto(base.nameChinese(), "", base.nameEnglish(), base.nameRussian(), base.definition(), base.examples(), base.listOfWordPackIds(), platform));
                        invalid.add(new WordDataCreateDto(base.nameChinese(), " ", base.nameEnglish(), base.nameRussian(), base.definition(), base.examples(), base.listOfWordPackIds(), platform));

                        invalid.add(new WordDataCreateDto(base.nameChinese(), base.transcription(), null, base.nameRussian(), base.definition(), base.examples(), base.listOfWordPackIds(), platform));
                        invalid.add(new WordDataCreateDto(base.nameChinese(), base.transcription(), "", base.nameRussian(), base.definition(), base.examples(), base.listOfWordPackIds(), platform));
                        invalid.add(new WordDataCreateDto(base.nameChinese(), base.transcription(), " ", base.nameRussian(), base.definition(), base.examples(), base.listOfWordPackIds(), platform));

                        invalid.add(new WordDataCreateDto(base.nameChinese(), base.transcription(), base.nameEnglish(), null, base.definition(), base.examples(), base.listOfWordPackIds(), platform));
                        invalid.add(new WordDataCreateDto(base.nameChinese(), base.transcription(), base.nameEnglish(), "", base.definition(), base.examples(), base.listOfWordPackIds(), platform));
                        invalid.add(new WordDataCreateDto(base.nameChinese(), base.transcription(), base.nameEnglish(), " ", base.definition(), base.examples(), base.listOfWordPackIds(), platform));

                        invalid.add(new WordDataCreateDto(base.nameChinese(), base.transcription(), base.nameEnglish(), base.nameRussian(), null, base.examples(), base.listOfWordPackIds(), platform));
                        invalid.add(new WordDataCreateDto(base.nameChinese(), base.transcription(), base.nameEnglish(), base.nameRussian(), "", base.examples(), base.listOfWordPackIds(), platform));
                        invalid.add(new WordDataCreateDto(base.nameChinese(), base.transcription(), base.nameEnglish(), base.nameRussian(), " ", base.examples(), base.listOfWordPackIds(), platform));

                        invalid.add(new WordDataCreateDto(base.nameChinese(), base.transcription(), base.nameEnglish(), base.nameRussian(), base.definition(), null, base.listOfWordPackIds(), platform));
                        invalid.add(new WordDataCreateDto(base.nameChinese(), base.transcription(), base.nameEnglish(), base.nameRussian(), base.definition(), base.examples(), null, platform));
                        invalid.add(new WordDataCreateDto(base.nameChinese(), base.transcription(), base.nameEnglish(), base.nameRussian(), base.definition(), base.examples(), base.listOfWordPackIds(), null));

                        return invalid.stream()
                                .map(request -> Arguments.of(platform, request));
                    });
        }
    }
}
