package my.project.controllers.user;

import my.project.models.dto.user.AuthenticationRequest;
import my.project.models.dto.user.AuthenticationResponse;
import my.project.models.dto.user.RegistrationRequest;
import my.project.config.AbstractIntegrationTest;
import my.project.util.MockMvcService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static my.project.models.entity.enumeration.Platform.CHINESE;
import static my.project.util.CommonConstants.*;
import static my.project.util.data.TestDataUtil.*;
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
