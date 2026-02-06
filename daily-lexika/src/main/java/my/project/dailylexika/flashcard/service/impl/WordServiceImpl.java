package my.project.dailylexika.flashcard.service.impl;

import lombok.RequiredArgsConstructor;
import my.project.dailylexika.config.I18nUtil;
import my.project.dailylexika.flashcard.service.WordDataService;
import my.project.dailylexika.flashcard.service.WordService;
import my.project.dailylexika.user._public.PublicRoleService;
import my.project.dailylexika.user._public.PublicUserService;
import my.project.library.dailylexika.dtos.flashcards.WordDto;
import my.project.library.dailylexika.dtos.user.RoleStatisticsDto;
import my.project.library.dailylexika.dtos.user.UserDto;
import my.project.library.dailylexika.enumerations.Platform;
import my.project.library.dailylexika.enumerations.Status;
import my.project.dailylexika.flashcard.model.entities.Word;
import my.project.dailylexika.flashcard.model.mappers.WordMapper;
import my.project.dailylexika.flashcard.persistence.WordRepository;
import my.project.library.util.datetime.DateUtil;
import my.project.library.util.exception.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
@Validated
public class WordServiceImpl implements WordService {

    private final WordRepository wordRepository;
    private final WordMapper wordMapper;
    private final WordDataService wordDataService;
    private final PublicUserService userService;
    private final PublicRoleService roleService;

