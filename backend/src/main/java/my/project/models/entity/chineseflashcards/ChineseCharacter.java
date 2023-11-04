package my.project.models.entity.chineseflashcards;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Data
@NoArgsConstructor
@Entity(name = "chinese_characters")
public class ChineseCharacter {

    @Id
    @SequenceGenerator(name = "sequence", sequenceName = "sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequence")
    private Long id;

    private String nameChineseSimplified;

    private String nameChineseTraditional;

    private String pinyin;

    private String nameEnglish;

    private String nameRussian;

    @Enumerated(EnumType.STRING)
    private Status status;

    private Integer correctStreak;

    @ManyToMany(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @ToString.Exclude
    private List<WordData> listOfWordData;

//    public ChineseCharacter(String nameChineseSimplified,
//                            String nameChineseTraditional,
//                            String pinyin,
//                            String nameEnglish,
//                            String nameRussian) {
//        this.nameChineseSimplified = nameChineseSimplified;
//        this.nameChineseTraditional = nameChineseTraditional;
//        this.pinyin = pinyin;
//        this.nameEnglish = nameEnglish;
//        this.nameRussian = nameRussian;
//        this.status = Status.NEW;
//        this.correctStreak = 0;
//    }
}
