package my.project.vocabulary.word;

import lombok.RequiredArgsConstructor;
import my.project.vocabulary.chinesecharacter.ChineseCharacter;
import my.project.vocabulary.review.Review;
import my.project.vocabulary.wordpack.WordPack;
import org.springframework.stereotype.Service;

import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WordDTOMapper implements Function<Word, WordDTO> {

    @Override
    public WordDTO apply(Word word) {
        return new WordDTO(
                word.getId(),
                word.getNameChineseSimplified(),
                word.getNameChineseTraditional(),
                word.getPinyin(),
                word.getNameEnglish(),
                word.getNameRussian(),
                word.getStatus(),
                word.getCurrentStreak(),
                word.getTotalStreak(),
                word.getOccurrence(),
                word.getListOfReviews().stream()
                        .map(Review::getId)
                        .collect(Collectors.toList()),
                word.getListOfChineseCharacters().stream()
                        .map(ChineseCharacter::getId)
                        .collect(Collectors.toList()),
                word.getListOfWordPacks().stream()
                        .map(WordPack::getName)
                        .collect(Collectors.toList())
        );
    }
}