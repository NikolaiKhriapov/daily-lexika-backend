package my.project.dailylexika.flashcard.service.impl;

import lombok.RequiredArgsConstructor;
import my.project.dailylexika.flashcard.model.entities.WordPack;
import my.project.dailylexika.flashcard.service.WordDataService;
import my.project.dailylexika.flashcard.service.WordPackService;
import my.project.dailylexika.user._public.PublicRoleService;
import my.project.dailylexika.user._public.PublicUserService;
import my.project.library.dailylexika.dtos.user.UserDto;
import my.project.library.util.datetime.DateUtil;
import my.project.dailylexika.config.I18nUtil;
import my.project.library.dailylexika.dtos.flashcards.WordDataDto;
import my.project.library.dailylexika.enumerations.Platform;
import my.project.dailylexika.flashcard.model.entities.WordData;
import my.project.dailylexika.flashcard.model.mappers.WordDataMapper;
import my.project.dailylexika.flashcard.persistence.WordDataRepository;
import my.project.library.util.exception.BadRequestException;
import my.project.library.util.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.text.Normalizer;
import java.util.*;
import java.util.Locale;

import static org.springframework.transaction.annotation.Propagation.REQUIRES_NEW;

@Service
@RequiredArgsConstructor
@Validated
public class WordDataServiceImpl implements WordDataService {

    private final WordDataRepository wordDataRepository;
    private final WordDataMapper wordDataMapper;
    private final WordPackService wordPackService;
    private final PublicUserService userService;
    private final PublicRoleService roleService;

    @Override
    @Transactional(readOnly = true)
    public List<WordDataDto> search(String query, Integer limit) {
        UserDto user = userService.getUser();
        Platform platform = roleService.getPlatformByRoleName(user.role());
        String normalizedQuery = query.trim();
        String transcriptionQuery = normalizeTranscriptionQuery(query);
        List<WordData> matches = wordDataRepository.searchByPlatformAndQuery(platform, user.translationLanguage(), normalizedQuery, transcriptionQuery, limit);
        return wordDataMapper.toDtoList(matches);
    }

    @Override
    @Transactional(readOnly = true)
    public List<WordData> getAllByPlatform(Platform platform) {
        return wordDataRepository.findAllByPlatform(platform);
    }

    @Override
    @Transactional(readOnly = true)
    public List<WordData> getAllByWordPackNameAndPlatform(String wordPackName, Platform platform) {
        return wordDataRepository.findAllByListOfWordPacks_NameAndPlatform(wordPackName, platform);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByWordPackNameAndPlatform(String wordPackName, Platform platform) {
        return wordDataRepository.countByListOfWordPacks_NameAndPlatform(wordPackName, platform) > 0;
    }

    @Override
    @Transactional
    public WordDataDto addCustomWordPackToWordData(Integer wordDataId, String wordPackName) {
        WordPack wordPack = wordPackService.getByName(wordPackName);

        wordPackService.throwIfWordPackCategoryNotCustom(wordPack);

        WordData wordData = getEntityById(wordDataId);
        List<WordPack> listOfWordPacks = wordData.getListOfWordPacks();
        if (!listOfWordPacks.contains(wordPack)) {
            listOfWordPacks.add(wordPack);
        } else {
            throw new BadRequestException(I18nUtil.getMessage("dailylexika-exceptions.wordPack.wordDataAlreadyAddedToWordPack", wordData.getId(), wordPack.getName()));
        }
        wordData.setListOfWordPacks(listOfWordPacks);
        wordData = wordDataRepository.save(wordData);

        return wordDataMapper.toDto(wordData);
    }

    @Override
    @Transactional
    public WordDataDto removeCustomWordPackFromWordData(Integer wordDataId, String wordPackName) {
        WordPack wordPack = wordPackService.getByName(wordPackName);

        wordPackService.throwIfWordPackCategoryNotCustom(wordPack);

        WordData wordData = getEntityById(wordDataId);
        List<WordPack> listOfWordPacks = wordData.getListOfWordPacks();
        if (listOfWordPacks.contains(wordPack)) {
            listOfWordPacks.remove(wordPack);
        } else {
            throw new BadRequestException(I18nUtil.getMessage("dailylexika-exceptions.wordPack.wordDataNotInWordPack", wordData.getId(), wordPackName));
        }
        wordData.setListOfWordPacks(listOfWordPacks);
        wordData = wordDataRepository.save(wordData);

        return wordDataMapper.toDto(wordData);
    }

    @Override
    @Transactional
    public void addCustomWordPackToWordDataByWordPackName(String wordPackNameToBeAdded, String wordPackNameOriginal) {
        UserDto user = userService.getUser();
        Platform platform = roleService.getPlatformByRoleName(user.role());
        WordPack wordPackToBeAdded = wordPackService.getByName(wordPackNameToBeAdded);

        wordPackService.throwIfWordPackCategoryNotCustom(wordPackToBeAdded);

        List<WordData> listOfWordData = getAllByWordPackNameAndPlatform(wordPackNameOriginal, platform);

        List<WordData> listOfWordDataToBeUpdated = new ArrayList<>();
        for (WordData wordData : listOfWordData) {
            List<WordPack> listOfWordPacks = wordData.getListOfWordPacks();
            if (!listOfWordPacks.contains(wordPackToBeAdded)) {
                listOfWordPacks.add(wordPackToBeAdded);
            }
            wordData.setListOfWordPacks(listOfWordPacks);
            listOfWordDataToBeUpdated.add(wordData);
        }
        wordDataRepository.saveAll(listOfWordDataToBeUpdated);
    }

    @Override
    @Transactional
    public void saveAll(List<WordData> listOfWordData) {
        wordDataRepository.saveAll(listOfWordData);
    }

    @Override
    @Transactional
    public void deleteAll(List<WordData> listOfWordData) {
        wordDataRepository.deleteAll(listOfWordData);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Integer> getAllWordDataIdByPlatform(Platform platform) {
        return wordDataRepository.findAllWordDataIdsByPlatform(platform);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Integer> getAllWordDataIdByWordPackNameAndPlatform(String wordPackName, Platform platform) {
        return wordDataRepository.findAllWordDataIdsByWordPackNameAndPlatform(wordPackName, platform);
    }

    @Override
    @Transactional(readOnly = true)
    public Integer getIdByWordOfTheDayDateAndPlatform(Platform platform) {
        return wordDataRepository.findIdByWordOfTheDayDateAndPlatform(DateUtil.nowInUtc().toLocalDate(), platform)
                .orElseThrow(() -> new ResourceNotFoundException(I18nUtil.getMessage("dailylexika-exceptions.wordData.wordOfTheDayDate.notFound")));
    }

    @Override
    @Transactional(readOnly = true, propagation = REQUIRES_NEW)
    public WordData getEntityById(Integer wordDataId) {
        return wordDataRepository.findById(wordDataId)
                .orElseThrow(() -> new ResourceNotFoundException(I18nUtil.getMessage("dailylexika-exceptions.wordData.notFound")));
    }

    private static String normalizeTranscriptionQuery(String query) {
        String trimmed = query == null ? "" : query.trim().toLowerCase(Locale.ROOT);
        String normalized = Normalizer.normalize(trimmed, Normalizer.Form.NFD);
        normalized = normalized.replaceAll("\\p{M}+", "");
        normalized = normalized
                .replace("u:", "u")
                .replace("v", "u")
                .replaceAll("\\d+", "");
        if (normalized.isBlank()) {
            normalized = normalized.toLowerCase(Locale.ROOT);
        }
        return normalized;
    }
}
