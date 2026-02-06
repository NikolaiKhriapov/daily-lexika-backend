package my.project.dailylexika.flashcard.persistence;

import my.project.dailylexika.flashcard.model.entities.Review;
import my.project.library.dailylexika.enumerations.Platform;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    Optional<Review> findByUserIdAndWordPack_Id(Integer userId, Long wordPackId);

    List<Review> findByUserIdAndWordPack_Platform(Integer userId, Platform platform);

    List<Review> findByWordPack_Id(Long wordPackId);

    boolean existsByUserIdAndWordPack_PlatformAndWordPack_Id(Integer userId, Platform platform, Long wordPackId);

    @Modifying
    @Query(
            value = """
                    DELETE FROM reviews_words
                    WHERE word_id IN (
                        SELECT id
                        FROM words
                        WHERE word_data_id = :wordDataId
                    )
                    """,
            nativeQuery = true
    )
    void deleteReviewWordLinksByWordDataId(Integer wordDataId);
}
