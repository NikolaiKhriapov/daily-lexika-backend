package my.project.repositories.chineseflashcards;

import my.project.models.entity.chineseflashcards.WordData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WordDataRepository extends JpaRepository<WordData, Long> {
}