package my.project.vocabulary.service;

import lombok.RequiredArgsConstructor;
import my.project.vocabulary.model.dto.WordStatisticsDTO;
import my.project.vocabulary.model.entity.Status;
import my.project.vocabulary.repository.WordRepository;
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
}