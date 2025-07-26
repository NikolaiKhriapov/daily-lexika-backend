package my.project.dailylexika.flashcard.persistence;

import my.project.library.dailylexika.enumerations.Platform;
import my.project.dailylexika.flashcard.model.entities.WordData;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface WordDataRepository extends JpaRepository<WordData, Integer> {

    @Cacheable(value = "wordDataCache", key = "#platform.name()")
    List<WordData> findAllByPlatform(Platform platform);

    List<WordData> findAllByListOfWordPacks_NameAndPlatform(String wordPackName, Platform platform);

    Long countByListOfWordPacks_NameAndPlatform(String wordPackName, Platform platform);

    @Query("""
                 SELECT wd.id FROM word_data wd
                 WHERE wd.wordOfTheDayDate = :wordOfTheDayDate
                 AND wd.platform = :platform
            """)
    Optional<Integer> findIdByWordOfTheDayDateAndPlatform(@Param("wordOfTheDayDate") LocalDate wordOfTheDayDate,
                                                       @Param("platform") Platform platform);

    @Query("""
                SELECT wd.id FROM word_data wd
                WHERE wd.platform = :platform
            """)
    List<Integer> findAllWordDataIdsByPlatform(@Param("platform") Platform platform);

    @Query("""
                SELECT wd.id FROM word_data wd
                JOIN wd.listOfWordPacks wp
                WHERE wp.name = :wordPackName
                AND wd.platform = :platform
            """)
    List<Integer> findAllWordDataIdsByWordPackNameAndPlatform(@Param("wordPackName") String wordPackName,
                                                           @Param("platform") Platform platform);
}
