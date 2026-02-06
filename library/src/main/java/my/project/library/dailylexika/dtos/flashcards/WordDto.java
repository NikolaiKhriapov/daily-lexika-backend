package my.project.library.dailylexika.dtos.flashcards;

import my.project.library.dailylexika.enumerations.Status;

import java.time.OffsetDateTime;

public record WordDto(

        Long id,
        Integer userId,
        WordDataDto wordDataDto,
        Status status,
        Short currentStreak,
        Short totalStreak,
        Short occurrence,
        OffsetDateTime dateOfLastOccurrence

) {
}
