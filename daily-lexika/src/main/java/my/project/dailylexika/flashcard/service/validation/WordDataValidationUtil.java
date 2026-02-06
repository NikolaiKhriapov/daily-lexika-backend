package my.project.dailylexika.flashcard.service.validation;

import my.project.library.dailylexika.enumerations.Platform;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Collection;
import java.util.regex.Pattern;

public final class WordDataValidationUtil {

    private static final String REGEX_EXCEPTIONS = "PDF|CD|DVD|IT|DNA|RNA|GPS|IP|IQ|CPU|VIP|USВ-|ISO|Wi-Fi|QR-|WeChat|WeChat Pay|D|i|H2O";
    private static final String REGEX_CH = "^(?:(?:" + REGEX_EXCEPTIONS + "|T恤|维生素C|U盘)|[\\u4e00-\\u9fff0-9\\s“”《》，、。.…？！；：%–/（）【】$¥])+$";
    private static final String REGEX_CH_PINYIN = "^(?:(?:" + REGEX_EXCEPTIONS + "|U pán)|[a-zāáǎàēéěèīíǐìōóǒòūúǔùüǖǘǚǜ0-9\\s“”,.…?!;:’%–$¥])+$";
    private static final String REGEX_EN = "^[a-zA-Zé0-9\\s“”,.…?!;:+%’/()$¥&—–-]+$";
    private static final String REGEX_EN_TRANSCRIPTION = "^[\\p{L}\\p{M}ˈˌː().]+(?:\\s*[\\p{L}\\p{M}ˈˌː().]+)*$";
    private static final String REGEX_RU = "^(?:(?:" + REGEX_EXCEPTIONS + ")|[а-яА-ЯёЁ0-9\\s№«»,.…?!;:%/()$¥—–-])+$";

    private static final Pattern PATTERN_CH = Pattern.compile(REGEX_CH);
    private static final Pattern PATTERN_CH_PINYIN = Pattern.compile(REGEX_CH_PINYIN);
    private static final Pattern PATTERN_EN = Pattern.compile(REGEX_EN);
    private static final Pattern PATTERN_EN_TRANSCRIPTION = Pattern.compile(REGEX_EN_TRANSCRIPTION);
    private static final Pattern PATTERN_RU = Pattern.compile(REGEX_RU);
    private static final Pattern PATTERN_WHITESPACE = Pattern.compile("\\s");

    public static final int MAX_NAME_ENGLISH_LENGTH = 30;
    public static final int MAX_NAME_RUSSIAN_LENGTH = 100;
    public static final int MAX_NAME_CHINESE_EN_PLATFORM_LENGTH = 19;
    public static final int MAX_NAME_CHINESE_CH_PLATFORM_LENGTH = 5;
    public static final int MAX_NAME_ENGLISH_CH_PLATFORM_LENGTH = 100;
    public static final int MAX_DEFINITION_LENGTH = 250;
    public static final int MAX_EXAMPLE_LENGTH = 250;
    public static final int MAX_TRANSCRIPTION_LENGTH = 50;

    private WordDataValidationUtil() {
    }

    public static List<String> validate(ValidationInput input) {
        List<String> errors = new ArrayList<>();
        switch (input.platform()) {
            case ENGLISH -> validateEnglish(input, errors);
            case CHINESE -> validateChinese(input, errors);
        }
        return errors;
    }

