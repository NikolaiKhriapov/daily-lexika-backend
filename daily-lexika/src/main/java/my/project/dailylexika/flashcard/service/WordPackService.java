package my.project.dailylexika.flashcard.service;

import my.project.dailylexika.flashcard.model.entities.WordPack;
import my.project.library.dailylexika.dtos.flashcards.WordPackDto;
import my.project.library.dailylexika.enumerations.Platform;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public interface WordPackService {
    List<WordPack> getAll();
    List<WordPackDto> getAllForUser();
    WordPack getByName(@NotBlank String wordPackName);
    void saveAll(@NotNull List<WordPack> wordPacks);
    void deleteAllByUserIdAndPlatform(@NotNull Integer userId, @NotNull Platform platform);
    void createCustomWordPack(@NotNull @Valid WordPackDto wordPackDto);
    void deleteCustomWordPack(@NotBlank String wordPackName);
    void throwIfWordPackCategoryNotCustom(@NotNull WordPack wordPack);
}
