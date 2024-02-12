package my.project.services.flashcards;

import lombok.RequiredArgsConstructor;
import my.project.models.dto.flashcards.ReviewDTO;
import my.project.models.dto.flashcards.ReviewStatisticsDTO;
import my.project.models.dto.flashcards.StatisticsDTO;
import my.project.models.entity.enumeration.Platform;
import my.project.models.entity.enumeration.Status;
import my.project.models.entity.flashcards.Word;
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

        List<Word> wordsKnown = wordRepository.findByUserIdAndStatusEqualsAndPlatformEquals(user.getId(), Status.KNOWN, platform);

        Integer charactersKnownCount = 0;
        if (platform == Platform.CHINESE) {
            charactersKnownCount = countUniqueCharacters(wordsKnown);
        }

        List<ReviewDTO> listOfReviews = reviewService.getAllReviews();
        List<ReviewStatisticsDTO> listOfReviewStatisticsDTO = listOfReviews.stream()
                .map(reviewDTO -> reviewService.getReviewStatistics(reviewDTO.id()))
                .toList();

        return new StatisticsDTO(
                roleStatistics.getCurrentStreak(),
                roleStatistics.getRecordStreak(),
                wordsKnown.size(),
                charactersKnownCount,
                listOfReviewStatisticsDTO
        );
    }

    private Integer countUniqueCharacters(List<Word> wordsKnown) {
        Set<Character> uniqueCharacters = new HashSet<>();

        for (Word word : wordsKnown) {
            char[] characters = word.getWordData().getNameChineseSimplified().toCharArray();
            for (char character : characters) {
                uniqueCharacters.add(character);
            }
        }

        return uniqueCharacters.size();
    }
}
