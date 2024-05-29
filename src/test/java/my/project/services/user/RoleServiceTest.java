package my.project.services.user;

import my.project.models.entities.enumerations.Platform;
import my.project.models.entities.user.RoleName;
import my.project.models.entities.user.RoleStatistics;
import my.project.models.entities.user.User;
import my.project.config.AbstractUnitTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Set;
import java.util.stream.Collectors;

import static my.project.util.data.TestDataUtil.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class RoleServiceTest extends AbstractUnitTest {

    private RoleService underTest;

    @BeforeEach
    void setUp() {
        underTest = new RoleService();
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
    @MethodSource("my.project.util.data.TestDataSource#getPlatformByRoleName")
    void getPlatformByRoleName(RoleName input, Platform expected) {
        // Given
        // When
        Platform actual = underTest.getPlatformByRoleName(input);

        // Then
        assertThat(actual).isEqualTo(expected);
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
}
