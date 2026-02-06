package my.project.dailylexika.flashcard.persistence;

import my.project.dailylexika.flashcard.model.entities.WordData;
import my.project.library.dailylexika.enumerations.Language;
import my.project.library.dailylexika.enumerations.Platform;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface WordDataRepositoryCustom {

    Page<WordData> searchByPlatformAndQuery(Platform platform,
                                            Language translationLanguage,
                                            boolean adminSearch,
                                            String query,
                                            String transcriptionQuery,
                                            Pageable pageable);
}
