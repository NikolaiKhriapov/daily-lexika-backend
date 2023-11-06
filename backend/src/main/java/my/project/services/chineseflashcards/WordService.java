package my.project.services.chineseflashcards;

import lombok.RequiredArgsConstructor;
import my.project.models.dto.chineseflashcards.WordStatisticsDTO;
import my.project.models.entity.chineseflashcards.Status;
import my.project.repositories.chineseflashcards.WordRepository;
import my.project.models.dto.user.UserDTO;
import my.project.models.entity.user.User;
import my.project.models.mapper.user.UserMapper;
import my.project.services.user.AuthenticationService;
import my.project.services.user.UserAccountService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WordService {

    private final WordRepository wordRepository;
    private final AuthenticationService authenticationService;

    public WordStatisticsDTO getWordStatistics() {
        Long userId = authenticationService.getAuthenticatedUser().id();
        return new WordStatisticsDTO(wordRepository.findAllByUserIdAndStatusIs(userId, Status.KNOWN).size());
    }

    public void deleteAllByUserId(Long userId) {
        wordRepository.deleteAllByUserId(userId);
    }
}