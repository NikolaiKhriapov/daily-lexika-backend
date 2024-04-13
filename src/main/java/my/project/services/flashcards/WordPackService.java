package my.project.services.flashcards;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import my.project.exception.BadRequestException;
import my.project.exception.ResourceAlreadyExistsException;
import my.project.exception.ResourceNotFoundException;
import my.project.models.dtos.flashcards.WordDto;
import my.project.models.dtos.flashcards.WordDataDto;
import my.project.models.dtos.flashcards.WordPackDto;
import my.project.models.entities.enumeration.Category;
import my.project.models.entities.enumeration.Platform;
import my.project.models.entities.flashcards.*;
import my.project.models.entities.user.User;
import my.project.models.mappers.flashcards.WordDataMapper;
import my.project.models.mappers.flashcards.WordMapper;
import my.project.models.mappers.flashcards.WordPackMapper;
import my.project.repositories.flashcards.ReviewRepository;
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
    private final AuthenticationService authenticationService;
    private final RoleService roleService;
    private final MessageSource messageSource;
    private final ReviewRepository reviewRepository;

    public WordPack findByName(String wordPackName) {
        return wordPackRepository.findById(wordPackName)
                .orElseThrow(() -> new ResourceNotFoundException(messageSource.getMessage(
                        "exception.wordPack.notFound", null, Locale.getDefault())));
    }

    public List<WordPack> findAll() {
        return wordPackRepository.findAll();
    }

    public void saveAll(List<WordPack> wordPacks) {
        wordPackRepository.saveAll(wordPacks);
    }

    public List<WordPackDto> getAllWordPacksForUser() {
        User user = authenticationService.getAuthenticatedUser();
        Platform platform = roleService.getPlatformByRoleName(user.getRole());

        List<WordPack> allWordPacksNotCustom = wordPackRepository.findByPlatformInAndCategoryNot(List.of(platform, Platform.SHARED), Category.CUSTOM);
        List<WordPack> allWordPacksCustom = wordPackRepository.findAllByUserIdAndPlatformAndCategoryCustom(user.getId(), platform);

        List<WordPack> allWordPacks = new ArrayList<>();
        allWordPacks.addAll(allWordPacksNotCustom);
        allWordPacks.addAll(allWordPacksCustom);

        return wordPackMapper.toDtoList(allWordPacks);
    }

    @Transactional
    public List<WordDto> getAllWordsForWordPack(String wordPackName, Pageable pageable) {
        User user = authenticationService.getAuthenticatedUser();
        Platform platform = roleService.getPlatformByRoleName(user.getRole());

        List<Long> wordDataIds = wordDataService.findAllWordDataIdByWordPackNameAndPlatform(wordPackName, platform);

        wordService.createOrUpdateWordsForUser(user.getId(), wordDataIds);

        Page<Word> wordsPage = wordService.findByUserIdAndWordDataIdIn(user.getId(), wordDataIds, pageable);

        return new ArrayList<>(wordMapper.toDtoList(wordsPage.getContent()));
    }

    public void createCustomWordPack(WordPackDto wordPackDTO) {
        User user = authenticationService.getAuthenticatedUser();
        Platform platform = roleService.getPlatformByRoleName(user.getRole());
        String wordPackName = wordPackDTO.name().trim();

        if (wordPackName.contains(";") || wordPackName.isBlank()) {
            throw new BadRequestException(messageSource.getMessage("exception.wordPack.invalidName", null, Locale.getDefault()));
        }

        if (!wordPackRepository.existsById(wordPackName + "__" + user.getId())) {
            wordPackRepository.save(new WordPack(
                    wordPackName + "__" + user.getId(),
                    wordPackDTO.description(),
                    Category.CUSTOM,
                    platform
            ));
        } else {
            throw new ResourceAlreadyExistsException(messageSource.getMessage("exception.wordPack.alreadyExists", null, Locale.getDefault())
                    .formatted(wordPackDTO.name()));
        }
    }

    public void deleteCustomWordPack(String wordPackName) {
        User user = authenticationService.getAuthenticatedUser();
        Platform platform = roleService.getPlatformByRoleName(user.getRole());

        WordPack wordPack = findByName(wordPackName);

        throwIfReviewExistsForWordPack(wordPackName);
        throwIfWordPackCategoryNotCustom(wordPack);

        List<WordData> listOfWordData = wordDataService.findAllByWordPackAndPlatform(wordPack, platform);
        listOfWordData.forEach(wordData -> {
            List<WordPack> listOfWordPacks = wordData.getListOfWordPacks();
            listOfWordPacks.remove(wordPack);
            wordData.setListOfWordPacks(listOfWordPacks);
        });

        wordPackRepository.delete(wordPack);
    }

    public WordDataDto addWordToCustomWordPack(String wordPackName, Long wordDataId) {
        WordPack wordPack = findByName(wordPackName);

        throwIfWordPackCategoryNotCustom(wordPack);

        WordData wordData = wordDataService.findById(wordDataId);

        List<WordPack> listOfWordPacks = wordData.getListOfWordPacks();
        if (!listOfWordPacks.contains(wordPack)) {
            listOfWordPacks.add(wordPack);
        } else {
            throw new BadRequestException(messageSource.getMessage("exception.wordPack.wordDataAlreadyAddedToWordPack", null, Locale.getDefault())
                    .formatted(wordData.getId(), wordPackName));
        }
        wordData.setListOfWordPacks(listOfWordPacks);

        return wordDataMapper.toDto(wordDataService.save(wordData));
    }

    public WordDataDto removeWordFromCustomWordPack(String wordPackName, Long wordDataId) {
        WordPack wordPack = findByName(wordPackName);

        throwIfWordPackCategoryNotCustom(wordPack);

        WordData wordData = wordDataService.findById(wordDataId);

        List<WordPack> listOfWordPacks = wordData.getListOfWordPacks();
        if (listOfWordPacks.contains(wordPack)) {
            listOfWordPacks.remove(wordPack);
        } else {
            throw new BadRequestException(messageSource.getMessage("exception.wordPack.wordDataNotInWordPack", null, Locale.getDefault())
                    .formatted(wordData.getId(), wordPackName));
        }
        wordData.setListOfWordPacks(listOfWordPacks);

        return wordDataMapper.toDto(wordDataService.save(wordData));
    }

    public void deleteAllByUserIdAndPlatform(Long userId, Platform platform) {
        List<WordPack> allWordPacksCustom = wordPackRepository.findAllByUserIdAndPlatformAndCategoryCustom(userId, platform);
        allWordPacksCustom.forEach(wordPack -> deleteCustomWordPack(wordPack.getName()));
    }

    private void throwIfReviewExistsForWordPack(String wordPackName) {
        Long userId = authenticationService.getAuthenticatedUser().getId();

        Optional<Review> review = reviewRepository.findByUserIdAndWordPack_Name(userId, wordPackName);
        review.ifPresent(reviewRepository::delete);
    }

    private void throwIfWordPackCategoryNotCustom(WordPack wordPack) {
        if (!wordPack.getCategory().equals(Category.CUSTOM)) {
            throw new BadRequestException(messageSource.getMessage("exception.wordPack.categoryNotCustom", null, Locale.getDefault()));
        }
    }
}
