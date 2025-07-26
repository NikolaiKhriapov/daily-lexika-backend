package my.project.dailylexika.flashcard.service;

import my.project.dailylexika.flashcard.model.entities.WordData;
import my.project.library.dailylexika.dtos.flashcards.WordDataDto;
import my.project.library.dailylexika.enumerations.Platform;

import java.util.List;

public interface WordDataService {
    List<WordDataDto> getAll();
    List<WordData> getAllByPlatform(Platform platform);
    List<WordData> getAllByWordPackNameAndPlatform(String wordPackName, Platform platform);
    WordDataDto addCustomWordPackToWordData(Integer wordDataId, String wordPackName);
    WordDataDto removeCustomWordPackFromWordData(Integer wordDataId, String wordPackName);
    void addCustomWordPackToWordDataByWordPackName(String wordPackNameToBeAdded, String wordPackNameOriginal);
    void saveAll(List<WordData> listOfWordData);
    void deleteAll(List<WordData> listOfWordData);

    List<Integer> getAllWordDataIdByPlatform(Platform platform);
    List<Integer> getAllWordDataIdByWordPackNameAndPlatform(String wordPackName, Platform platform);
    Integer getIdByWordOfTheDayDateAndPlatform(Platform platform);
    WordData getEntityById(Integer wordDataId);
}
