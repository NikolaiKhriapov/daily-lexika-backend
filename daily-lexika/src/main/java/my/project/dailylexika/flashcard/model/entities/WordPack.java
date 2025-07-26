package my.project.dailylexika.flashcard.model.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import my.project.library.dailylexika.enumerations.Category;
import my.project.library.dailylexika.enumerations.Platform;

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

    @Enumerated(EnumType.STRING)
    private Platform platform;
}
