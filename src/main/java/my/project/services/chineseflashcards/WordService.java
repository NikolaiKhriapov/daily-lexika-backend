package my.project.services.chineseflashcards;

import lombok.RequiredArgsConstructor;
import my.project.models.dto.chineseflashcards.WordStatisticsDTO;
import my.project.models.entity.chineseflashcards.Status;
import my.project.models.entity.chineseflashcards.Word;
import my.project.repositories.chineseflashcards.WordRepository;
import my.project.services.user.AuthenticationService;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WordService {

    private final WordRepository wordRepository;
    private final AuthenticationService authenticationService;

    public WordStatisticsDTO getWordStatistics() {
        Long userId = authenticationService.getAuthenticatedUser().id();
        return new WordStatisticsDTO(wordRepository.countByUserIdAndStatusEquals(userId, Status.KNOWN));
    }

    public List<Word> saveAll(List<Word> words) {
        return wordRepository.saveAll(words);
    }

    public List<Word> findByUserIdAndWordDataIdIn(Long userId, List<Long> wordDataIds) {
        return wordRepository.findByUserIdAndWordDataIdIn(userId, wordDataIds);
    }

    public List<Word> findByUserIdAndWordDataIdInAndStatusIn(Long userId, List<Long> wordDataIds, List<Status> status, Pageable pageable) {
        return wordRepository.findByUserIdAndWordDataIdInAndStatusIn(userId, wordDataIds, status, pageable);
    }

    public Integer countByUserIdAndWordDataIdInAndStatusEquals(Long userId, List<Long> wordDataIds, Status status) {
        return wordRepository.countByUserIdAndWordDataIdInAndStatusEquals(userId, wordDataIds, status);
    }

    public List<Word> findByUserIdAndWordDataIdInAndStatusInAndPeriodBetweenOrdered(Long userId, List<Long> wordDataIds, List<Status> statuses, Pageable pageable) {
        return wordRepository.findByUserIdAndWordDataIdInAndStatusInAndPeriodBetweenOrdered(userId, wordDataIds, statuses, pageable);
    }

    public void deleteAllByUserId(Long userId) {
        wordRepository.deleteAllByUserId(userId);
    }
}