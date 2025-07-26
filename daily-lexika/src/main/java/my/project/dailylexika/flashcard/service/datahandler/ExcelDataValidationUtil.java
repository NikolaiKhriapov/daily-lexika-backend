package my.project.dailylexika.flashcard.service.datahandler;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import my.project.dailylexika.flashcard.model.entities.WordData;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

@Slf4j
public class ExcelDataValidationUtil {

    private static final String REGEX_EXCEPTIONS = "PDF|CD|DVD|IT|DNA|RNA|GPS|IP|IQ|CPU|VIP|USВ-|ISO|Wi-Fi|QR-|WeChat|WeChat Pay|D|i|H2O";
    private static final String REGEX_CH = "^(?:(?:" + REGEX_EXCEPTIONS + "|T恤|维生素C|U盘)|[\\u4e00-\\u9fa50-9\\s“”《》，、。.…？！；：%–/（）【】$¥])+$";
    private static final String REGEX_CH_PINYIN = "^(?:(?:" + REGEX_EXCEPTIONS + "|U pán)|[a-zāáǎàēéěèīíǐìōóǒòūúǔùüǖǘǚǜ0-9\\s“”,.…?!;:’%–$¥])+$";
    private static final String REGEX_EN = "^[a-zA-Zé0-9\\s“”,.…?!;:+%’/()$¥&—–-]+$";
    private static final String REGEX_EN_TRANSCRIPTION = "^[\\p{L}\\p{M}ˈˌː().]+(?:\\s*[\\p{L}\\p{M}ˈˌː().]+)*$";
    private static final String REGEX_RU = "^(?:(?:" + REGEX_EXCEPTIONS + ")|[а-яА-ЯёЁ0-9\\s№«»,.…?!;:%/()$¥—–-])+$";

    private static final List<Integer> EXCEPTIONS_CH_PINYIN = List.of(2000464, 2000555, 2003518);
    private static final List<Integer> EXCEPTIONS_CH_EN = List.of(2003080);
    private static final List<Integer> EXCEPTIONS_CH_RU = List.of(2003080);

    public static void validateExcelWordDataEnglish(WordData wordData) {
//        validateEnglishNameEnglish("EN_Words", "name_english", wordData);
//        validateEnglishTranscription("EN_Words", "transcription", wordData);
//        validateEnglishNameRussian("EN_Words", "name_russian", wordData);
//        validateEnglishNameChinese("EN_Words", "name_chinese", wordData);
//        validateEnglishDefinition("EN_Words", "definition", wordData);
//        validateEnglishExamples("EN_Words", "examples", wordData);
    }

    public static void validateExcelWordDataChinese(WordData wordData) {
//        validateChineseNameChinese("CH_Words", "name_chinese", wordData);
//        validateChineseTranscription("CH_Words", "transcription", wordData);
//        validateChineseNameEnglish("CH_Words", "name_english", wordData);
//        validateChineseNameRussian("CH_Words", "name_russian", wordData);
//        validateChineseDefinition("CH_Words", "definition", wordData);
//        validateChineseExamples("CH_Words", "examples", wordData);
    }

    /**
     * First-level helper methods for ENGLISH
     */

    private static void validateEnglishNameEnglish(String sheet, String column, WordData wordData) {
        validateLengthIsLessThan(sheet, column, wordData.getNameEnglish(), wordData.getId(), 30);
        validateNotContains(sheet, column, wordData.getNameEnglish(), wordData.getId(), List.of("\\s"));
        validateRegexMatch(sheet, column, wordData.getNameEnglish(), wordData.getId(), REGEX_EN);
    }

    private static void validateEnglishTranscription(String sheet, String column, WordData wordData) {
        validateStartsWith(sheet, column, wordData.getTranscription(), wordData.getId(), "/");
        validateEndsWith(sheet, column, wordData.getTranscription(), wordData.getId(), "/");
        validateNotContains(sheet, column, wordData.getTranscription(), wordData.getId(), List.of("/ /"));

        String[] variants = wordData.getTranscription().replaceAll("/", "").split(", ");
        for (String variant : variants) {
            validateRegexMatch(sheet, column, variant, wordData.getId(), REGEX_EN_TRANSCRIPTION);
        }
    }

    private static void validateEnglishNameRussian(String sheet, String column, WordData wordData) {
        validateLengthIsLessThan(sheet, column, wordData.getNameRussian(), wordData.getId(), 100);
        validateNotContains(sheet, column, wordData.getNameRussian(), wordData.getId(), List.of(";", "  "));
        validateRegexMatch(sheet, column, wordData.getNameRussian(), wordData.getId(), REGEX_RU);
    }

