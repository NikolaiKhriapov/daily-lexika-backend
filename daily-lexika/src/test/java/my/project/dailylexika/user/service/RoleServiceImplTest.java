package my.project.dailylexika.user.service;

import my.project.dailylexika.user.model.mappers.RoleStatisticsMapper;
import my.project.dailylexika.user.service.impl.RoleServiceImpl;
import my.project.library.dailylexika.enumerations.Platform;
import my.project.library.dailylexika.enumerations.RoleName;
import my.project.dailylexika.user.model.entities.RoleStatistics;
import my.project.dailylexika.user.model.entities.User;
import my.project.dailylexika.config.AbstractUnitTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;

import java.util.Set;
import java.util.stream.Collectors;

import static my.project.dailylexika.util.data.TestDataUtil.generateUser;
import static my.project.dailylexika.util.data.TestDataUtil.mockAuthentication;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class RoleServiceImplTest extends AbstractUnitTest {

    private RoleServiceImpl underTest;
    @Mock
    private RoleStatisticsMapper roleStatisticsMapper;

    @BeforeEach
    void setUp() {
        underTest = new RoleServiceImpl(roleStatisticsMapper);
    }

    @ParameterizedTest
    @MethodSource("my.project.dailylexika.util.data.TestDataSource#addRoleToUserRoles")
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
    @MethodSource("my.project.dailylexika.util.data.TestDataSource#getPlatformByRoleName")
    void getPlatformByRoleName(RoleName input, Platform expected) {
        // Given
        // When
        Platform actual = underTest.getPlatformByRoleName(input);

        // Then
        assertThat(actual).isEqualTo(expected);
    }

    @ParameterizedTest
    @MethodSource("my.project.dailylexika.util.data.TestDataSource#getRoleNameByPlatform")
    void getRoleNameByPlatform(Platform input, RoleName expected) {
        // Given
        // When
        RoleName actual = underTest.getRoleNameByPlatform(input);

        // Then
        assertThat(actual).isEqualTo(expected);
    }

    @ParameterizedTest
    @MethodSource("my.project.dailylexika.util.data.TestDataSource#getRoleStatisticsEntity")
    void getRoleStatisticsEntity(RoleName initialRole, Set<RoleName> existingRoles, RoleName expectedRoleName) {
        // Given
        User user = generateUser(initialRole, existingRoles);
        mockAuthentication(user);

        // When
        RoleStatistics actual = underTest.getRoleStatisticsEntity();

        // Then
        assertThat(actual).isNotNull();
        assertThat(actual.getRoleName()).isEqualTo(expectedRoleName);
    }
}
