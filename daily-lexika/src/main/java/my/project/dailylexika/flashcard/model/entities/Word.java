package my.project.dailylexika.flashcard.model.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import my.project.library.util.datetime.DateUtil;
import my.project.library.dailylexika.enumerations.Status;

import java.time.OffsetDateTime;

@Data
@NoArgsConstructor
@Entity(name = "words")
public class Word {

    @Id
    @SequenceGenerator(name = "word_id_sequence", sequenceName = "word_id_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "word_id_sequence")
    private Long id;

    private Integer userId;

    @OneToOne
    private WordData wordData;

    @Enumerated(EnumType.STRING)
    private Status status;

    private Short currentStreak;

    private Short totalStreak;

    private Short occurrence;

    private OffsetDateTime dateOfLastOccurrence;

    public Word(Integer userId, WordData wordData) {
        this.userId = userId;
        this.wordData = wordData;
        this.status = Status.NEW;
        this.currentStreak = 0;
        this.totalStreak = 0;
        this.occurrence = 0;
        this.dateOfLastOccurrence = DateUtil.nowInUtc().minusDays(1);
    }
}
