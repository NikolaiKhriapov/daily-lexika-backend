package my.project.library.dailylexika.events.flashcard;

import lombok.Builder;
import my.project.library.dailylexika.enumerations.Platform;

@Builder
public record CustomWordPackToBeDeletedEvent(
        String wordPackName,
        Platform platform
) {
}
