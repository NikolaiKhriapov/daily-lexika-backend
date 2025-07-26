package my.project.dailylexika.util.data;

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

    /**
     * argument(0): platform on which the user is being registered)
     * argument(1): role that will be added to user
     */
    public static Stream<Arguments> register_newUser() {
        return Stream.of(
                arguments(CHINESE, USER_CHINESE),
                arguments(ENGLISH, USER_ENGLISH)
        );
    }

    /**
     * argument(0): platform on which the user is being registered (second one)
     * argument(1): user role (existing)
     * argument(2): user role (to be added)
     */
    public static Stream<Arguments> register_existingUserNewPlatform() {
        return Stream.of(
                arguments(CHINESE, USER_ENGLISH, USER_CHINESE),
                arguments(ENGLISH, USER_CHINESE, USER_ENGLISH)
        );
    }

    /**
     * argument(0): platform on which the user is being logged in
     * argument(1): user role (existing)
     */
    public static Stream<Arguments> login() {
        return Stream.of(
                arguments(ENGLISH, USER_ENGLISH),
                arguments(CHINESE, USER_CHINESE)
        );
    }

    // RoleServiceTest.java

    /**
     * argument(0): 'user.role' (user current role; 'null' means that user is not yet registered on any platform)
     * argument(1): input ('user.role' to be added to 'user.setOfRoleStatisticsDto'; platform which the user is being registered on)
     * argument(2): expected 'user.setOfRoleStatisticsDto' (platforms on which the user is to be registered)
     */
    public static Stream<Arguments> addRoleToUserRoles() {
        return Stream.of(
                arguments(null, USER_CHINESE, Set.of(USER_CHINESE)),
                arguments(null, USER_ENGLISH, Set.of(USER_ENGLISH)),
                arguments(USER_CHINESE, USER_ENGLISH, Set.of(USER_ENGLISH, USER_CHINESE)),
                arguments(USER_ENGLISH, USER_CHINESE, Set.of(USER_ENGLISH, USER_CHINESE))
        );
    }

    /**
     * argument(0): 'user.role' (user current role)
     * argument(1): 'user.setOfRoleStatisticsDto' (platforms on which the user is already registered)
     */
    public static Stream<Arguments> addRoleToUserRoles_throwIfUserAlreadyHasThisRole() {
        return Stream.of(
                arguments(USER_CHINESE, Set.of(USER_CHINESE)),
                arguments(USER_ENGLISH, Set.of(USER_ENGLISH)),
                arguments(USER_CHINESE, Set.of(USER_CHINESE, USER_ENGLISH)),
                arguments(USER_ENGLISH, Set.of(USER_CHINESE, USER_ENGLISH))
        );
    }

    /**
     * argument(0): input (roleName)
     * argument(1): expected (platform)
     */
    public static Stream<Arguments> getPlatformByRoleName() {
        return Stream.of(
                arguments(USER_CHINESE, CHINESE),
                arguments(USER_ENGLISH, ENGLISH)
        );
    }

    /**
     * argument(0): input (platform)
     * argument(1): expected (roleName)
     */
    public static Stream<Arguments> getRoleNameByPlatform() {
        return Stream.of(
                arguments(CHINESE, USER_CHINESE),
                arguments(ENGLISH, USER_ENGLISH)
        );
    }

    /**
     * argument(0): 'user.role' (user current role)
     * argument(1): 'user.setOfRoleStatisticsDto' (platforms on which the user is already registered)
     * argument(2): expected ('setOfRoleStatisticsDto.roleName')
     */
    public static Stream<Arguments> getRoleStatisticsEntity() {
        return Stream.of(
                arguments(USER_CHINESE, Set.of(USER_CHINESE), USER_CHINESE),
                arguments(USER_ENGLISH, Set.of(USER_ENGLISH), USER_ENGLISH),
                arguments(USER_ENGLISH, Set.of(USER_ENGLISH, USER_CHINESE), USER_ENGLISH),
                arguments(USER_CHINESE, Set.of(USER_ENGLISH, USER_CHINESE), USER_CHINESE)
        );
    }

    /**
     * argument(0): 'user.role' (user current role)
     * argument(1): 'user.setOfRoleStatisticsDto' (platforms on which the user is already registered)
     */
    public static Stream<Arguments> getRoleStatistics_throwWhenSetOfRoleStatisticsDoesNotContainCurrentRole() {
        return Stream.of(
                arguments(USER_CHINESE, Set.of(USER_ENGLISH)),
                arguments(USER_ENGLISH, Set.of(USER_CHINESE))
        );
    }

    /**
     * argument(0): 'user.role' (user current role; platform on which the user is already registered)
     * argument(1): 'user.role' (platform on which the user is not registered)
     */
    public static Stream<Arguments> throwIfUserNotRegisteredOnPlatform() {
        return Stream.of(
                arguments(USER_CHINESE, USER_ENGLISH),
                arguments(USER_ENGLISH, USER_CHINESE)
        );
    }

    /**
     * argument(0): 'user.role' (user current role; platform on which the user is already registered)
     * argument(1): input ('user.role'; platform on which the user is already registered)
     */
    public static Stream<Arguments> throwIfUserNotRegisteredOnPlatform_doNotThrowIfUserRegisteredOnPlatform() {
        return Stream.of(
                arguments(USER_CHINESE, USER_CHINESE),
                arguments(USER_ENGLISH, USER_ENGLISH)
        );
    }

    // WordDataRepository.java

    /**
     * argument(0): platform
     * argument(1): expected (number of wordDataId found)
     */
    public static Stream<Arguments> findAllWordDataIdsByWordPackName() {
        return Stream.of(
                arguments("HSK 1", 497),
                arguments("HSK 2", 764),
                arguments("HSK 3", 966),
                arguments("HSK 4", 995),
                arguments("HSK 5", 1067),
                arguments("HSK 6", 1134),
                arguments("HSK 7-9", 5619)
        );
    }
}
