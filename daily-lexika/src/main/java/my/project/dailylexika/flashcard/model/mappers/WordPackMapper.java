package my.project.dailylexika.flashcard.model.mappers;

import my.project.library.dailylexika.dtos.flashcards.WordPackDto;
import my.project.dailylexika.flashcard.model.entities.WordPack;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, uses = WordPackMapperHelper.class)
public interface WordPackMapper {

    @Mapping(target = "wordsTotal", source = "entity", qualifiedByName = "mapWordsTotal")
    @Mapping(target = "wordsNew", source = "entity", qualifiedByName = "mapWordsNew")
    @Mapping(target = "reviewId", source = "entity", qualifiedByName = "mapReviewId")
    WordPackDto toDto(WordPack entity);

    List<WordPackDto> toDtoList(List<WordPack> entityList);
}
