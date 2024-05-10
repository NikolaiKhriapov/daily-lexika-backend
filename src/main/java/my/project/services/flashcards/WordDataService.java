package my.project.services.flashcards;

import lombok.RequiredArgsConstructor;
import my.project.config.DateUtil;
import my.project.exception.ResourceNotFoundException;
import my.project.models.dtos.flashcards.WordDataDto;
import my.project.models.entities.enumerations.Platform;
import my.project.models.entities.flashcards.WordData;
import my.project.models.entities.flashcards.WordPack;
import my.project.models.entities.user.User;
import my.project.models.mappers.flashcards.WordDataMapper;
import my.project.repositories.flashcards.WordDataRepository;
import my.project.services.user.RoleService;
import org.springframework.context.MessageSource;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class WordDataService {

    private final WordDataRepository wordDataRepository;
    private final WordDataMapper wordDataMapper;
    private final RoleService roleService;
    private final MessageSource messageSource;

    public WordData findById(Integer wordDataId) {
        return wordDataRepository.findById(wordDataId)
                .orElseThrow(() -> new ResourceNotFoundException(messageSource.getMessage(
                        "exception.wordData.notFound", null, Locale.getDefault())));
    }

    public List<WordData> findAllByPlatform(Platform platform) {
        return wordDataRepository.findAllByPlatform(platform);
    }

    public List<WordData> findAllByWordPackAndPlatform(WordPack wordPack, Platform platform) {
        return wordDataRepository.findAllByListOfWordPacks_NameAndPlatform(wordPack.getName(), platform);
    }

    public WordData save(WordData wordData) {
        return wordDataRepository.save(wordData);
    }

    public void saveAll(List<WordData> listOfWordData) {
        wordDataRepository.saveAll(listOfWordData);
    }

    public void deleteAll(List<WordData> listOfWordData) {
        wordDataRepository.deleteAll(listOfWordData);
    }

    public List<WordDataDto> getAllWordData() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Platform platform = roleService.getPlatformByRoleName(user.getRole());

        List<WordData> allWordData = wordDataRepository.findAllByPlatform(platform);

        return wordDataMapper.toDtoList(allWordData);
    }

    public List<Integer> findAllWordDataIdByPlatform(Platform platform) {
        return wordDataRepository.findAllWordDataIdsByPlatform(platform);
    }

    public List<Integer> findAllWordDataIdByWordPackNameAndPlatform(String wordPackName, Platform platform) {
        return wordDataRepository.findAllWordDataIdsByWordPackNameAndPlatform(wordPackName, platform);
    }

    public Long countByWordPackNameAndPlatform(String wordPackName, Platform platform) {
        return wordDataRepository.countByListOfWordPacks_NameAndPlatform(wordPackName, platform);
    }

    public Integer findIdByWordOfTheDayDateAndPlatform(Platform platform) {
        return wordDataRepository.findIdByWordOfTheDayDateAndPlatform(DateUtil.nowInUtc().toLocalDate(), platform)
                .orElseThrow(() -> new ResourceNotFoundException(messageSource.getMessage(
                        "exception.wordData.wordOfTheDayDate.notFound", null, Locale.getDefault())));
    }
}
