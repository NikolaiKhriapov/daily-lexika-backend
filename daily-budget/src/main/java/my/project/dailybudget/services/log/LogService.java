package my.project.dailybudget.services.log;

import lombok.RequiredArgsConstructor;
import my.project.dailybudget.entities.log.Log;
import my.project.dailybudget.entities.user.User;
import my.project.dailybudget.mappers.log.LogMapper;
import my.project.dailybudget.repositories.log.LogRepository;
import my.project.library.dailybudget.dtos.log.LogDto;
import my.project.library.dailybudget.enumerations.LogAction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LogService {

    private final LogRepository logRepository;
    private final LogMapper logMapper;

    public void logAccountRegistration(User user) {
        logRepository.save(
                new Log(
                        user.getId(),
                        user.getEmail(),
                        LogAction.ACCOUNT_REGISTRATION
                )
        );
    }

    public void logAccountDeletion(User user) {
        logRepository.save(
                new Log(
                        user.getId(),
                        user.getEmail(),
                        LogAction.ACCOUNT_DELETION
                )
        );
    }

    public void logEmailUpdate(User user, String newEmail) {
        logRepository.save(
                new Log(
                        user.getId(),
                        user.getEmail(),
                        LogAction.EMAIL_UPDATE,
                        user.getEmail() + " -> " + newEmail
                )
        );
    }

    public Page<LogDto> getPageOfLogs(Pageable pageable) {
        Page<Log> pageOfLogs = logRepository.findAll(pageable);

        List<LogDto> listOfUserDto = logMapper.toDtoList(pageOfLogs.getContent());

        return new PageImpl<>(listOfUserDto, pageable, pageOfLogs.getTotalElements());
    }
}
