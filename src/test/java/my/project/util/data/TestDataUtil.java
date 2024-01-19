package my.project.util.data;

import my.project.models.dto.flashcards.ReviewDTO;
import my.project.models.dto.flashcards.ReviewStatisticsDTO;
import my.project.models.dto.user.AuthenticationRequest;
import my.project.models.dto.user.RegistrationRequest;
import my.project.models.entity.enumeration.Platform;
import my.project.models.entity.flashcards.WordData;
import my.project.models.entity.flashcards.WordPack;
import my.project.models.entity.user.RoleName;
import my.project.models.entity.user.RoleStatistics;
import my.project.models.entity.user.User;
import org.mockito.Mockito;
import org.springframework.context.MessageSource;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDate;
import java.util.*;

import static my.project.util.CommonConstants.ENCODED_PASSWORD;
import static org.mockito.BDDMockito.given;

public class TestDataUtil {

    private TestDataUtil() {
    }

    public static void mockAuthentication(User user) {
        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContext);
        given(securityContext.getAuthentication()).willReturn(authentication);
        given(authentication.getPrincipal()).willReturn(user);
    }

    public static void mockExceptionMessage(MessageSource messageSource, String key) {
        given(messageSource.getMessage(key, null, Locale.getDefault()))
                .willReturn("Test exception message");
    }

    public static RegistrationRequest generateRegistrationRequest(Platform platform) {
        return new RegistrationRequest(
                FakerUtil.generateName(),
                FakerUtil.generateEmail(),
                FakerUtil.generatePassword(),
                platform
        );
    }

    public static AuthenticationRequest generateAuthenticationRequest(Platform platform) {
        return new AuthenticationRequest(
                FakerUtil.generateEmail(),
                FakerUtil.generatePassword(),
                platform
        );
    }

    public static User generateUser(RegistrationRequest registrationRequest, RoleName roleName) {
        return User.builder()
                .id(FakerUtil.generateId())
                .name(registrationRequest.name())
                .email(registrationRequest.email())
                .password(ENCODED_PASSWORD)
                .role(roleName)
                .roleStatistics(new HashSet<>(Set.of(new RoleStatistics(roleName))))
                .build();
    }

    public static User generateUser(AuthenticationRequest authenticationRequest, RoleName roleName) {
        return User.builder()
                .id(FakerUtil.generateId())
                .name(FakerUtil.generateName())
                .email(authenticationRequest.email())
                .password(ENCODED_PASSWORD)
                .role(roleName)
                .roleStatistics(new HashSet<>(Set.of(new RoleStatistics(roleName))))
                .build();
    }

    public static User generateUser(RoleName roleName) {
        return User.builder()
                .id(FakerUtil.generateId())
                .name(FakerUtil.generateName())
                .email(FakerUtil.generateEmail())
                .password(FakerUtil.generatePassword())
                .role(roleName)
                .roleStatistics(Set.of(new RoleStatistics(roleName)))
                .build();
    }

    public static User generateUser(RoleName roleName, Set<RoleName> roleNamesForPlatforms) {
        User user = generateUser(roleName);

        Set<RoleStatistics> roleStatistics = new HashSet<>();
        if (roleNamesForPlatforms != null) {
            roleNamesForPlatforms.forEach(oneRoleName -> roleStatistics.add(new RoleStatistics(oneRoleName)));
        }
        user.setRoleStatistics(roleStatistics);

        return user;
    }

    public static ReviewDTO generateReviewDTO(Platform platform) {
        return new ReviewDTO(
                null,
                null,
                FakerUtil.generateRandomInt(20),
                FakerUtil.generateRandomInt(50),
                FakerUtil.generateWordPackName(platform),
                null,
                null,
                null
        );
    }

    public static WordPack generateWordPack(Platform platform) {
        return new WordPack(
                FakerUtil.generateWordPackName(platform),
                FakerUtil.generateWordPackDescription(),
                FakerUtil.generateWordPackCategory(platform),
                platform
        );
    }

    public static WordData generateWordData(List<WordPack> wordPacks, Platform platform) {
        return new WordData(
                FakerUtil.generateId(),
                FakerUtil.generateNameChineseSimplified(),
                FakerUtil.generateNameChineseTraditional(),
                FakerUtil.generatePinyin(),
                FakerUtil.generateNameEnglish(),
                FakerUtil.generateNameRussian(),
                wordPacks,
                platform
        );
    }

    public static List<WordData> generateWordData(List<WordPack> wordPacks, Platform platform, int number) {
        List<WordData> wordData = new ArrayList<>();
        for (int i = 0; i < number; i++) {
            wordData.add(generateWordData(wordPacks, platform));
        }
        return wordData;
    }

    // Statistics

    public static RoleStatistics generateRoleStatistics(RoleName roleName) {
        return new RoleStatistics(
                FakerUtil.generateId(),
                roleName,
                FakerUtil.generateRandomLong(100L),
                LocalDate.now(),
                FakerUtil.generateRandomLong(100L, 150L)
        );
    }

    public static ReviewStatisticsDTO generateReviewStatisticsDTO(Platform platform) {
        return new ReviewStatisticsDTO(
                FakerUtil.generateId(),
                FakerUtil.generateWordPackName(platform),
                FakerUtil.generateRandomInt(100),
                FakerUtil.generateRandomInt(100),
                FakerUtil.generateRandomInt(100)
        );
    }
}
