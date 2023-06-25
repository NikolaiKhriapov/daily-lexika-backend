package my.project.vocabulary.wordpack;

import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;
import my.project.vocabulary.review.Review;
import my.project.vocabulary.word.Word;

import java.util.List;

@Entity
@Data
public class WordPack {

    @Id
    private String name;

    private String description;

    @Enumerated(EnumType.STRING)
    private Category category;

    @ManyToMany(mappedBy = "listOfWordPacks", cascade = CascadeType.ALL)
    @ToString.Exclude
    private List<Word> listOfWords;

    @OneToOne
    private Review review;
}
