package my.project.dailylexika.flashcard.service;

import my.project.dailylexika.flashcard.model.entities.WordPack;
import my.project.library.dailylexika.dtos.flashcards.WordPackDto;
import my.project.library.dailylexika.enumerations.Platform;

import java.util.List;

public interface WordPackService {
    WordPack findByName(String wordPackName);
    List<WordPack> findAll();
    void saveAll(List<WordPack> wordPacks);
    List<WordPackDto> getAllWordPacksForUser();
    void createCustomWordPack(WordPackDto wordPackDto);
    void deleteCustomWordPack(String wordPackName);
    void deleteAllByUserIdAndPlatform(Integer userId, Platform platform);
    void throwIfWordPackCategoryNotCustom(WordPack wordPack);
}
