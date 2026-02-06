package my.project.dailylexika.flashcard.model.mappers;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import my.project.dailylexika.config.I18nUtil;
import my.project.dailylexika.flashcard.model.entities.WordPack;
import my.project.library.util.exception.InternalServerErrorException;
import org.mapstruct.Named;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@AllArgsConstructor
public class WordDataMapperHelper {

    @Named("mapExamples")
    public List<Map<String, String>> mapExamples(String examples) {
        try {
            return new ObjectMapper().readValue(examples, List.class);
        } catch (Exception e) {
            throw new InternalServerErrorException(I18nUtil.getMessage("dailylexika-exceptions.excel.parse", examples));
        }
    }

    @Named("mapListOfWordPackIds")
    public List<Long> mapListOfWordPacks(List<WordPack> wordPacks) {
        return wordPacks.stream()
                .map(WordPack::getId)
                .toList();
    }
}
