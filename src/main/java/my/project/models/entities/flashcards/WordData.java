package my.project.models.entities.flashcards;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import my.project.models.entities.enumeration.Platform;

import java.time.LocalDate;
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

    private LocalDate wordOfTheDayDate;

    @Enumerated(EnumType.STRING)
    private Platform platform;
}
