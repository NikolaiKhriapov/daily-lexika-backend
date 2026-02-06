package my.project.dailylexika.flashcard.service;

import my.project.dailylexika.flashcard.model.entities.WordPack;
import my.project.library.dailylexika.dtos.flashcards.WordPackCustomCreateDto;
import my.project.library.dailylexika.dtos.flashcards.WordPackUserDto;
import my.project.library.dailylexika.dtos.flashcards.admin.WordPackCreateDto;
import my.project.library.dailylexika.dtos.flashcards.admin.WordPackDto;
import my.project.library.dailylexika.dtos.flashcards.admin.WordPackUpdateDto;
import my.project.library.dailylexika.enumerations.Platform;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public interface WordPackService {
    List<WordPackUserDto> getAllForUser();
    Page<WordPackDto> getPage(@NotNull Platform platform, @NotNull Pageable pageable);
    WordPack getById(@NotNull Long wordPackId);
    WordPackDto getDtoById(@NotNull Long wordPackId);
    void deleteAllByUserIdAndPlatform(@NotNull Integer userId, @NotNull Platform platform);

    WordPackDto create(@NotNull @Valid WordPackCreateDto createDto);
    WordPackDto update(@NotNull Long wordPackId, @NotNull @Valid WordPackUpdateDto patchDto);
    void delete(@NotNull Long wordPackId);

    WordPackDto createCustomWordPack(@NotNull @Valid WordPackCustomCreateDto wordPackDto);
    void deleteCustomWordPack(@NotNull Long wordPackId);

    void throwIfWordPackCategoryNotCustom(@NotNull WordPack wordPack);
}
