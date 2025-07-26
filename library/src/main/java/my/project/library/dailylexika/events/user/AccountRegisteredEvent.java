package my.project.library.dailylexika.events.user;

import lombok.Builder;
import my.project.library.dailylexika.enumerations.Platform;

@Builder
public record AccountRegisteredEvent(
        Integer userId,
        String userName,
        String userEmail,
        Platform platform,
        boolean isUserAlreadyExists
) {
}
