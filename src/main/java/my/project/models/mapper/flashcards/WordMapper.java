package my.project.models.mapper.flashcards;

import lombok.AllArgsConstructor;
import my.project.models.dto.flashcards.WordDTO;
import my.project.models.entity.flashcards.*;
import my.project.models.mapper.Mapper;
import my.project.services.flashcards.WordDataService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class WordMapper implements Mapper<Word, WordDTO> {

    private final WordDataService wordDataService;

    @Override
    public WordDTO toDTO(Word entity) {
        WordData wordData = wordDataService.getWordData(entity.getWordData().getId());

        return new WordDTO(
                entity.getId(),
                wordData.getNameChineseSimplified(),
                wordData.getNameChineseTraditional(),
                wordData.getPinyin(),
                wordData.getNameEnglish(),
                wordData.getNameRussian(),
                entity.getStatus(),
                entity.getCurrentStreak(),
                entity.getTotalStreak(),
                entity.getOccurrence(),
                entity.getDateOfLastOccurrence(),
                wordData.getListOfWordPacks().stream()
                        .map(WordPack::getName)
                        .collect(Collectors.toList())
        );
    }

    public WordDTO toDTOShort(Word entity) {
        WordData wordData = wordDataService.getWordData(entity.getWordData().getId());

        return new WordDTO(
                entity.getId(),
                wordData.getNameChineseSimplified(),
                wordData.getNameChineseTraditional(),
                wordData.getPinyin(),
                wordData.getNameEnglish(),
                wordData.getNameRussian(),
                entity.getStatus(),
                null,
                entity.getTotalStreak(),
                null,
                null,
                null
        );
    }

    public List<WordDTO> toDTOShortList(List<Word> entities) {
        return entities.stream()
                .map(this::toDTOShort)
                .collect(Collectors.toList());
    }
}