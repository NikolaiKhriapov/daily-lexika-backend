package my.project.vocabulary.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import my.project.vocabulary.exception.ResourceNotFoundException;
import my.project.vocabulary.mapper.WordPackMapper;
import my.project.vocabulary.model.dto.WordPackDTO;
import my.project.vocabulary.model.entity.Word;
import my.project.vocabulary.model.dto.WordDTO;
import my.project.vocabulary.mapper.WordMapper;
import my.project.vocabulary.model.entity.WordPack;
import my.project.vocabulary.repository.WordPackRepository;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class WordPackService {

    private final WordPackRepository wordPackRepository;
    private final WordPackMapper wordPackMapper;
    private final WordMapper wordMapper;
    private final MessageSource messageSource;

    @Transactional
    public List<WordPackDTO> getAllWordPacks() {
        List<WordPack> allWordPacks = wordPackRepository.findAll();

        List<WordPackDTO> allWordPackDTOs = new ArrayList<>();
        for (WordPack oneWordPack : allWordPacks) {
            allWordPackDTOs.add(wordPackMapper.toDTO(oneWordPack));
        }

        return allWordPackDTOs;
    }

    @Transactional
    public WordPack getWordPack(String name) {
        return wordPackRepository.findById(name)
                .orElseThrow(() -> new ResourceNotFoundException(messageSource.getMessage(
                        "exception.wordPack.notFound", null, Locale.getDefault())));
    }

    @Transactional
    public List<WordDTO> getAllWordsForWordPack(String wordPackName) {
        List <WordDTO> allWordsForWordPack = new ArrayList<>();

        WordPack wordPack = getWordPack(wordPackName);

        for (Word oneWord : wordPack.getListOfWords()) {
            allWordsForWordPack.add(wordMapper.toDTO(oneWord));
        }

        return allWordsForWordPack;
    }
}
