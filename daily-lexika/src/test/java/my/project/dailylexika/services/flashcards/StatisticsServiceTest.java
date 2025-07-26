package my.project.dailylexika.services.flashcards;

import my.project.dailylexika.config.AbstractUnitTest;
import my.project.dailylexika.flashcard.service.ReviewService;
import my.project.dailylexika.flashcard.service.StatisticsService;
import my.project.library.dailylexika.dtos.flashcards.ReviewDto;
import my.project.library.dailylexika.dtos.flashcards.ReviewStatisticsDto;
import my.project.library.dailylexika.dtos.flashcards.StatisticsDto;
import my.project.dailylexika.flashcard.model.entities.Word;
import my.project.dailylexika.user.model.entities.RoleStatistics;
import my.project.dailylexika.user.model.entities.User;
import my.project.dailylexika.flashcard.persistence.WordRepository;
import my.project.dailylexika.user.service.RoleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.util.List;

import static my.project.library.dailylexika.enumerations.Platform.CHINESE;
import static my.project.library.dailylexika.enumerations.Status.KNOWN;
import static my.project.library.dailylexika.enumerations.RoleName.USER_CHINESE;
import static my.project.dailylexika.util.data.TestDataUtil.*;
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
