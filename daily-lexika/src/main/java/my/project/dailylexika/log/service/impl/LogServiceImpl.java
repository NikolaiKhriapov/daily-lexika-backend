package my.project.dailylexika.log.service.impl;

import lombok.RequiredArgsConstructor;
import my.project.dailylexika.log.model.mappers.LogMapper;
import my.project.dailylexika.log.service.LogService;
import my.project.library.dailylexika.dtos.log.LogDto;
import my.project.library.dailylexika.enumerations.LogAction;
import my.project.library.dailylexika.enumerations.Platform;
import my.project.dailylexika.log.model.entities.Log;
import my.project.dailylexika.log.persistence.LogRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LogServiceImpl implements LogService {

    private final LogRepository logRepository;
    private final LogMapper logMapper;

    @Override
    public Page<LogDto> getPageOfLogs(Pageable pageable) {
        Page<Log> pageOfLogs = logRepository.findAll(pageable);

        List<LogDto> listOfUserDto = logMapper.toDtoList(pageOfLogs.getContent());

        return new PageImpl<>(listOfUserDto, pageable, pageOfLogs.getTotalElements());
    }

    @Override
    public void logAccountRegistration(Integer userId, String userEmail, Platform platform) {
        logRepository.save(
                new Log(
                        userId,
                        userEmail,
                        LogAction.ACCOUNT_REGISTRATION,
                        platform
                )
        );
    }

    @Override
    public void logAccountDeletion(Integer userId, String userEmail, Platform platform) {
        logRepository.save(
                new Log(
                        userId,
                        userEmail,
                        LogAction.ACCOUNT_DELETION,
                        platform
                )
        );
    }

    @Override
    public void logEmailUpdate(Integer userId, String userEmail, Platform platform, String emailUpdated) {
        logRepository.save(
                new Log(
                        userId,
                        userEmail,
                        LogAction.EMAIL_UPDATE,
                        platform,
                        userEmail + " -> " + emailUpdated
                )
        );
    }
}
