package my.project.repositories.chineseflashcards;

import my.project.models.entity.chineseflashcards.Status;
import my.project.models.entity.chineseflashcards.Word;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WordRepository extends JpaRepository<Word, Long> {

    List<Word> findByUserIdAndWordDataIdIn(Long userId, List<Long> wordDataIds);

    List<Word> findByUserIdAndWordDataIdInAndStatusIn(Long userId, List<Long> wordDataIds, List<Status> status, Pageable pageable);

    @Query("SELECT COUNT(w) FROM words w WHERE w.userId = :userId AND w.status = :status")
    Integer countByUserIdAndStatusEquals(@Param("userId") Long userId, @Param("status") Status status);

    @Query("SELECT COUNT(w) FROM words w " +
            "WHERE w.userId = :userId " +
            "AND w.wordDataId IN :wordDataIds " +
            "AND w.status = :status")
    Integer countByUserIdAndWordDataIdInAndStatusEquals(
            @Param("userId") Long userId,
            @Param("wordDataIds") List<Long> wordDataIds,
            @Param("status") Status status
    );

    @Query("SELECT w FROM words w " +
            "WHERE w.userId = :userId " +
            "AND w.wordDataId IN :wordDataIds " +
            "AND w.status IN :statuses " +
            "AND (DATE_PART('day', AGE(CURRENT_DATE, w.dateOfLastOccurrence)) >= POWER(2, w.totalStreak)) " +
            "ORDER BY w.dateOfLastOccurrence DESC")
    List<Word> findByUserIdAndWordDataIdInAndStatusInAndPeriodBetweenOrdered(
            @Param("userId") Long userId,
            @Param("wordDataIds") List<Long> wordDataIds,
            @Param("statuses") List<Status> statuses,
            Pageable pageable
    );

    void deleteAllByUserId(Long userId);
}