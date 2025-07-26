package my.project.dailylexika.flashcard.service.impl;

import lombok.RequiredArgsConstructor;
import my.project.dailylexika.flashcard.service.ReviewService;
import my.project.dailylexika.flashcard.service.StatisticsService;
import my.project.dailylexika.flashcard.service.WordService;
import my.project.dailylexika.user._public.PublicRoleService;
import my.project.dailylexika.user._public.PublicUserService;
import my.project.library.dailylexika.dtos.flashcards.ReviewDto;
import my.project.library.dailylexika.dtos.flashcards.ReviewStatisticsDto;
import my.project.library.dailylexika.dtos.flashcards.StatisticsDto;
import my.project.library.dailylexika.dtos.flashcards.WordDto;
import my.project.library.dailylexika.dtos.user.RoleStatisticsDto;
import my.project.library.dailylexika.dtos.user.UserDto;
import my.project.library.dailylexika.enumerations.Platform;
import my.project.library.dailylexika.enumerations.Status;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class StatisticsServiceImpl implements StatisticsService {

    private final ReviewService reviewService;
    private final WordService wordService;
    private final PublicUserService userService;
    private final PublicRoleService roleService;

    @Override
    public StatisticsDto getStatistics() {
        UserDto user = userService.getUser();
        RoleStatisticsDto roleStatistics = roleService.getRoleStatistics();
        Platform platform = roleService.getPlatformByRoleName(user.role());

        List<WordDto> wordsKnown = wordService.getAllByUserIdAndStatusAndWordData_Platform(user.id(), Status.KNOWN, platform);

        Integer charactersKnownCount = 0;
        if (platform == Platform.CHINESE) {
            charactersKnownCount = countUniqueCharacters(wordsKnown);
        }

        List<ReviewDto> listOfReviews = reviewService.getAllReviews();
        List<ReviewStatisticsDto> listOfReviewStatisticsDto = listOfReviews.stream()
                .map(reviewDto -> reviewService.getReviewStatistics(reviewDto.id()))
                .toList();

        return new StatisticsDto(
                roleStatistics.currentStreak(),
                roleStatistics.recordStreak(),
                wordsKnown.size(),
                charactersKnownCount,
                listOfReviewStatisticsDto
        );
    }

    private Integer countUniqueCharacters(List<WordDto> wordsKnown) {
        Set<Character> uniqueCharacters = new HashSet<>();

        for (WordDto word : wordsKnown) {
            char[] characters = word.wordDataDto().nameChinese().toCharArray();
            for (char character : characters) {
                uniqueCharacters.add(character);
            }
        }

        return uniqueCharacters.size();
    }
}
