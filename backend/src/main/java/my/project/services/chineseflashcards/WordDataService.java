package my.project.services.chineseflashcards;

import lombok.RequiredArgsConstructor;
import my.project.exception.ResourceNotFoundException;
import my.project.models.entity.chineseflashcards.WordData;
import my.project.repositories.chineseflashcards.WordDataRepository;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class WordDataService {

    private final WordDataRepository wordDataRepository;
    private final MessageSource messageSource;

    public WordData getWordData(Long wordDataId) {
        return wordDataRepository.findById(wordDataId)
                .orElseThrow(() -> new ResourceNotFoundException(messageSource.getMessage(
                        "exception.wordData.notFound", null, Locale.getDefault())));
    }
}