package my.project.models.mapper.flashcards;

import lombok.AllArgsConstructor;
import my.project.models.dto.flashcards.WordDataDTO;
import my.project.models.entity.flashcards.WordData;
import my.project.models.entity.flashcards.WordPack;
import my.project.models.mapper.Mapper;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class WordDataMapper implements Mapper<WordData, WordDataDTO> {

    @Override
    public WordDataDTO toDTO(WordData entity) {
        return new WordDataDTO(
                entity.getId(),
                entity.getNameChineseSimplified(),
                entity.getTranscription(),
                entity.getNameEnglish(),
                entity.getNameRussian(),
                entity.getDefinition(),
                Arrays.stream(entity.getExamples().split(";"))
                        .map(String::strip)
                        .collect(Collectors.toSet()),
                entity.getListOfWordPacks().stream()
                        .map(WordPack::getName)
                        .toList(),
                entity.getPlatform()
        );
    }
}