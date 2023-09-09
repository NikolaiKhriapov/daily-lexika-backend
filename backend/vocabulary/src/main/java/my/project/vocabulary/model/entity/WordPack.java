package my.project.vocabulary.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "word_packs")
public class WordPack {

    @Id
    private String name;

    private String description;

    @Enumerated(EnumType.STRING)
    private Category category;

    @ManyToMany(mappedBy = "listOfWordPacks", cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @ToString.Exclude
    private List<Word> listOfWords;

    @OneToOne
    private Review review;
}
