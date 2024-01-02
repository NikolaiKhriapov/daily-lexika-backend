package my.project.repositories.flashcards;

import my.project.models.entity.flashcards.WordPack;
import my.project.models.entity.enumeration.Platform;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WordPackRepository extends JpaRepository<WordPack, String> {

    List<WordPack> findAllByPlatform(Platform platform);
}
