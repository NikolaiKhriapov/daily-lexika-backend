package my.project.config.datahandler;

import lombok.RequiredArgsConstructor;
import my.project.models.entity.flashcards.WordData;
import my.project.models.entity.enumeration.Platform;
import my.project.repositories.flashcards.WordDataRepository;
import my.project.models.entity.enumeration.Category;
import my.project.models.entity.flashcards.WordPack;
import my.project.repositories.flashcards.WordPackRepository;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ExcelDataHandler {

    private final WordPackRepository wordPackRepository;
    private final WordDataRepository wordDataRepository;

    public void importWordPacks(String file, String fileSheet, Platform platform) {
        List<WordPack> listOfWordPacks = getWordPacksFromExcel(file, fileSheet, platform);
        saveWordPacksToDatabase(listOfWordPacks);
        System.out.println("ExcelDataHandler Report: " + fileSheet + " updated!");
    }

    public void importWords(String file, String[] fileSheets, Platform platform) {
        for (String fileSheet : fileSheets) {
            List<WordData> listOfWordData = getWordsFromExcel(file, fileSheet, platform);
            saveWordsToDatabase(listOfWordData);
            System.out.println("ExcelDataHandler Report: " + fileSheet + " updated!");
        }
    }

    private List<WordPack> getWordPacksFromExcel(String file, String fileSheet, Platform platform) {
        try (FileInputStream is = new FileInputStream(file)) {
            Workbook workbook = new XSSFWorkbook(is);
            Sheet sheet = workbook.getSheet(fileSheet);
            Iterator<Row> rows = sheet.iterator();

            List<WordPack> listOfWordPacks = new ArrayList<>();

            int rowNumber = 0;
            while (rows.hasNext()) {
                Row currentRow = rows.next();

                if (rowNumber == 0) {
                    rowNumber++;
                    continue;
                }

                Iterator<Cell> cellsInRow = currentRow.iterator();

                WordPack wordPack = new WordPack();

                int cellIdx = 0;
                while (cellsInRow.hasNext()) {
                    Cell currentCell = cellsInRow.next();
                    switch (cellIdx) {
                        case 0 -> wordPack.setName(currentCell.getStringCellValue());
                        case 1 -> wordPack.setDescription(currentCell.getStringCellValue());
                        case 2 -> wordPack.setCategory(Category.valueOf(currentCell.getStringCellValue()));
                    }
                    cellIdx++;
                }
                wordPack.setPlatform(platform);

                listOfWordPacks.add(wordPack);
            }
            workbook.close();
            return listOfWordPacks;
        } catch (IOException e) {
            throw new RuntimeException("Failed to parse Excel file: " + e.getMessage());
        }
    }

    private List<WordData> getWordsFromExcel(String file, String fileSheet, Platform platform) {
        try (FileInputStream is = new FileInputStream(file)) {
            Workbook workbook = new XSSFWorkbook(is);
            Sheet sheet = workbook.getSheet(fileSheet);
            Iterator<Row> rows = sheet.iterator();

            List<WordData> listOfWordData = new ArrayList<>();

            int rowNumber = 0;
            while (rows.hasNext()) {
                Row currentRow = rows.next();

                if (rowNumber == 0) {
                    rowNumber++;
                    continue;
                }

                Iterator<Cell> cellsInRow = currentRow.iterator();

                WordData wordData = new WordData();

                int cellIdx = 0;
                while (cellsInRow.hasNext()) {
                    Cell currentCell = cellsInRow.next();

                    if (platform == Platform.CHINESE) {
                        switch (cellIdx) {
                            case 0 -> wordData.setId((long) currentCell.getNumericCellValue());
                            case 1 -> wordData.setNameChineseSimplified(currentCell.getStringCellValue());
                            case 2 -> wordData.setNameChineseTraditional(currentCell.getStringCellValue());
                            case 3 -> wordData.setPinyin(currentCell.getStringCellValue());
                            case 4 -> wordData.setNameEnglish(currentCell.getStringCellValue());
                            case 5 -> wordData.setNameRussian(currentCell.getStringCellValue());
                            case 6 -> {
                                List<WordPack> listOfWordPacks = new ArrayList<>();
                                WordPack wordPack = wordPackRepository.findById(currentCell.getStringCellValue())
                                        .orElseThrow(() -> new IllegalStateException("NOT FOUND"));
                                listOfWordPacks.add(wordPack);
                                wordData.setListOfWordPacks(listOfWordPacks);
                            }
                            default -> {
                            }
                        }
                    }
                    if (platform == Platform.ENGLISH) {
                        switch (cellIdx) {
                            case 0 -> wordData.setId((long) currentCell.getNumericCellValue());
                            case 1 -> wordData.setNameEnglish(currentCell.getStringCellValue());
                            case 2 -> wordData.setNameRussian(currentCell.getStringCellValue());
                            case 3 -> {
                                List<WordPack> listOfWordPacks = new ArrayList<>();
                                WordPack wordPack = wordPackRepository.findById(currentCell.getStringCellValue())
                                        .orElseThrow(() -> new IllegalStateException("NOT FOUND"));
                                listOfWordPacks.add(wordPack);
                                wordData.setListOfWordPacks(listOfWordPacks);
                            }
                            default -> {
                            }
                        }
                    }

                    cellIdx++;
                }

                if (wordData.getNameEnglish().length() > 250) {
                    wordData.setNameEnglish(wordData.getNameEnglish().substring(0, 250) + "...");
                }
                wordData.setPlatform(platform);

                listOfWordData.add(wordData);
            }
            workbook.close();
            return listOfWordData;
        } catch (IOException e) {
            throw new RuntimeException("Failed to parse Excel file: " + e.getMessage());
        }
    }

    private void saveWordPacksToDatabase(List<WordPack> listOfWordPacks) {
        List<WordPack> allWordPacks = wordPackRepository.findAll();
        List<String> allWordPackNames = allWordPacks.stream()
                .map(WordPack::getName)
                .toList();

        List<WordPack> wordsPacksToBeSavedOrUpdated = new ArrayList<>();
        for (WordPack wordPack : listOfWordPacks) {
            if (!allWordPackNames.contains(wordPack.getName())) {
                wordsPacksToBeSavedOrUpdated.add(wordPack);
            } else {
                WordPack wordPackToBeUpdated = wordPackRepository.findById(wordPack.getName())
                        .orElseThrow(() -> new IllegalStateException("NOT FOUND"));
                wordPackToBeUpdated.setDescription(wordPack.getDescription());
                wordPackToBeUpdated.setCategory(wordPack.getCategory());
                wordPackToBeUpdated.setPlatform(wordPack.getPlatform());
                wordsPacksToBeSavedOrUpdated.add(wordPackToBeUpdated);
            }
        }
        wordPackRepository.saveAll(wordsPacksToBeSavedOrUpdated);
    }

    private void saveWordsToDatabase(List<WordData> listOfWordData) {
        List<WordData> allWordData = wordDataRepository.findAll();
        List<Long> allWordsId = allWordData.stream().map(WordData::getId).toList();

        List<WordData> wordsToBeSaved = new ArrayList<>();
        List<WordData> wordsToBeUpdated = new ArrayList<>();
        for (WordData wordData : listOfWordData) {
            if (!allWordsId.contains(wordData.getId())) {
                wordsToBeSaved.add(wordData);
            } else {
                WordData wordDataToBeUpdated = wordDataRepository.findById(wordData.getId())
                        .orElseThrow(() -> new IllegalStateException("NOT FOUND"));
                wordDataToBeUpdated.setNameChineseSimplified(wordData.getNameChineseSimplified());
                wordDataToBeUpdated.setNameChineseTraditional(wordData.getNameChineseTraditional());
                wordDataToBeUpdated.setPinyin(wordData.getPinyin());
                wordDataToBeUpdated.setNameEnglish(wordData.getNameEnglish());
                wordDataToBeUpdated.setNameRussian(wordData.getNameRussian());
                wordDataToBeUpdated.setListOfWordPacks(wordData.getListOfWordPacks());
                wordDataToBeUpdated.setListOfWordPacks(wordData.getListOfWordPacks());
                wordDataToBeUpdated.setPlatform(wordData.getPlatform());
                wordsToBeUpdated.add(wordDataToBeUpdated);
            }
        }
        wordDataRepository.saveAll(wordsToBeSaved);
        wordDataRepository.saveAll(wordsToBeUpdated);
    }
}
