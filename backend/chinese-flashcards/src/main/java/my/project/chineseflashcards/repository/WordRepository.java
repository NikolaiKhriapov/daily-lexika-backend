package my.project.chineseflashcards.repository;

import my.project.chineseflashcards.model.entity.Status;
import my.project.chineseflashcards.model.entity.Word;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WordRepository extends JpaRepository<Word, Long> {

    List<Word> findByUserIdAndWordIdIn(Long userId, List<Long> wordDataIds);

    List<Word> findAllByUserIdAndStatusIs(Long userId, Status status);

    void deleteAllByUserId(Long userId);
}