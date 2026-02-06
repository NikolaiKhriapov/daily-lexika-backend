package my.project.dailylexika.flashcard.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import my.project.dailylexika.flashcard.model.entities.WordPack;
import my.project.dailylexika.flashcard.service.WordDataService;
import my.project.dailylexika.flashcard.service.WordPackService;
import my.project.dailylexika.user._public.PublicRoleService;
import my.project.dailylexika.user._public.PublicUserService;
import my.project.library.dailylexika.dtos.user.UserDto;
import my.project.library.dailylexika.events.flashcard.WordDataToBeDeletedEvent;
import my.project.library.util.datetime.DateUtil;
import my.project.dailylexika.config.I18nUtil;
import my.project.library.dailylexika.dtos.flashcards.WordDataDto;
import my.project.library.dailylexika.dtos.flashcards.admin.WordDataCreateDto;
import my.project.library.dailylexika.dtos.flashcards.admin.WordDataUpdateDto;
import my.project.library.dailylexika.enumerations.Category;
import my.project.library.dailylexika.enumerations.Platform;
import my.project.dailylexika.flashcard.model.entities.WordData;
import my.project.dailylexika.flashcard.model.mappers.WordDataMapper;
import my.project.dailylexika.flashcard.persistence.WordDataRepository;
import my.project.dailylexika.flashcard.service.validation.WordDataValidationUtil;
import my.project.library.util.exception.BadRequestException;
import my.project.library.util.exception.ResourceNotFoundException;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.text.Normalizer;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Validated
public class WordDataServiceImpl implements WordDataService {

    private final WordDataRepository wordDataRepository;
    private final WordDataMapper wordDataMapper;
    private final WordPackService wordPackService;
    private final PublicUserService userService;
    private final PublicRoleService roleService;
    private final ApplicationEventPublisher eventPublisher;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional(readOnly = true)
    public List<WordDataDto> search(String query, Integer limit) {
        UserDto user = userService.getUser();
        Platform platform = roleService.getPlatformByRoleName(user.role());
        String normalizedQuery = query.trim();
        String transcriptionQuery = normalizeTranscriptionQuery(query);
        List<WordData> matches = wordDataRepository
                .searchByPlatformAndQuery(platform, user.translationLanguage(), false, normalizedQuery, transcriptionQuery, PageRequest.of(0, limit))
                .getContent();
        return wordDataMapper.toDtoList(matches);
    }

