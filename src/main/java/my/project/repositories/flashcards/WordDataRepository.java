package my.project.repositories.flashcards;

import my.project.models.entities.enumeration.Platform;
import my.project.models.entities.flashcards.WordData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WordDataRepository extends JpaRepository<WordData, Long> {

    List<WordData> findAllByPlatform(Platform platform);

    List<WordData> findAllByListOfWordPacks_NameAndPlatform(String wordPackName, Platform platform);

    Long countByListOfWordPacks_NameAndPlatform(String wordPackName, Platform platform);

    @Query("""
                SELECT wd.id FROM word_data wd
                JOIN wd.listOfWordPacks wp
                WHERE wp.name = :wordPackName
                AND wd.platform = :platform
            """)
    List<Long> findAllWordDataIdsByWordPackNameAndPlatform(@Param("wordPackName") String wordPackName,
                                                           @Param("platform") Platform platform);
}
