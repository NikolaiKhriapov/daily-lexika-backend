package my.project.repositories.chineseflashcards;

import my.project.models.entity.chineseflashcards.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    List<Review> findAllByUserId(Long userId);

    void deleteAllByUserId(Long userId);
}
