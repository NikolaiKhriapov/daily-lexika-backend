package my.project.dailylexika.flashcard.model.mappers;

import my.project.library.dailylexika.dtos.flashcards.ReviewDto;
import my.project.dailylexika.flashcard.model.entities.Review;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, uses = {WordMapper.class, WordPackMapper.class})
public interface ReviewMapper {

    @Mapping(target = "wordPackId", source = "wordPack.id")
    @Mapping(target = "wordPackDto", source = "wordPack")
    @Mapping(target = "listOfWordDto", source = "listOfWords")
    ReviewDto toDto(Review entity);
}
