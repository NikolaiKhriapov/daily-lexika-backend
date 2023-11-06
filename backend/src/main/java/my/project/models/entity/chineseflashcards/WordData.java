package my.project.models.entity.chineseflashcards;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Data
@NoArgsConstructor
@Entity(name = "word_data")
public class WordData {

    @Id
    private Long id;

    private String nameChineseSimplified;

    private String nameChineseTraditional;

    private String pinyin;

    private String nameEnglish;

    private String nameRussian;

    @ManyToMany
    @ToString.Exclude
    private List<ChineseCharacter> listOfChineseCharacters;

    @ManyToMany(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinTable
    @ToString.Exclude
    private List<WordPack> listOfWordPacks;
}