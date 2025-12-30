package my.project.dailylexika.user.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import my.project.dailylexika.config.AbstractIntegrationTest;
import my.project.dailylexika.user.model.entities.RoleStatistics;
import my.project.dailylexika.user.persistence.UserRepository;
import my.project.library.dailylexika.dtos.user.AccountDeletionRequest;
import my.project.library.dailylexika.dtos.user.AuthenticationRequest;
import my.project.library.dailylexika.dtos.user.AuthenticationResponse;
import my.project.library.dailylexika.dtos.user.PasswordUpdateRequest;
import my.project.library.dailylexika.dtos.user.RegistrationRequest;
import my.project.library.dailylexika.dtos.user.UserDto;
import my.project.library.dailylexika.enumerations.Language;
import my.project.library.dailylexika.enumerations.Platform;
import my.project.library.dailylexika.enumerations.RoleName;
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
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import static my.project.library.admin.enumerations.RoleName.SUPER_ADMIN;
import static my.project.library.dailylexika.enumerations.Platform.CHINESE;
import static my.project.library.dailylexika.enumerations.Platform.ENGLISH;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
class UserControllerIT extends AbstractIntegrationTest {

    private static final String URI_REGISTER = "/api/v1/auth/register";
    private static final String URI_LOGIN = "/api/v1/auth/login";
    private static final String URI_USER_INFO = "/api/v1/users/info";
    private static final String URI_USER_PASSWORD = "/api/v1/users/password";
    private static final String URI_USERS = "/api/v1/users";
    private static final String DEFAULT_NAME = "Test User";
    private static final String UPDATED_NAME = "Updated User";
    private static final String DEFAULT_PASSWORD = "Pass123!";
    private static final String NEW_PASSWORD = "NewPass123!";
    private static final String ADMIN_SUBJECT = "admin@test.com";

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private UserRepository userRepository;

    @Test
    void getUser_unauthenticated_unauthorized() throws Exception {
        mockMvc.perform(get(URI_USER_INFO))
                .andExpect(status().isUnauthorized());
    }

    @ParameterizedTest
    @MethodSource("my.project.dailylexika.user.api.UserControllerIT$TestDataSource#getUser_authenticated_returnsUser")
    void getUser_authenticated_returnsUser(Platform platform) throws Exception {
        String email = uniqueEmail();
        MvcResult registerResult = mockMvc.perform(
                        post(URI_REGISTER)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(
                                        new RegistrationRequest(DEFAULT_NAME, email, DEFAULT_PASSWORD, platform)
                                ))
                )
                .andExpect(status().isCreated())
                .andReturn();
        AuthenticationResponse registerResponse = objectMapper.readValue(
                registerResult.getResponse().getContentAsString(),
                AuthenticationResponse.class
        );

