package my.project.dailylexika.flashcard.service;

import my.project.dailylexika.flashcard.model.entities.Word;
import my.project.library.dailylexika.dtos.flashcards.WordDto;
import my.project.library.dailylexika.enumerations.Platform;
import my.project.library.dailylexika.enumerations.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface WordService {
    Page<WordDto> getPageOfWordsByStatus(Status status, Pageable pageable);
    WordDto getWordOfTheDay();
    WordDto findByWordDataId(Integer wordDataId);
    void createAllWordsForUserAndPlatform(Integer userId, Platform platform);
    void updateWordsForUser(Integer userId, List<Integer> wordDataIds);
    Page<WordDto> findByUserIdAndWordDataIdIn(Integer userId, List<Integer> wordDataIds, Pageable pageable);
    Page<WordDto> getPageOfWordsByWordPackName(String wordPackName, Pageable pageable);
    List<Word> findAllByUserIdAndWordDataIdInAndStatusNewRandomLimited(Integer userId, List<Integer> wordDataIds, Integer limit);
    Integer countByUserIdAndWordData_IdInAndStatus(Integer userId, List<Integer> wordDataIds, Status status);
    List<Word> findAllByUserIdAndWordDataIdInAndStatusInReviewAndPeriodBetweenOrderedDescLimited(Integer userId, List<Integer> wordDataIds, Integer limit);
    List<Word> findAllByUserIdAndWordDataIdInAndStatusKnownAndPeriodBetweenOrderedAscLimited(Integer userId, List<Integer> wordDataIds, Integer limit);
    List<WordDto> getAllWordsByUserIdAndStatusAndWordData_Platform(Integer userId, Status status, Platform platform);
    void deleteAllByWordDataId(List<Integer> wordDataIds);
    void deleteAllByUserIdAndPlatform(Integer userId, Platform platform);
}
