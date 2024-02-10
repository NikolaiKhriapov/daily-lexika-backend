package my.project.services.flashcards;

import lombok.RequiredArgsConstructor;
import my.project.models.dto.flashcards.WordDTO;
import my.project.models.entity.enumeration.Platform;
import my.project.models.entity.enumeration.Status;
import my.project.models.entity.flashcards.Word;
import my.project.models.entity.user.RoleStatistics;
import my.project.models.mapper.flashcards.WordMapper;
import my.project.repositories.flashcards.WordRepository;
import my.project.services.user.AuthenticationService;
import my.project.services.user.RoleService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WordService {

    private final WordRepository wordRepository;
    private final WordMapper wordMapper;
    private final WordDataService wordDataService;
    private final RoleService roleService;
    private final AuthenticationService authenticationService;

    public List<WordDTO> getAllWordsByStatus(Status status, Pageable pageable) {
        Long userId = authenticationService.getAuthenticatedUser().getId();
        RoleStatistics currentRole = roleService.getRoleStatistics();
        Platform platform = roleService.getPlatformByRoleName(currentRole.getRoleName());
        List<Word> allWordsByStatus = wordRepository.findAllByUserIdAndPlatformAndStatus(userId, platform, status, pageable);
        return wordMapper.toDTOList(allWordsByStatus);
    }

    public void createOrUpdateWordsForUser(Long userId, List<Long> wordDataIds) {
        List<Word> existingWords = wordRepository.findByUserIdAndWordDataIdIn(userId, wordDataIds);
        List<Word> wordsToBeSaved = wordDataIds.stream()
                .filter(wordDataId -> existingWords.stream()
                        .noneMatch(word -> word.getWordData().getId().equals(wordDataId))
                )
                .map(wordDataId -> new Word(userId, wordDataService.findById(wordDataId))) // TODO::: change to mapper
                .toList();

        wordRepository.saveAll(wordsToBeSaved);
    }

    public Page<Word> findByUserIdAndWordDataIdIn(Long userId, List<Long> wordDataIds, Pageable pageable) {
        return wordRepository.findByUserIdAndWordDataIdIn(userId, wordDataIds, pageable);
    }

    public List<Word> findByUserIdAndWordDataIdInAndStatusIn(Long userId, List<Long> wordDataIds, List<Status> status, Pageable pageable) {
        return wordRepository.findByUserIdAndWordDataIdInAndStatusIn(userId, wordDataIds, status, pageable);
    }

    public Integer countByUserIdAndWordDataIdInAndStatusEquals(Long userId, List<Long> wordDataIds, Status status) {
        return wordRepository.countByUserIdAndWordDataIdInAndStatusEquals(userId, wordDataIds, status);
    }

    public List<Word> findByUserIdAndWordDataIdInAndStatusInAndPeriodBetweenOrdered(Long userId, List<Long> wordDataIds, List<Status> statuses, Pageable pageable) {
        return wordRepository.findByUserIdAndWordDataIdInAndStatusInAndPeriodBetweenOrdered(userId, wordDataIds, statuses, pageable);
    }

    public void deleteAllByUserIdAndPlatform(Long userId, Platform platform) {
        List<Word> allWordsByUserId = wordRepository.findAllByUserIdAndPlatform(userId, platform);
        wordRepository.deleteAll(allWordsByUserId);
    }
}