        MvcResult result = mockMvc.perform(
                        get(URI_USER_INFO)
                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + registerResponse.token())
                )
                .andExpect(status().isOk())
                .andReturn();
        UserDto userDto = objectMapper.readValue(result.getResponse().getContentAsString(), UserDto.class);

        assertThat(userDto.name()).isEqualTo(DEFAULT_NAME);
        assertThat(userDto.email()).isEqualTo(email.toLowerCase());
        assertThat(userDto.role()).isEqualTo(roleNameFor(platform));
        assertThat(userDto.id()).isNotNull();
    }

    @Test
    void updateUserInfo_unauthenticated_unauthorized() throws Exception {
        UserDto request = new UserDto(null, UPDATED_NAME, "update@test.com", null, null, null, null, null);

        mockMvc.perform(
                        patch(URI_USER_INFO)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isUnauthorized());
    }

    @ParameterizedTest
    @MethodSource("my.project.dailylexika.user.api.UserControllerIT$TestDataSource#updateUserInfo_updatesEmailLowercase_returnsUser")
    void updateUserInfo_updatesEmailLowercase_returnsUser(Platform platform) throws Exception {
        String email = uniqueEmail();
        MvcResult registerResult = mockMvc.perform(
                        post(URI_REGISTER)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(
                                        new RegistrationRequest(DEFAULT_NAME, email, DEFAULT_PASSWORD, platform)
                                ))
                )
                .andExpect(status().isCreated())
                .andReturn();
        AuthenticationResponse registerResponse = objectMapper.readValue(
                registerResult.getResponse().getContentAsString(),
                AuthenticationResponse.class
        );

        String updatedEmail = "Updated" + UUID.randomUUID() + "@Test.com";
        UserDto request = new UserDto(
                null,
                UPDATED_NAME,
                updatedEmail,
                null,
                null,
                Language.ENGLISH,
                Language.RUSSIAN,
                null
        );

        MvcResult result = mockMvc.perform(
                        patch(URI_USER_INFO)
                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + registerResponse.token())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isOk())
                .andReturn();
        UserDto userDto = objectMapper.readValue(result.getResponse().getContentAsString(), UserDto.class);

        assertThat(userDto.name()).isEqualTo(UPDATED_NAME);
        assertThat(userDto.email()).isEqualTo(updatedEmail.toLowerCase());
        assertThat(userRepository.existsByEmail(updatedEmail.toLowerCase())).isTrue();
    }

    @ParameterizedTest
    @MethodSource("my.project.dailylexika.user.api.UserControllerIT$TestDataSource#updateUserInfo_validation_missingOrBlankFields_badRequest")
    void updateUserInfo_validation_missingOrBlankFields_badRequest(Platform platform, UserDto request) throws Exception {
        String email = uniqueEmail();
        MvcResult registerResult = mockMvc.perform(
                        post(URI_REGISTER)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(
                                        new RegistrationRequest(DEFAULT_NAME, email, DEFAULT_PASSWORD, platform)
                                ))
                )
                .andExpect(status().isCreated())
                .andReturn();
        AuthenticationResponse registerResponse = objectMapper.readValue(
                registerResult.getResponse().getContentAsString(),
                AuthenticationResponse.class
        );

        MvcResult result = mockMvc.perform(
                        patch(URI_USER_INFO)
                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + registerResponse.token())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isBadRequest())
                .andReturn();
        ApiErrorDTO error = objectMapper.readValue(result.getResponse().getContentAsString(), ApiErrorDTO.class);

        assertThat(error.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(error.path()).isEqualTo(URI_USER_INFO);
    }

    @Test
    void updatePassword_unauthenticated_unauthorized() throws Exception {
        PasswordUpdateRequest request = new PasswordUpdateRequest(DEFAULT_PASSWORD, NEW_PASSWORD);

        mockMvc.perform(
                        patch(URI_USER_PASSWORD)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isUnauthorized());
    }

    @ParameterizedTest
    @MethodSource("my.project.dailylexika.user.api.UserControllerIT$TestDataSource#updatePassword_success_changesPassword")
    void updatePassword_success_changesPassword(Platform platform) throws Exception {
        String email = uniqueEmail();
        MvcResult registerResult = mockMvc.perform(
                        post(URI_REGISTER)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(
                                        new RegistrationRequest(DEFAULT_NAME, email, DEFAULT_PASSWORD, platform)
                                ))
                )
                .andExpect(status().isCreated())
                .andReturn();
        AuthenticationResponse registerResponse = objectMapper.readValue(
                registerResult.getResponse().getContentAsString(),
                AuthenticationResponse.class
        );

        PasswordUpdateRequest request = new PasswordUpdateRequest(DEFAULT_PASSWORD, NEW_PASSWORD);
        mockMvc.perform(
                        patch(URI_USER_PASSWORD)
                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + registerResponse.token())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isOk());

        mockMvc.perform(
                        post(URI_LOGIN)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(
                                        new AuthenticationRequest(email, NEW_PASSWORD, platform)
                                ))
                )
                .andExpect(status().isOk());

        mockMvc.perform(
                        post(URI_LOGIN)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(
                                        new AuthenticationRequest(email, DEFAULT_PASSWORD, platform)
                                ))
                )
                .andExpect(status().isBadRequest());
    }

    @ParameterizedTest
    @MethodSource("my.project.dailylexika.user.api.UserControllerIT$TestDataSource#updatePassword_wrongPassword_badRequest")
    void updatePassword_wrongPassword_badRequest(Platform platform) throws Exception {
        String email = uniqueEmail();
        MvcResult registerResult = mockMvc.perform(
                        post(URI_REGISTER)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(
                                        new RegistrationRequest(DEFAULT_NAME, email, DEFAULT_PASSWORD, platform)
                                ))
                )
                .andExpect(status().isCreated())
                .andReturn();
        AuthenticationResponse registerResponse = objectMapper.readValue(
                registerResult.getResponse().getContentAsString(),
                AuthenticationResponse.class
        );

        PasswordUpdateRequest request = new PasswordUpdateRequest("wrong-pass", NEW_PASSWORD);
        mockMvc.perform(
                        patch(URI_USER_PASSWORD)
                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + registerResponse.token())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isBadRequest());
    }

    @ParameterizedTest
    @MethodSource("my.project.dailylexika.user.api.UserControllerIT$TestDataSource#updatePassword_validation_missingOrBlankFields_badRequest")
    void updatePassword_validation_missingOrBlankFields_badRequest(Platform platform, PasswordUpdateRequest request) throws Exception {
        String email = uniqueEmail();
        MvcResult registerResult = mockMvc.perform(
                        post(URI_REGISTER)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(
                                        new RegistrationRequest(DEFAULT_NAME, email, DEFAULT_PASSWORD, platform)
                                ))
                )
                .andExpect(status().isCreated())
                .andReturn();
        AuthenticationResponse registerResponse = objectMapper.readValue(
                registerResult.getResponse().getContentAsString(),
                AuthenticationResponse.class
        );

        MvcResult result = mockMvc.perform(
                        patch(URI_USER_PASSWORD)
                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + registerResponse.token())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isBadRequest())
                .andReturn();
        ApiErrorDTO error = objectMapper.readValue(result.getResponse().getContentAsString(), ApiErrorDTO.class);

        assertThat(error.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(error.path()).isEqualTo(URI_USER_PASSWORD);
    }

    @Test
    void deleteAccount_unauthenticated_unauthorized() throws Exception {
        AccountDeletionRequest request = new AccountDeletionRequest(DEFAULT_PASSWORD);

        mockMvc.perform(
                        delete(URI_USERS)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isUnauthorized());
    }

    @ParameterizedTest
    @MethodSource("my.project.dailylexika.user.api.UserControllerIT$TestDataSource#deleteAccount_singleRole_deletesUser")
    void deleteAccount_singleRole_deletesUser(Platform platform) throws Exception {
        String email = uniqueEmail();
        MvcResult registerResult = mockMvc.perform(
                        post(URI_REGISTER)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(
                                        new RegistrationRequest(DEFAULT_NAME, email, DEFAULT_PASSWORD, platform)
                                ))
                )
                .andExpect(status().isCreated())
                .andReturn();
        AuthenticationResponse registerResponse = objectMapper.readValue(
                registerResult.getResponse().getContentAsString(),
                AuthenticationResponse.class
        );

        AccountDeletionRequest request = new AccountDeletionRequest(DEFAULT_PASSWORD);
        mockMvc.perform(
                        delete(URI_USERS)
                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + registerResponse.token())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isNoContent());

        assertThat(userRepository.existsByEmail(email.toLowerCase())).isFalse();
    }

    @ParameterizedTest
    @MethodSource("my.project.dailylexika.user.api.UserControllerIT$TestDataSource#deleteAccount_multiRole_removesOnlyCurrentRole")
    void deleteAccount_multiRole_removesOnlyCurrentRole(Platform platformToDelete) throws Exception {
        String email = uniqueEmail();
        mockMvc.perform(
                        post(URI_REGISTER)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(
                                        new RegistrationRequest(DEFAULT_NAME, email, DEFAULT_PASSWORD, ENGLISH)
                                ))
                )
                .andExpect(status().isCreated())
                .andReturn();
        mockMvc.perform(
                        post(URI_REGISTER)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(
                                        new RegistrationRequest(DEFAULT_NAME, email, DEFAULT_PASSWORD, CHINESE)
                                ))
                )
                .andExpect(status().isCreated())
                .andReturn();

        MvcResult loginResult = mockMvc.perform(
                        post(URI_LOGIN)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(
                                        new AuthenticationRequest(email, DEFAULT_PASSWORD, platformToDelete)
                                ))
                )
                .andExpect(status().isOk())
                .andReturn();
        AuthenticationResponse loginResponse = objectMapper.readValue(
                loginResult.getResponse().getContentAsString(),
                AuthenticationResponse.class
        );

        AccountDeletionRequest request = new AccountDeletionRequest(DEFAULT_PASSWORD);
        mockMvc.perform(
                        delete(URI_USERS)
                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + loginResponse.token())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isNoContent());

        List<RoleName> remainingRoles = userRepository.findUserByEmail(email.toLowerCase())
                .orElseThrow()
                .getRoleStatistics()
                .stream()
                .map(RoleStatistics::getRoleName)
                .toList();
        RoleName deletedRole = roleNameFor(platformToDelete);
        RoleName expectedRole = roleNameFor(platformToDelete == ENGLISH ? CHINESE : ENGLISH);

        assertThat(remainingRoles).hasSize(1);
        assertThat(remainingRoles).doesNotContain(deletedRole);
        assertThat(remainingRoles).contains(expectedRole);
    }

    @ParameterizedTest
    @MethodSource("my.project.dailylexika.user.api.UserControllerIT$TestDataSource#deleteAccount_wrongPassword_badRequest")
    void deleteAccount_wrongPassword_badRequest(Platform platform) throws Exception {
        String email = uniqueEmail();
        MvcResult registerResult = mockMvc.perform(
                        post(URI_REGISTER)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(
                                        new RegistrationRequest(DEFAULT_NAME, email, DEFAULT_PASSWORD, platform)
                                ))
                )
                .andExpect(status().isCreated())
                .andReturn();
        AuthenticationResponse registerResponse = objectMapper.readValue(
                registerResult.getResponse().getContentAsString(),
                AuthenticationResponse.class
        );

        AccountDeletionRequest request = new AccountDeletionRequest("wrong-pass");
        mockMvc.perform(
                        delete(URI_USERS)
                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + registerResponse.token())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isBadRequest());
    }

    @ParameterizedTest
    @MethodSource("my.project.dailylexika.user.api.UserControllerIT$TestDataSource#deleteAccount_validation_missingOrBlankFields_badRequest")
    void deleteAccount_validation_missingOrBlankFields_badRequest(Platform platform, AccountDeletionRequest request) throws Exception {
        String email = uniqueEmail();
        MvcResult registerResult = mockMvc.perform(
                        post(URI_REGISTER)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(
                                        new RegistrationRequest(DEFAULT_NAME, email, DEFAULT_PASSWORD, platform)
                                ))
                )
                .andExpect(status().isCreated())
                .andReturn();
        AuthenticationResponse registerResponse = objectMapper.readValue(
                registerResult.getResponse().getContentAsString(),
                AuthenticationResponse.class
        );

        MvcResult result = mockMvc.perform(
                        delete(URI_USERS)
                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + registerResponse.token())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isBadRequest())
                .andReturn();
        ApiErrorDTO error = objectMapper.readValue(result.getResponse().getContentAsString(), ApiErrorDTO.class);

        assertThat(error.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(error.path()).isEqualTo(URI_USERS);
    }

    @Test
    void getPageOfUsers_unauthenticated_unauthorized() throws Exception {
        mockMvc.perform(
                        get(URI_USERS)
                                .param("page", "0")
                                .param("size", "2"))
                .andExpect(status().isUnauthorized());
    }

    @ParameterizedTest
    @MethodSource("my.project.dailylexika.user.api.UserControllerIT$TestDataSource#getPageOfUsers_nonAdmin_forbidden")
    void getPageOfUsers_nonAdmin_forbidden(Platform platform) throws Exception {
        String email = uniqueEmail();
        MvcResult registerResult = mockMvc.perform(
                        post(URI_REGISTER)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(
                                        new RegistrationRequest(DEFAULT_NAME, email, DEFAULT_PASSWORD, platform)
                                ))
                )
                .andExpect(status().isCreated())
                .andReturn();
        AuthenticationResponse registerResponse = objectMapper.readValue(
                registerResult.getResponse().getContentAsString(),
                AuthenticationResponse.class
        );

        mockMvc.perform(
                        get(URI_USERS)
                                .param("page", "0")
                                .param("size", "2")
                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + registerResponse.token())
                )
                .andExpect(status().isForbidden());
    }

    @Test
    void getPageOfUsers_missingPageOrSize_badRequest() throws Exception {
        String adminToken = jwtService.generateToken(ADMIN_SUBJECT, SUPER_ADMIN.name());

        mockMvc.perform(
                        get(URI_USERS)
                                .param("size", "2")
                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken)
                )
                .andExpect(status().isBadRequest());

        mockMvc.perform(
                        get(URI_USERS)
                                .param("page", "0")
                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken)
                )
                .andExpect(status().isBadRequest());
    }

    @ParameterizedTest
    @MethodSource("my.project.dailylexika.user.api.UserControllerIT$TestDataSource#getPageOfUsers_superAdmin_returnsPage")
    void getPageOfUsers_superAdmin_returnsPage(String sortDirection) throws Exception {
        String emailOne = uniqueEmail();
        String emailTwo = uniqueEmail();
        mockMvc.perform(
                        post(URI_REGISTER)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(
                                        new RegistrationRequest(DEFAULT_NAME, emailOne, DEFAULT_PASSWORD, ENGLISH)
                                ))
                )
                .andExpect(status().isCreated())
                .andReturn();
        mockMvc.perform(
                        post(URI_REGISTER)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(
                                        new RegistrationRequest(DEFAULT_NAME, emailTwo, DEFAULT_PASSWORD, CHINESE)
                                ))
                )
                .andExpect(status().isCreated())
                .andReturn();

        String adminToken = jwtService.generateToken(ADMIN_SUBJECT, SUPER_ADMIN.name());
        MvcResult result = mockMvc.perform(
                        get(URI_USERS)
                                .param("page", "0")
                                .param("size", "2")
                                .param("sort", sortDirection)
                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken)
                )
                .andExpect(status().isOk())
                .andReturn();

        CustomPageImpl<UserDto> page = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {});
        List<Integer> ids = page.getContent().stream()
                .map(UserDto::id)
                .toList();

        assertThat(ids).hasSize(2);
        if ("desc".equalsIgnoreCase(sortDirection)) {
            assertThat(ids).isSortedAccordingTo(Comparator.reverseOrder());
        } else {
            assertThat(ids).isSorted();
        }
    }

    private static String uniqueEmail() {
        return "user-" + UUID.randomUUID() + "@test.com";
    }

    private static RoleName roleNameFor(Platform platform) {
        return platform == ENGLISH ? RoleName.USER_ENGLISH : RoleName.USER_CHINESE;
    }

    static class TestDataSource {
        static Stream<Platform> getUser_authenticated_returnsUser() {
            return Stream.of(ENGLISH, CHINESE);
        }

        static Stream<Platform> updateUserInfo_updatesEmailLowercase_returnsUser() {
            return Stream.of(ENGLISH, CHINESE);
        }

        static Stream<Arguments> updateUserInfo_validation_missingOrBlankFields_badRequest() {
            return Stream.of(ENGLISH, CHINESE)
                    .flatMap(platform -> Stream.of(
                            Arguments.of(platform, new UserDto(null, null, "valid@test.com", null, null, null, null, null)),
                            Arguments.of(platform, new UserDto(null, "", "valid@test.com", null, null, null, null, null)),
                            Arguments.of(platform, new UserDto(null, " ", "valid@test.com", null, null, null, null, null)),
                            Arguments.of(platform, new UserDto(null, "Name", null, null, null, null, null, null)),
                            Arguments.of(platform, new UserDto(null, "Name", "", null, null, null, null, null)),
                            Arguments.of(platform, new UserDto(null, "Name", " ", null, null, null, null, null))
                    ));
        }

        static Stream<Platform> updatePassword_success_changesPassword() {
            return Stream.of(ENGLISH, CHINESE);
        }

        static Stream<Platform> updatePassword_wrongPassword_badRequest() {
            return Stream.of(ENGLISH, CHINESE);
        }

        static Stream<Arguments> updatePassword_validation_missingOrBlankFields_badRequest() {
            return Stream.of(ENGLISH, CHINESE)
                    .flatMap(platform -> Stream.of(
                            Arguments.of(platform, new PasswordUpdateRequest(null, NEW_PASSWORD)),
                            Arguments.of(platform, new PasswordUpdateRequest("", NEW_PASSWORD)),
                            Arguments.of(platform, new PasswordUpdateRequest(" ", NEW_PASSWORD)),
                            Arguments.of(platform, new PasswordUpdateRequest(DEFAULT_PASSWORD, null)),
                            Arguments.of(platform, new PasswordUpdateRequest(DEFAULT_PASSWORD, "")),
                            Arguments.of(platform, new PasswordUpdateRequest(DEFAULT_PASSWORD, " "))
                    ));
        }

        static Stream<Platform> deleteAccount_singleRole_deletesUser() {
            return Stream.of(ENGLISH, CHINESE);
        }

        static Stream<Platform> deleteAccount_multiRole_removesOnlyCurrentRole() {
            return Stream.of(ENGLISH, CHINESE);
        }

        static Stream<Platform> deleteAccount_wrongPassword_badRequest() {
            return Stream.of(ENGLISH, CHINESE);
        }

        static Stream<Arguments> deleteAccount_validation_missingOrBlankFields_badRequest() {
            return Stream.of(ENGLISH, CHINESE)
                    .flatMap(platform -> Stream.of(
                            Arguments.of(platform, new AccountDeletionRequest(null)),
                            Arguments.of(platform, new AccountDeletionRequest("")),
                            Arguments.of(platform, new AccountDeletionRequest(" "))
                    ));
        }

        static Stream<Platform> getPageOfUsers_nonAdmin_forbidden() {
            return Stream.of(ENGLISH, CHINESE);
        }

        static Stream<Arguments> getPageOfUsers_superAdmin_returnsPage() {
            return Stream.of(
                    Arguments.of("desc"),
                    Arguments.of("asc"),
                    Arguments.of("invalid")
            );
        }
    }
}
