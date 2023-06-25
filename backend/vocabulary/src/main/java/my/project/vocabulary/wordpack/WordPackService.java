package my.project.vocabulary.wordpack;

import lombok.RequiredArgsConstructor;
import my.project.vocabulary.exception.ResourceNotFoundException;
import my.project.vocabulary.word.Word;
import my.project.vocabulary.word.WordDTO;
import my.project.vocabulary.word.WordDTOMapper;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class WordPackService {

    private final WordPackRepository wordPackRepository;
    private final WordPackDTOMapper wordPackDTOMapper;
    private final WordDTOMapper wordDTOMapper;
    private final MessageSource messageSource;

    public List<WordPackDTO> getAllWordPacks() {
        List<WordPack> allWordPacks = wordPackRepository.findAll();

        List<WordPackDTO> allWordPackDTOs = new ArrayList<>();
        for (WordPack oneWordPack : allWordPacks) {
            allWordPackDTOs.add(wordPackDTOMapper.apply(oneWordPack));
        }

        return allWordPackDTOs;
    }

    public WordPack getWordPack(String name) {
        return wordPackRepository.findById(name)
                .orElseThrow(() -> new ResourceNotFoundException(messageSource.getMessage(
                        "exception.wordPack.notFound", null, Locale.getDefault())));
    }

    public List<WordDTO> getAllWordsForWordPack(String wordPackName) {
        List <WordDTO> allWordsForWordPack = new ArrayList<>();

        WordPack wordPack = getWordPack(wordPackName);

        for (Word oneWord : wordPack.getListOfWords()) {
            allWordsForWordPack.add(wordDTOMapper.apply(oneWord));
        }

        return allWordsForWordPack;
    }
}
