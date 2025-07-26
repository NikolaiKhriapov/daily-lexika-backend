package my.project.dailylexika.flashcard.service;

import my.project.dailylexika.flashcard.model.entities.WordData;
import my.project.library.dailylexika.dtos.flashcards.WordDataDto;
import my.project.library.dailylexika.enumerations.Platform;

import java.util.List;

public interface WordDataService {
    List<WordData> findAllByPlatform(Platform platform);
    List<WordData> findAllByWordPackNameAndPlatform(String wordPackName, Platform platform);
    WordDataDto addCustomWordPackToWordData(Integer wordDataId, String wordPackName);
    WordDataDto removeCustomWordPackFromWordData(Integer wordDataId, String wordPackName);
    void addCustomWordPackToWordDataByWordPackName(String wordPackNameToBeAdded, String wordPackNameOriginal);
    void saveAll(List<WordData> listOfWordData);
    void deleteAll(List<WordData> listOfWordData);
    List<WordDataDto> getAllWordData();
    List<Integer> findAllWordDataIdByPlatform(Platform platform);
    List<Integer> findAllWordDataIdByWordPackNameAndPlatform(String wordPackName, Platform platform);
    Integer findIdByWordOfTheDayDateAndPlatform(Platform platform);
    WordData findEntityById(Integer wordDataId);
}
