package my.project.dailylexika.util.data;

import my.project.library.dailylexika.dtos.user.AuthenticationRequest;
import my.project.library.dailylexika.dtos.user.RegistrationRequest;
import org.junit.jupiter.params.provider.Arguments;

import java.util.Set;
import java.util.stream.Stream;

import static my.project.library.dailylexika.enumerations.Platform.CHINESE;
import static my.project.library.dailylexika.enumerations.Platform.ENGLISH;
import static my.project.library.dailylexika.enumerations.RoleName.USER_CHINESE;
import static my.project.library.dailylexika.enumerations.RoleName.USER_ENGLISH;
import static org.junit.jupiter.params.provider.Arguments.arguments;

public class TestDataSource {

    // AuthenticationServiceTest.java

    public static Stream<Arguments> register_newUser() {
        return Stream.of(
                arguments(CHINESE, USER_CHINESE),
                arguments(ENGLISH, USER_ENGLISH)
        );
    }

    public static Stream<Arguments> register_existingUserNewPlatform() {
        return Stream.of(
                arguments(CHINESE, USER_ENGLISH, USER_CHINESE),
                arguments(ENGLISH, USER_CHINESE, USER_ENGLISH)
        );
    }

    public static Stream<Arguments> register_emailNormalization() {
        return Stream.of(
                arguments(ENGLISH, USER_ENGLISH, "User@Test.com", "user@test.com"),
                arguments(CHINESE, USER_CHINESE, "UPPER@TEST.COM", "upper@test.com")
        );
    }

    public static Stream<Arguments> register_throwIfInvalidInput() {
        return Stream.of(
                arguments(new RegistrationRequest(null, "user@test.com", "pass", ENGLISH)),
                arguments(new RegistrationRequest(" ", "user@test.com", "pass", ENGLISH)),
                arguments(new RegistrationRequest("", "user@test.com", "pass", ENGLISH)),
                arguments(new RegistrationRequest("User", null, "pass", ENGLISH)),
                arguments(new RegistrationRequest("User", " ", "pass", ENGLISH)),
                arguments(new RegistrationRequest("User", "", "pass", ENGLISH)),
                arguments(new RegistrationRequest("User", "user@test.com", null, ENGLISH)),
                arguments(new RegistrationRequest("User", "user@test.com", " ", ENGLISH)),
                arguments(new RegistrationRequest("User", "user@test.com", "", ENGLISH)),
                arguments(new RegistrationRequest("User", "user@test.com", "pass", null))
        );
    }

    public static Stream<Arguments> register_throwIfExistingUserInvalidPassword() {
        return Stream.of(
                arguments(CHINESE),
                arguments(ENGLISH)
        );
    }

    public static Stream<Arguments> register_throwIfExistingUserAlreadyHasRole() {
        return register_newUser();
    }

    public static Stream<Arguments> login_success() {
        return Stream.of(
                arguments(CHINESE, USER_CHINESE),
                arguments(ENGLISH, USER_ENGLISH)
        );
    }

    public static Stream<Arguments> login_mixedCaseEmail() {
        return Stream.of(
                arguments(ENGLISH, USER_ENGLISH, "User@Test.com"),
                arguments(CHINESE, USER_CHINESE, "MiXeD@Example.com")
        );
    }

    public static Stream<Arguments> login_throwIfInvalidInput() {
        return Stream.of(
                arguments(new AuthenticationRequest(null, "pass", ENGLISH)),
                arguments(new AuthenticationRequest(" ", "pass", ENGLISH)),
                arguments(new AuthenticationRequest("", "pass", ENGLISH)),
                arguments(new AuthenticationRequest("user@test.com", null, ENGLISH)),
                arguments(new AuthenticationRequest("user@test.com", " ", ENGLISH)),
                arguments(new AuthenticationRequest("user@test.com", "", ENGLISH)),
                arguments(new AuthenticationRequest("user@test.com", "pass", null))
        );
    }

    public static Stream<Arguments> login_throwIfBadCredentials() {
        return Stream.of(
                arguments(CHINESE),
                arguments(ENGLISH)
        );
    }

    public static Stream<Arguments> login_throwIfUserNotRegisteredOnPlatform() {
        return login_success();
    }

