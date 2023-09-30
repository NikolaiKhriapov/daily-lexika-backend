package my.project.chineseflashcards.service;

import lombok.RequiredArgsConstructor;
import my.project.chineseflashcards.model.dto.WordStatisticsDTO;
import my.project.chineseflashcards.model.entity.Status;
import my.project.chineseflashcards.repository.WordRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WordService {

    private final WordRepository wordRepository;

    public WordStatisticsDTO getWordStatistics(Long userId) {
        return new WordStatisticsDTO(
                wordRepository.findAllByUserIdAndStatusIs(userId, Status.KNOWN).size()
        );
    }

    public void deleteAllByUserId(Long userId) {
        wordRepository.deleteAllByUserId(userId);
    }
}