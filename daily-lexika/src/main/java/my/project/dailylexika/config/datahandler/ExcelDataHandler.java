package my.project.dailylexika.config.datahandler;

import lombok.RequiredArgsConstructor;
import my.project.library.util.datetime.DateUtil;
import my.project.dailylexika.config.i18n.I18nUtil;
import my.project.dailylexika.entities.flashcards.WordData;
import my.project.library.dailylexika.enumerations.Platform;
import my.project.library.dailylexika.enumerations.Category;
import my.project.dailylexika.entities.flashcards.WordPack;
import my.project.dailylexika.services.flashcards.WordDataService;
import my.project.dailylexika.services.flashcards.WordPackService;
import my.project.dailylexika.services.flashcards.WordService;
import my.project.library.util.exception.InternalServerErrorException;
import org.apache.poi.ss.usermodel.*;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.*;

@Component
@RequiredArgsConstructor
public class ExcelDataHandler {

    private final WordPackService wordPackService;
    private final WordDataService wordDataService;
    private final WordService wordService;

    public static Set<LocalDate> datesCounterForValidation = new HashSet<>();

    @Transactional
    public void importWordPacks(String fileClasspath, String fileSheet, Platform platform) {
        List<WordPack> listOfWordPacks = getWordPacksFromExcel(fileClasspath, fileSheet, platform);
        saveWordPacksToDatabase(listOfWordPacks);
        System.out.println("ExcelDataHandler Report: " + fileSheet + " updated!");
    }

    @Transactional
    public void importWords(String fileClasspath, String fileSheet, Platform platform) {
        List<WordData> listOfWordData = getWordsFromExcel(fileClasspath, fileSheet, platform);
        saveWordsToDatabase(listOfWordData, platform);
        System.out.println("ExcelDataHandler Report: " + fileSheet + " updated!");
        datesCounterForValidation.clear();
    }

    private List<WordPack> getWordPacksFromExcel(String fileClasspath, String fileSheet, Platform platform) {
        try (InputStream is = new ClassPathResource(fileClasspath).getInputStream()) {
            Workbook workbook = WorkbookFactory.create(is);
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
            throw new InternalServerErrorException("Failed to parse Excel file: " + e.getMessage());
        }
    }

