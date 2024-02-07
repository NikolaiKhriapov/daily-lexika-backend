package my.project.models.entity.flashcards;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import my.project.models.entity.enumeration.Platform;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "word_data")
public class WordData {

    @Id
    private Long id;

    private String nameChineseSimplified;

    private String transcription;

    private String nameEnglish;

    private String nameRussian;

    private String definition;

    private String examples;

    @ManyToMany(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH}, fetch = FetchType.LAZY)
    @JoinTable
    private List<WordPack> listOfWordPacks;

    @Enumerated(EnumType.STRING)
    private Platform platform;
}
