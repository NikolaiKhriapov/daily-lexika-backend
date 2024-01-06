package my.project.services.flashcards;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import my.project.exception.ResourceNotFoundException;
import my.project.models.dto.flashcards.WordDTO;
import my.project.models.entity.enumeration.Platform;
import my.project.models.entity.user.User;
import my.project.models.mapper.flashcards.WordPackMapper;
import my.project.models.dto.flashcards.WordPackDTO;
import my.project.models.mapper.flashcards.WordMapper;
import my.project.models.entity.flashcards.WordPack;
import my.project.models.entity.flashcards.Word;
import my.project.repositories.flashcards.WordPackRepository;
import my.project.services.user.AuthenticationService;
import my.project.services.user.RoleService;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    private final RoleService roleService;
    private final MessageSource messageSource;

    public List<WordPackDTO> getAllWordPacks() {
        User user = authenticationService.getAuthenticatedUser();
        Platform platform = roleService.getPlatformByRoleName(user.getRole());

        List<WordPack> allWordPacks = wordPackRepository.findAllByPlatform(platform);

        List<WordPackDTO> allWordPackDTOs = new ArrayList<>();
        for (WordPack oneWordPack : allWordPacks) {
            allWordPackDTOs.add(wordPackMapper.toDTO(oneWordPack));
        }

        return allWordPackDTOs;
    }

    public WordPack getWordPackByName(String wordPackName) {
        return wordPackRepository.findById(wordPackName)
                .orElseThrow(() -> new ResourceNotFoundException(messageSource.getMessage(
                        "exception.wordPack.notFound", null, Locale.getDefault())));
    }

    public WordPackDTO getWordPackDTOByName(String wordPackName) {
        WordPack wordPack = getWordPackByName(wordPackName);
        return wordPackMapper.toDTO(wordPack);
    }

    @Transactional
    public List<WordDTO> getAllWordsForWordPack(String wordPackName, Pageable pageable) {
        Long userId = authenticationService.getAuthenticatedUser().getId();
        WordPack wordPack = getWordPackByName(wordPackName);

        List<Long> wordDataIds = wordDataService.getListOfAllWordDataIdsByWordPack(wordPack);

        Page<Word> wordsPage = wordService.findByUserIdAndWordDataIdIn(userId, wordDataIds, pageable);
        List<Word> listOfWords = wordsPage.getContent();

        return new ArrayList<>(wordMapper.toDTOShortList(listOfWords));
    }
}
