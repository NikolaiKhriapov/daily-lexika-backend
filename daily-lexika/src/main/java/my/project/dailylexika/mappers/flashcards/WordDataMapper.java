package my.project.dailylexika.mappers.flashcards;

import my.project.library.dailylexika.dtos.flashcards.WordDataDto;
import my.project.dailylexika.entities.flashcards.WordData;
import my.project.dailylexika.entities.flashcards.WordPack;
import org.mapstruct.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface WordDataMapper {

    @Mapping(target = "examples", source = "examples", qualifiedByName = "mapExamples")
    @Mapping(target = "listOfWordPackNames", source = "listOfWordPacks", qualifiedByName = "mapListOfWordPackNames")
    WordDataDto toDto(WordData entity);

    List<WordDataDto> toDtoList(List<WordData> entityList);

    @Named("mapExamples")
    default List<String> mapExamples(String examples) {
        return Arrays.stream(examples.split(System.lineSeparator()))
                .map(String::strip)
                .collect(Collectors.toList());
    }

    @Named("mapListOfWordPackNames")
    default List<String> mapListOfWordPacks(List<WordPack> wordPacks) {
        return wordPacks.stream()
                .map(WordPack::getName)
                .toList();
    }
}