    private List<WordData> getWordsFromExcel(String fileClasspath, String fileSheet, Platform platform) {
        try (InputStream is = new ClassPathResource(fileClasspath).getInputStream()) {
            Workbook workbook = WorkbookFactory.create(is);
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

                    if (platform == Platform.ENGLISH) {
                        switch (cellIdx) {
                            case 0 -> wordData.setId((int) currentCell.getNumericCellValue());
                            case 1 -> wordData.setNameEnglish(currentCell.getStringCellValue());
                            case 2 -> wordData.setTranscription(currentCell.getStringCellValue());
                            case 3 -> wordData.setNameRussian(currentCell.getStringCellValue());
                            case 4 -> wordData.setNameChinese(currentCell.getStringCellValue());
                            case 5 -> wordData.setDefinition(currentCell.getStringCellValue());
                            case 6 -> wordData.setExamples(currentCell.getStringCellValue());
                            case 7 -> wordData.setListOfWordPacks(getWordPacksFromCellValue(wordData, currentCell.getStringCellValue(), "EN__"));
                            case 8 -> wordData.setWordOfTheDayDate(DateUtil.convertDateToLocalDate(currentCell.getDateCellValue()));
                            default -> {
                            }
                        }
                    }
                    if (platform == Platform.CHINESE) {
                        switch (cellIdx) {
                            case 0 -> wordData.setId((int) currentCell.getNumericCellValue());
                            case 1 -> wordData.setNameChinese(currentCell.getStringCellValue());
                            case 2 -> wordData.setTranscription(currentCell.getStringCellValue().replaceAll("(?<!,) ", ""));
                            case 3 -> wordData.setNameEnglish(currentCell.getStringCellValue());
                            case 4 -> wordData.setNameRussian(currentCell.getStringCellValue());
                            case 5 -> wordData.setDefinition(currentCell.getStringCellValue());
                            case 6 -> wordData.setExamples(currentCell.getStringCellValue());
                            case 7 -> wordData.setListOfWordPacks(getWordPacksFromCellValue(wordData, currentCell.getStringCellValue(), "CH__"));
                            case 8 -> wordData.setWordOfTheDayDate(DateUtil.convertDateToLocalDate(currentCell.getDateCellValue()));
                            default -> {
                            }
                        }
                    }

                    cellIdx++;
                }

                wordData.setPlatform(platform);

                validateExcelWordData(wordData);

                if (wordData.getNameEnglish().length() > 250) {
                    wordData.setNameEnglish(wordData.getNameEnglish().substring(0, 250) + "...");
                }
                if (wordData.getNameRussian().length() > 250) {
                    wordData.setNameRussian(wordData.getNameRussian().substring(0, 250) + "...");
                }

                listOfWordData.add(wordData);
            }
            workbook.close();
            return listOfWordData;
        } catch (IOException e) {
            throw new InternalServerErrorException(I18nUtil.getMessage("dailylexika-exceptions.excel.parse", e.getMessage()));
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

    private void saveWordsToDatabase(List<WordData> listOfNewWordData, Platform platform) {
        List<WordData> allExistingWordData = wordDataService.findAllByPlatform(platform);
        List<Integer> allExistingWordDataIds = allExistingWordData.stream().map(WordData::getId).toList();
        List<Integer> allNewWordDataIds = listOfNewWordData.stream().map(WordData::getId).toList();

        List<WordData> wordsToBeDeleted = new ArrayList<>();
        for (WordData wordData : allExistingWordData) {
            if (!allNewWordDataIds.contains(wordData.getId())) {
                wordsToBeDeleted.add(wordData);
            }
        }

        List<WordData> wordsToBeSavedOrUpdated = new ArrayList<>();
        for (WordData wordData : listOfNewWordData) {
            if (!allExistingWordDataIds.contains(wordData.getId())) {
                wordsToBeSavedOrUpdated.add(wordData);
            } else {
                WordData wordDataToBeUpdated = wordDataService.findById(wordData.getId());
                wordDataToBeUpdated.setNameChinese(wordData.getNameChinese());
                wordDataToBeUpdated.setTranscription(wordData.getTranscription());
                wordDataToBeUpdated.setNameEnglish(wordData.getNameEnglish());
                wordDataToBeUpdated.setNameRussian(wordData.getNameRussian());
                wordDataToBeUpdated.setListOfWordPacks(wordData.getListOfWordPacks());
                wordDataToBeUpdated.setDefinition(wordData.getDefinition());
                wordDataToBeUpdated.setExamples(wordData.getExamples());
                wordDataToBeUpdated.setPlatform(wordData.getPlatform());
                wordDataToBeUpdated.setWordOfTheDayDate(wordData.getWordOfTheDayDate());
                wordsToBeSavedOrUpdated.add(wordDataToBeUpdated);
            }
        }
        wordDataService.saveAll(wordsToBeSavedOrUpdated);

        wordService.deleteAllByWordDataId(wordsToBeDeleted.stream().map(WordData::getId).toList());
        wordDataService.deleteAll(wordsToBeDeleted);
    }

    private List<WordPack> getWordPacksFromCellValue(WordData wordData, String stringCellValue, String prefix) {
        List<WordPack> listOfWordPacks = new ArrayList<>();

        List<String> wordPackNames = Arrays.stream(stringCellValue.split(";"))
                .map(wordPackName -> prefix + wordPackName)
                .toList();
        wordPackNames.forEach(wordPackName -> {
            WordPack wordPack = wordPackService.findByName(wordPackName);
            listOfWordPacks.add(wordPack);
        });

        try {
            WordData wordDataExisting = wordDataService.findById(wordData.getId());
            for (WordPack wordPack : wordDataExisting.getListOfWordPacks()) {
                if (wordPack.getCategory() == Category.CUSTOM) {
                    listOfWordPacks.add(wordPack);
                }
            }
        } catch (Exception ignored) {
        }
        return listOfWordPacks;
    }

    private void validateExcelWordData(WordData wordData) {
        switch (wordData.getPlatform()) {
            //TODO::: add validation for characters language
            //TODO::: 'definitions' must not end with '.'
//            case ENGLISH -> validateExcelWordDataEnglish(wordData);
//            case CHINESE -> validateExcelWordDataChinese(wordData);
        }
    }

    private void validateExcelWordDataEnglish(WordData wordData) {
        if (wordData.getNameEnglish().length() > 100) {
            System.out.println("!!!!! Excel data (EN_Words) validation failed (name_english): " + wordData.getId());
        }
        if (!wordData.getTranscription().startsWith("/") || !wordData.getTranscription().endsWith("/") || wordData.getTranscription().contains("/ /")) {
            System.out.println("!!!!! Excel data (EN_Words) validation failed (transcription): " + wordData.getId());
        }
        if (wordData.getNameRussian().length() > 100) {
            System.out.println("!!!!! Excel data (EN_Words) validation failed (name_russian): " + wordData.getId());
        }
        if (wordData.getNameChinese().length() > 19 || wordData.getNameChinese().contains(",") || wordData.getNameChinese().contains(";") || wordData.getNameChinese().contains("；") || wordData.getNameChinese().contains(" ")) {
            System.out.println("!!!!! Excel data (EN_Words) validation failed (name_chinese): " + wordData.getId());
        }
        if (wordData.getExamples().split(System.lineSeparator()).length != 5) {
            System.out.println("!!!!! Excel data (EN_Words) validation failed (examples): " + wordData.getId());
        }
        if (datesCounterForValidation.contains(wordData.getWordOfTheDayDate())) {
            System.out.println("!!!!! Excel data (EN_Words) validation failed (word_of_the_day_date): " + wordData.getId());
        } else {
            datesCounterForValidation.add(wordData.getWordOfTheDayDate());
        }
    }

    private void validateExcelWordDataChinese(WordData wordData) {
        if (wordData.getNameChinese().length() > 19) {
            System.out.println("!!!!! Excel data (CH_Words) validation failed (name_chinese): " + wordData.getId());
        }
        if (wordData.getTranscription().split(" ").length != wordData.getNameChinese().length()) {
            if (wordData.getTranscription().endsWith("r")) {
                if (wordData.getTranscription().split(" ").length != (wordData.getNameChinese().length() - 1)) {
                    System.out.println("!!!!! Excel data (CH_Words) validation failed (transcription): " + wordData.getId() + " " + wordData.getTranscription() + " " + wordData.getTranscription().split(" ").length + " ::: " + (wordData.getNameChinese().length()));
                }
            } else {
                System.out.println("!!!!! Excel data (CH_Words) validation failed (transcription): " + wordData.getId() + " " + wordData.getTranscription() + " " + wordData.getTranscription().split(" ").length + " ::: " + wordData.getNameChinese().length());
            }
        }
        if (!wordData.getNameEnglish().equals("[TODO]")) {
            if (wordData.getNameEnglish().length() > 100 || wordData.getNameEnglish().contains(";") || wordData.getNameEnglish().contains("  ")) {
                System.out.println("!!!!! Excel data (CH_Words) validation failed (name_english): " + wordData.getId());
            }
        }
        if (!wordData.getNameRussian().equals("[TODO]")) {
            if (wordData.getNameRussian().length() > 100 || wordData.getNameRussian().contains(";") || wordData.getNameRussian().contains("  ")) {
                System.out.println("!!!!! Excel data (CH_Words) validation failed (name_russian): " + wordData.getId());
            }
        }
        if (!wordData.getDefinition().equals("[TODO]")) {
            if (wordData.getDefinition().endsWith(".") && !wordData.getDefinition().endsWith("etc.")) {
                System.out.println("!!!!! Excel data (CH_Words) validation failed (definition): " + wordData.getId());
            }
        }
        if (!wordData.getExamples().equals("[TODO]")) {
            String[] examplesArray = wordData.getExamples().split(System.lineSeparator() + System.lineSeparator());
            if (examplesArray.length != 5) {
                System.out.println("!!!!! Excel data (CH_Words) validation failed (examples): " + wordData.getId());
            } else {
                Arrays.stream(examplesArray).forEach(oneExample -> {
                    if (oneExample.split(System.lineSeparator()).length != 4) {
                        System.out.println("!!!!! Excel data (CH_Words) validation failed (examples): " + wordData.getId());
                    }
                });
            }
        }
        if (datesCounterForValidation.contains(wordData.getWordOfTheDayDate())) {
            System.out.println("!!!!! Excel data (CH_Words) validation failed (word_of_the_day_date): " + wordData.getId());
        } else {
            datesCounterForValidation.add(wordData.getWordOfTheDayDate());
        }
    }
}
