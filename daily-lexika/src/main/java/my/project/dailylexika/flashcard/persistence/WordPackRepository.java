package my.project.dailylexika.flashcard.persistence;

import my.project.library.dailylexika.enumerations.Category;
import my.project.dailylexika.flashcard.model.entities.WordPack;
import my.project.library.dailylexika.enumerations.Platform;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WordPackRepository extends JpaRepository<WordPack, Long> {

    List<WordPack> findAllByPlatformAndCategoryNot(Platform platform, Category category);

    Page<WordPack> findAllByPlatformAndCategoryNot(Platform platform, Category category, Pageable pageable);

    @Query("""
                SELECT wp FROM word_packs wp
                WHERE (wp.platform = :platform)
                AND wp.category = 'CUSTOM'
                AND wp.userId = :userId
            """)
    List<WordPack> findAllByUserIdAndPlatformAndCategoryCustom(Integer userId, Platform platform);

    boolean existsByPlatformAndName(Platform platform, String name);

    boolean existsByPlatformAndNameAndUserId(Platform platform, String name, Integer userId);
}
