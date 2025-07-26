package my.project.dailylexika.flashcard.service;

import my.project.dailylexika.flashcard.model.entities.Word;
import my.project.library.dailylexika.dtos.flashcards.WordDto;
import my.project.library.dailylexika.enumerations.Platform;
import my.project.library.dailylexika.enumerations.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface WordService {
    WordDto getByWordDataId(Integer wordDataId);
    Page<WordDto> getByUserIdAndWordDataIdIn(Integer userId, List<Integer> wordDataIds, Pageable pageable);
    Page<WordDto> getPageByWordPackName(String wordPackName, Pageable pageable);
    Page<WordDto> getPageByStatus(Status status, Pageable pageable);
    List<Word> getAllByUserIdAndWordDataIdInAndStatusNewRandomLimited(Integer userId, List<Integer> wordDataIds, Integer limit);
    List<Word> getAllByUserIdAndWordDataIdInAndStatusInReviewAndPeriodBetweenOrderedDescLimited(Integer userId, List<Integer> wordDataIds, Integer limit);
    List<Word> getAllByUserIdAndWordDataIdInAndStatusKnownAndPeriodBetweenOrderedAscLimited(Integer userId, List<Integer> wordDataIds, Integer limit);
    List<WordDto> getAllByUserIdAndStatusAndWordData_Platform(Integer userId, Status status, Platform platform);
    void createAllWordsForUserAndPlatform(Integer userId, Platform platform);
    void updateWordsForUser(Integer userId, List<Integer> wordDataIds);
    void deleteAllByWordDataId(List<Integer> wordDataIds);
    void deleteAllByUserIdAndPlatform(Integer userId, Platform platform);
    WordDto getWordOfTheDay();
    Integer countByUserIdAndWordData_IdInAndStatus(Integer userId, List<Integer> wordDataIds, Status status);
}
