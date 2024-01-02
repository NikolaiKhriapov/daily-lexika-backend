package my.project.models.mapper.flashcards;

import lombok.RequiredArgsConstructor;
import my.project.models.dto.flashcards.WordPackDTO;
import my.project.models.entity.flashcards.WordPack;
import my.project.services.flashcards.WordDataService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WordPackMapper implements Mapper<WordPack, WordPackDTO> {

    private final WordDataService wordDataService;

    @Override
    public WordPackDTO toDTO(WordPack entity) {
        return new WordPackDTO(
                entity.getName(),
                entity.getDescription(),
                entity.getCategory(),
                (long) wordDataService.getListOfAllWordDataIdsByWordPack(entity).size()
        );
    }
}
