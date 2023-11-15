package my.project.config.datahandler;

import lombok.RequiredArgsConstructor;
import my.project.models.entity.chineseflashcards.WordData;
import my.project.repositories.chineseflashcards.WordDataRepository;
import my.project.models.entity.chineseflashcards.Category;
import my.project.models.entity.chineseflashcards.WordPack;
import my.project.repositories.chineseflashcards.WordPackRepository;
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

    public void importWordPacks(String file, String fileSheet) {
        List<WordPack> listOfWordPacks = getWordPacksFromExcel(file, fileSheet);
        saveWordPacksToDatabase(listOfWordPacks);
        System.out.println("ExcelDataHandler Report: " + fileSheet + " updated!");
    }

    public void importWords(String file, String[] fileSheets) {
        for (String fileSheet : fileSheets) {
            List<WordData> listOfWordData = getWordsFromExcel(file, fileSheet);
            saveWordsToDatabase(listOfWordData);
            System.out.println("ExcelDataHandler Report: " + fileSheet + " updated!");
        }
    }

    public List<WordPack> getWordPacksFromExcel(String file, String fileSheet) {
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

                listOfWordPacks.add(wordPack);
            }
            workbook.close();
            return listOfWordPacks;
        } catch (IOException e) {
            throw new RuntimeException("Failed to parse Excel file: " + e.getMessage());
        }
    }

    public List<WordData> getWordsFromExcel(String file, String fileSheet) {
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
                    cellIdx++;
                }

                if (wordData.getNameEnglish().length() > 250) {
                    wordData.setNameEnglish(wordData.getNameEnglish().substring(0, 250) + "...");
                }

                listOfWordData.add(wordData);
            }
            workbook.close();
            return listOfWordData;
        } catch (IOException e) {
            throw new RuntimeException("Failed to parse Excel file: " + e.getMessage());
        }
    }

    public void saveWordPacksToDatabase(List<WordPack> listOfWordPacks) {
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
                wordsPacksToBeSavedOrUpdated.add(wordPackToBeUpdated);
            }
        }
        wordPackRepository.saveAll(wordsPacksToBeSavedOrUpdated);
    }

    public void saveWordsToDatabase(List<WordData> listOfWordData) {
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
                wordsToBeUpdated.add(wordDataToBeUpdated);
            }
        }
        wordDataRepository.saveAll(wordsToBeSaved);
        wordDataRepository.saveAll(wordsToBeUpdated);
    }

//    public void importWordsFromDict(String file, String fileSheet) {
//        List<WordData> listOfWords = getWordsFromDict(file, fileSheet);
//        saveWordsToDatabase(listOfWords);
//        System.out.println("ExcelDataHandler Report: " + fileSheet + " updated!");
//    }
//
//    public List<WordData> getWordsFromDict(String file, String fileSheet) {
//        try (FileInputStream is = new FileInputStream(file)) {
//            Workbook workbook = new XSSFWorkbook(is);
//            Sheet sheet = workbook.getSheet(fileSheet);
//            Iterator<Row> rows = sheet.iterator();
//
//            List<WordData> listOfWords = new ArrayList<>();
//
//            int rowNumber = 0;
//            while (rows.hasNext()) {
//                Row currentRow = rows.next();
//
//                Iterator<Cell> cellsInRow = currentRow.iterator();
//
//                WordData word = new WordData();
//
//                int cellIdx = 0;
//                while (cellsInRow.hasNext()) {
//                    Cell currentCell = cellsInRow.next();
//                    word.setId(50000000L + currentRow.getRowNum());
//                    word.setNameRussian(" ");
//                    switch (cellIdx) {
//                        case 0 -> word.setNameChineseTraditional(currentCell.getStringCellValue());
//                        case 1 -> word.setNameChineseSimplified(currentCell.getStringCellValue());
//                        case 2 -> word.setPinyin(currentCell.getStringCellValue());
//                        case 3 -> word.setNameEnglish(currentCell.getStringCellValue());
//                    }
//                    cellIdx++;
//                }
//
//                if (word.getNameChineseSimplified().length() == 1) {
//
//                    if (word.getNameEnglish().length() > 250) {
//                        word.setNameEnglish(word.getNameEnglish().substring(0, 250) + "...");
//                    }
//
//                    listOfWords.add(word);
//                }
//            }
//            workbook.close();
//            return listOfWords;
//        } catch (IOException e) {
//            throw new RuntimeException("Failed to parse Excel file: " + e.getMessage());
//        }
//    }
}
