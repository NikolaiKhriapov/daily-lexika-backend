package my.project.services.chineseflashcards;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import my.project.exception.ResourceNotFoundException;
import my.project.models.dto.chineseflashcards.WordDTO;
import my.project.models.mapper.chineseflashcards.WordPackMapper;
import my.project.models.dto.chineseflashcards.WordPackDTO;
import my.project.models.mapper.chineseflashcards.WordMapper;
import my.project.models.entity.chineseflashcards.WordPack;
import my.project.models.entity.chineseflashcards.Word;
import my.project.repositories.chineseflashcards.WordPackRepository;
import my.project.services.user.AuthenticationService;
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
    private final WordService wordService;
    private final WordDataService wordDataService;
    private final AuthenticationService authenticationService;
    private final MessageSource messageSource;

    public List<WordPackDTO> getAllWordPacks() {
        List<WordPack> allWordPacks = wordPackRepository.findAll();

        List<WordPackDTO> allWordPackDTOs = new ArrayList<>();
        for (WordPack oneWordPack : allWordPacks) {
            allWordPackDTOs.add(wordPackMapper.toDTO(oneWordPack));
        }

        return allWordPackDTOs;
    }

    public WordPackDTO getWordPackDTOByName(String wordPackName) {
        WordPack wordPack = getWordPackByName(wordPackName);
        return wordPackMapper.toDTO(wordPack);
    }

    @Transactional
    public List<WordDTO> getAllWordsForWordPack(String wordPackName) {
        Long userId = authenticationService.getAuthenticatedUser().id();
        WordPack wordPack = getWordPackByName(wordPackName);

        List<Long> wordDataIds = wordDataService.getListOfAllWordDataIdsByWordPack(wordPack);

        List<Word> existingWords = wordService.findByUserIdAndWordDataIdIn(userId, wordDataIds);
        List<Word> wordsToBeSaved = wordDataService.getListOfAllWordDataIdsByWordPack(wordPack).stream()
                .filter(wordDataId -> existingWords.stream()
                        .noneMatch(word -> word.getWordDataId().equals(wordDataId))
                )
                .map(wordDataId -> new Word(userId, wordDataId)) // TODO::: change to converter
                .toList();

        List<Word> savedWords = wordService.saveAll(wordsToBeSaved);

        List<Word> listOfWords = new ArrayList<>();
        listOfWords.addAll(existingWords);
        listOfWords.addAll(savedWords);

        return new ArrayList<>(wordMapper.toDTOShortList(listOfWords));
    }

    public WordPack getWordPackByName(String wordPackName) {
        return wordPackRepository.findById(wordPackName)
                .orElseThrow(() -> new ResourceNotFoundException(messageSource.getMessage(
                        "exception.wordPack.notFound", null, Locale.getDefault())));
    }
}