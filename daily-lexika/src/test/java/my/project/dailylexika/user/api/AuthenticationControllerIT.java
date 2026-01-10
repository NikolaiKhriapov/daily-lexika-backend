package my.project.dailylexika.user.api;

import my.project.dailylexika.config.AbstractIntegrationTest;
import my.project.library.dailylexika.dtos.user.AuthenticationRequest;
import my.project.library.dailylexika.dtos.user.AuthenticationResponse;
import my.project.library.dailylexika.dtos.user.RegistrationRequest;
import my.project.library.dailylexika.enumerations.Platform;
import my.project.library.util.exception.ApiErrorDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.UUID;
import java.util.stream.Stream;

import static my.project.library.dailylexika.enumerations.Platform.CHINESE;
import static my.project.library.dailylexika.enumerations.Platform.ENGLISH;
import static my.project.dailylexika.util.TestDataFactory.buildUniqueEmail;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AuthenticationControllerIT extends AbstractIntegrationTest {

    private static final String URI_REGISTER = "/api/v1/auth/register";
    private static final String URI_LOGIN = "/api/v1/auth/login";
    private static final String DEFAULT_NAME = "Test User";
    private static final String DEFAULT_PASSWORD = "Pass123!";

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @ParameterizedTest
    @MethodSource("my.project.dailylexika.user.api.AuthenticationControllerIT$TestDataSource#register_createsUser_returnsCreatedToken")
    void register_createsUser_returnsCreatedToken(Platform platform) throws Exception {
        // AuthenticationController.register
        RegistrationRequest registrationRequest = new RegistrationRequest(DEFAULT_NAME, buildUniqueEmail(), DEFAULT_PASSWORD, platform);
        MvcResult result = mockMvc
                .perform(
                        post(URI_REGISTER)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(registrationRequest))
                )
                .andExpect(status().isCreated())
                .andReturn();
        AuthenticationResponse authenticationResponse = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                AuthenticationResponse.class
        );

        // Assertions
        assertThat(authenticationResponse.token()).isNotBlank();
    }

    @ParameterizedTest
    @MethodSource("my.project.dailylexika.user.api.AuthenticationControllerIT$TestDataSource#register_existingUserNewPlatform_returnsCreatedToken")
    void register_existingUserNewPlatform_returnsCreatedToken(Platform registeredPlatform, Platform newPlatform) throws Exception {
        // AuthenticationController.register
        String email = buildUniqueEmail();
        RegistrationRequest registrationRequest1 = new RegistrationRequest(DEFAULT_NAME, email, DEFAULT_PASSWORD, registeredPlatform);
        mockMvc.perform(
                        post(URI_REGISTER)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(registrationRequest1))
                )
                .andExpect(status().isCreated())
                .andReturn();

        // AuthenticationController.register
        RegistrationRequest registrationRequest2 = new RegistrationRequest(DEFAULT_NAME, email, DEFAULT_PASSWORD, newPlatform);
        MvcResult result = mockMvc
                .perform(
                        post(URI_REGISTER)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(registrationRequest2))
                )
                .andExpect(status().isCreated())
                .andReturn();
        AuthenticationResponse authenticationResponse = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                AuthenticationResponse.class
        );

        // Assertions
        assertThat(authenticationResponse.token()).isNotBlank();
    }

    @ParameterizedTest
    @MethodSource("my.project.dailylexika.user.api.AuthenticationControllerIT$TestDataSource#register_existingUserSamePlatform_conflict")
    void register_existingUserSamePlatform_conflict(Platform platform) throws Exception {
        // AuthenticationController.register
        String email = buildUniqueEmail();
        RegistrationRequest registrationRequest1 = new RegistrationRequest(DEFAULT_NAME, email, DEFAULT_PASSWORD, platform);
        mockMvc.perform(
                        post(URI_REGISTER)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(registrationRequest1))
                )
                .andExpect(status().isCreated())
                .andReturn();

        // AuthenticationController.register
        RegistrationRequest registrationRequest2 = new RegistrationRequest(DEFAULT_NAME, email, DEFAULT_PASSWORD, platform);
        MvcResult result = mockMvc
                .perform(
                        post(URI_REGISTER)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(registrationRequest2))
                )
                .andExpect(status().isConflict())
                .andReturn();
        ApiErrorDTO error = objectMapper.readValue(result.getResponse().getContentAsString(), ApiErrorDTO.class);

        // Assertions
        assertThat(error.statusCode()).isEqualTo(HttpStatus.CONFLICT.value());
        assertThat(error.path()).isEqualTo(URI_REGISTER);
    }

    @ParameterizedTest
    @MethodSource("my.project.dailylexika.user.api.AuthenticationControllerIT$TestDataSource#register_existingUserWrongPassword_badRequest")
    void register_existingUserWrongPassword_badRequest(Platform platform) throws Exception {
        // AuthenticationController.register
        String email = buildUniqueEmail();
        RegistrationRequest registrationRequest1 = new RegistrationRequest(DEFAULT_NAME, email, DEFAULT_PASSWORD, platform);
        mockMvc.perform(
                        post(URI_REGISTER)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(registrationRequest1))
                )
                .andExpect(status().isCreated())
                .andReturn();

        // AuthenticationController.register
        RegistrationRequest registrationRequest2 = new RegistrationRequest(DEFAULT_NAME, email, "wrongPass1", platform);
        MvcResult result = mockMvc
                .perform(
                        post(URI_REGISTER)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(registrationRequest2))
                )
                .andExpect(status().isBadRequest())
                .andReturn();
        ApiErrorDTO error = objectMapper.readValue(result.getResponse().getContentAsString(), ApiErrorDTO.class);

        // Assertions
        assertThat(error.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(error.path()).isEqualTo(URI_REGISTER);
    }

    @ParameterizedTest
    @MethodSource("my.project.dailylexika.user.api.AuthenticationControllerIT$TestDataSource#register_validation_missingOrBlankFields_badRequest")
    void register_validation_missingOrBlankFields_badRequest(RegistrationRequest registrationRequest) throws Exception {
        // AuthenticationController.register
        MvcResult result = mockMvc
                .perform(
                        post(URI_REGISTER)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(registrationRequest))
                )
                .andExpect(status().isBadRequest())
                .andReturn();
        ApiErrorDTO error = objectMapper.readValue(result.getResponse().getContentAsString(), ApiErrorDTO.class);

        // Assertions
        assertThat(error.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(error.path()).isEqualTo(URI_REGISTER);
    }

    @ParameterizedTest
    @MethodSource("my.project.dailylexika.user.api.AuthenticationControllerIT$TestDataSource#register_emailCaseInsensitive_allowsLogin")
    void register_emailCaseInsensitive_allowsLogin(Platform platform) throws Exception {
        // AuthenticationController.register
        String mixedCaseEmail = "UserCase" + UUID.randomUUID() + "@Test.com";
        RegistrationRequest registrationRequest = new RegistrationRequest(DEFAULT_NAME, mixedCaseEmail, DEFAULT_PASSWORD, platform);
        mockMvc.perform(
                        post(URI_REGISTER)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(registrationRequest))
                )
                .andExpect(status().isCreated())
                .andReturn();

        // AuthenticationController.login
        AuthenticationRequest authenticationRequest = new AuthenticationRequest(mixedCaseEmail.toLowerCase(), DEFAULT_PASSWORD, platform);
        MvcResult result = mockMvc
                .perform(
                        post(URI_LOGIN)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(authenticationRequest))
                )
                .andExpect(status().isOk())
                .andReturn();
        AuthenticationResponse authenticationResponse = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                AuthenticationResponse.class
        );

        // Assertions
        assertThat(authenticationResponse.token()).isNotBlank();
    }

    @ParameterizedTest
    @MethodSource("my.project.dailylexika.user.api.AuthenticationControllerIT$TestDataSource#login_success_returnsOkToken")
    void login_success_returnsOkToken(Platform platform) throws Exception {
        // AuthenticationController.register
        String email = buildUniqueEmail();
        RegistrationRequest registrationRequest = new RegistrationRequest(DEFAULT_NAME, email, DEFAULT_PASSWORD, platform);
        mockMvc.perform(
                        post(URI_REGISTER)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(registrationRequest))
                )
                .andExpect(status().isCreated())
                .andReturn();

        // AuthenticationController.login
        AuthenticationRequest authenticationRequest = new AuthenticationRequest(email, DEFAULT_PASSWORD, platform);
        MvcResult result = mockMvc
                .perform(
                        post(URI_LOGIN)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(authenticationRequest))
                )
                .andExpect(status().isOk())
                .andReturn();
        AuthenticationResponse authenticationResponse = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                AuthenticationResponse.class
        );

        // Assertions
        assertThat(authenticationResponse.token()).isNotBlank();
    }

    @ParameterizedTest
    @MethodSource("my.project.dailylexika.user.api.AuthenticationControllerIT$TestDataSource#login_wrongPassword_badRequest")
    void login_wrongPassword_badRequest(Platform platform) throws Exception {
        // AuthenticationController.register
        String email = buildUniqueEmail();
        RegistrationRequest registrationRequest = new RegistrationRequest(DEFAULT_NAME, email, DEFAULT_PASSWORD, platform);
        mockMvc.perform(
                        post(URI_REGISTER)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(registrationRequest))
                )
                .andExpect(status().isCreated())
                .andReturn();

        // AuthenticationController.login
        AuthenticationRequest authenticationRequest = new AuthenticationRequest(email, "wrongPass1", platform);
        MvcResult result = mockMvc
                .perform(
                        post(URI_LOGIN)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(authenticationRequest))
                )
                .andExpect(status().isBadRequest())
                .andReturn();
        ApiErrorDTO error = objectMapper.readValue(result.getResponse().getContentAsString(), ApiErrorDTO.class);

        // Assertions
        assertThat(error.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(error.path()).isEqualTo(URI_LOGIN);
    }

    @ParameterizedTest
    @MethodSource("my.project.dailylexika.user.api.AuthenticationControllerIT$TestDataSource#login_userNotRegisteredOnPlatform_notFound")
    void login_userNotRegisteredOnPlatform_notFound(Platform registeredPlatform, Platform loginPlatform) throws Exception {
        // AuthenticationController.register
        String email = buildUniqueEmail();
        RegistrationRequest registrationRequest = new RegistrationRequest(DEFAULT_NAME, email, DEFAULT_PASSWORD, registeredPlatform);
        mockMvc.perform(
                        post(URI_REGISTER)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(registrationRequest))
                )
                .andExpect(status().isCreated())
                .andReturn();

        // AuthenticationController.login
        AuthenticationRequest authenticationRequest = new AuthenticationRequest(email, DEFAULT_PASSWORD, loginPlatform);
        MvcResult result = mockMvc
                .perform(
                        post(URI_LOGIN)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(authenticationRequest))
                )
                .andExpect(status().isNotFound())
                .andReturn();
        ApiErrorDTO error = objectMapper.readValue(result.getResponse().getContentAsString(), ApiErrorDTO.class);

        // Assertions
        assertThat(error.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
        assertThat(error.path()).isEqualTo(URI_LOGIN);
    }

    @ParameterizedTest
    @MethodSource("my.project.dailylexika.user.api.AuthenticationControllerIT$TestDataSource#login_validation_missingOrBlankFields_badRequest")
    void login_validation_missingOrBlankFields_badRequest(AuthenticationRequest authenticationRequest) throws Exception {
        // AuthenticationController.login
        MvcResult result = mockMvc.perform(
                        post(URI_LOGIN)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(authenticationRequest))
                )
                .andExpect(status().isBadRequest())
                .andReturn();
        ApiErrorDTO error = objectMapper.readValue(result.getResponse().getContentAsString(), ApiErrorDTO.class);

        // Assertions
        assertThat(error.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(error.path()).isEqualTo(URI_LOGIN);
    }

    @ParameterizedTest
    @MethodSource("my.project.dailylexika.user.api.AuthenticationControllerIT$TestDataSource#login_emailCaseInsensitive_returnsOkToken")
    void login_emailCaseInsensitive_returnsOkToken(Platform platform) throws Exception {
        // AuthenticationController.register
        String email = buildUniqueEmail();
        RegistrationRequest registrationRequest = new RegistrationRequest(DEFAULT_NAME, email, DEFAULT_PASSWORD, platform);
        mockMvc.perform(
                        post(URI_REGISTER)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(registrationRequest))
                )
                .andExpect(status().isCreated())
                .andReturn();

        // AuthenticationController.login
        AuthenticationRequest authenticationRequest = new AuthenticationRequest(email.toUpperCase(), DEFAULT_PASSWORD, platform);
        MvcResult result = mockMvc
                .perform(
                        post(URI_LOGIN)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(authenticationRequest))
                )
                .andExpect(status().isOk())
                .andReturn();
        AuthenticationResponse authenticationResponse = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                AuthenticationResponse.class
        );

        // Assertions
        assertThat(authenticationResponse.token()).isNotBlank();
    }

    static class TestDataSource {
        static Stream<Platform> register_createsUser_returnsCreatedToken() {
            return Stream.of(ENGLISH, CHINESE);
        }

        static Stream<Platform> register_existingUserSamePlatform_conflict() {
            return Stream.of(ENGLISH, CHINESE);
        }

        static Stream<Arguments> register_existingUserNewPlatform_returnsCreatedToken() {
            return Stream.of(
                    Arguments.of(ENGLISH, CHINESE),
                    Arguments.of(CHINESE, ENGLISH)
            );
        }

        static Stream<Platform> register_existingUserWrongPassword_badRequest() {
            return Stream.of(ENGLISH, CHINESE);
        }

        static Stream<RegistrationRequest> register_validation_missingOrBlankFields_badRequest() {
            Stream<RegistrationRequest> platformSpecific = Stream.of(ENGLISH, CHINESE)
                    .flatMap(platform -> Stream.of(
                            new RegistrationRequest(null, "valid@test.com", DEFAULT_PASSWORD, platform),
                            new RegistrationRequest("", "valid@test.com", DEFAULT_PASSWORD, platform),
                            new RegistrationRequest(" ", "valid@test.com", DEFAULT_PASSWORD, platform),
                            new RegistrationRequest(DEFAULT_NAME, null, DEFAULT_PASSWORD, platform),
                            new RegistrationRequest(DEFAULT_NAME, "", DEFAULT_PASSWORD, platform),
                            new RegistrationRequest(DEFAULT_NAME, " ", DEFAULT_PASSWORD, platform),
                            new RegistrationRequest(DEFAULT_NAME, "valid@test.com", null, platform),
                            new RegistrationRequest(DEFAULT_NAME, "valid@test.com", "", platform),
                            new RegistrationRequest(DEFAULT_NAME, "valid@test.com", " ", platform)
                    ));

            return Stream.concat(
                    platformSpecific,
                    Stream.of(new RegistrationRequest(DEFAULT_NAME, "valid@test.com", DEFAULT_PASSWORD, null))
            );
        }

        static Stream<Platform> register_emailCaseInsensitive_allowsLogin() {
            return Stream.of(ENGLISH, CHINESE);
        }

        static Stream<Platform> login_success_returnsOkToken() {
            return Stream.of(ENGLISH, CHINESE);
        }

        static Stream<Platform> login_wrongPassword_badRequest() {
            return Stream.of(ENGLISH, CHINESE);
        }

        static Stream<AuthenticationRequest> login_validation_missingOrBlankFields_badRequest() {
            Stream<AuthenticationRequest> platformSpecific = Stream.of(ENGLISH, CHINESE)
                    .flatMap(platform -> Stream.of(
                            new AuthenticationRequest(null, DEFAULT_PASSWORD, platform),
                            new AuthenticationRequest("", DEFAULT_PASSWORD, platform),
                            new AuthenticationRequest(" ", DEFAULT_PASSWORD, platform),
                            new AuthenticationRequest("valid@test.com", null, platform),
                            new AuthenticationRequest("valid@test.com", "", platform),
                            new AuthenticationRequest("valid@test.com", " ", platform)
                    ));

            return Stream.concat(
                    platformSpecific,
                    Stream.of(new AuthenticationRequest("valid@test.com", DEFAULT_PASSWORD, null))
            );
        }

        static Stream<Arguments> login_userNotRegisteredOnPlatform_notFound() {
            return Stream.of(
                    Arguments.of(ENGLISH, CHINESE),
                    Arguments.of(CHINESE, ENGLISH)
            );
        }

        static Stream<Platform> login_emailCaseInsensitive_returnsOkToken() {
            return Stream.of(ENGLISH, CHINESE);
        }
    }
}
