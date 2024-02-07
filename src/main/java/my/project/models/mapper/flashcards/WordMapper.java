package my.project.models.mapper.flashcards;

import lombok.AllArgsConstructor;
import my.project.models.dto.flashcards.WordDTO;
import my.project.models.entity.flashcards.*;
import my.project.models.mapper.Mapper;
import my.project.services.flashcards.WordDataService;
import org.springframework.stereotype.Service;

import java.util.Arrays;
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
                wordData.getTranscription(),
                wordData.getNameEnglish(),
                wordData.getNameRussian(),
                wordData.getDefinition(),
                Arrays.stream(wordData.getExamples().split(";"))
                        .map(String::strip)
                        .collect(Collectors.toSet()),
                wordData.getListOfWordPacks().stream()
                        .map(WordPack::getName)
                        .toList(),
                entity.getStatus(),
                entity.getCurrentStreak(),
                entity.getTotalStreak(),
                entity.getOccurrence(),
                entity.getDateOfLastOccurrence()
        );
    }
}