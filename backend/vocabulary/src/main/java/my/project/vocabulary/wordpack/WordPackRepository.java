package my.project.vocabulary.wordpack;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WordPackRepository extends JpaRepository<WordPack, String> {
}
