package my.project.dailylexika.user.service;

import my.project.dailylexika.user._public.PublicRoleService;
import my.project.dailylexika.user.model.mappers.RoleStatisticsMapper;
import my.project.dailylexika.user.service.impl.RoleServiceImpl;
import my.project.library.dailylexika.dtos.user.RoleStatisticsDto;
import my.project.library.dailylexika.enumerations.Platform;
import my.project.library.dailylexika.enumerations.RoleName;
import my.project.dailylexika.user.model.entities.RoleStatistics;
import my.project.dailylexika.user.model.entities.User;
import my.project.dailylexika.config.AbstractUnitTest;
import my.project.library.util.exception.ResourceAlreadyExistsException;
import my.project.library.util.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.BDDMockito.given;

class RoleServiceImplTest extends AbstractUnitTest {

    private RoleServiceImpl underTest;
    @Mock
    private RoleStatisticsMapper roleStatisticsMapper;

    @BeforeEach
    void setUp() {
        underTest = new RoleServiceImpl(roleStatisticsMapper);
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
    @MethodSource("my.project.dailylexika.util.data.TestDataSource#getRoleNameByPlatform_throwIfInvalidInput")
    void getRoleNameByPlatform_throwIfInvalidInput(Platform input) {
        // Given
        RoleService validatedService = createValidatedService();

        // When / Then
        assertThatThrownBy(() -> validatedService.getRoleNameByPlatform(input))
                .isInstanceOf(jakarta.validation.ConstraintViolationException.class);
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
    @MethodSource("my.project.dailylexika.util.data.TestDataSource#getPlatformByRoleName_throwIfInvalidInput")
    void getPlatformByRoleName_throwIfInvalidInput(RoleName input) {
        // Given
        PublicRoleService validatedService = createValidatedPublicService();

        // When / Then
        assertThatThrownBy(() -> validatedService.getPlatformByRoleName(input))
                .isInstanceOf(jakarta.validation.ConstraintViolationException.class);
    }

    @ParameterizedTest
    @MethodSource("my.project.dailylexika.util.data.TestDataSource#addRoleToUserRoles")
    void addRoleToUserRoles(RoleName initialRole, RoleName input, Set<RoleName> expectedRoleNames) {
        // Given
        User user = buildUser(initialRole, initialRole == null ? null : Set.of(initialRole));

        // When
        underTest.addRoleToUserRoles(user, input);

        // Then
        Set<RoleName> actualRoleNames = user.getRoleStatistics().stream().map(RoleStatistics::getRoleName).collect(Collectors.toSet());
        assertThat(actualRoleNames).isEqualTo(expectedRoleNames);
    }

    @ParameterizedTest
    @MethodSource("my.project.dailylexika.util.data.TestDataSource#addRoleToUserRoles_initializeRolesWhenNull")
    void addRoleToUserRoles_initializeRolesWhenNull(RoleName input, Set<RoleName> expectedRoleNames) {
        // Given
        User user = buildUser(null, null);
        user.setRoleStatistics(null);

        // When
        underTest.addRoleToUserRoles(user, input);

        // Then
        Set<RoleName> actualRoleNames = user.getRoleStatistics().stream().map(RoleStatistics::getRoleName).collect(Collectors.toSet());
        assertThat(actualRoleNames).isEqualTo(expectedRoleNames);
    }

    @ParameterizedTest
    @MethodSource("my.project.dailylexika.util.data.TestDataSource#addRoleToUserRoles_throwIfInvalidInput")
    void addRoleToUserRoles_throwIfInvalidInput(User user, RoleName roleName) {
        // Given
        RoleService validatedService = createValidatedService();

        // When / Then
        assertThatThrownBy(() -> validatedService.addRoleToUserRoles(user, roleName))
                .isInstanceOf(jakarta.validation.ConstraintViolationException.class);
    }

    @ParameterizedTest
    @MethodSource("my.project.dailylexika.util.data.TestDataSource#addRoleToUserRoles_throwIfUserAlreadyHasThisRole")
    void addRoleToUserRoles_throwIfUserAlreadyHasThisRole(RoleName initialRole, Set<RoleName> existingRoles) {
        // Given
        User user = buildUser(initialRole, existingRoles);

        // When / Then
        assertThatThrownBy(() -> underTest.addRoleToUserRoles(user, initialRole))
                .isInstanceOf(ResourceAlreadyExistsException.class);
    }

    @ParameterizedTest
    @MethodSource("my.project.dailylexika.util.data.TestDataSource#getRoleStatistics")
    void getRoleStatistics(RoleName initialRole, Set<RoleName> existingRoles, RoleName expectedRoleName) {
        // Given
        User user = buildUser(initialRole, existingRoles);
        RoleStatistics roleStatistics = user.getRoleStatistics().stream()
                .filter(role -> role.getRoleName().equals(expectedRoleName))
                .findFirst()
                .orElseThrow();
        RoleStatisticsDto expected = new RoleStatisticsDto(1L, expectedRoleName, 0L, roleStatistics.getDateOfLastStreak(), 0L);

        mockAuthentication(user);
        given(roleStatisticsMapper.toDto(roleStatistics)).willReturn(expected);

        // When
        RoleStatisticsDto actual = underTest.getRoleStatistics();

        // Then
        assertThat(actual).isEqualTo(expected);
    }

    @ParameterizedTest
    @MethodSource("my.project.dailylexika.util.data.TestDataSource#getRoleStatistics_throwWhenSetOfRoleStatisticsDoesNotContainCurrentRole")
    void getRoleStatistics_throwWhenSetOfRoleStatisticsDoesNotContainCurrentRole(RoleName initialRole, Set<RoleName> existingRoles) {
        // Given
        User user = buildUser(initialRole, existingRoles);
        mockAuthentication(user);

        // When / Then
        assertThatThrownBy(() -> underTest.getRoleStatistics())
                .isInstanceOf(IllegalStateException.class);
    }

    @ParameterizedTest
    @MethodSource("my.project.dailylexika.util.data.TestDataSource#getRoleStatisticsEntity")
    void getRoleStatisticsEntity(RoleName initialRole, Set<RoleName> existingRoles, RoleName expectedRoleName) {
        // Given
        User user = buildUser(initialRole, existingRoles);
        mockAuthentication(user);

        // When
        RoleStatistics actual = underTest.getRoleStatisticsEntity();

        // Then
        assertThat(actual).isNotNull();
        assertThat(actual.getRoleName()).isEqualTo(expectedRoleName);
    }

    @ParameterizedTest
    @MethodSource("my.project.dailylexika.util.data.TestDataSource#getRoleStatisticsEntity_throwWhenSetOfRoleStatisticsDoesNotContainCurrentRole")
    void getRoleStatisticsEntity_throwWhenSetOfRoleStatisticsDoesNotContainCurrentRole(RoleName initialRole, Set<RoleName> existingRoles) {
        // Given
        User user = buildUser(initialRole, existingRoles);
        mockAuthentication(user);

        // When / Then
        assertThatThrownBy(() -> underTest.getRoleStatisticsEntity())
                .isInstanceOf(IllegalStateException.class);
    }

    @ParameterizedTest
    @MethodSource("my.project.dailylexika.util.data.TestDataSource#throwIfUserNotRegisteredOnPlatform")
    void throwIfUserNotRegisteredOnPlatform(RoleName initialRole, Set<RoleName> existingRoles, RoleName input) {
        // Given
        User user = buildUser(initialRole, existingRoles);

        // When / Then
        assertThatThrownBy(() -> underTest.throwIfUserNotRegisteredOnPlatform(user, input))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @ParameterizedTest
    @MethodSource("my.project.dailylexika.util.data.TestDataSource#throwIfUserNotRegisteredOnPlatform_throwIfInvalidInput")
    void throwIfUserNotRegisteredOnPlatform_throwIfInvalidInput(User user, RoleName roleName) {
        // Given
        RoleService validatedService = createValidatedService();

        // When / Then
        assertThatThrownBy(() -> validatedService.throwIfUserNotRegisteredOnPlatform(user, roleName))
                .isInstanceOf(jakarta.validation.ConstraintViolationException.class);
    }

    @ParameterizedTest
    @MethodSource("my.project.dailylexika.util.data.TestDataSource#throwIfUserNotRegisteredOnPlatform_doNotThrowIfUserRegisteredOnPlatform")
    void throwIfUserNotRegisteredOnPlatform_doNotThrowIfUserRegisteredOnPlatform(RoleName initialRole, Set<RoleName> existingRoles, RoleName input) {
        // Given
        User user = buildUser(initialRole, existingRoles);

        // When / Then
        underTest.throwIfUserNotRegisteredOnPlatform(user, input);
    }

    private User buildUser(RoleName roleName, Set<RoleName> roleNamesForPlatforms) {
        User user = User.builder()
                .id(1)
                .name("Test User")
                .email("test@example.com")
                .password("password")
                .role(roleName)
                .build();

        Set<RoleStatistics> roleStatistics = new HashSet<>();
        if (roleNamesForPlatforms != null) {
            roleNamesForPlatforms.forEach(oneRoleName -> roleStatistics.add(new RoleStatistics(oneRoleName)));
        }
        user.setRoleStatistics(roleStatistics);
        return user;
    }

    private void mockAuthentication(User user) {
        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContext);
        given(securityContext.getAuthentication()).willReturn(authentication);
        given(authentication.getPrincipal()).willReturn(user);
    }

    private RoleService createValidatedService() {
        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();
        MethodValidationPostProcessor processor = new MethodValidationPostProcessor();
        processor.setValidator(validator);
        processor.afterPropertiesSet();
        RoleServiceImpl service = new RoleServiceImpl(roleStatisticsMapper);
        return (RoleService) processor.postProcessAfterInitialization(service, "roleService");
    }

    private PublicRoleService createValidatedPublicService() {
        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();
        MethodValidationPostProcessor processor = new MethodValidationPostProcessor();
        processor.setValidator(validator);
        processor.afterPropertiesSet();
        RoleServiceImpl service = new RoleServiceImpl(roleStatisticsMapper);
        return (PublicRoleService) processor.postProcessAfterInitialization(service, "publicRoleService");
    }
}