    // RoleServiceTest.java

    public static Stream<Arguments> getRoleNameByPlatform() {
        return Stream.of(
                arguments(CHINESE, USER_CHINESE),
                arguments(ENGLISH, USER_ENGLISH)
        );
    }

    public static Stream<Arguments> getPlatformByRoleName() {
        return Stream.of(
                arguments(USER_CHINESE, CHINESE),
                arguments(USER_ENGLISH, ENGLISH)
        );
    }

    public static Stream<Arguments> addRoleToUserRoles() {
        return Stream.of(
                arguments(null, USER_CHINESE, Set.of(USER_CHINESE)),
                arguments(null, USER_ENGLISH, Set.of(USER_ENGLISH)),
                arguments(USER_CHINESE, USER_ENGLISH, Set.of(USER_ENGLISH, USER_CHINESE)),
                arguments(USER_ENGLISH, USER_CHINESE, Set.of(USER_ENGLISH, USER_CHINESE))
        );
    }

    public static Stream<Arguments> addRoleToUserRoles_initializeRolesWhenNull() {
        return Stream.of(
                arguments(USER_CHINESE, Set.of(USER_CHINESE)),
                arguments(USER_ENGLISH, Set.of(USER_ENGLISH))
        );
    }

    public static Stream<Arguments> addRoleToUserRoles_throwIfUserAlreadyHasThisRole() {
        return Stream.of(
                arguments(USER_CHINESE, Set.of(USER_CHINESE)),
                arguments(USER_ENGLISH, Set.of(USER_ENGLISH)),
                arguments(USER_CHINESE, Set.of(USER_CHINESE, USER_ENGLISH)),
                arguments(USER_ENGLISH, Set.of(USER_CHINESE, USER_ENGLISH))
        );
    }

    public static Stream<Arguments> getRoleStatistics() {
        return Stream.of(
                arguments(USER_CHINESE, Set.of(USER_CHINESE), USER_CHINESE),
                arguments(USER_ENGLISH, Set.of(USER_ENGLISH), USER_ENGLISH),
                arguments(USER_ENGLISH, Set.of(USER_ENGLISH, USER_CHINESE), USER_ENGLISH),
                arguments(USER_CHINESE, Set.of(USER_ENGLISH, USER_CHINESE), USER_CHINESE)
        );
    }

    public static Stream<Arguments> getRoleStatisticsEntity() {
        return getRoleStatistics();
    }

    public static Stream<Arguments> getRoleStatisticsEntity_throwWhenSetOfRoleStatisticsDoesNotContainCurrentRole() {
        return Stream.of(
                arguments(USER_CHINESE, Set.of(USER_ENGLISH)),
                arguments(USER_ENGLISH, Set.of(USER_CHINESE)),
                arguments(USER_CHINESE, Set.of()),
                arguments(USER_ENGLISH, Set.of())
        );
    }

    public static Stream<Arguments> getRoleStatistics_throwWhenSetOfRoleStatisticsDoesNotContainCurrentRole() {
        return getRoleStatisticsEntity_throwWhenSetOfRoleStatisticsDoesNotContainCurrentRole();
    }

    public static Stream<Arguments> throwIfUserNotRegisteredOnPlatform() {
        return Stream.of(
                arguments(USER_CHINESE, Set.of(USER_CHINESE), USER_ENGLISH),
                arguments(USER_ENGLISH, Set.of(USER_ENGLISH), USER_CHINESE),
                arguments(USER_CHINESE, Set.of(), USER_CHINESE),
                arguments(USER_ENGLISH, Set.of(), USER_ENGLISH)
        );
    }

    public static Stream<Arguments> throwIfUserNotRegisteredOnPlatform_doNotThrowIfUserRegisteredOnPlatform() {
        return Stream.of(
                arguments(USER_CHINESE, Set.of(USER_CHINESE), USER_CHINESE),
                arguments(USER_ENGLISH, Set.of(USER_ENGLISH), USER_ENGLISH),
                arguments(USER_CHINESE, Set.of(USER_CHINESE, USER_ENGLISH), USER_CHINESE),
                arguments(USER_ENGLISH, Set.of(USER_CHINESE, USER_ENGLISH), USER_ENGLISH)
        );
    }
}
