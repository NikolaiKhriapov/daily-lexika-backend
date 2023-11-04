package my.project.models.entity.chineseflashcards;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "reviews")
public class Review {

    @Id
    @SequenceGenerator(name = "sequence", sequenceName = "sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequence")
    private Long id;

    private Long userId;

    private Integer maxNewWordsPerDay;

    private Integer maxReviewWordsPerDay;

    @OneToOne
    private WordPack wordPack;

    @ManyToMany(fetch = FetchType.EAGER)
    @ToString.Exclude
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
