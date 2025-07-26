package my.project.dailylexika.flashcard.service;

import lombok.RequiredArgsConstructor;
import my.project.library.util.datetime.DateUtil;
import my.project.dailylexika.config.i18n.I18nUtil;
import my.project.library.dailylexika.dtos.flashcards.WordDataDto;
import my.project.library.dailylexika.enumerations.Platform;
import my.project.dailylexika.flashcard.model.entities.WordData;
import my.project.dailylexika.user.model.entities.User;
import my.project.dailylexika.flashcard.model.mappers.WordDataMapper;
import my.project.dailylexika.flashcard.persistence.WordDataRepository;
import my.project.dailylexika.user.service.RoleService;
import my.project.library.util.exception.ResourceNotFoundException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class WordDataService {

    private final WordDataRepository wordDataRepository;
    private final WordDataMapper wordDataMapper;
    private final RoleService roleService;

    public WordData findById(Integer wordDataId) {
        return wordDataRepository.findById(wordDataId)
                .orElseThrow(() -> new ResourceNotFoundException(I18nUtil.getMessage("dailylexika-exceptions.wordData.notFound")));
    }

    public List<WordData> findAllByPlatform(Platform platform) {
        return wordDataRepository.findAllByPlatform(platform);
    }

    public List<WordData> findAllByWordPackNameAndPlatform(String wordPackName, Platform platform) {
        return wordDataRepository.findAllByListOfWordPacks_NameAndPlatform(wordPackName, platform);
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
                .orElseThrow(() -> new ResourceNotFoundException(I18nUtil.getMessage("dailylexika-exceptions.wordData.wordOfTheDayDate.notFound")));
    }
}