    private static void validateEnglishNameChinese(String sheet, String column, WordData wordData) {
        validateLengthIsLessThan(sheet, column, wordData.getNameChinese(), wordData.getId(), 19);
        validateNotContains(sheet, column, wordData.getNameChinese(), wordData.getId(), List.of(",", ";", "；", " "));
        validateRegexMatch(sheet, column, wordData.getNameChinese(), wordData.getId(), REGEX_CH);
    }

    private static void validateEnglishDefinition(String sheet, String column, WordData wordData) {
        validateStartsWithUpperCase(sheet, column, wordData.getDefinition(), wordData.getId());
        validateEndsWith(sheet, column, wordData.getDefinition(), wordData.getId(), ".");
        validateRegexMatch(sheet, column, wordData.getDefinition(), wordData.getId(), REGEX_EN);
    }

    private static void validateEnglishExamples(String sheet, String column, WordData wordData) {
        List<Map<String, String>> examplesList = extractExamples(sheet, column, wordData);
        if (examplesList == null) return;
        validateCollectionSize(sheet, column + ": Examples", examplesList, wordData.getId(), 5);

        for (Map<String, String> example : examplesList) {
            validateCollectionSize(sheet, column + ": Translations", example.keySet(), wordData.getId(), 3);

            String exampleCh = example.get("ch");
            validateNotEndsWith(sheet, column + ":CH", exampleCh, wordData.getId(), List.of(".", "?", "!"));
            validateRegexMatch(sheet, column + ":CH", exampleCh, wordData.getId(), REGEX_CH);

            String exampleEn = example.get("en");
            validateRegexMatch(sheet, column + ":EN", exampleEn, wordData.getId(), REGEX_EN);

            String exampleRu = example.get("ru");
            validateRegexMatch(sheet, column + ":RU", exampleRu, wordData.getId(), REGEX_RU);
        }
    }

    /**
     * First-level helper methods for CHINESE
     */

    private static void validateChineseNameChinese(String sheet, String column, WordData wordData) {
        validateLengthIsLessThan(sheet, column, wordData.getNameChinese(), wordData.getId(), 5);
        validateNotContains(sheet, column, wordData.getNameChinese(), wordData.getId(), List.of(",", ";", "；", " "));
        validateRegexMatch(sheet, column, wordData.getNameChinese(), wordData.getId(), REGEX_CH);
    }

    private static void validateChineseTranscription(String sheet, String column, WordData wordData) {
        validateTranscriptionChineseWordCount(sheet, column, wordData);
        validateRegexMatch(sheet, column, wordData.getTranscription(), wordData.getId(), REGEX_CH_PINYIN);
    }

    private static void validateChineseNameEnglish(String sheet, String column, WordData wordData) {
        validateLengthIsLessThan(sheet, column, wordData.getNameEnglish(), wordData.getId(), 100);
        validateNotContains(sheet, column, wordData.getNameEnglish(), wordData.getId(), List.of(";", "  "));

        if (!EXCEPTIONS_CH_EN.contains(wordData.getId())) {
            validateRegexMatch(sheet, column, wordData.getNameEnglish(), wordData.getId(), REGEX_EN);
        }
    }

    private static void validateChineseNameRussian(String sheet, String column, WordData wordData) {
        validateLengthIsLessThan(sheet, column, wordData.getNameRussian(), wordData.getId(), 100);
        validateNotContains(sheet, column, wordData.getNameEnglish(), wordData.getId(), List.of(";", "  "));

        if (!EXCEPTIONS_CH_RU.contains(wordData.getId())) {
            validateRegexMatch(sheet, column, wordData.getNameRussian(), wordData.getId(), REGEX_RU);
        }
    }

    private static void validateChineseDefinition(String sheet, String column, WordData wordData) {
        validateNotEndsWith(sheet, column, wordData.getDefinition(), wordData.getId(), List.of(".", "?", "!"));
        validateRegexMatch(sheet, column, wordData.getDefinition(), wordData.getId(), REGEX_CH);
    }

    private static void validateChineseExamples(String sheet, String column, WordData wordData) {
        List<Map<String, String>> examplesList = extractExamples(sheet, column, wordData);
        if (examplesList == null) return;
        validateCollectionSize(sheet, column + ": Examples", examplesList, wordData.getId(), 5);

        for (Map<String, String> example : examplesList) {
            validateCollectionSize(sheet, column + ": Translations", example.keySet(), wordData.getId(), 4);

            String exampleCh = example.get("ch");
            validateRegexMatch(sheet, column + ":CH", exampleCh, wordData.getId(), REGEX_CH);
            validateNotEndsWith(sheet, column + ":CH", exampleCh, wordData.getId(), List.of(".", "?", "!"));
            validateExampleChineseContainsWord(sheet, column + ":CH", exampleCh, wordData.getId(), wordData.getNameChinese());


            String examplePinyin = example.get("pinyin");
            validateRegexMatch(sheet, column + ":PY", examplePinyin, wordData.getId(), REGEX_CH_PINYIN);

            String exampleEn = example.get("en");
            validateRegexMatch(sheet, column + ":EN", exampleEn, wordData.getId(), REGEX_EN);

            String exampleRu = example.get("ru");
            validateRegexMatch(sheet, column + ":RU", exampleRu, wordData.getId(), REGEX_RU);
        }
    }

