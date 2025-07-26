package my.project.dailylexika.log.service;

import my.project.library.dailylexika.dtos.log.LogDto;
import my.project.library.dailylexika.enumerations.Platform;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface LogService {
    Page<LogDto> getPageOfLogs(Pageable pageable);
    void logAccountRegistration(Integer userId, String userEmail, Platform platform);
    void logAccountDeletion(Integer userId, String userEmail, Platform platform);
    void logEmailUpdate(Integer userId, String userEmail, Platform platform, String emailUpdated);
}
