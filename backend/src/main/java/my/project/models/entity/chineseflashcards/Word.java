package my.project.models.entity.chineseflashcards;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@Entity(name = "words")
public class Word {

    @Id
    @SequenceGenerator(name = "word_id_sequence", sequenceName = "word_id_sequence")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "word_id_sequence")
    private Long id;

    private Long userId;

    private Long wordId;

    @Enumerated(EnumType.STRING)
    private Status status;

    private Integer currentStreak;

    private Integer totalStreak;

    private Integer occurrence;

    private LocalDate dateOfLastOccurrence;

    @ManyToMany
    @ToString.Exclude
    private List<Review> listOfReviews;

    public Word(Long userId,
                Long wordId) {
        this.userId = userId;
        this.wordId = wordId;
        this.status = Status.NEW;
        this.currentStreak = 0;
        this.totalStreak = 0;
        this.occurrence = 0;
        this.dateOfLastOccurrence = LocalDate.now().minusDays(1);
        this.listOfReviews = new ArrayList<>();
    }
}