package my.project.vocabulary.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Data
@NoArgsConstructor
@Entity(name = "reviews")
public class Review {

    @Id
    @SequenceGenerator(name = "sequence", sequenceName = "sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequence")
    private Long id;

    private Integer maxNewWordsPerDay;

    private Integer maxReviewWordsPerDay;

    @OneToOne
    private WordPack wordPack;

    @ManyToMany
    @ToString.Exclude
    private List<Word> listOfWords;

    public Review(Integer maxNewWordsPerDay, Integer maxReviewWordsPerDay, WordPack wordPack, List<Word> listOfWords) {
        this.maxNewWordsPerDay = maxNewWordsPerDay;
        this.maxReviewWordsPerDay = maxReviewWordsPerDay;
        this.wordPack = wordPack;
        this.listOfWords = listOfWords;
    }
}
