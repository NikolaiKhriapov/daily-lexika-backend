package my.project.chineseflashcards.repository;

import my.project.chineseflashcards.model.entity.WordData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WordDataRepository extends JpaRepository<WordData, Long> {
}