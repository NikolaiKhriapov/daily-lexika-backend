package my.project.models.mappers.flashcards;

import my.project.models.dtos.flashcards.WordDto;
import my.project.models.entities.flashcards.Word;
import org.mapstruct.*;

import java.util.List;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        uses = WordDataMapper.class
)
public interface WordMapper {

    @Mapping(target = "wordDataDto", source = "wordData")
    WordDto toDto(Word entity);

    List<WordDto> toDtoList(List<Word> entityList);
}