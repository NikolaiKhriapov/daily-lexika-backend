package my.project.services.user;

import my.project.exception.ResourceAlreadyExistsException;
import my.project.exception.ResourceNotFoundException;
import my.project.models.entities.enumerations.Platform;
import my.project.models.entities.user.RoleName;
import my.project.models.entities.user.RoleStatistics;
import my.project.models.entities.user.User;
import my.project.config.AbstractUnitTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.springframework.context.MessageSource;

import java.util.Set;
import java.util.stream.Collectors;

import static my.project.util.data.TestDataUtil.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.BDDMockito.then;

class RoleServiceTest extends AbstractUnitTest {

    private RoleService underTest;
    @Mock
    private MessageSource messageSource;

    @BeforeEach
    void setUp() {
        underTest = new RoleService(messageSource);
    }

    @ParameterizedTest
    @MethodSource("my.project.util.data.TestDataSource#addRoleToUserRoles")
    void addRoleToUserRoles(RoleName initialRole, RoleName input, Set<RoleName> expectedRoleNames) {
        // Given
        User user = generateUser(initialRole, initialRole == null ? null : Set.of(initialRole));

        // When
        underTest.addRoleToUserRoles(user, input);

        // Then
        Set<RoleName> actualRoleNames = user.getRoleStatistics().stream().map(RoleStatistics::getRoleName).collect(Collectors.toSet());
        assertThat(actualRoleNames).isEqualTo(expectedRoleNames);
    }

    @ParameterizedTest
    @MethodSource("my.project.util.data.TestDataSource#addRoleToUserRoles_throwIfUserAlreadyHasThisRole")
    void addRoleToUserRoles_throwIfUserAlreadyHasThisRole(RoleName initialRole, Set<RoleName> existingRoles) {
        // Given
        User user = generateUser(initialRole, existingRoles);

        mockExceptionMessage(messageSource, "exception.authentication.userAlreadyRegisteredOnPlatform");

        // When & Then
        assertThatThrownBy(() -> underTest.addRoleToUserRoles(user, initialRole))
                .isInstanceOf(ResourceAlreadyExistsException.class);
    }

    @ParameterizedTest
    @MethodSource("my.project.util.data.TestDataSource#getPlatformByRoleName")
    void getPlatformByRoleName(RoleName input, Platform expected) {
        // Given
        // When
        Platform actual = underTest.getPlatformByRoleName(input);

        // Then
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void getPlatformByRoleName_throwIfAdmin() {
        // Given
        RoleName roleName = RoleName.ADMIN;

        mockExceptionMessage(messageSource, "exception.role.invalidRole");

        // When & Then
        assertThatThrownBy(() -> underTest.getPlatformByRoleName(roleName))
                .isInstanceOf(IllegalStateException.class);
    }

    @ParameterizedTest
    @MethodSource("my.project.util.data.TestDataSource#getRoleNameByPlatform")
    void getRoleNameByPlatform(Platform input, RoleName expected) {
        // Given
        // When
        RoleName actual = underTest.getRoleNameByPlatform(input);

        // Then
        assertThat(actual).isEqualTo(expected);
    }

    @ParameterizedTest
    @MethodSource("my.project.util.data.TestDataSource#getRoleStatistics")
    void getRoleStatistics(RoleName initialRole, Set<RoleName> existingRoles, RoleName expectedRoleName) {
        // Given
        User user = generateUser(initialRole, existingRoles);
        mockAuthentication(user);

        // When
        RoleStatistics actual = underTest.getRoleStatistics();

        // Then
        assertThat(actual).isNotNull();
        assertThat(actual.getRoleName()).isEqualTo(expectedRoleName);
    }

    @ParameterizedTest
    @MethodSource("my.project.util.data.TestDataSource#getRoleStatistics_throwWhenSetOfRoleStatisticsDoesNotContainCurrentRole")
    void getRoleStatistics_throwWhenSetOfRoleStatisticsDoesNotContainCurrentRole(RoleName initialRole, Set<RoleName> existingRoles) {
        // Given
        User user = generateUser(initialRole, existingRoles);
        mockAuthentication(user);

        mockExceptionMessage(messageSource, "exception.role.setOfRoleStatisticsDoesNotContainCurrentRole");

        // When & Then
        assertThatThrownBy(() -> underTest.getRoleStatistics())
                .isInstanceOf(IllegalStateException.class);
    }

    @ParameterizedTest
    @MethodSource("my.project.util.data.TestDataSource#throwIfUserNotRegisteredOnPlatform")
    void throwIfUserNotRegisteredOnPlatform(RoleName currentRole, RoleName input) {
        // Given
        User user = generateUser(currentRole);

        mockExceptionMessage(messageSource, "exception.authentication.userNotRegisteredOnPlatform");

        // When & Then
        assertThatThrownBy(() -> underTest.throwIfUserNotRegisteredOnPlatform(user, input))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @ParameterizedTest
    @MethodSource("my.project.util.data.TestDataSource#throwIfUserNotRegisteredOnPlatform_doNotThrowIfUserRegisteredOnPlatform")
    void throwIfUserNotRegisteredOnPlatform_doNotThrowIfUserRegisteredOnPlatform(RoleName currentRole, RoleName input) {
        // Given
        User user = generateUser(currentRole);

        // When
        underTest.throwIfUserNotRegisteredOnPlatform(user, input);

        // Then
        then(messageSource).shouldHaveNoInteractions();
    }
}
