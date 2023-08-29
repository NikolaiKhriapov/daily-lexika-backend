package my.project.vocabulary.repository;

import my.project.vocabulary.model.entity.ChineseCharacter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChineseCharacterRepository extends JpaRepository<ChineseCharacter, Long> {
}
