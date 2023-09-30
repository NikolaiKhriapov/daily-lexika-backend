package my.project.chineseflashcards.repository;

import my.project.chineseflashcards.model.entity.WordPack;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WordPackRepository extends JpaRepository<WordPack, String> {
}
