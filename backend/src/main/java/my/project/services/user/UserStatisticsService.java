package my.project.services.user;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import my.project.models.dto.user.UserDTO;
import my.project.models.mapper.user.UserMapper;
import my.project.models.entity.user.User;
import my.project.repositories.user.UserRepository;
import org.springframework.context.MessageSource;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

import static java.time.temporal.ChronoUnit.DAYS;

@Service
@RequiredArgsConstructor
public class UserStatisticsService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final MessageSource messageSource;

    public UserDTO getUserStatistics() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userMapper.toDTOStatistics(user);
    }

    @Transactional
    public void updateUserStreak() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long daysFromLastStreak = DAYS.between(user.getDateOfLastStreak(), LocalDate.now());
        Long differenceBetweenRecordStreakAndCurrentStreak = user.getRecordStreak() - user.getCurrentStreak();

        updateCurrentStreak(user, daysFromLastStreak);
        updateRecordStreak(user, differenceBetweenRecordStreakAndCurrentStreak, daysFromLastStreak);
        user.setDateOfLastStreak(LocalDate.now());

        userRepository.save(user);
    }

    private void updateCurrentStreak(User user, Long daysFromLastStreak) {
        if (daysFromLastStreak == 1) {
            user.setCurrentStreak(user.getCurrentStreak() + 1);
        } else if (daysFromLastStreak > 1) {
            user.setCurrentStreak(1L);
        } else if (daysFromLastStreak < 0) {
            throw new RuntimeException(messageSource.getMessage(
                    "exception.userStatistics.updateUserStreak.erroneousCurrentStreak", null, Locale.getDefault()));
        }
    }

    private void updateRecordStreak(User user, Long differenceBetweenRecordStreakAndCurrentStreak, Long daysFromLastStreak) {
        if (differenceBetweenRecordStreakAndCurrentStreak == 0 && daysFromLastStreak > 0) {
            user.setRecordStreak(user.getRecordStreak() + 1);
        } else if (differenceBetweenRecordStreakAndCurrentStreak < 0) {
            throw new RuntimeException(messageSource.getMessage(
                    "exception.userStatistics.updateUserStreak.erroneousCurrentStreak", null, Locale.getDefault()));
        }
    }
}