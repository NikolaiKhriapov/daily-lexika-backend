package my.project.services.log;

import lombok.RequiredArgsConstructor;
import my.project.models.entities.enumerations.LogAction;
import my.project.models.entities.enumerations.Platform;
import my.project.models.entities.log.Log;
import my.project.models.entities.user.User;
import my.project.repositories.log.LogRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LogService {

    private final LogRepository logRepository;

    public void logAccountRegistration(User user, Platform platform) {
        logRepository.save(
                new Log(
                        user.getId(),
                        user.getEmail(),
                        LogAction.ACCOUNT_REGISTRATION,
                        platform
                )
        );
    }

    public void logAccountDeletion(User user, Platform platform) {
        logRepository.save(
                new Log(
                        user.getId(),
                        user.getEmail(),
                        LogAction.ACCOUNT_DELETION,
                        platform
                )
        );
    }
}
