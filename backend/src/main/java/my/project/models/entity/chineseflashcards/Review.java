package my.project.models.entity.chineseflashcards;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "reviews")
public class Review {

    @Id
    @SequenceGenerator(name = "review_id_sequence", sequenceName = "review_id_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "review_id_sequence")
    private Long id;

    private Long userId;

    private Integer maxNewWordsPerDay;

    private Integer maxReviewWordsPerDay;

    @OneToOne
    private WordPack wordPack;

    @ManyToMany(fetch = FetchType.EAGER)
    @OrderColumn
    private List<Word> listOfWords;

    private LocalDate dateLastCompleted;

    private LocalDate dateGenerated;

    public Review(Long userId,
                  Integer maxNewWordsPerDay,
                  Integer maxReviewWordsPerDay,
                  WordPack wordPack,
                  List<Word> listOfWords) {
        this.userId = userId;
        this.maxNewWordsPerDay = maxNewWordsPerDay;
        this.maxReviewWordsPerDay = maxReviewWordsPerDay;
        this.wordPack = wordPack;
        this.listOfWords = listOfWords;
        this.dateGenerated = LocalDate.now();
    }
}
