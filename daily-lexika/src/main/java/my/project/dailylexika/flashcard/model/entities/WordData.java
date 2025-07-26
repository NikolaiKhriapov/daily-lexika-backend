package my.project.dailylexika.flashcard.model.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import my.project.library.dailylexika.enumerations.Platform;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "word_data")
public class WordData {

    @Id
    private Integer id;

    private String nameChinese;

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
