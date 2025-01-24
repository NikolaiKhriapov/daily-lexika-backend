package my.project.dailylexika.mappers.flashcards;

import com.fasterxml.jackson.databind.ObjectMapper;
import my.project.dailylexika.config.i18n.I18nUtil;
import my.project.library.dailylexika.dtos.flashcards.WordDataDto;
import my.project.dailylexika.entities.flashcards.WordData;
import my.project.dailylexika.entities.flashcards.WordPack;
import my.project.library.util.exception.InternalServerErrorException;
import org.mapstruct.*;

import java.util.List;
import java.util.Map;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface WordDataMapper {

    @Mapping(target = "examples", source = "examples", qualifiedByName = "mapExamples")
    @Mapping(target = "listOfWordPackNames", source = "listOfWordPacks", qualifiedByName = "mapListOfWordPackNames")
    WordDataDto toDto(WordData entity);

    List<WordDataDto> toDtoList(List<WordData> entityList);

    @Named("mapExamples")
    default List<Map<String, String>> mapExamples(String examples) {
        if (examples.equals("[TODO]")) return null;

        try {
            return new ObjectMapper().readValue(examples, List.class);
        } catch (Exception e) {
            throw new InternalServerErrorException(I18nUtil.getMessage("dailylexika-exceptions.excel.parse", examples));
        }
    }

    @Named("mapListOfWordPackNames")
    default List<String> mapListOfWordPacks(List<WordPack> wordPacks) {
        return wordPacks.stream()
                .map(WordPack::getName)
                .toList();
    }
}
