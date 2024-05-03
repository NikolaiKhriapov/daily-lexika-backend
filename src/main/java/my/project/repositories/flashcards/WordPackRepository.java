package my.project.repositories.flashcards;

import my.project.models.entities.enumeration.Category;
import my.project.models.entities.flashcards.WordPack;
import my.project.models.entities.enumeration.Platform;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WordPackRepository extends JpaRepository<WordPack, String> {

    List<WordPack> findAllByPlatformAndCategoryNot(Platform platform, Category category);

    @Query("""
                SELECT wp FROM word_packs wp
                WHERE (wp.platform = :platform)
                AND wp.category = 'CUSTOM'
                AND wp.name LIKE CONCAT('%__', :userId)
            """)
    List<WordPack> findAllByUserIdAndPlatformAndCategoryCustom(@Param("userId") Integer userId, @Param("platform") Platform platform);
}
