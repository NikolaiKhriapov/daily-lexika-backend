package my.project.services.flashcards;

import lombok.RequiredArgsConstructor;
import my.project.exception.ResourceNotFoundException;
import my.project.models.dtos.flashcards.WordDto;
import my.project.models.entities.enumerations.Platform;
import my.project.models.entities.enumerations.Status;
import my.project.models.entities.flashcards.Word;
import my.project.models.entities.user.RoleStatistics;
import my.project.models.entities.user.User;
import my.project.models.mappers.flashcards.WordMapper;
import my.project.repositories.flashcards.WordRepository;
import my.project.services.user.RoleService;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class WordService {

    private final WordRepository wordRepository;
    private final WordMapper wordMapper;
    private final WordDataService wordDataService;
    private final RoleService roleService;
    private final MessageSource messageSource;

    public List<WordDto> getAllWordsByStatus(Status status, Pageable pageable) {
        Integer userId = ((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();
        RoleStatistics currentRole = roleService.getRoleStatistics();
        Platform platform = roleService.getPlatformByRoleName(currentRole.getRoleName());
        List<Word> allWordsByStatus = wordRepository.findByUserIdAndWordData_PlatformAndStatus(userId, platform, status, pageable);
        return wordMapper.toDtoList(allWordsByStatus);
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
                .orElseThrow(() -> new ResourceNotFoundException(messageSource.getMessage(
                        "exception.word.notFound", null, Locale.getDefault())));

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

    public List<Word> findByUserIdAndWordDataIdInAndStatusIn(Integer userId, List<Integer> wordDataIds, List<Status> status, Pageable pageable) {
        return wordRepository.findByUserIdAndWordDataIdInAndStatusIn(userId, wordDataIds, status, pageable);
    }

    public Integer countByUserIdAndWordData_IdInAndStatus(Integer userId, List<Integer> wordDataIds, Status status) {
        return wordRepository.countByUserIdAndWordData_IdInAndStatus(userId, wordDataIds, status);
    }

    public List<Word> findByUserIdAndWordDataIdInAndStatusInAndPeriodBetweenOrdered(Integer userId, List<Integer> wordDataIds, List<Status> statuses, Pageable pageable) {
        return wordRepository.findByUserIdAndWordDataIdInAndStatusInAndPeriodBetweenOrdered(userId, wordDataIds, statuses, pageable);
    }

    public void deleteAllByWordDataId(List<Integer> wordDataIds) {
        wordDataIds.forEach(wordRepository::deleteAllByWordData_Id);
    }

    public void deleteAllByUserIdAndPlatform(Integer userId, Platform platform) {
        List<Word> allWordsByUserId = wordRepository.findByUserIdAndWordData_Platform(userId, platform);
        wordRepository.deleteAll(allWordsByUserId);
    }
}
