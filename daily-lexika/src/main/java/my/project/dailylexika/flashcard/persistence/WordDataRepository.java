package my.project.dailylexika.flashcard.persistence;

import my.project.library.dailylexika.enumerations.Platform;
import my.project.dailylexika.flashcard.model.entities.WordData;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface WordDataRepository extends JpaRepository<WordData, Integer>, WordDataRepositoryCustom {

    @Override
    @CacheEvict(value = "wordDataCache", allEntries = true)
    <S extends WordData> S save(S entity);

    @Override
    @CacheEvict(value = "wordDataCache", allEntries = true)
    <S extends WordData> List<S> saveAll(Iterable<S> entities);

    @Override
    @CacheEvict(value = "wordDataCache", allEntries = true)
    void deleteAll(Iterable<? extends WordData> entities);

    @EntityGraph(attributePaths = "listOfWordPacks")
    Page<WordData> findAllByPlatform(Platform platform, Pageable pageable);

    @EntityGraph(attributePaths = "listOfWordPacks")
    Optional<WordData> findWithWordPacksById(Integer id);

    List<WordData> findAllByListOfWordPacks_IdAndPlatform(Long wordPackId, Platform platform);

    Long countByListOfWordPacks_IdAndPlatform(Long wordPackId, Platform platform);

    @Query("""
                 SELECT wd.id FROM word_data wd
                 WHERE wd.wordOfTheDayDate = :wordOfTheDayDate
                 AND wd.platform = :platform
            """)
    Optional<Integer> findIdByWordOfTheDayDateAndPlatform(LocalDate wordOfTheDayDate,
                                                          Platform platform);

    @Query("""
                SELECT MAX(wd.wordOfTheDayDate) FROM word_data wd
                WHERE wd.platform = :platform
            """)
    LocalDate findMaxWordOfTheDayDateByPlatform(Platform platform);

    @Query("""
                SELECT wd.id FROM word_data wd
                WHERE wd.platform = :platform
            """)
    List<Integer> findAllWordDataIdsByPlatform(Platform platform);

    @Query("""
                SELECT wd.id FROM word_data wd
                WHERE wd.platform = :platform
                ORDER BY wd.id
            """)
    List<Integer> findAllWordDataIdsByPlatformOrderByIdAsc(Platform platform);

    @Query("""
                SELECT wd.id FROM word_data wd
                JOIN wd.listOfWordPacks wp
                WHERE wp.id = :wordPackId
                AND wd.platform = :platform
            """)
    List<Integer> findAllWordDataIdsByWordPackIdAndPlatform(Long wordPackId, Platform platform);

    @Modifying
    @Query(
            value = """
                    DELETE FROM word_data_word_packs
                    WHERE word_data_id = :wordDataId
                    """,
            nativeQuery = true
    )
    void deleteAllWordPackLinksByWordDataId(Integer wordDataId);
}
