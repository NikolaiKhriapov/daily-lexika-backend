package my.project.repositories.flashcards;

import my.project.models.entity.enumeration.Status;
import my.project.models.entity.flashcards.Word;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WordRepository extends JpaRepository<Word, Long> {

    List<Word> findByUserIdAndWordDataIdInAndStatusIn(Long userId, List<Long> wordDataIds, List<Status> status, Pageable pageable);

    @Query("SELECT w FROM words w WHERE w.userId = :userId AND w.wordDataId IN :wordDataIds " +
            "ORDER BY CASE w.status " +
            "WHEN my.project.models.entity.enumeration.Status.KNOWN THEN 1 " +
            "WHEN my.project.models.entity.enumeration.Status.IN_REVIEW THEN 2 " +
            "WHEN my.project.models.entity.enumeration.Status.NEW THEN 3 " +
            "ELSE 4 END ASC")
    Page<Word> findByUserIdAndWordDataIdIn(Long userId, List<Long> wordDataIds, Pageable pageable);

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