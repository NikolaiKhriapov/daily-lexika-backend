package my.project.repositories.flashcards;

import my.project.models.entity.enumeration.Platform;
import my.project.models.entity.flashcards.WordData;
import my.project.models.entity.flashcards.WordPack;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface WordDataRepository extends JpaRepository<WordData, Long> {

    @Query("SELECT wd.id FROM word_data wd JOIN wd.listOfWordPacks wp WHERE wp.name = :wordPackName")
    List<Long> findAllWordDataIdsByWordPackName(@Param("wordPackName") String wordPackName);

    @Query("SELECT wd FROM word_data wd JOIN wd.listOfWordPacks wp WHERE wp = :wordPack")
    List<WordData> findAllByWordPack(WordPack wordPack);

    @Query("SELECT wd FROM word_data wd " +
            "WHERE (wd.platform = :platform " +
            "AND (" +
                "(wd.platform = 'ENGLISH' AND (LOWER(wd.nameEnglish) LIKE :searchQuery%)) " +
                "OR " +
                "(wd.platform = 'CHINESE' AND (" +
                    "(LOWER(wd.nameChineseSimplified) LIKE %:searchQuery%) " +
                    "OR " +
                    "(LOWER(CAST(unaccent(wd.transcription) AS string)) LIKE :searchQuery%)" +
                "))" +
            ")) ORDER BY CASE WHEN wd.platform = 'ENGLISH' THEN wd.nameChineseSimplified END ASC, " +
                        "CASE WHEN wd.platform = 'CHINESE' THEN wd.nameEnglish END ASC"
    )
    List<WordData> findPageByPlatformAndTranscriptionContainingIgnoreCase(@Param("searchQuery") String searchQuery,
                                                                          @Param("platform") Platform platform);
}
