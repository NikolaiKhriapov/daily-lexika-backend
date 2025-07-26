package my.project.dailylexika.flashcard.service.impl;

import lombok.RequiredArgsConstructor;
import my.project.dailylexika.config.I18nUtil;
import my.project.dailylexika.flashcard.model.entities.WordPack;
import my.project.dailylexika.flashcard.service.WordPackService;
import my.project.dailylexika.user._public.PublicRoleService;
import my.project.dailylexika.user._public.PublicUserService;
import my.project.library.dailylexika.dtos.flashcards.WordPackDto;
import my.project.library.dailylexika.dtos.user.UserDto;
import my.project.library.dailylexika.enumerations.Category;
import my.project.library.dailylexika.enumerations.Platform;
import my.project.dailylexika.flashcard.model.mappers.WordPackMapper;
import my.project.dailylexika.flashcard.persistence.WordPackRepository;
import my.project.library.dailylexika.events.flashcard.CustomWordPackToBeDeletedEvent;
import my.project.library.util.exception.BadRequestException;
import my.project.library.util.exception.ResourceAlreadyExistsException;
import my.project.library.util.exception.ResourceNotFoundException;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WordPackServiceImpl implements WordPackService {

    private final WordPackRepository wordPackRepository;
    private final WordPackMapper wordPackMapper;
    private final PublicUserService userService;
    private final PublicRoleService roleService;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public List<WordPack> getAll() {
        return wordPackRepository.findAll();
    }

    @Override
    public List<WordPackDto> getAllForUser() {
        UserDto user = userService.getUser();
        Platform platform = roleService.getPlatformByRoleName(user.role());

        List<WordPack> allWordPacksNotCustom = wordPackRepository.findAllByPlatformAndCategoryNot(platform, Category.CUSTOM);

        List<WordPack> allWordPacksCustom = wordPackRepository.findAllByUserIdAndPlatformAndCategoryCustom(user.id(), platform);
        List<WordPack> allWordPacksCustomFiltered
                = allWordPacksCustom.stream()
                .filter(wordPack -> wordPack.getName().endsWith("__" + user.id()))
                .toList();

        List<WordPack> allWordPacks = new ArrayList<>();
        allWordPacks.addAll(allWordPacksNotCustom);
        allWordPacks.addAll(allWordPacksCustomFiltered);

        return wordPackMapper.toDtoList(allWordPacks);
    }

    @Override
    public WordPack getByName(String wordPackName) {
        return wordPackRepository.findById(wordPackName)
                .orElseThrow(() -> new ResourceNotFoundException(I18nUtil.getMessage("dailylexika-exceptions.wordPack.notFound", wordPackName)));
    }

    @Override
    public void saveAll(List<WordPack> wordPacks) {
        wordPackRepository.saveAll(wordPacks);
    }

    @Override
    @Transactional
    public void deleteAllByUserIdAndPlatform(Integer userId, Platform platform) {
        wordPackRepository.findAllByUserIdAndPlatformAndCategoryCustom(userId, platform)
                .stream()
                .filter(wordPack -> wordPack.getName().endsWith("__" + userId))
                .forEach(wordPack -> deleteCustomWordPack(wordPack.getName()));
    }

    @Override
    public void createCustomWordPack(WordPackDto wordPackDto) {
        UserDto user = userService.getUser();
        Platform platform = roleService.getPlatformByRoleName(user.role());
        String wordPackName = wordPackDto.name().trim();

        if (wordPackName.contains(";") || wordPackName.isBlank()) {
            throw new BadRequestException(I18nUtil.getMessage("dailylexika-exceptions.wordPack.invalidName"));
        }

        String wordPackNameDecorated = decorateWordPackName(wordPackName, user.id(), platform);

        if (!wordPackRepository.existsById(wordPackNameDecorated)) {
            wordPackRepository.save(new WordPack(
                    wordPackNameDecorated,
                    wordPackDto.description(),
                    Category.CUSTOM,
                    platform
            ));
        } else {
            throw new ResourceAlreadyExistsException(I18nUtil.getMessage("dailylexika-exceptions.wordPack.alreadyExists", wordPackDto.name()));
        }
    }

    @Override
    @Transactional
    public void deleteCustomWordPack(String wordPackName) {
        WordPack wordPack = getByName(wordPackName);
        throwIfWordPackCategoryNotCustom(wordPack);
        publishCustomWordPackToBeDeletedEvent(wordPack);
        wordPackRepository.delete(wordPack);
    }

    @Override
    public void throwIfWordPackCategoryNotCustom(WordPack wordPack) {
        if (!wordPack.getCategory().equals(Category.CUSTOM)) {
            throw new BadRequestException(I18nUtil.getMessage("dailylexika-exceptions.wordPack.categoryNotCustom"));
        }
    }

    private String decorateWordPackName(String wordPackName, Integer userId, Platform platform) {
        String prefix = switch (platform) {
            case CHINESE -> "CH__";
            case ENGLISH -> "EN__";
        };
        return prefix + wordPackName + "__" + userId;
    }

    private void publishCustomWordPackToBeDeletedEvent(WordPack wordPack) {
        UserDto user = userService.getUser();
        Platform platform = roleService.getPlatformByRoleName(user.role());

        eventPublisher.publishEvent(
                CustomWordPackToBeDeletedEvent.builder()
                        .wordPackName(wordPack.getName())
                        .platform(platform)
                        .build()
        );
    }
}
