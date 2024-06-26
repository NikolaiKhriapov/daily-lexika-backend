package my.project.dailylexika.entities.flashcards;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import my.project.library.util.datetime.DateUtil;

import java.time.OffsetDateTime;
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

    private Integer userId;

    private Integer maxNewWordsPerDay;

    private Integer maxReviewWordsPerDay;

    @OneToOne
    private WordPack wordPack;

    @ManyToMany(fetch = FetchType.EAGER)
    @OrderColumn
    private List<Word> listOfWords;

    private Integer actualSize;

    private OffsetDateTime dateLastCompleted;

    private OffsetDateTime dateGenerated;

    public Review(Integer userId,
                  Integer maxNewWordsPerDay,
                  Integer maxReviewWordsPerDay,
                  WordPack wordPack,
                  List<Word> listOfWords,
                  Integer actualSize) {
        this.userId = userId;
        this.maxNewWordsPerDay = maxNewWordsPerDay;
        this.maxReviewWordsPerDay = maxReviewWordsPerDay;
        this.wordPack = wordPack;
        this.listOfWords = listOfWords;
        this.actualSize = actualSize;
        this.dateGenerated = DateUtil.nowInUtc();
    }
}
