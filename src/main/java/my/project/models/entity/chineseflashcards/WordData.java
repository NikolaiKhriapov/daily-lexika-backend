package my.project.models.entity.chineseflashcards;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    private List<ChineseCharacter> listOfChineseCharacters;

    @ManyToMany(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH}, fetch = FetchType.LAZY)
    @JoinTable
    private List<WordPack> listOfWordPacks;
}