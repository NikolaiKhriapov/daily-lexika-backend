package my.project.dailylexika.flashcard.service;

import my.project.dailylexika.flashcard.model.entities.WordData;
import my.project.library.dailylexika.dtos.flashcards.WordDataDto;
import my.project.library.dailylexika.enumerations.Platform;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.util.List;

public interface WordDataService {
    List<WordDataDto> search(@NotBlank String query, @NotNull @Positive Integer limit);
    List<WordData> getAllByPlatform(@NotNull Platform platform);
    List<WordData> getAllByWordPackNameAndPlatform(@NotBlank String wordPackName, @NotNull Platform platform);
    boolean existsByWordPackNameAndPlatform(@NotBlank String wordPackName, @NotNull Platform platform);
    WordDataDto addCustomWordPackToWordData(@NotNull Integer wordDataId, @NotBlank String wordPackName);
    WordDataDto removeCustomWordPackFromWordData(@NotNull Integer wordDataId, @NotBlank String wordPackName);
    void addCustomWordPackToWordDataByWordPackName(@NotBlank String wordPackNameToBeAdded, @NotBlank String wordPackNameOriginal);
    void saveAll(@NotNull List<WordData> listOfWordData);
    void deleteAll(@NotNull List<WordData> listOfWordData);

    List<Integer> getAllWordDataIdByPlatform(@NotNull Platform platform);
    List<Integer> getAllWordDataIdByWordPackNameAndPlatform(@NotBlank String wordPackName, @NotNull Platform platform);
    Integer getIdByWordOfTheDayDateAndPlatform(@NotNull Platform platform);
    WordData getEntityById(@NotNull Integer wordDataId);
}
