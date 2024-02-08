package my.project.models.mapper.flashcards;

import lombok.AllArgsConstructor;
import my.project.models.dto.flashcards.WordDTO;
import my.project.models.entity.flashcards.*;
import my.project.models.mapper.Mapper;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class WordMapper implements Mapper<Word, WordDTO> {

    private final WordDataMapper wordDataMapper;

    @Override
    public WordDTO toDTO(Word entity) {
        return new WordDTO(
                entity.getId(),
                wordDataMapper.toDTO(entity.getWordData()),
                entity.getStatus(),
                entity.getCurrentStreak(),
                entity.getTotalStreak(),
                entity.getOccurrence(),
                entity.getDateOfLastOccurrence()
        );
    }
}