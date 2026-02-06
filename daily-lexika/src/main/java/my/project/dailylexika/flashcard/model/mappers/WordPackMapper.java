package my.project.dailylexika.flashcard.model.mappers;

import my.project.library.dailylexika.dtos.flashcards.WordPackUserDto;
import my.project.dailylexika.flashcard.model.entities.WordPack;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, uses = WordPackMapperHelper.class)
public interface WordPackMapper {

    @Mapping(target = "wordsTotal", source = "entity", qualifiedByName = "mapWordsTotal")
    @Mapping(target = "wordsNew", source = "entity", qualifiedByName = "mapWordsNew")
    @Mapping(target = "reviewId", source = "entity", qualifiedByName = "mapReviewId")
    WordPackUserDto toDto(WordPack entity);

    List<WordPackUserDto> toDtoList(List<WordPack> entityList);
}
