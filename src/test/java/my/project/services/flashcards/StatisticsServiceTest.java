package my.project.services.flashcards;

import my.project.config.AbstractUnitTest;
import my.project.models.dtos.flashcards.ReviewDto;
import my.project.models.dtos.flashcards.ReviewStatisticsDto;
import my.project.models.dtos.flashcards.StatisticsDto;
import my.project.models.entities.flashcards.Word;
import my.project.models.entities.user.RoleStatistics;
import my.project.models.entities.user.User;
import my.project.repositories.flashcards.WordRepository;
import my.project.services.user.RoleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.util.List;

import static my.project.models.entities.enumerations.Platform.CHINESE;
import static my.project.models.entities.enumerations.Status.KNOWN;
import static my.project.models.entities.user.RoleName.USER_CHINESE;
import static my.project.util.data.TestDataUtil.*;
import static my.project.util.data.TestDataUtil.generateReviewStatisticsDTO;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

class StatisticsServiceTest extends AbstractUnitTest {

    private StatisticsService underTest;
    @Mock
    private WordRepository wordRepository;
    @Mock
    private ReviewService reviewService;
    @Mock
    private RoleService roleService;

    @BeforeEach
    void setUp() {
        underTest = new StatisticsService(wordRepository, reviewService, roleService);
    }

    @Test
    void getStatistics() {
        // Given
        User user = generateUser(USER_CHINESE);
        RoleStatistics roleStatistics = generateRoleStatistics(user.getRole());
        List<ReviewDto> listOfReviewDto = List.of(generateReviewDTO(CHINESE), generateReviewDTO(CHINESE));
        ReviewStatisticsDto reviewStatisticsDTO = generateReviewStatisticsDTO(CHINESE);
        List<Word> wordsKnown = List.of(generateWord(CHINESE, KNOWN), generateWord(CHINESE, KNOWN));

        mockAuthentication(user);
        given(roleService.getRoleStatistics()).willReturn(roleStatistics);
        given(roleService.getPlatformByRoleName(any())).willCallRealMethod();
        given(reviewService.getAllReviews()).willReturn(listOfReviewDto);
        given(reviewService.getReviewStatistics(any())).willReturn(reviewStatisticsDTO);
        given(wordRepository.findByUserIdAndStatusAndWordData_Platform(any(), any(), any())).willReturn(wordsKnown);

        // When
        StatisticsDto statisticsDTO = underTest.getStatistics();

        // Then
        assertThat(statisticsDTO.currentStreak()).isEqualTo(roleStatistics.getCurrentStreak());
        assertThat(statisticsDTO.recordStreak()).isEqualTo(roleStatistics.getRecordStreak());
        assertThat(statisticsDTO.wordsKnown()).isEqualTo(wordsKnown.size());
        assertThat(statisticsDTO.listOfReviewStatisticsDto()).asList().hasSize((listOfReviewDto.size()));
    }
}
