package my.project.dailylexika.services.log;

import lombok.RequiredArgsConstructor;
import my.project.library.dailylexika.enumerations.LogAction;
import my.project.library.dailylexika.enumerations.Platform;
import my.project.dailylexika.entities.log.Log;
import my.project.dailylexika.entities.user.User;
import my.project.dailylexika.repositories.log.LogRepository;
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

    public void logEmailUpdate(User user, Platform platform, String newEmail) {
        logRepository.save(
                new Log(
                        user.getId(),
                        user.getEmail(),
                        LogAction.EMAIL_UPDATE,
                        platform,
                        user.getEmail() + " -> " + newEmail
                )
        );
    }
}
