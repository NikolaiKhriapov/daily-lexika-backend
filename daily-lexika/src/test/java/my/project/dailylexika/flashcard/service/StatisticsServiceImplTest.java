package my.project.dailylexika.flashcard.service;

import my.project.dailylexika.config.AbstractUnitTest;
import my.project.dailylexika.flashcard.service.impl.StatisticsServiceImpl;
import my.project.dailylexika.user._public.PublicRoleService;
import my.project.dailylexika.user._public.PublicUserService;
import my.project.library.dailylexika.dtos.flashcards.ReviewDto;
import my.project.library.dailylexika.dtos.flashcards.ReviewStatisticsDto;
import my.project.library.dailylexika.dtos.flashcards.StatisticsDto;
import my.project.library.dailylexika.dtos.flashcards.WordDataDto;
import my.project.library.dailylexika.dtos.flashcards.WordDto;
import my.project.library.dailylexika.dtos.user.RoleStatisticsDto;
import my.project.library.dailylexika.dtos.user.UserDto;
import my.project.library.dailylexika.enumerations.Platform;
import my.project.library.dailylexika.enumerations.RoleName;
import my.project.library.dailylexika.enumerations.Status;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;

import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static my.project.library.dailylexika.enumerations.Platform.CHINESE;
import static my.project.library.dailylexika.enumerations.Platform.ENGLISH;
import static my.project.library.dailylexika.enumerations.RoleName.USER_CHINESE;
import static my.project.library.dailylexika.enumerations.RoleName.USER_ENGLISH;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.BDDMockito.given;

class StatisticsServiceImplTest extends AbstractUnitTest {

    private static final Integer USER_ID = 1;

    private StatisticsServiceImpl underTest;
    @Mock
    private ReviewService reviewService;
    @Mock
    private WordService wordService;
    @Mock
    private PublicUserService userService;
    @Mock
    private PublicRoleService roleService;

    @BeforeEach
    void setUp() {
        underTest = new StatisticsServiceImpl(
                reviewService,
                wordService,
                userService,
                roleService
        );
    }

    @ParameterizedTest
    @MethodSource("my.project.dailylexika.flashcard.service.StatisticsServiceImplTest$TestDataSource#getStatistics_returnsEnglishStatsWithZeroCharacters")
    void getStatistics_returnsEnglishStatsWithZeroCharacters(Platform platform, RoleName roleName) {
        // Given
        mockUser(USER_ID, roleName);
        RoleStatisticsDto roleStats = new RoleStatisticsDto(1L, roleName, 2L, null, 5L);
        List<WordDto> wordsKnown = List.of(
                buildWordDto(1, "你好", platform),
                buildWordDto(2, "学习", platform)
        );

        given(roleService.getRoleStatistics()).willReturn(roleStats);
        given(roleService.getPlatformByRoleName(roleName)).willReturn(platform);
        given(wordService.getAllByUserIdAndStatusAndWordData_Platform(USER_ID, Status.KNOWN, platform)).willReturn(wordsKnown);
        given(reviewService.getAllReviews()).willReturn(List.of());

        // When
        StatisticsDto actual = underTest.getStatistics();

        // Then
        assertThat(actual.currentStreak()).isEqualTo(2L);
        assertThat(actual.recordStreak()).isEqualTo(5L);
        assertThat(actual.wordsKnown()).isEqualTo(2);
        assertThat(actual.charactersKnown()).isEqualTo(0);
        assertThat(actual.listOfReviewStatisticsDto()).isEmpty();
    }

    @ParameterizedTest
    @MethodSource("my.project.dailylexika.flashcard.service.StatisticsServiceImplTest$TestDataSource#getStatistics_returnsChineseStatsWithUniqueCharacterCount")
    void getStatistics_returnsChineseStatsWithUniqueCharacterCount(Platform platform, RoleName roleName) {
        // Given
        mockUser(USER_ID, roleName);
        RoleStatisticsDto roleStats = new RoleStatisticsDto(1L, roleName, 1L, null, 3L);
        List<WordDto> wordsKnown = List.of(
                buildWordDto(1, "你好", platform),
                buildWordDto(2, "好学", platform)
        );

        given(roleService.getRoleStatistics()).willReturn(roleStats);
        given(roleService.getPlatformByRoleName(roleName)).willReturn(platform);
        given(wordService.getAllByUserIdAndStatusAndWordData_Platform(USER_ID, Status.KNOWN, platform)).willReturn(wordsKnown);
        given(reviewService.getAllReviews()).willReturn(List.of());

        // When
        StatisticsDto actual = underTest.getStatistics();

        // Then
        assertThat(actual.wordsKnown()).isEqualTo(2);
        assertThat(actual.charactersKnown()).isEqualTo(3);
        assertThat(actual.listOfReviewStatisticsDto()).isEmpty();
    }

