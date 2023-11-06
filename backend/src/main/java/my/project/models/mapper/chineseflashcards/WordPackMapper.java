package my.project.models.mapper.chineseflashcards;

import lombok.RequiredArgsConstructor;
import my.project.models.dto.chineseflashcards.WordPackDTO;
import my.project.models.entity.chineseflashcards.WordPack;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WordPackMapper implements Mapper<WordPack, WordPackDTO> {

    @Override
    public WordPackDTO toDTO(WordPack entity) {
        return new WordPackDTO(
                entity.getName(),
                entity.getDescription(),
                entity.getCategory(),
                (long) entity.getListOfWordData().size(),
                entity.getReview()
        );
    }

    public WordPackDTO toDTOWithoutReview(WordPack entity) {
        return new WordPackDTO(
                entity.getName(),
                entity.getDescription(),
                entity.getCategory(),
                (long) entity.getListOfWordData().size(),
                null
        );
    }
}
