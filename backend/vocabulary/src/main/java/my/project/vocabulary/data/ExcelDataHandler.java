package my.project.vocabulary.data;

import lombok.RequiredArgsConstructor;
import my.project.vocabulary.model.entity.Word;
import my.project.vocabulary.repository.WordRepository;
import my.project.vocabulary.model.entity.Category;
import my.project.vocabulary.model.entity.WordPack;
import my.project.vocabulary.repository.WordPackRepository;
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
    private final WordRepository wordRepository;

    public void importWordPacks(String file, String fileSheet) {
        List<WordPack> listOfWordPacks = getWordPacksFromExcel(file, fileSheet);
        saveWordPacksToDatabase(listOfWordPacks);
        System.out.println("ExcelDataHandler Report: " + fileSheet + " updated!");
    }

    public void importWords(String file, String[] fileSheets) {
        for (String fileSheet : fileSheets) {
            List<Word> listOfWords = getWordsFromExcel(file, fileSheet);
            saveWordsToDatabase(listOfWords);
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

    public List<Word> getWordsFromExcel(String file, String fileSheet) {
        try (FileInputStream is = new FileInputStream(file)) {
            Workbook workbook = new XSSFWorkbook(is);
            Sheet sheet = workbook.getSheet(fileSheet);
            Iterator<Row> rows = sheet.iterator();

            List<Word> listOfWords = new ArrayList<>();

            int rowNumber = 0;
            while (rows.hasNext()) {
                Row currentRow = rows.next();

                if (rowNumber == 0) {
                    rowNumber++;
                    continue;
                }

                Iterator<Cell> cellsInRow = currentRow.iterator();

                Word word = new Word();

                int cellIdx = 0;
                while (cellsInRow.hasNext()) {
                    Cell currentCell = cellsInRow.next();
                    switch (cellIdx) {
                        case 0 -> word.setId((long) currentCell.getNumericCellValue());
                        case 1 -> word.setNameChineseSimplified(currentCell.getStringCellValue());
                        case 2 -> word.setNameChineseTraditional(currentCell.getStringCellValue());
                        case 3 -> word.setPinyin(currentCell.getStringCellValue());
                        case 4 -> word.setNameEnglish(currentCell.getStringCellValue());
                        case 5 -> word.setNameRussian(currentCell.getStringCellValue());
                        case 6 -> {
                            List<WordPack> listOfWordPacks = new ArrayList<>();
                            WordPack wordPack = wordPackRepository.findById(currentCell.getStringCellValue())
                                    .orElseThrow(() -> new IllegalStateException("NOT FOUND"));
                            listOfWordPacks.add(wordPack);
                            word.setListOfWordPacks(listOfWordPacks);
                        }
                        default -> {
                        }
                    }
                    cellIdx++;
                }

                if (word.getNameEnglish().length() > 250) {
                    word.setNameEnglish(word.getNameEnglish().substring(0, 250) + "...");
                }

                listOfWords.add(word);
            }
            workbook.close();
            return listOfWords;
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

    public void saveWordsToDatabase(List<Word> listOfWords) {
        List<Word> allWords = wordRepository.findAll();
        List<Long> allWordsId = allWords.stream().map(Word::getId).toList();

        List<Word> wordsToBeSaved = new ArrayList<>();
        List<Word> wordsToBeUpdated = new ArrayList<>();
        for (Word word : listOfWords) {
            if (!allWordsId.contains(word.getId())) {
                wordsToBeSaved.add(word);
            } else {
                Word wordToBeUpdated = wordRepository.findById(word.getId())
                        .orElseThrow(() -> new IllegalStateException("NOT FOUND"));
                wordToBeUpdated.setNameChineseSimplified(word.getNameChineseSimplified());
                wordToBeUpdated.setNameChineseTraditional(word.getNameChineseTraditional());
                wordToBeUpdated.setPinyin(word.getPinyin());
                wordToBeUpdated.setNameEnglish(word.getNameEnglish());
                wordToBeUpdated.setNameRussian(word.getNameRussian());
                wordToBeUpdated.setListOfWordPacks(word.getListOfWordPacks());
                wordsToBeUpdated.add(wordToBeUpdated);
            }
        }
        wordRepository.saveAll(wordsToBeSaved);
        wordRepository.saveAll(wordsToBeUpdated);
    }

//    public void importWordsFromDict(String file, String fileSheet) {
//        List<Word> listOfWords = getWordsFromDict(file, fileSheet);
//        saveWordsToDatabase(listOfWords);
//        System.out.println("ExcelDataHandler Report: " + fileSheet + " updated!");
//    }
//
//    public List<Word> getWordsFromDict(String file, String fileSheet) {
//        try (FileInputStream is = new FileInputStream(file)) {
//            Workbook workbook = new XSSFWorkbook(is);
//            Sheet sheet = workbook.getSheet(fileSheet);
//            Iterator<Row> rows = sheet.iterator();
//
//            List<Word> listOfWords = new ArrayList<>();
//
//            int rowNumber = 0;
//            while (rows.hasNext()) {
//                Row currentRow = rows.next();
//
//                Iterator<Cell> cellsInRow = currentRow.iterator();
//
//                Word word = new Word();
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