    @ParameterizedTest
    @MethodSource("my.project.dailylexika.flashcard.service.StatisticsServiceImplTest$TestDataSource#getStatistics_returnsEmptyReviewsWhenNoReviews")
    void getStatistics_returnsEmptyReviewsWhenNoReviews(Platform platform, RoleName roleName) {
        // Given
        mockUser(USER_ID, roleName);
        RoleStatisticsDto roleStats = new RoleStatisticsDto(1L, roleName, 0L, null, 0L);

        given(roleService.getRoleStatistics()).willReturn(roleStats);
        given(roleService.getPlatformByRoleName(roleName)).willReturn(platform);
        given(wordService.getAllByUserIdAndStatusAndWordData_Platform(USER_ID, Status.KNOWN, platform)).willReturn(List.of());
        given(reviewService.getAllReviews()).willReturn(List.of());

        // When
        StatisticsDto actual = underTest.getStatistics();

        // Then
        assertThat(actual.wordsKnown()).isZero();
        assertThat(actual.charactersKnown()).isZero();
        assertThat(actual.listOfReviewStatisticsDto()).isEmpty();
    }

    @ParameterizedTest
    @MethodSource("my.project.dailylexika.flashcard.service.StatisticsServiceImplTest$TestDataSource#getStatistics_mapsReviewStatisticsForAllReviewIds")
    void getStatistics_mapsReviewStatisticsForAllReviewIds(Platform platform, RoleName roleName) {
        // Given
        mockUser(USER_ID, roleName);
        RoleStatisticsDto roleStats = new RoleStatisticsDto(1L, roleName, 0L, null, 0L);
        ReviewDto review1 = new ReviewDto(11L, 1L, null, null, null, null, null, null, null, null);
        ReviewDto review2 = new ReviewDto(22L, 1L, null, null, null, null, null, null, null, null);
        ReviewStatisticsDto stats1 = new ReviewStatisticsDto(11L, 1L, 1, 2, 3);
        ReviewStatisticsDto stats2 = new ReviewStatisticsDto(22L, 2L, 4, 5, 6);

        given(roleService.getRoleStatistics()).willReturn(roleStats);
        given(roleService.getPlatformByRoleName(roleName)).willReturn(platform);
        given(wordService.getAllByUserIdAndStatusAndWordData_Platform(USER_ID, Status.KNOWN, platform)).willReturn(List.of());
        given(reviewService.getAllReviews()).willReturn(List.of(review1, review2));
        given(reviewService.getReviewStatistics(review1.id())).willReturn(stats1);
        given(reviewService.getReviewStatistics(review2.id())).willReturn(stats2);

        // When
        StatisticsDto actual = underTest.getStatistics();

        // Then
        assertThat(actual.listOfReviewStatisticsDto()).containsExactly(stats1, stats2);
    }

    private void mockUser(Integer id, RoleName roleName) {
        UserDto user = new UserDto(id, "User", "user@test.com", roleName, Set.of(), null, null, null);
        given(userService.getUser()).willReturn(user);
    }

    private WordDto buildWordDto(Integer id, String nameChinese, Platform platform) {
        WordDataDto wordDataDto = new WordDataDto(id, nameChinese, null, null, null, null, null, null, null, platform);
        return new WordDto((long) id, USER_ID, wordDataDto, Status.KNOWN, (short) 0, (short) 0, (short) 0, null);
    }

    private static class TestDataSource {

        public static Stream<Arguments> getStatistics_returnsEnglishStatsWithZeroCharacters() {
            return Stream.of(
                    arguments(ENGLISH, USER_ENGLISH)
            );
        }

        public static Stream<Arguments> getStatistics_returnsChineseStatsWithUniqueCharacterCount() {
            return Stream.of(
                    arguments(CHINESE, USER_CHINESE)
            );
        }

        public static Stream<Arguments> getStatistics_returnsEmptyReviewsWhenNoReviews() {
            return Stream.of(
                    arguments(ENGLISH, USER_ENGLISH),
                    arguments(CHINESE, USER_CHINESE)
            );
        }

        public static Stream<Arguments> getStatistics_mapsReviewStatisticsForAllReviewIds() {
            return getStatistics_returnsEmptyReviewsWhenNoReviews();
        }
    }
}
