package my.project.dailylexika.flashcard.model.mappers;

import my.project.library.dailylexika.dtos.flashcards.WordDto;
import my.project.dailylexika.flashcard.model.entities.Word;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, uses = WordDataMapper.class)
public interface WordMapper {

    @Mapping(target = "wordDataDto", source = "wordData")
    WordDto toDto(Word entity);

    List<WordDto> toDtoList(List<Word> entityList);
}