    private static void validateEnglish(ValidationInput input, List<String> errors) {
        EnumSet<Field> fields = input.fields();
        if (fields.contains(Field.NAME_ENGLISH)) {
            validateLengthIsLessThan("nameEnglish", input.nameEnglish(), MAX_NAME_ENGLISH_LENGTH, errors);
            validateNotContainsWhitespace("nameEnglish", input.nameEnglish(), errors);
            validateRegexMatch("nameEnglish", input.nameEnglish(), PATTERN_EN, errors);
        }
        if (fields.contains(Field.TRANSCRIPTION)) {
            String transcription = input.transcription();
            if (transcription == null) {
                errors.add("transcription must be provided");
            } else {
                validateLengthIsLessThan("transcription", transcription, MAX_TRANSCRIPTION_LENGTH, errors);
                validateStartsWith("transcription", transcription, "/", errors);
                validateEndsWith("transcription", transcription, "/", errors);
                validateNotContains("transcription", transcription, List.of("/ /"), errors);
                if (transcription.contains(" ,")) {
                    errors.add("transcription must not contain spaces before comma");
                }
                String normalized = transcription.replaceAll("/", "");
                String[] variants = normalized.split(", ");
                for (String variant : variants) {
                    if (variant.startsWith(" ") || variant.endsWith(" ") || variant.contains("  ")) {
                        errors.add("transcription must not contain extra spaces");
                    }
                    validateRegexMatch("transcription", variant, PATTERN_EN_TRANSCRIPTION, errors);
                }
            }
        }
        if (fields.contains(Field.NAME_RUSSIAN)) {
            validateLengthIsLessThan("nameRussian", input.nameRussian(), MAX_NAME_RUSSIAN_LENGTH, errors);
            validateNotContains("nameRussian", input.nameRussian(), List.of(";", "  "), errors);
            validateRegexMatch("nameRussian", input.nameRussian(), PATTERN_RU, errors);
        }
        if (fields.contains(Field.NAME_CHINESE)) {
            validateLengthIsLessThan("nameChinese", input.nameChinese(), MAX_NAME_CHINESE_EN_PLATFORM_LENGTH, errors);
            validateNotContains("nameChinese", input.nameChinese(), List.of(",", ";", "；", " "), errors);
            validateRegexMatch("nameChinese", input.nameChinese(), PATTERN_CH, errors);
        }
        if (fields.contains(Field.DEFINITION)) {
            validateLengthIsLessThan("definition", input.definition(), MAX_DEFINITION_LENGTH, errors);
            validateStartsWithUpperCase("definition", input.definition(), errors);
            validateEndsWith("definition", input.definition(), ".", errors);
            validateRegexMatch("definition", input.definition(), PATTERN_EN, errors);
        }
        if (fields.contains(Field.EXAMPLES)) {
            validateEnglishExamples(input, errors);
        }
    }

    private static void validateChinese(ValidationInput input, List<String> errors) {
        EnumSet<Field> fields = input.fields();
        if (fields.contains(Field.NAME_CHINESE)) {
            validateLengthIsLessThan("nameChinese", input.nameChinese(), MAX_NAME_CHINESE_CH_PLATFORM_LENGTH, errors);
            validateNotContains("nameChinese", input.nameChinese(), List.of(",", ";", "；", " "), errors);
            validateRegexMatch("nameChinese", input.nameChinese(), PATTERN_CH, errors);
        }
        if (fields.contains(Field.TRANSCRIPTION) || fields.contains(Field.NAME_CHINESE)) {
            validateTranscriptionChineseWordCount(input, errors);
            validateRegexMatch("transcription", input.transcription(), PATTERN_CH_PINYIN, errors);
        }
        if (fields.contains(Field.NAME_ENGLISH)) {
            validateLengthIsLessThan("nameEnglish", input.nameEnglish(), MAX_NAME_ENGLISH_CH_PLATFORM_LENGTH, errors);
            validateNotContains("nameEnglish", input.nameEnglish(), List.of(";", "  "), errors);
            validateRegexMatch("nameEnglish", input.nameEnglish(), PATTERN_EN, errors);
        }
        if (fields.contains(Field.NAME_RUSSIAN)) {
            validateLengthIsLessThan("nameRussian", input.nameRussian(), MAX_NAME_RUSSIAN_LENGTH, errors);
            validateNotContains("nameRussian", input.nameRussian(), List.of(";", "  "), errors);
            validateRegexMatch("nameRussian", input.nameRussian(), PATTERN_RU, errors);
        }
        if (fields.contains(Field.DEFINITION)) {
            validateLengthIsLessThan("definition", input.definition(), MAX_DEFINITION_LENGTH, errors);
            validateNotEndsWith("definition", input.definition(), List.of(".", "?", "!"), errors);
            validateRegexMatch("definition", input.definition(), PATTERN_CH, errors);
        }
        if (fields.contains(Field.EXAMPLES) || fields.contains(Field.NAME_CHINESE)) {
            validateChineseExamples(input, errors);
        }
    }

