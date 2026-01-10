package my.project.dailylexika.flashcard.service;

import my.project.dailylexika.flashcard.model.entities.WordData;
import my.project.library.dailylexika.dtos.flashcards.WordDataDto;
import my.project.library.dailylexika.dtos.flashcards.admin.WordDataCreateDto;
import my.project.library.dailylexika.dtos.flashcards.admin.WordDataUpdateDto;
import my.project.library.dailylexika.enumerations.Platform;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.util.List;

public interface WordDataService {
    List<WordDataDto> search(@NotBlank String query, @NotNull @Positive Integer limit);
    List<WordData> getAllByWordPackIdAndPlatform(@NotNull Long wordPackId, @NotNull Platform platform);
    boolean existsByWordPackIdAndPlatform(@NotNull Long wordPackId, @NotNull Platform platform);
    WordDataDto addCustomWordPackToWordData(@NotNull Integer wordDataId, @NotNull Long wordPackId);
    WordDataDto removeCustomWordPackFromWordData(@NotNull Integer wordDataId, @NotNull Long wordPackId);
    void addCustomWordPackToWordDataByWordPackId(@NotNull Long wordPackIdToBeAdded, @NotNull Long wordPackIdOriginal);
    void removeWordPackReferences(@NotNull Long wordPackId, @NotNull Platform platform);

    Page<WordDataDto> getPage(@NotNull Platform platform, String query, @NotNull Pageable pageable);
    WordDataDto getById(@NotNull Integer wordDataId);
    WordDataDto create(@NotNull @Valid WordDataCreateDto createDto);
    WordDataDto update(@NotNull Integer wordDataId, @NotNull @Valid WordDataUpdateDto patchDto);
    void delete(@NotNull Integer wordDataId);

    List<Integer> getAllWordDataIdByPlatform(@NotNull Platform platform);
    List<Integer> getAllWordDataIdByWordPackIdAndPlatform(@NotNull Long wordPackId, @NotNull Platform platform);
    Integer getIdByWordOfTheDayDateAndPlatform(@NotNull Platform platform);
    WordData getEntityById(@NotNull Integer wordDataId);
}