    @Override
    @Transactional(readOnly = true)
    public List<WordData> getAllByWordPackIdAndPlatform(Long wordPackId, Platform platform) {
        return wordDataRepository.findAllByListOfWordPacks_IdAndPlatform(wordPackId, platform);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByWordPackIdAndPlatform(Long wordPackId, Platform platform) {
        return wordDataRepository.countByListOfWordPacks_IdAndPlatform(wordPackId, platform) > 0;
    }

    @Override
    @Transactional
    public WordDataDto addCustomWordPackToWordData(Integer wordDataId, Long wordPackId) {
        WordPack wordPack = wordPackService.getById(wordPackId);

        wordPackService.throwIfWordPackCategoryNotCustom(wordPack);
        throwIfNotOwner(wordPack);

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
    public WordDataDto removeCustomWordPackFromWordData(Integer wordDataId, Long wordPackId) {
        WordPack wordPack = wordPackService.getById(wordPackId);

        wordPackService.throwIfWordPackCategoryNotCustom(wordPack);
        throwIfNotOwner(wordPack);

        WordData wordData = getEntityById(wordDataId);
        List<WordPack> listOfWordPacks = wordData.getListOfWordPacks();
        if (listOfWordPacks.contains(wordPack)) {
            listOfWordPacks.remove(wordPack);
        } else {
            throw new BadRequestException(I18nUtil.getMessage("dailylexika-exceptions.wordPack.wordDataNotInWordPack", wordData.getId(), wordPack.getName()));
        }
        wordData.setListOfWordPacks(listOfWordPacks);
        wordData = wordDataRepository.save(wordData);

        return wordDataMapper.toDto(wordData);
    }

    @Override
    @Transactional
    public void addCustomWordPackToWordDataByWordPackId(Long wordPackIdToBeAdded, Long wordPackIdOriginal) {
        UserDto user = userService.getUser();
        Platform platform = roleService.getPlatformByRoleName(user.role());
        WordPack wordPackToBeAdded = wordPackService.getById(wordPackIdToBeAdded);
        WordPack wordPackOriginal = wordPackService.getById(wordPackIdOriginal);

        wordPackService.throwIfWordPackCategoryNotCustom(wordPackToBeAdded);
        throwIfNotOwner(wordPackToBeAdded);
        if (wordPackOriginal.getCategory() == Category.CUSTOM) {
            throwIfNotOwner(wordPackOriginal);
        }

        List<WordData> listOfWordData = getAllByWordPackIdAndPlatform(wordPackIdOriginal, platform);

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
    public void removeWordPackReferences(Long wordPackId, Platform platform) {
        List<WordData> wordDataList = wordDataRepository.findAllByListOfWordPacks_IdAndPlatform(wordPackId, platform);
        if (!wordDataList.isEmpty()) {
            wordDataList.forEach(wordData -> wordData.getListOfWordPacks()
                    .removeIf(wordPackItem -> wordPackItem.getId().equals(wordPackId)));
            wordDataRepository.saveAll(wordDataList);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Page<WordDataDto> getPage(Platform platform, String query, Pageable pageable) {
        if (query == null || query.isBlank()) {
            Sort sort = switch (platform) {
                case ENGLISH -> Sort.by(Sort.Direction.ASC, "nameEnglish");
                case CHINESE -> Sort.by(Sort.Direction.ASC, "nameChinese");
            };
            PageRequest sortedPage = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);
            return wordDataRepository.findAllByPlatform(platform, sortedPage)
                    .map(wordDataMapper::toDto);
        }
        String normalizedQuery = query.trim();
        String transcriptionQuery = normalizeTranscriptionQuery(normalizedQuery);
        return wordDataRepository.searchByPlatformAndQuery(platform, null, true, normalizedQuery, transcriptionQuery, pageable)
                .map(wordDataMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public WordDataDto getById(Integer wordDataId) {
        WordData entity = getEntityById(wordDataId);
        return wordDataMapper.toDto(entity);
    }

    @Override
    @Transactional
    public WordDataDto create(WordDataCreateDto createDto) {
        Integer generatedId = resolveNextId(createDto.platform());
        LocalDate nextWordOfTheDayDate = resolveNextWordOfTheDayDate(createDto.platform());
        validateCreate(createDto, generatedId);

        WordData wordData = new WordData();
        wordData.setId(generatedId);
        wordData.setNameChinese(createDto.nameChinese());
        wordData.setTranscription(createDto.transcription());
        wordData.setNameEnglish(createDto.nameEnglish());
        wordData.setNameRussian(createDto.nameRussian());
        wordData.setDefinition(createDto.definition());
        wordData.setExamples(serializeExamples(createDto.examples()));
        wordData.setListOfWordPacks(resolveWordPacks(createDto.listOfWordPackIds()));
        wordData.setWordOfTheDayDate(nextWordOfTheDayDate);
        wordData.setPlatform(createDto.platform());

        WordData saved = wordDataRepository.save(wordData);
        return wordDataMapper.toDto(saved);
    }

    @Override
    @Transactional
    public WordDataDto update(Integer wordDataId, WordDataUpdateDto dto) {
        WordData wordData = getEntityById(wordDataId);
        validateUpdate(wordData, dto);

        if (dto.nameChinese() != null) {
            wordData.setNameChinese(dto.nameChinese());
        }
        if (dto.transcription() != null) {
            wordData.setTranscription(dto.transcription());
        }
        if (dto.nameEnglish() != null) {
            wordData.setNameEnglish(dto.nameEnglish());
        }
        if (dto.nameRussian() != null) {
            wordData.setNameRussian(dto.nameRussian());
        }
        if (dto.definition() != null) {
            wordData.setDefinition(dto.definition());
        }
        if (dto.examples() != null) {
            wordData.setExamples(serializeExamples(dto.examples()));
        }
        if (dto.listOfWordPackIds() != null) {
            List<WordPack> resolved = resolveWordPacks(dto.listOfWordPackIds());
            List<WordPack> existingCustom = wordData.getListOfWordPacks().stream()
                    .filter(wordPack -> wordPack.getCategory() == Category.CUSTOM)
                    .toList();
            List<WordPack> merged = new ArrayList<>(resolved);
            existingCustom.stream()
                    .filter(pack -> !merged.contains(pack))
                    .forEach(merged::add);
            wordData.setListOfWordPacks(merged);
        }

        WordData saved = wordDataRepository.save(wordData);
        return wordDataMapper.toDto(saved);
    }

    @Override
    @Transactional
    public void delete(Integer wordDataId) {
        WordData wordData = getEntityById(wordDataId);
        publishWordDataToBeDeletedEvent(wordDataId);
        wordDataRepository.deleteAllWordPackLinksByWordDataId(wordDataId);
        wordDataRepository.deleteAll(List.of(wordData));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Integer> getAllWordDataIdByPlatform(Platform platform) {
        return wordDataRepository.findAllWordDataIdsByPlatform(platform);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Integer> getAllWordDataIdByWordPackIdAndPlatform(Long wordPackId, Platform platform) {
        return wordDataRepository.findAllWordDataIdsByWordPackIdAndPlatform(wordPackId, platform);
    }

    @Override
    @Transactional(readOnly = true)
    public Integer getIdByWordOfTheDayDateAndPlatform(Platform platform) {
        return wordDataRepository.findIdByWordOfTheDayDateAndPlatform(DateUtil.nowInUtc().toLocalDate(), platform)
                .orElseThrow(() -> new ResourceNotFoundException(I18nUtil.getMessage("dailylexika-exceptions.wordData.wordOfTheDayDate.notFound")));
    }

    @Override
    @Transactional(readOnly = true)
    public WordData getEntityById(Integer wordDataId) {
        return wordDataRepository.findById(wordDataId)
                .orElseThrow(() -> new ResourceNotFoundException(I18nUtil.getMessage("dailylexika-exceptions.wordData.notFound")));
    }

    private List<WordPack> resolveWordPacks(List<Long> wordPackIds) {
        List<WordPack> wordPacks = new ArrayList<>();
        for (Long wordPackId : wordPackIds) {
            WordPack wordPack = wordPackService.getById(wordPackId);
            if (wordPack.getCategory() == Category.CUSTOM) {
                throw new BadRequestException(I18nUtil.getMessage("dailylexika-exceptions.wordPack.onlyNonCustomAllowed"));
            }
            wordPacks.add(wordPack);
        }
        return wordPacks;
    }

    private String serializeExamples(Object examples) {
        try {
            return objectMapper.writeValueAsString(examples);
        } catch (Exception e) {
            throw new BadRequestException(I18nUtil.getMessage("dailylexika-exceptions.wordData.examples.invalid"));
        }
    }

    private void validateCreate(WordDataCreateDto createDto, Integer generatedId) {
        WordDataValidationUtil.ValidationInput input = new WordDataValidationUtil.ValidationInput(
                generatedId,
                createDto.platform(),
                createDto.nameChinese(),
                createDto.transcription(),
                createDto.nameEnglish(),
                createDto.nameRussian(),
                createDto.definition(),
                createDto.examples(),
                EnumSet.allOf(WordDataValidationUtil.Field.class),
                true
        );
        validateInput(input);
    }

    private Integer resolveNextId(Platform platform) {
        int baseId = switch (platform) {
            case CHINESE -> 2_000_001;
            case ENGLISH -> 3_000_001;
        };
        List<Integer> ids = wordDataRepository.findAllWordDataIdsByPlatformOrderByIdAsc(platform);
        int expected = baseId;
        for (Integer id : ids) {
            if (id == expected) {
                expected++;
                continue;
            }
            if (id > expected) {
                break;
            }
        }
        return expected;
    }

    private LocalDate resolveNextWordOfTheDayDate(Platform platform) {
        LocalDate latest = wordDataRepository.findMaxWordOfTheDayDateByPlatform(platform);
        if (latest == null) {
            return DateUtil.nowInUtc().toLocalDate();
        }
        return latest.plusDays(1);
    }

    private void validateUpdate(WordData wordData, WordDataUpdateDto patchDto) {
        EnumSet<WordDataValidationUtil.Field> fields = EnumSet.noneOf(WordDataValidationUtil.Field.class);
        if (patchDto.nameChinese() != null) {
            fields.add(WordDataValidationUtil.Field.NAME_CHINESE);
        }
        if (patchDto.transcription() != null) {
            fields.add(WordDataValidationUtil.Field.TRANSCRIPTION);
        }
        if (patchDto.nameEnglish() != null) {
            fields.add(WordDataValidationUtil.Field.NAME_ENGLISH);
        }
        if (patchDto.nameRussian() != null) {
            fields.add(WordDataValidationUtil.Field.NAME_RUSSIAN);
        }
        if (patchDto.definition() != null) {
            fields.add(WordDataValidationUtil.Field.DEFINITION);
        }
        if (patchDto.examples() != null) {
            fields.add(WordDataValidationUtil.Field.EXAMPLES);
        }
        if (fields.isEmpty()) {
            return;
        }

        String nameChinese = patchDto.nameChinese() != null ? patchDto.nameChinese() : wordData.getNameChinese();
        String transcription = patchDto.transcription() != null ? patchDto.transcription() : wordData.getTranscription();
        String nameEnglish = patchDto.nameEnglish() != null ? patchDto.nameEnglish() : wordData.getNameEnglish();
        String nameRussian = patchDto.nameRussian() != null ? patchDto.nameRussian() : wordData.getNameRussian();
        String definition = patchDto.definition() != null ? patchDto.definition() : wordData.getDefinition();

        List<Map<String, String>> examples = patchDto.examples();
        boolean examplesProvided = patchDto.examples() != null;
        if (!examplesProvided && wordData.getPlatform() == Platform.CHINESE && fields.contains(WordDataValidationUtil.Field.NAME_CHINESE)) {
            examples = parseExamples(wordData.getExamples());
        }

        WordDataValidationUtil.ValidationInput input = new WordDataValidationUtil.ValidationInput(
                wordData.getId(),
                wordData.getPlatform(),
                nameChinese,
                transcription,
                nameEnglish,
                nameRussian,
                definition,
                examples,
                fields,
                examplesProvided
        );
        validateInput(input);
    }

    private void validateInput(WordDataValidationUtil.ValidationInput input) {
        List<String> errors = WordDataValidationUtil.validate(input);
        if (!errors.isEmpty()) {
            throw new BadRequestException(I18nUtil.getMessage("dailylexika-exceptions.wordData.validation", String.join("; ", errors)));
        }
    }

    @SuppressWarnings("unchecked")
    private List<Map<String, String>> parseExamples(String examples) {
        try {
            return objectMapper.readValue(examples, List.class);
        } catch (Exception e) {
            return null;
        }
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

    private void publishWordDataToBeDeletedEvent(Integer wordDataId) {
        eventPublisher.publishEvent(
                WordDataToBeDeletedEvent.builder()
                        .wordDataId(wordDataId)
                        .build()
        );
    }

    private void throwIfNotOwner(WordPack wordPack) {
        Integer userId = userService.getUser().id();
        if (wordPack.getUserId() == null || !wordPack.getUserId().equals(userId)) {
            throw new BadRequestException(I18nUtil.getMessage("dailylexika-exceptions.wordPack.categoryNotCustom"));
        }
    }
}
