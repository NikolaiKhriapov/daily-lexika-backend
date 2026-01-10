package my.project.dailylexika.flashcard.service.impl;

import lombok.RequiredArgsConstructor;
import my.project.dailylexika.config.I18nUtil;
import my.project.dailylexika.flashcard.model.entities.WordPack;
import my.project.dailylexika.flashcard.service.WordPackService;
import my.project.dailylexika.user._public.PublicRoleService;
import my.project.dailylexika.user._public.PublicUserService;
import my.project.library.dailylexika.dtos.flashcards.WordPackCustomCreateDto;
import my.project.library.dailylexika.dtos.flashcards.WordPackUserDto;
import my.project.library.dailylexika.dtos.flashcards.admin.WordPackCreateDto;
import my.project.library.dailylexika.dtos.flashcards.admin.WordPackDto;
import my.project.library.dailylexika.dtos.flashcards.admin.WordPackUpdateDto;
import my.project.library.dailylexika.dtos.user.UserDto;
import my.project.library.dailylexika.enumerations.Category;
import my.project.library.dailylexika.enumerations.Platform;
import my.project.dailylexika.flashcard.model.mappers.WordPackMapper;
import my.project.dailylexika.flashcard.persistence.WordPackRepository;
import my.project.library.dailylexika.events.flashcard.WordPackToBeDeletedEvent;
import my.project.library.util.exception.BadRequestException;
import my.project.library.util.exception.ResourceAlreadyExistsException;
import my.project.library.util.exception.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.ArrayList;
import java.util.List;

@Service
@Validated
@RequiredArgsConstructor
public class WordPackServiceImpl implements WordPackService {

    private final WordPackRepository wordPackRepository;
    private final WordPackMapper wordPackMapper;
    private final PublicUserService userService;
    private final PublicRoleService roleService;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional(readOnly = true)
    public List<WordPackUserDto> getAllForUser() {
        UserDto user = userService.getUser();
        Platform platform = roleService.getPlatformByRoleName(user.role());

        List<WordPack> allWordPacksNotCustom = wordPackRepository.findAllByPlatformAndCategoryNot(platform, Category.CUSTOM);
        List<WordPack> allWordPacksCustom = wordPackRepository.findAllByUserIdAndPlatformAndCategoryCustom(user.id(), platform);

        List<WordPack> allWordPacks = new ArrayList<>();
        allWordPacks.addAll(allWordPacksNotCustom);
        allWordPacks.addAll(allWordPacksCustom);

        return wordPackMapper.toDtoList(allWordPacks);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<WordPackDto> getPage(Platform platform, Pageable pageable) {
        PageRequest sortedPage = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by("name"));
        return wordPackRepository.findAllByPlatformAndCategoryNot(platform, Category.CUSTOM, sortedPage)
                .map(this::toWordPackDto);
    }

    @Override
    public WordPack getById(Long wordPackId) {
        return wordPackRepository.findById(wordPackId)
                .orElseThrow(() -> new ResourceNotFoundException(I18nUtil.getMessage("dailylexika-exceptions.wordPack.notFound", wordPackId)));
    }

    @Override
    @Transactional(readOnly = true)
    public WordPackDto getDtoById(Long wordPackId) {
        return toWordPackDto(getNonCustomWordPack(wordPackId));
    }

    @Override
    @Transactional
    public void deleteAllByUserIdAndPlatform(Integer userId, Platform platform) {
        wordPackRepository.findAllByUserIdAndPlatformAndCategoryCustom(userId, platform)
                .forEach(wordPack -> deleteCustomWordPack(wordPack, true));
    }

    @Override
    @Transactional
    public WordPackDto create(WordPackCreateDto createDto) {
        if (createDto.category() == Category.CUSTOM) {
            throw new BadRequestException(I18nUtil.getMessage("dailylexika-exceptions.wordPack.onlyNonCustomAllowed"));
        }
        String wordPackName = createDto.name().trim();
        if (wordPackRepository.existsByPlatformAndName(createDto.platform(), wordPackName)) {
            throw new ResourceAlreadyExistsException(I18nUtil.getMessage("dailylexika-exceptions.wordPack.alreadyExists", wordPackName));
        }

        WordPack wordPack = new WordPack(null, wordPackName, createDto.description(), createDto.category(), createDto.platform(), null);
        WordPack saved = wordPackRepository.save(wordPack);
        return toWordPackDto(saved);
    }

