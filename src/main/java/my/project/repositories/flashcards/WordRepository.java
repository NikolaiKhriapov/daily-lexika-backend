package my.project.repositories.flashcards;

import my.project.models.entities.enumeration.Platform;
import my.project.models.entities.enumeration.Status;
import my.project.models.entities.flashcards.Word;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WordRepository extends JpaRepository<Word, Integer> {

    List<Word> findAllByUserId(Integer userId);

    Optional<Word> findByUserIdAndWordData_Id(Integer userId, Integer wordDataId);

    List<Word> findByUserIdAndWordDataIdIn(Integer userId, List<Integer> wordDataIds);

    List<Word> findByUserIdAndWordDataIdInAndStatusIn(Integer userId, List<Integer> wordDataIds, List<Status> status, Pageable pageable);

    List<Word> findByUserIdAndStatusAndWordData_Platform(Integer userId, Status status, Platform platform);

    Integer countByUserIdAndWordData_IdInAndStatus(Integer userId, List<Integer> wordDataIds, Status status);

    List<Word> findByUserIdAndWordData_Platform(Integer userId, Platform platform);

    List<Word> findByUserIdAndWordData_PlatformAndStatus(Integer userId, Platform platform, Status status, Pageable pageable);

    void deleteAllByWordData_Id(Integer id);

    @Query("""
                SELECT w FROM words w WHERE w.userId = :userId AND w.wordData.id IN :wordDataIds
                ORDER BY CASE w.status
                WHEN my.project.models.entities.enumeration.Status.KNOWN THEN 1
                WHEN my.project.models.entities.enumeration.Status.IN_REVIEW THEN 2
                WHEN my.project.models.entities.enumeration.Status.NEW THEN 3
                ELSE 4 END ASC
            """
    )
    Page<Word> findByUserIdAndWordDataIdIn(Integer userId, List<Integer> wordDataIds, Pageable pageable);

    @Query("""
                SELECT w FROM words w
                WHERE w.userId = :userId
                AND w.wordData.id IN :wordDataIds
                AND w.status IN :statuses
                AND (DATE_PART('day', AGE(CURRENT_DATE, w.dateOfLastOccurrence)) >= POWER(2, w.totalStreak))
                ORDER BY w.dateOfLastOccurrence DESC
            """)
    List<Word> findByUserIdAndWordDataIdInAndStatusInAndPeriodBetweenOrdered(@Param("userId") Integer userId,
                                                                             @Param("wordDataIds") List<Integer> wordDataIds,
                                                                             @Param("statuses") List<Status> statuses,
                                                                             Pageable pageable);
}
