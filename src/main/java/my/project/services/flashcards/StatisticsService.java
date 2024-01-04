package my.project.services.flashcards;

import lombok.RequiredArgsConstructor;
import my.project.models.dto.flashcards.ReviewDTO;
import my.project.models.dto.flashcards.ReviewStatisticsDTO;
import my.project.models.dto.flashcards.StatisticsDTO;
import my.project.models.entity.enumeration.Status;
import my.project.models.entity.user.User;
import my.project.repositories.flashcards.WordRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class StatisticsService {

    private final WordRepository wordRepository;
    private final ReviewService reviewService;

    public StatisticsDTO getStatistics() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<ReviewDTO> listOfReviews = reviewService.getAllReviews();
        List<ReviewStatisticsDTO> listOfReviewStatisticsDTO = listOfReviews.stream()
                .map(reviewDTO -> reviewService.getReviewStatistics(reviewDTO.id()))
                .toList();

        return new StatisticsDTO(
                user.getCurrentStreak(),
                user.getRecordStreak(),
                wordRepository.countByUserIdAndStatusEquals(user.getId(), Status.KNOWN),
                listOfReviewStatisticsDTO
        );
    }
}