    private static void validateEnglishExamples(ValidationInput input, List<String> errors) {
        List<Map<String, String>> examplesList = input.examples();
        if (examplesList == null) {
            errors.add("examples must be provided");
            return;
        }
        validateCollectionSize("examples", examplesList, 5, errors);

        for (Map<String, String> example : examplesList) {
            validateCollectionSize("examples.translations", example.keySet(), 3, errors);

            String exampleCh = requiredExampleValue(example, "ch", errors);
            if (exampleCh != null) {
                validateLengthIsLessThan("examples.ch", exampleCh, MAX_EXAMPLE_LENGTH, errors);
                validateNotEndsWith("examples.ch", exampleCh, List.of(".", "?", "!"), errors);
                validateRegexMatch("examples.ch", exampleCh, PATTERN_CH, errors);
            }

            String exampleEn = requiredExampleValue(example, "en", errors);
            if (exampleEn != null) {
                validateLengthIsLessThan("examples.en", exampleEn, MAX_EXAMPLE_LENGTH, errors);
                validateRegexMatch("examples.en", exampleEn, PATTERN_EN, errors);
            }

            String exampleRu = requiredExampleValue(example, "ru", errors);
            if (exampleRu != null) {
                validateLengthIsLessThan("examples.ru", exampleRu, MAX_EXAMPLE_LENGTH, errors);
                validateRegexMatch("examples.ru", exampleRu, PATTERN_RU, errors);
            }
        }
    }

    private static void validateChineseExamples(ValidationInput input, List<String> errors) {
        List<Map<String, String>> examplesList = input.examples();
        if (examplesList == null) {
            errors.add("examples must be provided");
            return;
        }
        validateCollectionSize("examples", examplesList, 5, errors);

        for (Map<String, String> example : examplesList) {
            validateCollectionSize("examples.translations", example.keySet(), 4, errors);

            String exampleCh = requiredExampleValue(example, "ch", errors);
            if (exampleCh != null) {
                validateLengthIsLessThan("examples.ch", exampleCh, MAX_EXAMPLE_LENGTH, errors);
                validateRegexMatch("examples.ch", exampleCh, PATTERN_CH, errors);
                validateNotEndsWith("examples.ch", exampleCh, List.of(".", "?", "!"), errors);
                validateExampleChineseContainsWord("examples.ch", exampleCh, input.nameChinese(), errors);
            }

            String examplePinyin = requiredExampleValue(example, "pinyin", errors);
            if (examplePinyin != null) {
                validateLengthIsLessThan("examples.pinyin", examplePinyin, MAX_EXAMPLE_LENGTH, errors);
                validateRegexMatch("examples.pinyin", examplePinyin, PATTERN_CH_PINYIN, errors);
            }

            String exampleEn = requiredExampleValue(example, "en", errors);
            if (exampleEn != null) {
                validateLengthIsLessThan("examples.en", exampleEn, MAX_EXAMPLE_LENGTH, errors);
                validateRegexMatch("examples.en", exampleEn, PATTERN_EN, errors);
            }

            String exampleRu = requiredExampleValue(example, "ru", errors);
            if (exampleRu != null) {
                validateLengthIsLessThan("examples.ru", exampleRu, MAX_EXAMPLE_LENGTH, errors);
                validateRegexMatch("examples.ru", exampleRu, PATTERN_RU, errors);
            }
        }
    }

    private static String requiredExampleValue(Map<String, String> example, String key, List<String> errors) {
        String value = example.get(key);
        if (value == null || value.isBlank()) {
            errors.add("examples." + key + " must be provided");
            return null;
        }
        return value;
    }

