package my.project.vocabulary.repository;

import my.project.vocabulary.model.entity.WordPack;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WordPackRepository extends JpaRepository<WordPack, String> {
}
