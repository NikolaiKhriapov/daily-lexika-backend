package my.project.services.flashcards;

import my.project.config.AbstractUnitTest;
import my.project.models.dto.flashcards.ReviewDTO;
import my.project.models.dto.flashcards.ReviewStatisticsDTO;
import my.project.models.dto.flashcards.StatisticsDTO;
import my.project.models.entity.user.RoleStatistics;
import my.project.models.entity.user.User;
import my.project.repositories.flashcards.WordRepository;
import my.project.services.user.RoleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.util.List;

import static my.project.models.entity.enumeration.Platform.CHINESE;
import static my.project.models.entity.user.RoleName.USER_CHINESE;
import static my.project.util.data.FakerUtil.generateRandomInt;
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
        List<ReviewDTO> listOfReviewDTO = List.of(generateReviewDTO(CHINESE), generateReviewDTO(CHINESE));
        ReviewStatisticsDTO reviewStatisticsDTO = generateReviewStatisticsDTO(CHINESE);
        int wordsKnown = generateRandomInt(5000);

        mockAuthentication(user);
        given(roleService.getRoleStatistics()).willReturn(roleStatistics);
        given(roleService.getPlatformByRoleName(any())).willCallRealMethod();
        given(reviewService.getAllReviews()).willReturn(listOfReviewDTO);
        given(reviewService.getReviewStatistics(any())).willReturn(reviewStatisticsDTO);
        given(wordRepository.countByUserIdAndStatusEqualsAndPlatformEquals(any(), any(), any())).willReturn(wordsKnown);

        // When
        StatisticsDTO statisticsDTO = underTest.getStatistics();

        // Then
        assertThat(statisticsDTO.currentStreak()).isEqualTo(roleStatistics.getCurrentStreak());
        assertThat(statisticsDTO.recordStreak()).isEqualTo(roleStatistics.getRecordStreak());
        assertThat(statisticsDTO.wordsKnown()).isEqualTo(wordsKnown);
        assertThat(statisticsDTO.listOfReviewStatisticsDTO()).asList().hasSize((listOfReviewDTO.size()));
    }
}
