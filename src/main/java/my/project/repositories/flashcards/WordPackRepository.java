package my.project.repositories.flashcards;

import my.project.models.entities.flashcards.WordPack;
import my.project.models.entities.enumeration.Platform;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WordPackRepository extends JpaRepository<WordPack, String> {

    @Query("SELECT wp FROM word_packs wp WHERE wp.platform = :platform AND wp.category != 'CUSTOM'")
    List<WordPack> findAllByPlatformAndCategoryNotCustom(@Param("platform") Platform platform);

    @Query("SELECT wp FROM word_packs wp " +
            "WHERE wp.platform = :platform " +
            "AND wp.category = 'CUSTOM' " +
            "AND wp.name LIKE CONCAT('%', :userId)")
    List<WordPack> findAllByPlatformAndUserIdAndCategoryCustom(@Param("platform") Platform platform, @Param("userId") Long userId);
}
