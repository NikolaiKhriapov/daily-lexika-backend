package my.project.services.flashcards;

import lombok.RequiredArgsConstructor;
import my.project.models.dto.flashcards.ReviewDTO;
import my.project.models.dto.flashcards.ReviewStatisticsDTO;
import my.project.models.dto.flashcards.StatisticsDTO;
import my.project.models.entity.enumeration.Platform;
import my.project.models.entity.enumeration.Status;
import my.project.models.entity.user.RoleStatistics;
import my.project.models.entity.user.User;
import my.project.repositories.flashcards.WordRepository;
import my.project.services.user.RoleService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class StatisticsService {

    private final WordRepository wordRepository;
    private final ReviewService reviewService;
    private final RoleService roleService;

    public StatisticsDTO getStatistics() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        RoleStatistics roleStatistics = roleService.getRoleStatistics();
        Platform platform = roleService.getPlatformByRoleName(user.getRole());

        List<ReviewDTO> listOfReviews = reviewService.getAllReviews();
        List<ReviewStatisticsDTO> listOfReviewStatisticsDTO = listOfReviews.stream()
                .map(reviewDTO -> reviewService.getReviewStatistics(reviewDTO.id()))
                .toList();

        return new StatisticsDTO(
                roleStatistics.getCurrentStreak(),
                roleStatistics.getRecordStreak(),
                wordRepository.countByUserIdAndStatusEqualsAndPlatformEquals(user.getId(), Status.KNOWN, platform),
                listOfReviewStatisticsDTO
        );
    }
}
