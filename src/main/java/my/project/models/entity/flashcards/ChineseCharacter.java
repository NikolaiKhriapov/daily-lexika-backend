package my.project.models.entity.flashcards;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import my.project.models.entity.enumeration.Status;

@Data
@NoArgsConstructor
@Entity(name = "chinese_characters")
public class ChineseCharacter {

    @Id
    @SequenceGenerator(name = "chinese_character_id_sequence", sequenceName = "chinese_character_id_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "chinese_character_id_sequence")
    private Long id;

    private String nameChineseSimplified;

    private String nameChineseTraditional;

    private String pinyin;

    private String nameEnglish;

    private String nameRussian;

    @Enumerated(EnumType.STRING)
    private Status status;

    private Integer correctStreak;
}
