package my.project.dailylexika.services.flashcards;

import lombok.RequiredArgsConstructor;
import my.project.dailylexika.config.i18n.I18nUtil;
import my.project.library.dailylexika.dtos.flashcards.WordDto;
import my.project.library.dailylexika.enumerations.Platform;
import my.project.library.dailylexika.enumerations.Status;
import my.project.dailylexika.entities.flashcards.Word;
import my.project.dailylexika.entities.user.RoleStatistics;
import my.project.dailylexika.entities.user.User;
import my.project.dailylexika.mappers.flashcards.WordMapper;
import my.project.dailylexika.repositories.flashcards.WordRepository;
import my.project.dailylexika.services.user.RoleService;
import my.project.library.util.exception.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WordService {

    private final WordRepository wordRepository;
    private final WordMapper wordMapper;
    private final WordDataService wordDataService;
    private final RoleService roleService;

    public Page<WordDto> getPageOfWordsByStatus(Status status, Pageable pageable) {
        Integer userId = ((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();
        RoleStatistics currentRole = roleService.getRoleStatistics();
        Platform platform = roleService.getPlatformByRoleName(currentRole.getRoleName());

        Page<Word> pageOfWords = wordRepository.findByUserIdAndWordData_PlatformAndStatus(userId, platform, status, pageable);
        List<WordDto> listOfWordDto = wordMapper.toDtoList(pageOfWords.getContent());

        return new PageImpl<>(listOfWordDto, pageable, pageOfWords.getTotalElements());
    }

    public WordDto getWordOfTheDay() {
        RoleStatistics currentRole = roleService.getRoleStatistics();
        Platform platform = roleService.getPlatformByRoleName(currentRole.getRoleName());

        Integer wordDataId = wordDataService.findIdByWordOfTheDayDateAndPlatform(platform);

        return findByWordDataId(wordDataId);
    }

    public WordDto findByWordDataId(Integer wordDataId) {
        Integer userId = ((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();

        Word word = wordRepository.findByUserIdAndWordData_Id(userId, wordDataId)
                .orElseThrow(() -> new ResourceNotFoundException(I18nUtil.getMessage("dailylexika-exceptions.word.notFound")));

        return wordMapper.toDto(word);
    }

    public void createAllWordsForUserAndPlatform(Integer userId, Platform platform) {
        List<Integer> allWordDataIdByPlatform = wordDataService.findAllWordDataIdByPlatform(platform);
        List<Word> allExistingWordsByUser = wordRepository.findAllByUserId(userId);

        List<Word> wordsToBeSaved = allWordDataIdByPlatform.stream()
                .filter(wordDataId -> allExistingWordsByUser.stream()
                        .noneMatch(word -> word.getWordData().getId().equals(wordDataId))
                )
                .map(wordDataId -> new Word(userId, wordDataService.findById(wordDataId)))
                .toList();

        wordRepository.saveAll(wordsToBeSaved);
    }

    public synchronized void updateWordsForUser(Integer userId, List<Integer> wordDataIds) {
        List<Word> existingWords = wordRepository.findByUserIdAndWordDataIdIn(userId, wordDataIds);
        List<Word> wordsToBeSaved = wordDataIds.stream()
                .filter(wordDataId -> existingWords.stream()
                        .noneMatch(word -> word.getWordData().getId().equals(wordDataId))
                )
                .map(wordDataId -> new Word(userId, wordDataService.findById(wordDataId)))
                .toList();

        wordRepository.saveAll(wordsToBeSaved);
    }

    public Page<Word> findByUserIdAndWordDataIdIn(Integer userId, List<Integer> wordDataIds, Pageable pageable) {
        return wordRepository.findByUserIdAndWordDataIdIn(userId, wordDataIds, pageable);
    }

    public List<Word> findAllByUserIdAndWordDataIdInAndStatusInRandomLimited(Integer userId, List<Integer> wordDataIds, List<Status> statuses, Integer limit) {
        return wordRepository.findAllByUserIdAndWordDataIdInAndStatusInRandomLimited(userId, wordDataIds, statuses, limit);
    }

    public Long countByWordPackNameAndStatusForUser(String wordPackName, Status status) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Platform platform = roleService.getPlatformByRoleName(user.getRole());

        List<Integer> listOfWordDataId = wordDataService.findAllWordDataIdByWordPackNameAndPlatform(wordPackName, platform);
        return (long) countByUserIdAndWordData_IdInAndStatus(user.getId(), listOfWordDataId, status);
    }

    public Integer countByUserIdAndWordData_IdInAndStatus(Integer userId, List<Integer> wordDataIds, Status status) {
        return wordRepository.countByUserIdAndWordData_IdInAndStatus(userId, wordDataIds, status);
    }

    public List<Word> findAllByUserIdAndWordDataIdInAndStatusInAndPeriodBetweenOrderedLimited(Integer userId, List<Integer> wordDataIds, List<Status> statuses, Integer limit) {
        return wordRepository.findAllByUserIdAndWordDataIdInAndStatusInAndPeriodBetweenOrderedLimited(userId, wordDataIds, statuses, limit);
    }

    public void deleteAllByWordDataId(List<Integer> wordDataIds) {
        wordDataIds.forEach(wordRepository::deleteAllByWordData_Id);
    }

    public void deleteAllByUserIdAndPlatform(Integer userId, Platform platform) {
        List<Word> allWordsByUserId = wordRepository.findByUserIdAndWordData_Platform(userId, platform);
        wordRepository.deleteAll(allWordsByUserId);
    }
}
