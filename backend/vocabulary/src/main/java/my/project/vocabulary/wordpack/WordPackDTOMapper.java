package my.project.vocabulary.wordpack;

import lombok.RequiredArgsConstructor;
import my.project.vocabulary.word.Word;
import org.springframework.stereotype.Service;

import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WordPackDTOMapper implements Function<WordPack, WordPackDTO> {

    @Override
    public WordPackDTO apply(WordPack wordPack) {
        return new WordPackDTO(
                wordPack.getName(),
                wordPack.getDescription(),
                wordPack.getCategory(),
                wordPack.getListOfWords().stream()
                        .map(Word::getId)
                        .collect(Collectors.toList()),
                wordPack.getReview()
        );
    }
}
