package my.project.repositories.flashcards;

import my.project.models.entities.flashcards.Review;
import my.project.models.entities.enumerations.Platform;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    Optional<Review> findByUserIdAndWordPack_Name(Integer userId, String wordPackName);

    List<Review> findByUserIdAndWordPack_Platform(Integer userId, Platform platform);

    boolean existsByUserIdAndWordPack_PlatformAndWordPack_Name(Integer userId, Platform platform, String wordPackName);
}
