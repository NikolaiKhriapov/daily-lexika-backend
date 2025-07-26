package my.project.dailylexika.log.listener;

import lombok.AllArgsConstructor;
import my.project.dailylexika.log.service.LogService;
import my.project.library.dailylexika.events.user.AccountDeletedEvent;
import my.project.library.dailylexika.events.user.AccountRegisteredEvent;
import my.project.library.dailylexika.events.user.UserEmailUpdatedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class LogListener {

    private final LogService logService;

    @EventListener
    public void on(AccountRegisteredEvent event) {
        logService.logAccountRegistration(event.userId(), event.userEmail(), event.platform());
    }

    @EventListener
    public void on(AccountDeletedEvent event) {
        logService.logAccountDeletion(event.userId(), event.userEmail(), event.platform());
    }

    @EventListener
    public void on(UserEmailUpdatedEvent event) {
        logService.logEmailUpdate(event.userId(), event.userEmail(), event.platform(), event.emailUpdated());
    }
}
