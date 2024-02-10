package my.project.config.datahandler;

import lombok.RequiredArgsConstructor;
import my.project.models.entity.flashcards.WordData;
import my.project.models.entity.enumeration.Platform;
import my.project.models.entity.enumeration.Category;
import my.project.models.entity.flashcards.WordPack;
import my.project.services.flashcards.WordDataService;
import my.project.services.flashcards.WordPackService;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

@Component
@RequiredArgsConstructor
public class ExcelDataHandler {

    private final WordPackService wordPackService;
    private final WordDataService wordDataService;
    private final MessageSource messageSource;

    @Transactional
    public void importWordPacks(String file, String fileSheet, Platform platform) {
        List<WordPack> listOfWordPacks = getWordPacksFromExcel(file, fileSheet, platform);
        saveWordPacksToDatabase(listOfWordPacks);
        System.out.println("ExcelDataHandler Report: " + fileSheet + " updated!");
    }

    @Transactional
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
                            case 2 -> wordData.setTranscription(currentCell.getStringCellValue().replaceAll("(?<!,) ", ""));
                            case 3 -> wordData.setNameEnglish(currentCell.getStringCellValue());
                            case 4 -> wordData.setNameRussian(currentCell.getStringCellValue());
                            case 5 -> wordData.setListOfWordPacks(getWordPacksFromCellValue(wordData, currentCell.getStringCellValue()));
                            case 6 -> wordData.setDefinition(currentCell.getStringCellValue());
                            case 7 -> wordData.setExamples(currentCell.getStringCellValue());
                            default -> {
                            }
                        }
                    }
                    if (platform == Platform.ENGLISH) {
                        switch (cellIdx) {
                            case 0 -> wordData.setId((long) currentCell.getNumericCellValue());
                            case 1 -> wordData.setNameEnglish(currentCell.getStringCellValue());
                            case 2 -> wordData.setTranscription(currentCell.getStringCellValue());
                            case 3 -> wordData.setNameRussian(currentCell.getStringCellValue());
                            case 4 -> wordData.setNameChineseSimplified(currentCell.getStringCellValue());
                            case 5 -> wordData.setListOfWordPacks(getWordPacksFromCellValue(wordData, currentCell.getStringCellValue()));
                            case 6 -> wordData.setDefinition(currentCell.getStringCellValue());
                            case 7 -> wordData.setExamples(currentCell.getStringCellValue());
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
            throw new RuntimeException(messageSource.getMessage("exception.excel.parse", null, Locale.getDefault())
                    .formatted(e.getMessage()));
        }
    }

    private void saveWordPacksToDatabase(List<WordPack> listOfWordPacks) {
        List<WordPack> allWordPacks = wordPackService.findAll();
        List<String> allWordPackNames = allWordPacks.stream()
                .map(WordPack::getName)
                .toList();

        List<WordPack> wordsPacksToBeSavedOrUpdated = new ArrayList<>();
        for (WordPack wordPack : listOfWordPacks) {
            if (!allWordPackNames.contains(wordPack.getName())) {
                wordsPacksToBeSavedOrUpdated.add(wordPack);
            } else {
                WordPack wordPackToBeUpdated = wordPackService.findByName(wordPack.getName());
                wordPackToBeUpdated.setDescription(wordPack.getDescription());
                wordPackToBeUpdated.setCategory(wordPack.getCategory());
                wordPackToBeUpdated.setPlatform(wordPack.getPlatform());
                wordsPacksToBeSavedOrUpdated.add(wordPackToBeUpdated);
            }
        }
        wordPackService.saveAll(wordsPacksToBeSavedOrUpdated);
    }

    private void saveWordsToDatabase(List<WordData> listOfWordData) {
        List<WordData> allWordData = wordDataService.findAll();
        List<Long> allWordsId = allWordData.stream().map(WordData::getId).toList();

        List<WordData> wordsToBeSavedOrUpdated = new ArrayList<>();
        for (WordData wordData : listOfWordData) {
            if (!allWordsId.contains(wordData.getId())) {
                wordsToBeSavedOrUpdated.add(wordData);
            } else {
                WordData wordDataToBeUpdated = wordDataService.findById(wordData.getId());
                wordDataToBeUpdated.setNameChineseSimplified(wordData.getNameChineseSimplified());
                wordDataToBeUpdated.setTranscription(wordData.getTranscription());
                wordDataToBeUpdated.setNameEnglish(wordData.getNameEnglish());
                wordDataToBeUpdated.setNameRussian(wordData.getNameRussian());
                wordDataToBeUpdated.setListOfWordPacks(wordData.getListOfWordPacks());
                wordDataToBeUpdated.setDefinition(wordData.getDefinition());
                wordDataToBeUpdated.setExamples(wordData.getExamples());
                wordDataToBeUpdated.setPlatform(wordData.getPlatform());
                wordsToBeSavedOrUpdated.add(wordDataToBeUpdated);
            }
        }
        wordDataService.saveAll(wordsToBeSavedOrUpdated);
    }

    private List<WordPack> getWordPacksFromCellValue(WordData wordData, String stringCellValue) {
        List<WordPack> listOfWordPacks = new ArrayList<>();

        List<String> wordPackNames = Arrays.stream(stringCellValue.split(";")).toList();
        wordPackNames.forEach(wordPackName -> {
            WordPack wordPack = wordPackService.findByName(wordPackName);
            listOfWordPacks.add(wordPack);
        });

        WordData wordDataExisting = wordDataService.findById(wordData.getId());
        for (WordPack wordPack : wordDataExisting.getListOfWordPacks()) {
            if (wordPack.getCategory() == Category.CUSTOM) {
                listOfWordPacks.add(wordPack);
            }
        }
        return listOfWordPacks;
    }
}
