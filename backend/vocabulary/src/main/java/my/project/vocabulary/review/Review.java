package my.project.vocabulary.review;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import my.project.vocabulary.word.Word;
import my.project.vocabulary.wordpack.WordPack;

import java.util.List;

@Entity
@Data
@NoArgsConstructor
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
