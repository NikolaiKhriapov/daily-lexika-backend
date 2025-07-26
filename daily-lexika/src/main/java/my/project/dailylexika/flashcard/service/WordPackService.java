package my.project.dailylexika.flashcard.service;

import my.project.dailylexika.flashcard.model.entities.WordPack;
import my.project.library.dailylexika.dtos.flashcards.WordPackDto;
import my.project.library.dailylexika.enumerations.Platform;

import java.util.List;

public interface WordPackService {
    List<WordPack> getAll();
    List<WordPackDto> getAllForUser();
    WordPack getByName(String wordPackName);
    void saveAll(List<WordPack> wordPacks);
    void deleteAllByUserIdAndPlatform(Integer userId, Platform platform);
    void createCustomWordPack(WordPackDto wordPackDto);
    void deleteCustomWordPack(String wordPackName);
    void throwIfWordPackCategoryNotCustom(WordPack wordPack);
}
