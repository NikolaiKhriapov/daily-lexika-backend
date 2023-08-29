package my.project.vocabulary.mapper;

import lombok.RequiredArgsConstructor;
import my.project.vocabulary.model.dto.WordPackDTO;
import my.project.vocabulary.model.entity.Word;
import my.project.vocabulary.model.entity.WordPack;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WordPackMapper implements Mapper<WordPack, WordPackDTO> {

    @Override
    public WordPackDTO toDTO(WordPack entity) {
        return new WordPackDTO(
                entity.getName(),
                entity.getDescription(),
                entity.getCategory(),
                entity.getListOfWords().stream()
                        .map(Word::getId)
                        .collect(Collectors.toList()),
                entity.getReview()
        );
    }
}
