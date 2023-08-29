package my.project.vocabulary.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;
import my.project.vocabulary.model.entity.Category;
import my.project.vocabulary.model.entity.Review;
import my.project.vocabulary.model.entity.Word;

import java.util.List;

@Data
@Entity(name = "word_packs")
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