    private static void validateLengthIsLessThan(String field, String value, int length, List<String> errors) {
        if (value == null) {
            errors.add(field + " must be provided");
            return;
        }
        if (value.length() > length) {
            errors.add(field + " length must be <= " + length);
        }
    }

    private static void validateStartsWith(String field, String value, String str, List<String> errors) {
        if (value == null) {
            errors.add(field + " must be provided");
            return;
        }
        if (!value.startsWith(str)) {
            errors.add(field + " must start with '" + str + "'");
        }
    }

    private static void validateEndsWith(String field, String value, String str, List<String> errors) {
        if (value == null) {
            errors.add(field + " must be provided");
            return;
        }
        if (!value.endsWith(str)) {
            errors.add(field + " must end with '" + str + "'");
        }
    }

    private static void validateNotEndsWith(String field, String value, List<String> list, List<String> errors) {
        if (value == null) {
            errors.add(field + " must be provided");
            return;
        }
        for (String el : list) {
            if (value.endsWith(el)) {
                errors.add(field + " must not end with '" + el + "'");
            }
        }
    }

    private static void validateStartsWithUpperCase(String field, String value, List<String> errors) {
        if (value == null) {
            errors.add(field + " must be provided");
            return;
        }
        if (value.isEmpty() || !Character.isUpperCase(value.charAt(0))) {
            errors.add(field + " must start with an uppercase letter");
        }
    }

    private static void validateNotContains(String field, String value, List<String> list, List<String> errors) {
        if (value == null) {
            errors.add(field + " must be provided");
            return;
        }
        for (String el : list) {
            if (value.contains(el)) {
                errors.add(field + " must not contain '" + el + "'");
            }
        }
    }

    private static void validateNotContainsWhitespace(String field, String value, List<String> errors) {
        if (value == null) {
            errors.add(field + " must be provided");
            return;
        }
        if (PATTERN_WHITESPACE.matcher(value).find()) {
            errors.add(field + " must not contain whitespace");
        }
    }

    private static void validateRegexMatch(String field, String value, Pattern pattern, List<String> errors) {
        if (value == null) {
            errors.add(field + " must be provided");
            return;
        }
        if (!pattern.matcher(value).matches()) {
            errors.add(field + " contains invalid characters");
        }
    }

    private static void validateCollectionSize(String field, Collection<?> collection, int expectedSize, List<String> errors) {
        if (collection.size() != expectedSize) {
            errors.add(field + " must contain " + expectedSize + " items");
        }
    }

    private static void validateTranscriptionChineseWordCount(ValidationInput input, List<String> errors) {
        if (input.transcription() == null || input.nameChinese() == null) {
            errors.add("transcription and nameChinese must be provided");
            return;
        }
        int expectedCount = input.nameChinese().length();
        int wordCount = input.transcription().split(" ").length;
        if (input.transcription().endsWith("r") &&
                (!input.transcription().endsWith("er") &&
                        !input.transcription().endsWith("ēr") &&
                        !input.transcription().endsWith("ér") &&
                        !input.transcription().endsWith("ěr") &&
                        !input.transcription().endsWith("èr"))
        ) {
            expectedCount -= 1;
        }
        if (wordCount != expectedCount) {
            errors.add("transcription word count must match nameChinese length");
        }
    }

    private static void validateExampleChineseContainsWord(String field, String exampleChinese, String nameChinese, List<String> errors) {
        if (nameChinese == null) {
            errors.add("nameChinese must be provided");
            return;
        }
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
            errors.add(field + " must contain a word from nameChinese");
        }
    }

    public enum Field {
        NAME_CHINESE,
        TRANSCRIPTION,
        NAME_ENGLISH,
        NAME_RUSSIAN,
        DEFINITION,
        EXAMPLES
    }

    public record ValidationInput(
            Integer id,
            Platform platform,
            String nameChinese,
            String transcription,
            String nameEnglish,
            String nameRussian,
            String definition,
            List<Map<String, String>> examples,
            EnumSet<Field> fields,
            boolean examplesProvided
    ) {
    }
}
