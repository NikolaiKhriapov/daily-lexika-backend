package my.project.dailylexika.log.service;

import lombok.RequiredArgsConstructor;
import my.project.dailylexika.log.model.mappers.LogMapper;
import my.project.library.dailylexika.dtos.log.LogDto;
import my.project.library.dailylexika.enumerations.LogAction;
import my.project.library.dailylexika.enumerations.Platform;
import my.project.dailylexika.log.model.entities.Log;
import my.project.dailylexika.user.model.entities.User;
import my.project.dailylexika.log.persistence.LogRepository;
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

    public Page<LogDto> getPageOfLogs(Pageable pageable) {
        Page<Log> pageOfLogs = logRepository.findAll(pageable);

        List<LogDto> listOfUserDto = logMapper.toDtoList(pageOfLogs.getContent());

        return new PageImpl<>(listOfUserDto, pageable, pageOfLogs.getTotalElements());
    }
}
