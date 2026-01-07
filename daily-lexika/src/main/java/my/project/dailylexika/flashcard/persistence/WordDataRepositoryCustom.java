package my.project.dailylexika.flashcard.persistence;

import my.project.dailylexika.flashcard.model.entities.WordData;
import my.project.library.dailylexika.enumerations.Language;
import my.project.library.dailylexika.enumerations.Platform;

import java.util.List;

public interface WordDataRepositoryCustom {

    List<WordData> searchByPlatformAndQuery(Platform platform, Language translationLanguage, String query, String transcriptionQuery, int limit);
}
