package my.project.vocabulary.mapper;

import my.project.vocabulary.model.entity.ChineseCharacter;
import my.project.vocabulary.model.entity.Review;
import my.project.vocabulary.model.entity.Word;
import my.project.vocabulary.model.dto.WordDTO;
import my.project.vocabulary.model.entity.WordPack;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
public class WordMapper implements Mapper<Word, WordDTO> {

    @Override
    public WordDTO toDTO(Word entity) {
        return new WordDTO(
                entity.getId(),
                entity.getNameChineseSimplified(),
                entity.getNameChineseTraditional(),
                entity.getPinyin(),
                entity.getNameEnglish(),
                entity.getNameRussian(),
                entity.getStatus(),
                entity.getCurrentStreak(),
                entity.getTotalStreak(),
                entity.getOccurrence(),
                entity.getDateOfLastOccurrence(),
                entity.getListOfReviews().stream()
                        .map(Review::getId)
                        .collect(Collectors.toList()),
                entity.getListOfChineseCharacters().stream()
                        .map(ChineseCharacter::getId)
                        .collect(Collectors.toList()),
                entity.getListOfWordPacks().stream()
                        .map(WordPack::getName)
                        .collect(Collectors.toList())
        );
    }
}