    @Override
    @Transactional(readOnly = true)
    public WordDto getByWordDataId(Integer wordDataId) {
        Integer userId = userService.getUser().id();
        Word word = wordRepository.findByUserIdAndWordData_Id(userId, wordDataId)
                .orElseThrow(() -> new ResourceNotFoundException(I18nUtil.getMessage("dailylexika-exceptions.word.notFound")));
        return wordMapper.toDto(word);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<WordDto> getByUserIdAndWordDataIdIn(Integer userId, List<Integer> wordDataIds, Pageable pageable) {
        Page<Word> wordsPage = wordRepository.findByUserIdAndWordDataIdIn(userId, wordDataIds, pageable);
        return wordsPage.map(wordMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<WordDto> getPageByWordPackId(Long wordPackId, Pageable pageable) {
        UserDto user = userService.getUser();
        Platform platform = roleService.getPlatformByRoleName(user.role());

        List<Integer> wordDataIds = wordDataService.getAllWordDataIdByWordPackIdAndPlatform(wordPackId, platform);
        Page<WordDto> pageOfWords = getByUserIdAndWordDataIdIn(user.id(), wordDataIds, pageable);

        return new PageImpl<>(pageOfWords.getContent(), pageable, pageOfWords.getTotalElements());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<WordDto> getPageByStatus(Status status, Pageable pageable) {
        Integer userId = userService.getUser().id();
        RoleStatisticsDto currentRole = roleService.getRoleStatistics();
        Platform platform = roleService.getPlatformByRoleName(currentRole.roleName());

        Page<Word> pageOfWords = wordRepository.findByUserIdAndWordData_PlatformAndStatus(userId, platform, status, pageable);
        List<WordDto> listOfWordDto = wordMapper.toDtoList(pageOfWords.getContent());

        return new PageImpl<>(listOfWordDto, pageable, pageOfWords.getTotalElements());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Word> getAllByUserIdAndWordDataIdInAndStatusNewRandomLimited(Integer userId, List<Integer> wordDataIds, Integer limit) {
        return wordRepository.findAllByUserIdAndWordDataIdInAndStatusNewRandomLimited(userId, wordDataIds, limit);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Word> getAllByUserIdAndWordDataIdInAndStatusInReviewAndPeriodBetweenOrderedDescLimited(Integer userId, List<Integer> wordDataIds, Integer limit) {
        List<Word> allReviewWords = wordRepository.findAllByUserIdAndWordDataIdInAndStatusInReviewAndPeriodBetweenOrderedDescLimited(userId, wordDataIds);
        return allReviewWords.stream()
                .filter(w -> ChronoUnit.DAYS.between(w.getDateOfLastOccurrence(), DateUtil.nowInUtc()) >= Math.pow(2, w.getTotalStreak()))
                .sorted((w1, w2) -> w2.getDateOfLastOccurrence().compareTo(w1.getDateOfLastOccurrence()))
                .limit(limit)
                .toList();

    }

    @Override
    @Transactional(readOnly = true)
    public List<Word> getAllByUserIdAndWordDataIdInAndStatusKnownAndPeriodBetweenOrderedAscLimited(Integer userId, List<Integer> wordDataIds, Integer limit) {
        List<Word> allKnownWords = wordRepository.findAllByUserIdAndWordDataIdInAndStatusKnownAndPeriodBetweenOrderedAscLimited(userId, wordDataIds);
        return  allKnownWords.stream()
                .filter(w -> ChronoUnit.DAYS.between(w.getDateOfLastOccurrence(), DateUtil.nowInUtc()) >= Math.pow(2, w.getTotalStreak()))
                .sorted((w1, w2) -> w1.getDateOfLastOccurrence().compareTo(w2.getDateOfLastOccurrence()))
                .limit(limit)
                .toList();

    }

    @Override
    @Transactional(readOnly = true)
    public List<WordDto> getAllByUserIdAndStatusAndWordData_Platform(Integer userId, Status status, Platform platform) {
        List<Word> words = wordRepository.findByUserIdAndStatusAndWordData_Platform(userId, status, platform);
        return wordMapper.toDtoList(words);
    }

    @Override
    @Transactional
    public void createAllWordsForUserAndPlatform(Integer userId, Platform platform) {
        List<Integer> allWordDataIdByPlatform = wordDataService.getAllWordDataIdByPlatform(platform);
        List<Word> allExistingWordsByUser = wordRepository.findAllByUserId(userId);

        List<Word> wordsToBeSaved = allWordDataIdByPlatform.stream()
                .filter(wordDataId -> allExistingWordsByUser.stream()
                        .noneMatch(word -> word.getWordData().getId().equals(wordDataId))
                )
                .map(wordDataId -> new Word(userId, wordDataService.getEntityById(wordDataId)))
                .toList();

        wordRepository.saveAll(wordsToBeSaved);
    }

    @Override
    @Transactional
    public synchronized void updateWordsForUser(Integer userId, List<Integer> wordDataIds) {
        List<Word> existingWords = wordRepository.findByUserIdAndWordDataIdIn(userId, wordDataIds);
        List<Word> wordsToBeSaved = wordDataIds.stream()
                .filter(wordDataId -> existingWords.stream()
                        .noneMatch(word -> word.getWordData().getId().equals(wordDataId))
                )
                .map(wordDataId -> new Word(userId, wordDataService.getEntityById(wordDataId)))
                .toList();
        wordRepository.saveAll(wordsToBeSaved);
    }

    @Override
    @Transactional
    public void deleteAllByWordDataId(List<Integer> wordDataIds) {
        wordDataIds.forEach(wordRepository::deleteAllByWordData_Id);
    }

    @Override
    @Transactional
    public void deleteAllByUserIdAndPlatform(Integer userId, Platform platform) {
        List<Word> allWordsByUserId = wordRepository.findByUserIdAndWordData_Platform(userId, platform);
        wordRepository.deleteAll(allWordsByUserId);
    }

    @Override
    @Transactional(readOnly = true)
    public WordDto getWordOfTheDay() {
        RoleStatisticsDto currentRole = roleService.getRoleStatistics();
        Platform platform = roleService.getPlatformByRoleName(currentRole.roleName());
        Integer wordDataId = wordDataService.getIdByWordOfTheDayDateAndPlatform(platform);
        return getByWordDataId(wordDataId);
    }

    @Override
    @Transactional(readOnly = true)
    public Integer countByUserIdAndWordData_IdInAndStatus(Integer userId, List<Integer> wordDataIds, Status status) {
        return wordRepository.countByUserIdAndWordData_IdInAndStatus(userId, wordDataIds, status);
    }
}
