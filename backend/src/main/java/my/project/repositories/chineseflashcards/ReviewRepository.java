package my.project.repositories.chineseflashcards;

import my.project.models.entity.chineseflashcards.Review;
import my.project.models.entity.chineseflashcards.Word;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    List<Review> findAllByUserId(Long userId);

    @Query("SELECT DISTINCT r.wordPack.name FROM reviews r WHERE r.userId = :userId")
    List<String> findAllReviewNamesByUserId(@Param("userId") Long userId);

    @Query("SELECT r.id FROM reviews r JOIN r.listOfWords w WHERE w = :word")
    List<Long> findAllReviewIdsByWord(@Param("word") Word word);

    void deleteAllByUserId(Long userId);
}