    @Override
    @Transactional
    public WordPackDto update(Long wordPackId, WordPackUpdateDto patchDto) {
        WordPack wordPack = getNonCustomWordPack(wordPackId);

        if (patchDto.description() != null) {
            wordPack.setDescription(patchDto.description());
        }
        if (patchDto.category() != null) {
            if (patchDto.category() == Category.CUSTOM) {
                throw new BadRequestException(I18nUtil.getMessage("dailylexika-exceptions.wordPack.onlyNonCustomAllowed"));
            }
            wordPack.setCategory(patchDto.category());
        }

        WordPack saved = wordPackRepository.save(wordPack);
        return toWordPackDto(saved);
    }

    @Override
    @Transactional
    public void delete(Long wordPackId) {
        WordPack wordPack = getNonCustomWordPack(wordPackId);
        publishWordPackToBeDeletedEvent(wordPack);
        wordPackRepository.delete(wordPack);
    }

    @Override
    @Transactional
    public WordPackDto createCustomWordPack(WordPackCustomCreateDto wordPackDto) {
        UserDto user = userService.getUser();
        Platform platform = roleService.getPlatformByRoleName(user.role());
        String wordPackName = wordPackDto.name().trim();

        if (wordPackRepository.existsByPlatformAndNameAndUserId(platform, wordPackName, user.id())) {
            throw new ResourceAlreadyExistsException(I18nUtil.getMessage("dailylexika-exceptions.wordPack.alreadyExists", wordPackDto.name()));
        }

        WordPack saved = wordPackRepository.save(new WordPack(
                null,
                wordPackName,
                wordPackDto.description(),
                Category.CUSTOM,
                platform,
                user.id()
        ));
        return toWordPackDto(saved);
    }

    @Override
    @Transactional
    public void deleteCustomWordPack(Long wordPackId) {
        WordPack wordPack = getById(wordPackId);
        deleteCustomWordPack(wordPack, false);
    }

    private void throwIfAuthenticatedUserIsNotWordPackUser(WordPack wordPack) {
        Integer userId = userService.getUser().id();
        if (wordPack.getUserId() == null || !wordPack.getUserId().equals(userId)) {
            throw new BadRequestException(I18nUtil.getMessage("dailylexika-exceptions.wordPack.notOwner"));
        }
    }

    @Override
    public void throwIfWordPackCategoryNotCustom(WordPack wordPack) {
        if (!wordPack.getCategory().equals(Category.CUSTOM)) {
            throw new BadRequestException(I18nUtil.getMessage("dailylexika-exceptions.wordPack.categoryNotCustom"));
        }
    }

    private void deleteCustomWordPack(WordPack wordPack, boolean skipOwnerCheck) {
        throwIfWordPackCategoryNotCustom(wordPack);
        if (!skipOwnerCheck) {
            throwIfAuthenticatedUserIsNotWordPackUser(wordPack);
        }
        publishWordPackToBeDeletedEvent(wordPack);
        wordPackRepository.delete(wordPack);
    }

    private WordPack getNonCustomWordPack(Long wordPackId) {
        WordPack wordPack = getById(wordPackId);
        if (wordPack.getCategory() == Category.CUSTOM) {
            throw new BadRequestException(I18nUtil.getMessage("dailylexika-exceptions.wordPack.onlyNonCustomAllowed"));
        }
        return wordPack;
    }

    private WordPackDto toWordPackDto(WordPack wordPack) {
        return new WordPackDto(
                wordPack.getId(),
                wordPack.getName(),
                wordPack.getDescription(),
                wordPack.getCategory(),
                wordPack.getPlatform(),
                wordPack.getUserId()
        );
    }

    private void publishWordPackToBeDeletedEvent(WordPack wordPack) {
        eventPublisher.publishEvent(
                WordPackToBeDeletedEvent.builder()
                        .wordPackId(wordPack.getId())
                        .platform(wordPack.getPlatform())
                        .build()
        );
    }
}
