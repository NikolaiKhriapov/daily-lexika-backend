package my.project.repositories.flashcards;

import my.project.models.entities.enumerations.Platform;
import my.project.models.entities.enumerations.Status;
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

    List<Word> findByUserIdAndStatusAndWordData_Platform(Integer userId, Status status, Platform platform);

    Integer countByUserIdAndWordData_IdInAndStatus(Integer userId, List<Integer> wordDataIds, Status status);

    List<Word> findByUserIdAndWordData_Platform(Integer userId, Platform platform);

    Page<Word> findByUserIdAndWordData_PlatformAndStatus(Integer userId, Platform platform, Status status, Pageable pageable);

    void deleteAllByWordData_Id(Integer id);

    @Query("""
                SELECT w FROM words w WHERE w.userId = :userId AND w.wordData.id IN :wordDataIds
                ORDER BY CASE w.status
                WHEN my.project.models.entities.enumerations.Status.KNOWN THEN 1
                WHEN my.project.models.entities.enumerations.Status.IN_REVIEW THEN 2
                WHEN my.project.models.entities.enumerations.Status.NEW THEN 3
                ELSE 4 END ASC
            """
    )
    Page<Word> findByUserIdAndWordDataIdIn(@Param("userId") Integer userId,
                                           @Param("wordDataIds") List<Integer> wordDataIds,
                                           Pageable pageable);

    @Query("""
                 SELECT w FROM words w
                 WHERE w.userId = :userId
                 AND w.wordData.id IN :wordDataIds
                 AND w.status IN :statuses
                 ORDER BY RANDOM()
                 LIMIT :limit
            """)
    List<Word> findAllByUserIdAndWordDataIdInAndStatusInRandomLimited(@Param("userId") Integer userId,
                                                                      @Param("wordDataIds") List<Integer> wordDataIds,
                                                                      @Param("statuses") List<Status> statuses,
                                                                      @Param("limit") Integer limit);

    @Query("""
                SELECT w FROM words w
                WHERE w.userId = :userId
                AND w.wordData.id IN :wordDataIds
                AND w.status IN :statuses
                AND (DATE(now()) - DATE(w.dateOfLastOccurrence)) >= POWER(2, w.totalStreak)
                ORDER BY w.dateOfLastOccurrence DESC
                LIMIT :limit
            """)
    List<Word> findAllByUserIdAndWordDataIdInAndStatusInAndPeriodBetweenOrderedLimited(@Param("userId") Integer userId,
                                                                                       @Param("wordDataIds") List<Integer> wordDataIds,
                                                                                       @Param("statuses") List<Status> statuses,
                                                                                       @Param("limit") Integer limit);
}
