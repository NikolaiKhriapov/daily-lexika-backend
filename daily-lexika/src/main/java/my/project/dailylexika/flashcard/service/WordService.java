package my.project.dailylexika.flashcard.service;

import my.project.dailylexika.flashcard.model.entities.Word;
import my.project.library.dailylexika.dtos.flashcards.WordDto;
import my.project.library.dailylexika.enumerations.Platform;
import my.project.library.dailylexika.enumerations.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public interface WordService {
    WordDto getByWordDataId(@NotNull Integer wordDataId);
    Page<WordDto> getByUserIdAndWordDataIdIn(@NotNull Integer userId, @NotNull List<Integer> wordDataIds, @NotNull Pageable pageable);
    Page<WordDto> getPageByWordPackName(@NotBlank String wordPackName, @NotNull Pageable pageable);
    Page<WordDto> getPageByStatus(@NotNull Status status, @NotNull Pageable pageable);
    List<Word> getAllByUserIdAndWordDataIdInAndStatusNewRandomLimited(@NotNull Integer userId, @NotNull List<Integer> wordDataIds, @NotNull Integer limit);
    List<Word> getAllByUserIdAndWordDataIdInAndStatusInReviewAndPeriodBetweenOrderedDescLimited(@NotNull Integer userId, @NotNull List<Integer> wordDataIds, @NotNull Integer limit);
    List<Word> getAllByUserIdAndWordDataIdInAndStatusKnownAndPeriodBetweenOrderedAscLimited(@NotNull Integer userId, @NotNull List<Integer> wordDataIds, @NotNull Integer limit);
    List<WordDto> getAllByUserIdAndStatusAndWordData_Platform(@NotNull Integer userId, @NotNull Status status, @NotNull Platform platform);
    void createAllWordsForUserAndPlatform(@NotNull Integer userId, @NotNull Platform platform);
    void updateWordsForUser(@NotNull Integer userId, @NotNull List<Integer> wordDataIds);
    void deleteAllByWordDataId(@NotNull List<Integer> wordDataIds);
    void deleteAllByUserIdAndPlatform(@NotNull Integer userId, @NotNull Platform platform);
    WordDto getWordOfTheDay();
    Integer countByUserIdAndWordData_IdInAndStatus(@NotNull Integer userId, @NotNull List<Integer> wordDataIds, @NotNull Status status);
}
