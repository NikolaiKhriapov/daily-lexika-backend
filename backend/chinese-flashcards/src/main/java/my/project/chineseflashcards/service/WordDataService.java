package my.project.chineseflashcards.service;

import lombok.RequiredArgsConstructor;
import my.project.chineseflashcards.exception.ResourceNotFoundException;
import my.project.chineseflashcards.model.entity.WordData;
import my.project.chineseflashcards.repository.WordDataRepository;
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