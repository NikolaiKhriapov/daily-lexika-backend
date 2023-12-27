package my.project.repositories.chineseflashcards;

import my.project.models.entity.chineseflashcards.WordData;
import my.project.models.entity.chineseflashcards.WordPack;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface WordDataRepository extends JpaRepository<WordData, Long> {

    @Query("SELECT wd.id FROM word_data wd JOIN wd.listOfWordPacks wp WHERE wp = :wordPack")
    List<Long> findAllWordDataIdsByWordPack(@Param("wordPack") WordPack wordPack);
}