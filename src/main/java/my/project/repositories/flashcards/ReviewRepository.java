package my.project.repositories.flashcards;

import my.project.models.entity.flashcards.Review;
import my.project.models.entity.enumeration.Platform;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    @Query("SELECT r FROM reviews r WHERE r.userId = :userId AND r.wordPack.name = :wordPackName")
    Optional<Review> findByUserIdAndWordPackName(Long userId, String wordPackName);

    @Query("SELECT r FROM reviews r WHERE r.userId = :userId AND r.wordPack.platform = :platform")
    List<Review> findAllByUserIdAndPlatform(Long userId, Platform platform);

    @Query("SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END " +
            "FROM reviews r " +
            "WHERE r.userId = :userId " +
            "AND r.wordPack.platform = :platform " +
            "AND r.wordPack.name = :wordPackName")
    boolean existsByUserIdAndPlatformAndWordPackName(Long userId, Platform platform, String wordPackName);
}
