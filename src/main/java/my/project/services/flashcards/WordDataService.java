package my.project.services.flashcards;

import lombok.RequiredArgsConstructor;
import my.project.exception.ResourceNotFoundException;
import my.project.models.dtos.flashcards.WordDataDto;
import my.project.models.entities.enumeration.Platform;
import my.project.models.entities.flashcards.WordData;
import my.project.models.entities.flashcards.WordPack;
import my.project.models.entities.user.User;
import my.project.models.mappers.flashcards.WordDataMapper;
import my.project.repositories.flashcards.WordDataRepository;
import my.project.services.user.AuthenticationService;
import my.project.services.user.RoleService;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class WordDataService {

    private final WordDataRepository wordDataRepository;
    private final WordDataMapper wordDataMapper;
    private final AuthenticationService authenticationService;
    private final RoleService roleService;
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

    public List<WordDataDto> getAllWordData() {
        User user = authenticationService.getAuthenticatedUser();
        Platform platform = roleService.getPlatformByRoleName(user.getRole());

        List<WordData> allWordData = findAll().stream()
                .filter(wordData -> wordData.getPlatform() == platform).toList();

    return wordDataMapper.toDtoList(allWordData);
    }

    public List<Long> getAllWordDataIdByWordPackName(String wordPackName) {
        return wordDataRepository.findAllWordDataIdsByWordPackName(wordPackName);
    }
}
