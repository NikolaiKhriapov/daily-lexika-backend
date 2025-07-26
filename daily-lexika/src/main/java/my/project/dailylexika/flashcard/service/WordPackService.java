package my.project.dailylexika.flashcard.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import my.project.dailylexika.config.i18n.I18nUtil;
import my.project.dailylexika.flashcard.model.entities.Review;
import my.project.dailylexika.flashcard.model.entities.Word;
import my.project.dailylexika.flashcard.model.entities.WordData;
import my.project.dailylexika.flashcard.model.entities.WordPack;
import my.project.library.dailylexika.dtos.flashcards.WordDto;
import my.project.library.dailylexika.dtos.flashcards.WordDataDto;
import my.project.library.dailylexika.dtos.flashcards.WordPackDto;
import my.project.library.dailylexika.enumerations.Category;
import my.project.library.dailylexika.enumerations.Platform;
import my.project.dailylexika.user.model.entities.User;
import my.project.dailylexika.flashcard.model.mappers.WordDataMapper;
import my.project.dailylexika.flashcard.model.mappers.WordMapper;
import my.project.dailylexika.flashcard.model.mappers.WordPackMapper;
import my.project.dailylexika.flashcard.persistence.ReviewRepository;
import my.project.dailylexika.flashcard.persistence.WordPackRepository;
import my.project.dailylexika.user.service.RoleService;
import my.project.library.util.exception.BadRequestException;
import my.project.library.util.exception.ResourceAlreadyExistsException;
import my.project.library.util.exception.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class WordPackService {

    private final WordPackRepository wordPackRepository;
    private final WordPackMapper wordPackMapper;
    private final WordMapper wordMapper;
    private final WordService wordService;
    private final WordDataService wordDataService;
    private final WordDataMapper wordDataMapper;
    private final RoleService roleService;
    private final ReviewRepository reviewRepository;

    public WordPack findByName(String wordPackName) {
        return wordPackRepository.findById(wordPackName)
                .orElseThrow(() -> new ResourceNotFoundException(I18nUtil.getMessage("dailylexika-exceptions.wordPack.notFound", wordPackName)));
    }

    public List<WordPack> findAll() {
        return wordPackRepository.findAll();
    }

    public void saveAll(List<WordPack> wordPacks) {
        wordPackRepository.saveAll(wordPacks);
    }

    public List<WordPackDto> getAllWordPacksForUser() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Platform platform = roleService.getPlatformByRoleName(user.getRole());

        List<WordPack> allWordPacksNotCustom = wordPackRepository.findAllByPlatformAndCategoryNot(platform, Category.CUSTOM);

        List<WordPack> allWordPacksCustom = wordPackRepository.findAllByUserIdAndPlatformAndCategoryCustom(user.getId(), platform);
        List<WordPack> allWordPacksCustomFiltered
                = allWordPacksCustom.stream()
                .filter(wordPack -> wordPack.getName().endsWith("__" + user.getId()))
                .toList();

        List<WordPack> allWordPacks = new ArrayList<>();
        allWordPacks.addAll(allWordPacksNotCustom);
        allWordPacks.addAll(allWordPacksCustomFiltered);

        return wordPackMapper.toDtoList(allWordPacks);
    }

    @Transactional
    public Page<WordDto> getPageOfWordsForWordPack(String wordPackName, Pageable pageable) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Platform platform = roleService.getPlatformByRoleName(user.getRole());

        List<Integer> wordDataIds = wordDataService.findAllWordDataIdByWordPackNameAndPlatform(wordPackName, platform);

        Page<Word> pageOfWords = wordService.findByUserIdAndWordDataIdIn(user.getId(), wordDataIds, pageable);
        List<WordDto> listOfWordDto = wordMapper.toDtoList(pageOfWords.getContent());

        return new PageImpl<>(listOfWordDto, pageable, pageOfWords.getTotalElements());
    }

    public void createCustomWordPack(WordPackDto wordPackDTO) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Platform platform = roleService.getPlatformByRoleName(user.getRole());
        String wordPackName = wordPackDTO.name().trim();

        if (wordPackName.contains(";") || wordPackName.isBlank()) {
            throw new BadRequestException(I18nUtil.getMessage("dailylexika-exceptions.wordPack.invalidName"));
        }

        String wordPackNameDecorated = decorateWordPackName(wordPackName, user.getId(), platform);

        if (!wordPackRepository.existsById(wordPackNameDecorated)) {
            wordPackRepository.save(new WordPack(
                    wordPackNameDecorated,
                    wordPackDTO.description(),
                    Category.CUSTOM,
                    platform
            ));
        } else {
            throw new ResourceAlreadyExistsException(I18nUtil.getMessage("dailylexika-exceptions.wordPack.alreadyExists", wordPackDTO.name()));
        }
    }

    private String decorateWordPackName(String wordPackName, Integer userId, Platform platform) {
        String prefix = switch (platform) {
            case CHINESE -> "CH__";
            case ENGLISH -> "EN__";
        };
        return prefix + wordPackName + "__" + userId;
    }

    public void deleteCustomWordPack(String wordPackName) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Platform platform = roleService.getPlatformByRoleName(user.getRole());

        WordPack wordPack = findByName(wordPackName);

        throwIfReviewExistsForWordPack(wordPackName);
        throwIfWordPackCategoryNotCustom(wordPack);

        List<WordData> listOfWordData = wordDataService.findAllByWordPackNameAndPlatform(wordPack.getName(), platform);
        listOfWordData.forEach(wordData -> {
            List<WordPack> listOfWordPacks = wordData.getListOfWordPacks();
            listOfWordPacks.remove(wordPack);
            wordData.setListOfWordPacks(listOfWordPacks);
        });

        wordPackRepository.delete(wordPack);
    }

    public WordDataDto addWordToCustomWordPack(String wordPackName, Integer wordDataId) {
        WordPack wordPack = findByName(wordPackName);

        throwIfWordPackCategoryNotCustom(wordPack);

        WordData wordData = wordDataService.findById(wordDataId);

        List<WordPack> listOfWordPacks = wordData.getListOfWordPacks();
        if (!listOfWordPacks.contains(wordPack)) {
            listOfWordPacks.add(wordPack);
        } else {
            throw new BadRequestException(I18nUtil.getMessage("dailylexika-exceptions.wordPack.wordDataAlreadyAddedToWordPack", wordData.getId(), wordPackName));
        }
        wordData.setListOfWordPacks(listOfWordPacks);

        return wordDataMapper.toDto(wordDataService.save(wordData));
    }

    public void addAllWordsFromWordPackToCustomWordPack(String wordPackNameTo, String wordPackNameFrom) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Platform platform = roleService.getPlatformByRoleName(user.getRole());

        WordPack wordPackToBeUpdated = findByName(wordPackNameTo);

        throwIfWordPackCategoryNotCustom(wordPackToBeUpdated);

        List<WordData> listOfWordData = wordDataService.findAllByWordPackNameAndPlatform(wordPackNameFrom, platform);

        List<WordData> listOfWordDataToBeUpdated = new ArrayList<>();
        for (WordData wordData : listOfWordData) {
            List<WordPack> listOfWordPacks = wordData.getListOfWordPacks();
            if (!listOfWordPacks.contains(wordPackToBeUpdated)) {
                listOfWordPacks.add(wordPackToBeUpdated);
            }
            wordData.setListOfWordPacks(listOfWordPacks);
            listOfWordDataToBeUpdated.add(wordData);
        }

        wordDataService.saveAll(listOfWordDataToBeUpdated);
    }

    public WordDataDto removeWordFromCustomWordPack(String wordPackName, Integer wordDataId) {
        WordPack wordPack = findByName(wordPackName);

        throwIfWordPackCategoryNotCustom(wordPack);

        WordData wordData = wordDataService.findById(wordDataId);

        List<WordPack> listOfWordPacks = wordData.getListOfWordPacks();
        if (listOfWordPacks.contains(wordPack)) {
            listOfWordPacks.remove(wordPack);
        } else {
            throw new BadRequestException(I18nUtil.getMessage("dailylexika-exceptions.wordPack.wordDataNotInWordPack", wordData.getId(), wordPackName));
        }
        wordData.setListOfWordPacks(listOfWordPacks);

        return wordDataMapper.toDto(wordDataService.save(wordData));
    }

    public void deleteAllByUserIdAndPlatform(Integer userId, Platform platform) {
        List<WordPack> allWordPacksCustom = wordPackRepository.findAllByUserIdAndPlatformAndCategoryCustom(userId, platform);
        List<WordPack> allWordPacksCustomFiltered
                = allWordPacksCustom.stream()
                .filter(wordPack -> wordPack.getName().endsWith("__" + userId))
                .toList();

        allWordPacksCustomFiltered.forEach(wordPack -> deleteCustomWordPack(wordPack.getName()));
    }

    private void throwIfReviewExistsForWordPack(String wordPackName) {
        Integer userId = ((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();

        Optional<Review> review = reviewRepository.findByUserIdAndWordPack_Name(userId, wordPackName);
        review.ifPresent(reviewRepository::delete);
    }

    private void throwIfWordPackCategoryNotCustom(WordPack wordPack) {
        if (!wordPack.getCategory().equals(Category.CUSTOM)) {
            throw new BadRequestException(I18nUtil.getMessage("dailylexika-exceptions.wordPack.categoryNotCustom"));
        }
    }
}
