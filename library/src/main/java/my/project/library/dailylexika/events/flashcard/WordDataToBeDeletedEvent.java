package my.project.library.dailylexika.events.flashcard;

import lombok.Builder;

@Builder
public record WordDataToBeDeletedEvent(
        Integer wordDataId
) {
}
