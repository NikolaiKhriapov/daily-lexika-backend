package my.project.vocabulary.chinesecharacter;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChineseCharacterRepository extends JpaRepository<ChineseCharacter, Long> {
}
