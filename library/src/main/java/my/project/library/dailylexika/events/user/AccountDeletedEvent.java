package my.project.library.dailylexika.events.user;

import lombok.Builder;
import my.project.library.dailylexika.enumerations.Platform;

@Builder
public record AccountDeletedEvent(
        Integer userId,
        String userEmail,
        Platform platform,
        boolean isDeleteUser
) {
}
