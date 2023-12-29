package my.project.models.entity.chineseflashcards;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
}
