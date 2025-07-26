package my.project.dailylexika.flashcard.service;

import my.project.dailylexika.config.AbstractUnitTest;
import my.project.dailylexika.flashcard.service.impl.StatisticsServiceImpl;
import my.project.dailylexika.user._public.PublicRoleService;
import my.project.dailylexika.user._public.PublicUserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

class StatisticsServiceImplTest extends AbstractUnitTest {

    private StatisticsServiceImpl underTest;
    @Mock
    private WordService wordService;
    @Mock
    private ReviewService reviewService;
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

    @Test
    void getStatistics() {
        // Given
//        User user = generateUser(USER_CHINESE);
//        RoleStatisticsDto roleStatistics = generateRoleStatistics(user.getRole());
//        List<ReviewDto> listOfReviewDto = List.of(generateReviewDTO(CHINESE), generateReviewDTO(CHINESE));
//        ReviewStatisticsDto reviewStatisticsDTO = generateReviewStatisticsDTO(CHINESE);
//        List<WordDto> wordsKnown = List.of(generateWord(CHINESE, KNOWN), generateWord(CHINESE, KNOWN));
//
//        mockAuthentication(user);
//        given(roleService.getRoleStatistics()).willReturn(roleStatistics);
//        given(roleService.getPlatformByRoleName(any())).willCallRealMethod();
//        given(reviewService.getAllReviews()).willReturn(listOfReviewDto);
//        given(reviewService.getReviewStatistics(any())).willReturn(reviewStatisticsDTO);
//        given(wordService.getAllWordsByUserIdAndStatusAndWordData_Platform(any(), any(), any())).willReturn(wordsKnown);
//
//        // When
//        StatisticsDto statisticsDTO = underTest.getStatistics();
//
//        // Then
//        assertThat(statisticsDTO.currentStreak()).isEqualTo(roleStatistics.currentStreak());
//        assertThat(statisticsDTO.recordStreak()).isEqualTo(roleStatistics.recordStreak());
//        assertThat(statisticsDTO.wordsKnown()).isEqualTo(wordsKnown.size());
//        assertThat(statisticsDTO.listOfReviewStatisticsDto()).asList().hasSize((listOfReviewDto.size()));
    }
}
