package my.project.dailylexika.flashcard.persistence;

import my.project.library.dailylexika.enumerations.Platform;
import my.project.library.dailylexika.enumerations.Status;
import my.project.dailylexika.flashcard.model.entities.Word;
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
                WHEN my.project.library.dailylexika.enumerations.Status.KNOWN THEN 1
                WHEN my.project.library.dailylexika.enumerations.Status.IN_REVIEW THEN 2
                WHEN my.project.library.dailylexika.enumerations.Status.NEW THEN 3
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
                 AND w.status = my.project.library.dailylexika.enumerations.Status.NEW
                 ORDER BY RANDOM()
                 LIMIT :limit
            """)
    List<Word> findAllByUserIdAndWordDataIdInAndStatusNewRandomLimited(@Param("userId") Integer userId,
                                                                       @Param("wordDataIds") List<Integer> wordDataIds,
                                                                       @Param("limit") Integer limit);

    @Query("""
                SELECT w FROM words w
                WHERE w.userId = :userId
                AND w.wordData.id IN :wordDataIds
                AND w.status = my.project.library.dailylexika.enumerations.Status.IN_REVIEW
            """)
    List<Word> findAllByUserIdAndWordDataIdInAndStatusInReviewAndPeriodBetweenOrderedDescLimited(@Param("userId") Integer userId,
                                                                                                 @Param("wordDataIds") List<Integer> wordDataIds);

    @Query("""
                SELECT w FROM words w
                WHERE w.userId = :userId
                AND w.wordData.id IN :wordDataIds
                AND w.status = my.project.library.dailylexika.enumerations.Status.KNOWN
            """)
    List<Word> findAllByUserIdAndWordDataIdInAndStatusKnownAndPeriodBetweenOrderedAscLimited(@Param("userId") Integer userId,
                                                                                             @Param("wordDataIds") List<Integer> wordDataIds);
}
