package my.project.services.flashcards;

import lombok.RequiredArgsConstructor;
import my.project.exception.ResourceNotFoundException;
import my.project.models.dto.flashcards.WordDataDTO;
import my.project.models.entity.flashcards.WordData;
import my.project.models.entity.flashcards.WordPack;
import my.project.models.mapper.flashcards.WordDataMapper;
import my.project.repositories.flashcards.WordDataRepository;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class WordDataService {

    private final WordDataRepository wordDataRepository;
    private final WordDataMapper wordDataMapper;
    private final MessageSource messageSource;

    public WordData findById(Long wordDataId) {
        return wordDataRepository.findById(wordDataId)
                .orElseThrow(() -> new ResourceNotFoundException(messageSource.getMessage(
                        "exception.wordData.notFound", null, Locale.getDefault())));
    }

    public List<WordData> findAll() {
        return wordDataRepository.findAll();
    }

    public List<WordData> findAllByWordPack(WordPack wordPack) {
        return wordDataRepository.findAllByWordPack(wordPack);
    }

    public WordData save(WordData wordData) {
        return wordDataRepository.save(wordData);
    }

    public void saveAll(List<WordData> listOfWordData) {
        wordDataRepository.saveAll(listOfWordData);
    }

    public List<WordDataDTO> getAllWordData() {
        return wordDataMapper.toDTOList(findAll());
    }

    public List<Long> getListOfAllWordDataIdsByWordPackName(String wordPackName) {
        return wordDataRepository.findAllWordDataIdsByWordPackName(wordPackName);
    }
}
