package my.project.repositories.chineseflashcards;

import my.project.models.entity.chineseflashcards.WordPack;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WordPackRepository extends JpaRepository<WordPack, String> {
}
