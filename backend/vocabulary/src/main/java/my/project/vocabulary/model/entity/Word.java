package my.project.vocabulary.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

import java.util.List;

@Data
@Entity(name = "words")
public class Word {

    @Id
    private Long id;

    private String nameChineseSimplified;

    private String nameChineseTraditional;

    private String pinyin;

    private String nameEnglish;

    private String nameRussian;

    @Enumerated(EnumType.STRING)
    private Status status;

    private Integer currentStreak;

    private Integer totalStreak;

    private Integer occurrence;

    @ManyToMany
    @ToString.Exclude
    private List<Review> listOfReviews;

    @ManyToMany
    @ToString.Exclude
    private List<ChineseCharacter> listOfChineseCharacters;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable
    @ToString.Exclude
    private List<WordPack> listOfWordPacks;

    public Word() {
        this.status = Status.NEW;
        this.currentStreak = 0;
        this.totalStreak = 0;
        this.occurrence = 0;
    }

    public Word(String nameEnglish, String nameRussian) {
        this.nameEnglish = nameEnglish;
        this.nameRussian = nameRussian;
        this.status = Status.NEW;
        this.currentStreak = 0;
        this.totalStreak = 0;
        this.occurrence = 0;
    }
}
