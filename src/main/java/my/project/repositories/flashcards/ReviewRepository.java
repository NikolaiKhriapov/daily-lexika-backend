package my.project.repositories.flashcards;

import my.project.models.entity.flashcards.Review;
import my.project.models.entity.flashcards.Word;
import my.project.models.entity.enumeration.Platform;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    @Query("SELECT r FROM reviews r WHERE r.userId = :userId AND r.wordPack.platform = :platform")
    List<Review> findAllByUserIdAndPlatform(Long userId, Platform platform);

    @Query("SELECT DISTINCT r.wordPack.name FROM reviews r WHERE r.userId = :userId")
    List<String> findAllReviewNamesByUserId(@Param("userId") Long userId);

    @Query("SELECT r.id FROM reviews r JOIN r.listOfWords w WHERE w = :word")
    List<Long> findAllReviewIdsByWord(@Param("word") Word word);

    void deleteAllByUserId(Long userId);
}
