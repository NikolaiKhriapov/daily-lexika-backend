package my.project.dailylexika.util.data;

import my.project.library.util.datetime.DateUtil;
import my.project.library.dailylexika.dtos.flashcards.ReviewDto;
import my.project.library.dailylexika.dtos.flashcards.ReviewStatisticsDto;
import my.project.library.dailylexika.dtos.user.AuthenticationRequest;
import my.project.library.dailylexika.dtos.user.RegistrationRequest;
import my.project.library.dailylexika.enumerations.Platform;
import my.project.library.dailylexika.enumerations.Status;
import my.project.dailylexika.flashcard.model.entities.Word;
import my.project.dailylexika.flashcard.model.entities.WordData;
import my.project.dailylexika.flashcard.model.entities.WordPack;
import my.project.library.dailylexika.enumerations.RoleName;
import my.project.dailylexika.user.model.entities.RoleStatistics;
import my.project.dailylexika.user.model.entities.User;
import org.mockito.Mockito;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDate;
import java.util.*;

import static my.project.dailylexika.util.CommonConstants.ENCODED_PASSWORD;
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
                .id(FakerUtil.generateIntId())
                .name(registrationRequest.name())
                .email(registrationRequest.email())
                .password(ENCODED_PASSWORD)
                .role(roleName)
                .roleStatistics(new HashSet<>(Set.of(new RoleStatistics(roleName))))
                .build();
    }

    public static User generateUser(AuthenticationRequest authenticationRequest, RoleName roleName) {
        return User.builder()
                .id(FakerUtil.generateIntId())
                .name(FakerUtil.generateName())
                .email(authenticationRequest.email())
                .password(ENCODED_PASSWORD)
                .role(roleName)
                .roleStatistics(new HashSet<>(Set.of(new RoleStatistics(roleName))))
                .dateOfRegistration(DateUtil.nowInUtc())
                .build();
    }

    public static User generateUser(RoleName roleName) {
        return User.builder()
                .id(FakerUtil.generateIntId())
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

    public static ReviewDto generateReviewDTO(Platform platform) {
        return new ReviewDto(
                null,
                null,
                FakerUtil.generateRandomInt(20),
                FakerUtil.generateRandomInt(50),
                FakerUtil.generateWordPackDTO(platform),
                null,
                70,
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
                FakerUtil.generateIntId(),
                FakerUtil.generateNameChinese(),
                FakerUtil.generateTranscription(),
                FakerUtil.generateNameEnglish(),
                FakerUtil.generateNameRussian(),
                FakerUtil.generateDefinition(),
                FakerUtil.generateExamples(),
                wordPacks,
                LocalDate.now().plusDays(FakerUtil.generateRandomLong(0L, 100L)),
                platform
        );
    }

    public static Word generateWord(Platform platform, Status status) {
        List<WordPack> listOfWordPacks = List.of(generateWordPack(platform), generateWordPack(platform));
        Word word = new Word(FakerUtil.generateIntId(), generateWordData(listOfWordPacks, platform));
        word.setStatus(status);
        switch (status) {
            case NEW -> word.setTotalStreak((short) 0);
            case IN_REVIEW -> word.setTotalStreak((short) FakerUtil.generateRandomInt(4));
            case KNOWN -> word.setTotalStreak((short) 5);
        }
        return word;
    }

    // Statistics

    public static RoleStatistics generateRoleStatistics(RoleName roleName) {
        return new RoleStatistics(
                FakerUtil.generateLongId(),
                roleName,
                FakerUtil.generateRandomLong(100L),
                DateUtil.nowInUtc(),
                FakerUtil.generateRandomLong(100L, 150L)
        );
    }

    public static ReviewStatisticsDto generateReviewStatisticsDTO(Platform platform) {
        return new ReviewStatisticsDto(
                FakerUtil.generateLongId(),
                FakerUtil.generateWordPackName(platform),
                FakerUtil.generateRandomInt(100),
                FakerUtil.generateRandomInt(100),
                FakerUtil.generateRandomInt(100)
        );
    }
}