    /**
     * Second-level helper methods
     */

    private static void validateLengthIsLessThan(String sheet, String column, String value, Integer wordDataId, int length) {
        if (value.length() > length) {
            log.error("Excel validation: {} ({}): {}: length is {} but should be less than {}", sheet, column, wordDataId, value.length(), length);}
    }

    private static void validateStartsWith(String sheet, String column, String value, Integer wordDataId, String str) {
        if (!value.startsWith(str)) {
            log.error("Excel validation: {} ({}): {}: '{}' must start with '{}'", sheet, column, wordDataId, value, str);
        }
    }

    private static void validateEndsWith(String sheet, String column, String value, Integer wordDataId, String str) {
        if (!value.endsWith(str)) {
            log.error("Excel validation: {} ({}): {}: '{}' must end with '{}'", sheet, column, wordDataId, value, str);
        }
    }

    private static void validateNotEndsWith(String sheet, String column, String value, Integer wordDataId, List<String> list) {
        for (String el : list) {
            if (value.endsWith(el)) {
                log.error("Excel validation: {} ({}): {}: '{}' must not end with '{}'", sheet, column, wordDataId, value, el);
            }
        }
    }

    private static void validateStartsWithUpperCase(String sheet, String column, String value, Integer wordDataId) {
        if (!Character.isUpperCase(value.charAt(0))) {
            log.error("Excel validation: {} ({}): {}: '{}' must start with an uppercase letter", sheet, column, wordDataId, value);
        }
    }

    private static void validateNotContains(String sheet, String column, String value, Integer wordDataId, List<String> list) {
        for (String el : list) {
            if (value.contains(el)) {
                log.error("Excel validation: {} ({}): {}: '{}' must not contain '{}'", sheet, column, wordDataId, value, el);
            }
        }
    }

    private static void validateRegexMatch(String sheet, String column, String value, Integer wordDataId, String regex) {
        if (!Pattern.compile(regex).matcher(value).matches()) {
            log.error("Excel validation: {} ({}): {}: value '{}' does not match regex", sheet, column, wordDataId, value);
        }
    }

    private static void validateCollectionSize(String sheet, String column, Collection<?> collection, Integer wordDataId, int expectedSize) {
        if (collection.size() != expectedSize) {
            log.error("Excel validation: {} ({}): {}: expected collection size {}, but actual size is {}", sheet, column, wordDataId, expectedSize, collection.size());
        }
    }

    private static void validateTranscriptionChineseWordCount(String sheet, String column, WordData wordData) {
        if (EXCEPTIONS_CH_PINYIN.contains(wordData.getId())) return;

        int expectedCount = wordData.getNameChinese().length();
        int wordCount = wordData.getTranscription().split(" ").length;
        if (wordData.getTranscription().endsWith("r") &&
                (!wordData.getTranscription().endsWith("er") &&
                    !wordData.getTranscription().endsWith("ēr") &&
                    !wordData.getTranscription().endsWith("ér") &&
                    !wordData.getTranscription().endsWith("ěr") &&
                    !wordData.getTranscription().endsWith("èr"))
        ) {
            expectedCount -= 1;
        }
        if (wordCount != expectedCount) {
            log.error("Excel ({}): Validation failed ({}): {}: {}: {} ::: {}", sheet, column, wordData.getId(), wordData.getTranscription(), expectedCount, wordCount);
        }
    }

    private static void validateExampleChineseContainsWord(String sheet, String column, String exampleChinese, Integer wordDataId, String nameChinese) {
        String[] wordsChinese = nameChinese.split("，");
        boolean atLeastOneMatches = false;

        for (String word : wordsChinese) {
            boolean matches = true;
            for (int i = 0; i < word.length(); i++) {
                char ch = word.charAt(i);
                if (exampleChinese.indexOf(ch) == -1) {
                    matches = false;
                    break;
                }
            }
            if (matches) {
                atLeastOneMatches = true;
                break;
            }
        }

        if (!atLeastOneMatches) {
            log.error("Excel validation: {} ({}): {}: Example '{}' does not contain any word from '{}'", sheet, column, wordDataId, exampleChinese, nameChinese);
        }
    }

    private static List<Map<String, String>> extractExamples(String sheet, String column, WordData wordData) {
        try {
            return new ObjectMapper().readValue(wordData.getExamples(), List.class);
        } catch (Exception e) {
            log.error("Excel ({}): Validation failed ({}: JSON): {}", sheet, column, wordData.getId());
            return null;
        }
    }
}
