package my.project.dailylexika.user.api;

import my.project.library.dailylexika.dtos.user.AuthenticationRequest;
import my.project.library.dailylexika.dtos.user.AuthenticationResponse;
import my.project.library.dailylexika.dtos.user.RegistrationRequest;
import my.project.dailylexika.config.AbstractIntegrationTest;
import my.project.dailylexika.util.MockMvcService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static my.project.library.dailylexika.enumerations.Platform.CHINESE;
import static my.project.dailylexika.util.CommonConstants.*;
import static my.project.dailylexika.util.data.TestDataUtil.generateRegistrationRequest;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class AuthenticationControllerIT extends AbstractIntegrationTest {

    @Autowired
    private MockMvcService mockMvcService;

    @Test
    void register() {
        // Given
        RegistrationRequest registrationRequest = generateRegistrationRequest(CHINESE);

        // When
        AuthenticationResponse authenticationResponse = mockMvcService
                .performPost(URI_REGISTER, registrationRequest, status().isCreated())
                .getResponse(AuthenticationResponse.class);

        // Then
        assertThat(authenticationResponse.token()).isNotNull();
        assertThat(authenticationResponse.token()).isNotEmpty();
    }

    @Test
    void login() {
        // Given
        AuthenticationRequest authenticationRequest = new AuthenticationRequest(TEST_EMAIL_CHINESE, TEST_PASSWORD, CHINESE);

        // When
        AuthenticationResponse authenticationResponse = mockMvcService
                .performPost(URI_LOGIN, authenticationRequest, status().isOk())
                .getResponse(AuthenticationResponse.class);

        // Then
        assertThat(authenticationResponse.token()).isNotNull();
        assertThat(authenticationResponse.token()).isNotEmpty();
    }
}
