package my.project.dailylexika.flashcard.model.mappers;

import my.project.library.dailylexika.dtos.flashcards.WordDataDto;
import my.project.dailylexika.flashcard.model.entities.WordData;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, uses = WordDataMapperHelper.class)
public interface WordDataMapper {

    @Mapping(target = "examples", source = "examples", qualifiedByName = "mapExamples")
    @Mapping(target = "listOfWordPackNames", source = "listOfWordPacks", qualifiedByName = "mapListOfWordPackNames")
    WordDataDto toDto(WordData entity);

    List<WordDataDto> toDtoList(List<WordData> entityList);
}
