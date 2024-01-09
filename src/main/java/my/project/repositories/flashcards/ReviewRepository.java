package my.project.repositories.flashcards;

import my.project.models.entity.flashcards.Review;
import my.project.models.entity.enumeration.Platform;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    @Query("SELECT r FROM reviews r WHERE r.userId = :userId AND r.wordPack.name = :wordPackName")
    Review findByUserIdAndWordPackName(Long userId, String wordPackName);

    @Query("SELECT r FROM reviews r WHERE r.userId = :userId AND r.wordPack.platform = :platform")
    List<Review> findAllByUserIdAndPlatform(Long userId, Platform platform);

    @Query("SELECT DISTINCT r.wordPack.name FROM reviews r WHERE r.userId = :userId")
    List<String> findAllReviewNamesByUserId(@Param("userId") Long userId);

    void deleteAllByUserId(Long userId);
}
