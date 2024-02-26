package my.project.models.mappers.flashcards;

import my.project.models.dtos.flashcards.ReviewDto;
import my.project.models.entities.flashcards.Review;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        uses = {WordMapper.class, WordPackMapper.class}
)
public interface ReviewMapper {

    @Mapping(target = "wordPackDto", source = "wordPack")
    @Mapping(target = "listOfWordDto", source = "listOfWords")
    ReviewDto toDto(Review entity);
}
