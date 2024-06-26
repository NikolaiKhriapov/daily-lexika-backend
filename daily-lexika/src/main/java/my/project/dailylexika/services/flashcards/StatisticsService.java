package my.project.dailylexika.services.flashcards;

import lombok.RequiredArgsConstructor;
import my.project.library.dailylexika.dtos.flashcards.ReviewDto;
import my.project.library.dailylexika.dtos.flashcards.ReviewStatisticsDto;
import my.project.library.dailylexika.dtos.flashcards.StatisticsDto;
import my.project.library.dailylexika.enumerations.Platform;
import my.project.library.dailylexika.enumerations.Status;
import my.project.dailylexika.entities.flashcards.Word;
import my.project.dailylexika.entities.user.RoleStatistics;
import my.project.dailylexika.entities.user.User;
import my.project.dailylexika.repositories.flashcards.WordRepository;
import my.project.dailylexika.services.user.RoleService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class StatisticsService {

    private final WordRepository wordRepository;
    private final ReviewService reviewService;
    private final RoleService roleService;

    public StatisticsDto getStatistics() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        RoleStatistics roleStatistics = roleService.getRoleStatistics();
        Platform platform = roleService.getPlatformByRoleName(user.getRole());

        List<Word> wordsKnown = wordRepository.findByUserIdAndStatusAndWordData_Platform(user.getId(), Status.KNOWN, platform);

        Integer charactersKnownCount = 0;
        if (platform == Platform.CHINESE) {
            charactersKnownCount = countUniqueCharacters(wordsKnown);
        }

        List<ReviewDto> listOfReviews = reviewService.getAllReviews();
        List<ReviewStatisticsDto> listOfReviewStatisticsDto = listOfReviews.stream()
                .map(reviewDTO -> reviewService.getReviewStatistics(reviewDTO.id()))
                .toList();

        return new StatisticsDto(
                roleStatistics.getCurrentStreak(),
                roleStatistics.getRecordStreak(),
                wordsKnown.size(),
                charactersKnownCount,
                listOfReviewStatisticsDto
        );
    }

    private Integer countUniqueCharacters(List<Word> wordsKnown) {
        Set<Character> uniqueCharacters = new HashSet<>();

        for (Word word : wordsKnown) {
            char[] characters = word.getWordData().getNameChinese().toCharArray();
            for (char character : characters) {
                uniqueCharacters.add(character);
            }
        }

        return uniqueCharacters.size();
    }
}
