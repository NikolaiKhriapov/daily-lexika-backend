package my.project.models.entity.chineseflashcards;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@Entity(name = "words")
public class Word {

    @Id
    @SequenceGenerator(name = "word_id_sequence", sequenceName = "word_id_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "word_id_sequence")
    private Long id;

    private Long userId;

    private Long wordDataId;

    @Enumerated(EnumType.STRING)
    private Status status;

    private Integer currentStreak;

    private Integer totalStreak;

    private Integer occurrence;

    private LocalDate dateOfLastOccurrence;

    public Word(Long userId, Long wordDataId) {
        this.userId = userId;
        this.wordDataId = wordDataId;
        this.status = Status.NEW;
        this.currentStreak = 0;
        this.totalStreak = 0;
        this.occurrence = 0;
        this.dateOfLastOccurrence = LocalDate.now().minusDays(1);
    }
}