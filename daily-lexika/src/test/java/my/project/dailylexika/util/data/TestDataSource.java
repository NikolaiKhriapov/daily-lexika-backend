package my.project.dailylexika.util.data;

import my.project.dailylexika.user.model.entities.User;
import my.project.library.dailylexika.dtos.flashcards.WordPackDto;
import my.project.library.dailylexika.dtos.user.AccountDeletionRequest;
import my.project.library.dailylexika.dtos.user.AuthenticationRequest;
import my.project.library.dailylexika.dtos.user.PasswordUpdateRequest;
import my.project.library.dailylexika.dtos.user.RegistrationRequest;
import my.project.library.dailylexika.dtos.user.UserDto;
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

    public static Stream<Arguments> getRoleNameByPlatform_throwIfInvalidInput() {
        return Stream.of(
                arguments((Object) null)
        );
    }

    public static Stream<Arguments> getPlatformByRoleName() {
        return Stream.of(
                arguments(USER_CHINESE, CHINESE),
                arguments(USER_ENGLISH, ENGLISH)
        );
    }

    public static Stream<Arguments> getPlatformByRoleName_throwIfInvalidInput() {
        return Stream.of(
                arguments((Object) null)
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

    public static Stream<Arguments> addRoleToUserRoles_throwIfInvalidInput() {
        User user = User.builder()
                .id(1)
                .name("User")
                .email("user@test.com")
                .password("pass")
                .role(USER_ENGLISH)
                .build();
        return Stream.of(
                arguments(null, USER_ENGLISH),
                arguments(user, null)
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

    public static Stream<Arguments> throwIfUserNotRegisteredOnPlatform_throwIfInvalidInput() {
        User user = User.builder()
                .id(1)
                .name("User")
                .email("user@test.com")
                .password("pass")
                .role(USER_ENGLISH)
                .build();
        return Stream.of(
                arguments(null, USER_ENGLISH),
                arguments(user, null)
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

    // UserServiceTest.java

    public static Stream<Arguments> updateUserInfo_publishUserEmailUpdatedEvent() {
        return Stream.of(
                arguments(USER_CHINESE, CHINESE, "Updated@Test.com", "updated@test.com"),
                arguments(USER_ENGLISH, ENGLISH, "MIXED@Example.com", "mixed@example.com")
        );
    }

    public static Stream<Arguments> updateUserInfo_throwIfInvalidInput() {
        return Stream.of(
                arguments(new UserDto(1, null, "user@test.com", USER_ENGLISH, Set.of(), null, null, null)),
                arguments(new UserDto(1, "", "user@test.com", USER_ENGLISH, Set.of(), null, null, null)),
                arguments(new UserDto(1, " ", "user@test.com", USER_ENGLISH, Set.of(), null, null, null)),
                arguments(new UserDto(1, "User", null, USER_ENGLISH, Set.of(), null, null, null)),
                arguments(new UserDto(1, "User", "", USER_ENGLISH, Set.of(), null, null, null)),
                arguments(new UserDto(1, "User", " ", USER_ENGLISH, Set.of(), null, null, null))
        );
    }

    public static Stream<Arguments> updatePassword_throwIfInvalidInput() {
        return Stream.of(
                arguments(new PasswordUpdateRequest(null, "newpass")),
                arguments(new PasswordUpdateRequest("", "newpass")),
                arguments(new PasswordUpdateRequest(" ", "newpass")),
                arguments(new PasswordUpdateRequest("current", null)),
                arguments(new PasswordUpdateRequest("current", "")),
                arguments(new PasswordUpdateRequest("current", " "))
        );
    }

    public static Stream<Arguments> deleteAccount() {
        return Stream.of(
                arguments(USER_CHINESE, CHINESE),
                arguments(USER_ENGLISH, ENGLISH)
        );
    }

    public static Stream<Arguments> deleteAccount_removeRoleOnly() {
        return Stream.of(
                arguments(USER_CHINESE, USER_ENGLISH, CHINESE),
                arguments(USER_ENGLISH, USER_CHINESE, ENGLISH)
        );
    }

    public static Stream<Arguments> deleteAccount_throwIfInvalidInput() {
        return Stream.of(
                arguments(new AccountDeletionRequest(null)),
                arguments(new AccountDeletionRequest("")),
                arguments(new AccountDeletionRequest(" "))
        );
    }

    public static Stream<Arguments> existsByEmail() {
        return Stream.of(
                arguments("User@Test.com", "user@test.com", true),
                arguments("lower@test.com", "lower@test.com", false)
        );
    }

    public static Stream<Arguments> existsByEmail_throwIfInvalidInput() {
        return Stream.of(
                arguments((Object) null),
                arguments(""),
                arguments(" ")
        );
    }

    public static Stream<Arguments> getUserEntityByEmail() {
        return Stream.of(
                arguments("User@Test.com", "user@test.com"),
                arguments("MIXED@Example.com", "mixed@example.com")
        );
    }

    public static Stream<Arguments> getUserEntityByEmail_throwIfInvalidInput() {
        return Stream.of(
                arguments((Object) null),
                arguments(""),
                arguments(" ")
        );
    }

    public static Stream<Arguments> updateCurrentStreak() {
        return Stream.of(
                arguments(0L),
                arguments(5L)
        );
    }

    public static Stream<Arguments> updateCurrentStreak_throwIfInvalidInput() {
        return Stream.of(
                arguments((Object) null),
                arguments(-1L),
                arguments(-5L)
        );
    }

    public static Stream<Arguments> updateRecordStreak() {
        return updateCurrentStreak();
    }

    public static Stream<Arguments> updateRecordStreak_throwIfInvalidInput() {
        return updateCurrentStreak_throwIfInvalidInput();
    }

    // WordPackServiceImplTest.java

    public static Stream<Arguments> getAllForUser_returnsNonCustomAndOwnedCustom() {
        return Stream.of(
                arguments(ENGLISH, USER_ENGLISH, "EN__"),
                arguments(CHINESE, USER_CHINESE, "CH__")
        );
    }

    public static Stream<Arguments> getAllForUser_filtersCustomByOwnerSuffix() {
        return getAllForUser_returnsNonCustomAndOwnedCustom();
    }

    public static Stream<Arguments> getByName_throwIfInvalidInput() {
        return Stream.of(
                arguments((Object) null),
                arguments(""),
                arguments(" ")
        );
    }

    public static Stream<Arguments> saveAll_throwIfInvalidInput() {
        return Stream.of(
                arguments((Object) null)
        );
    }

    public static Stream<Arguments> deleteAllByUserIdAndPlatform_deletesOwnedCustomOnly() {
        return Stream.of(
                arguments(ENGLISH, "EN__"),
                arguments(CHINESE, "CH__")
        );
    }

    public static Stream<Arguments> deleteAllByUserIdAndPlatform_throwIfInvalidInput() {
        return Stream.of(
                arguments(null, ENGLISH),
                arguments(1, null)
        );
    }

    public static Stream<Arguments> createCustomWordPack_createsWithDecoratedName() {
        return Stream.of(
                arguments(ENGLISH, USER_ENGLISH, "EN__"),
                arguments(CHINESE, USER_CHINESE, "CH__")
        );
    }

    public static Stream<Arguments> createCustomWordPack_trimsNameBeforeDecoration() {
        return createCustomWordPack_createsWithDecoratedName();
    }

    public static Stream<Arguments> createCustomWordPack_allowsCaseSensitiveDistinctNames() {
        return createCustomWordPack_createsWithDecoratedName();
    }

    public static Stream<Arguments> createCustomWordPack_preservesEmbeddedSuffix() {
        return createCustomWordPack_createsWithDecoratedName();
    }

    public static Stream<Arguments> createCustomWordPack_throwIfInvalidInput() {
        return Stream.of(
                arguments((Object) null),
                arguments(new WordPackDto(null, "desc", null, null, null, null, null)),
                arguments(new WordPackDto("", "desc", null, null, null, null, null)),
                arguments(new WordPackDto(" ", "desc", null, null, null, null, null)),
                arguments(new WordPackDto("name", null, null, null, null, null, null)),
                arguments(new WordPackDto("name", "", null, null, null, null, null)),
                arguments(new WordPackDto("name", " ", null, null, null, null, null))
        );
    }

    public static Stream<Arguments> createCustomWordPack_throwIfInvalidName() {
        return Stream.of(
                arguments("  ;  "),
                arguments("name;with;semicolon")
        );
    }

    public static Stream<Arguments> createCustomWordPack_throwIfAlreadyExists() {
        return createCustomWordPack_createsWithDecoratedName();
    }

    public static Stream<Arguments> deleteCustomWordPack_deletesAndPublishesEvent() {
        return Stream.of(
                arguments(ENGLISH, USER_ENGLISH, "EN__"),
                arguments(CHINESE, USER_CHINESE, "CH__")
        );
    }

    public static Stream<Arguments> deleteCustomWordPack_throwIfInvalidInput() {
        return getByName_throwIfInvalidInput();
    }

    public static Stream<Arguments> deleteCustomWordPack_throwIfCategoryNotCustom() {
        return deleteCustomWordPack_deletesAndPublishesEvent();
    }

    public static Stream<Arguments> throwIfWordPackCategoryNotCustom_throwIfNotCustom() {
        return Stream.of(
                arguments(ENGLISH, USER_ENGLISH, "EN__"),
                arguments(CHINESE, USER_CHINESE, "CH__")
        );
    }

    public static Stream<Arguments> throwIfWordPackCategoryNotCustom_throwIfInvalidInput() {
        return Stream.of(
                arguments((Object) null)
        );
    }
}
