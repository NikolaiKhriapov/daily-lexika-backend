package my.project.dailylexika.flashcard.service.validation;

import my.project.dailylexika.config.AbstractUnitTest;
import my.project.dailylexika.flashcard.service.validation.WordDataValidationUtil.Field;
import my.project.dailylexika.flashcard.service.validation.WordDataValidationUtil.ValidationInput;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static my.project.library.dailylexika.enumerations.Platform.CHINESE;
import static my.project.library.dailylexika.enumerations.Platform.ENGLISH;
import static my.project.dailylexika.flashcard.service.validation.WordDataValidationUtil.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;

class WordDataValidationUtilTest extends AbstractUnitTest {

    private static final String ENGLISH_CORRECT_NAME_ENGLISH = "hello";
    private static final String ENGLISH_CORRECT_NAME_CHINESE = "你好";
    private static final String ENGLISH_CORRECT_NAME_RUSSIAN = "Привет";
    private static final String ENGLISH_CORRECT_TRANSCRIPTION = "/word/";
    private static final String ENGLISH_CORRECT_DEFINITION = "Hello.";
    private static final String ENGLISH_CORRECT_EXAMPLE_CH = "你好";
    private static final String ENGLISH_CORRECT_EXAMPLE_EN = "Hello";
    private static final String ENGLISH_CORRECT_EXAMPLE_RU = "Привет";

    private static final String CHINESE_CORRECT_NAME_CHINESE = "你好";
    private static final String CHINESE_CORRECT_TRANSCRIPTION = "ni hao";
    private static final String CHINESE_CORRECT_NAME_ENGLISH = "hello world";
    private static final String CHINESE_CORRECT_NAME_RUSSIAN = "Привет";
    private static final String CHINESE_CORRECT_DEFINITION = "你好";
    private static final String CHINESE_CORRECT_EXAMPLE_CH = "你好";
    private static final String CHINESE_CORRECT_EXAMPLE_PINYIN = "ni hao";
    private static final String CHINESE_CORRECT_EXAMPLE_EN = "Hello";
    private static final String CHINESE_CORRECT_EXAMPLE_RU = "Привет";

    @ParameterizedTest
    @MethodSource("my.project.dailylexika.flashcard.service.validation.WordDataValidationUtilTest$TestDataSource#validate_returnsNoErrors")
    void validate_returnsNoErrors(ValidationInput input) {
        // When
        List<String> errors = WordDataValidationUtil.validate(input);

        // Then
        assertThat(errors).isEmpty();
    }

    @ParameterizedTest
    @MethodSource("my.project.dailylexika.flashcard.service.validation.WordDataValidationUtilTest$TestDataSource#validate_respectsSelectedFields")
    void validate_respectsSelectedFields(ValidationInput input) {
        // When
        List<String> errors = WordDataValidationUtil.validate(input);

        // Then
        assertThat(errors).isEmpty();
    }

    @ParameterizedTest
    @MethodSource("my.project.dailylexika.flashcard.service.validation.WordDataValidationUtilTest$TestDataSource#validate_allowsChineseTranscriptionWithRRule")
    void validate_allowsChineseTranscriptionWithRRule(ValidationInput input) {
        // When
        List<String> errors = WordDataValidationUtil.validate(input);

        // Then
        assertThat(errors).isEmpty();
    }

    @ParameterizedTest
    @MethodSource("my.project.dailylexika.flashcard.service.validation.WordDataValidationUtilTest$TestDataSource#validate_english_returnsErrors")
    void validate_english_returnsErrors(ValidationInput input) {
        // When
        List<String> errors = WordDataValidationUtil.validate(input);

        // Then
        assertThat(errors).isNotEmpty();
    }

    @ParameterizedTest
    @MethodSource("my.project.dailylexika.flashcard.service.validation.WordDataValidationUtilTest$TestDataSource#validate_chinese_returnsErrors")
    void validate_chinese_returnsErrors(ValidationInput input) {
        // When
        List<String> errors = WordDataValidationUtil.validate(input);

        // Then
        assertThat(errors).isNotEmpty();
    }

    private static class TestDataSource {

        public static Stream<Arguments> validate_returnsNoErrors() {
            String englishMaxName = "a".repeat(MAX_NAME_ENGLISH_LENGTH);
            String englishMaxTranscription = "/" + "a".repeat(MAX_TRANSCRIPTION_LENGTH - 2) + "/";
            String englishMaxChinese = "你".repeat(MAX_NAME_CHINESE_EN_PLATFORM_LENGTH);
            String englishMaxRussian = "а".repeat(MAX_NAME_RUSSIAN_LENGTH);
            String englishMaxDefinition = "A" + "a".repeat(MAX_DEFINITION_LENGTH - 2) + ".";
            String englishMaxExampleCh = "你".repeat(MAX_EXAMPLE_LENGTH);
            String englishMaxExampleEn = "A" + "a".repeat(MAX_EXAMPLE_LENGTH - 1);
            String englishMaxExampleRu = "а".repeat(MAX_EXAMPLE_LENGTH);

            String englishMinName = "a";
            String englishMinTranscription = "/a/";
            String englishMinChinese = "你";
            String englishMinRussian = "а";
            String englishMinDefinition = "A.";
            String englishMinExampleCh = "你";
            String englishMinExampleEn = "A";
            String englishMinExampleRu = "а";

            String chineseMaxName = "你".repeat(MAX_NAME_CHINESE_CH_PLATFORM_LENGTH);
            String chineseMaxTranscription = "ni ni ni ni ni";
            String chineseMaxEnglish = "a".repeat(MAX_NAME_ENGLISH_CH_PLATFORM_LENGTH);
            String chineseMaxRussian = "а".repeat(MAX_NAME_RUSSIAN_LENGTH);
            String chineseMaxDefinition = "你".repeat(MAX_DEFINITION_LENGTH);
            String chineseMaxExampleCh = "你好".repeat(MAX_EXAMPLE_LENGTH / 2);
            String chineseMaxExamplePinyin = "a".repeat(MAX_EXAMPLE_LENGTH);
            String chineseMaxExampleEn = "A" + "a".repeat(MAX_EXAMPLE_LENGTH - 1);
            String chineseMaxExampleRu = "а".repeat(MAX_EXAMPLE_LENGTH);

            String chineseMinName = "你";
            String chineseMinTranscription = "ni";
            String chineseMinEnglish = "a";
            String chineseMinRussian = "а";
            String chineseMinDefinition = "你";
            String chineseMinExampleCh = "你";
            String chineseMinExamplePinyin = "ni";
            String chineseMinExampleEn = "A";
            String chineseMinExampleRu = "а";

            return Stream.of(
                    arguments(new ValidationInput(1, ENGLISH, ENGLISH_CORRECT_NAME_CHINESE, ENGLISH_CORRECT_TRANSCRIPTION, ENGLISH_CORRECT_NAME_ENGLISH, ENGLISH_CORRECT_NAME_RUSSIAN, ENGLISH_CORRECT_DEFINITION, englishExamples(), EnumSet.allOf(Field.class), true)),
                    arguments(new ValidationInput(1, ENGLISH, englishMaxChinese, ENGLISH_CORRECT_TRANSCRIPTION, englishMaxName, englishMaxRussian, ENGLISH_CORRECT_DEFINITION, englishExamples(), EnumSet.allOf(Field.class), true)),
                    arguments(new ValidationInput(1, ENGLISH, ENGLISH_CORRECT_NAME_CHINESE, "/ˈwɜːd/, /wɝd/", "word", ENGLISH_CORRECT_NAME_RUSSIAN, ENGLISH_CORRECT_DEFINITION, englishExamples(), EnumSet.allOf(Field.class), true)),
                    arguments(new ValidationInput(1, ENGLISH, ENGLISH_CORRECT_NAME_CHINESE, ENGLISH_CORRECT_TRANSCRIPTION, ENGLISH_CORRECT_NAME_ENGLISH, ENGLISH_CORRECT_NAME_RUSSIAN, englishMaxDefinition, englishExamplesWithValues(englishMaxExampleCh, englishMaxExampleEn, englishMaxExampleRu), EnumSet.allOf(Field.class), true)),
                    arguments(new ValidationInput(1, ENGLISH, englishMinChinese, englishMinTranscription, englishMinName, englishMinRussian, englishMinDefinition, englishExamplesWithValues(englishMinExampleCh, englishMinExampleEn, englishMinExampleRu), EnumSet.allOf(Field.class), true)),
                    arguments(new ValidationInput(1, ENGLISH, ENGLISH_CORRECT_NAME_CHINESE, englishMaxTranscription, ENGLISH_CORRECT_NAME_ENGLISH, ENGLISH_CORRECT_NAME_RUSSIAN, ENGLISH_CORRECT_DEFINITION, englishExamples(), EnumSet.allOf(Field.class), true)),
                    arguments(new ValidationInput(1, CHINESE, CHINESE_CORRECT_NAME_CHINESE, CHINESE_CORRECT_TRANSCRIPTION, CHINESE_CORRECT_NAME_ENGLISH, CHINESE_CORRECT_NAME_RUSSIAN, CHINESE_CORRECT_DEFINITION, chineseExamples(CHINESE_CORRECT_NAME_CHINESE), EnumSet.allOf(Field.class), true)),
                    arguments(new ValidationInput(1, CHINESE, chineseMaxName, chineseMaxTranscription, chineseMaxEnglish, chineseMaxRussian, chineseMaxDefinition, chineseExamplesWithValues(chineseMaxExampleCh, chineseMaxExamplePinyin, chineseMaxExampleEn, chineseMaxExampleRu), EnumSet.allOf(Field.class), true)),
                    arguments(new ValidationInput(1, CHINESE, chineseMinName, chineseMinTranscription, chineseMinEnglish, chineseMinRussian, chineseMinDefinition, chineseExamplesWithValues(chineseMinExampleCh, chineseMinExamplePinyin, chineseMinExampleEn, chineseMinExampleRu), EnumSet.allOf(Field.class), true))
            );
        }

        public static Stream<Arguments> validate_respectsSelectedFields() {
            return Stream.of(
                    arguments(new ValidationInput(1, ENGLISH, ENGLISH_CORRECT_NAME_CHINESE, ENGLISH_CORRECT_TRANSCRIPTION, "bad name", ENGLISH_CORRECT_NAME_RUSSIAN, ENGLISH_CORRECT_DEFINITION, englishExamples(), EnumSet.of(Field.NAME_RUSSIAN), true)),
                    arguments(new ValidationInput(1, CHINESE, null, CHINESE_CORRECT_TRANSCRIPTION, CHINESE_CORRECT_NAME_ENGLISH, CHINESE_CORRECT_NAME_RUSSIAN, CHINESE_CORRECT_DEFINITION, chineseExamples(CHINESE_CORRECT_NAME_CHINESE), EnumSet.of(Field.NAME_ENGLISH), true))
            );
        }

        public static Stream<Arguments> validate_allowsChineseTranscriptionWithRRule() {
            return Stream.of(
                    arguments(new ValidationInput(1, CHINESE, "花儿", "huar", CHINESE_CORRECT_NAME_ENGLISH, CHINESE_CORRECT_NAME_RUSSIAN, CHINESE_CORRECT_DEFINITION, chineseExamples("花儿"), EnumSet.allOf(Field.class), true))
            );
        }

        public static Stream<Arguments> validate_english_returnsErrors() {
            Map<String, String> example = englishExample();
            return Stream.of(
                    arguments(new ValidationInput(1, ENGLISH, ENGLISH_CORRECT_NAME_CHINESE, ENGLISH_CORRECT_TRANSCRIPTION, null, ENGLISH_CORRECT_NAME_RUSSIAN, ENGLISH_CORRECT_DEFINITION, englishExamples(), EnumSet.allOf(Field.class), true)),
                    arguments(new ValidationInput(1, ENGLISH, ENGLISH_CORRECT_NAME_CHINESE, ENGLISH_CORRECT_TRANSCRIPTION, "a".repeat(MAX_NAME_ENGLISH_LENGTH + 1), ENGLISH_CORRECT_NAME_RUSSIAN, ENGLISH_CORRECT_DEFINITION, englishExamples(), EnumSet.allOf(Field.class), true)),
                    arguments(new ValidationInput(1, ENGLISH, ENGLISH_CORRECT_NAME_CHINESE, ENGLISH_CORRECT_TRANSCRIPTION, "bad name", ENGLISH_CORRECT_NAME_RUSSIAN, ENGLISH_CORRECT_DEFINITION, englishExamples(), EnumSet.allOf(Field.class), true)),
                    arguments(new ValidationInput(1, ENGLISH, ENGLISH_CORRECT_NAME_CHINESE, ENGLISH_CORRECT_TRANSCRIPTION, "hello ", ENGLISH_CORRECT_NAME_RUSSIAN, ENGLISH_CORRECT_DEFINITION, englishExamples(), EnumSet.allOf(Field.class), true)),
                    arguments(new ValidationInput(1, ENGLISH, ENGLISH_CORRECT_NAME_CHINESE, ENGLISH_CORRECT_TRANSCRIPTION, " hello", ENGLISH_CORRECT_NAME_RUSSIAN, ENGLISH_CORRECT_DEFINITION, englishExamples(), EnumSet.allOf(Field.class), true)),
                    arguments(new ValidationInput(1, ENGLISH, ENGLISH_CORRECT_NAME_CHINESE, ENGLISH_CORRECT_TRANSCRIPTION, "bad_name", ENGLISH_CORRECT_NAME_RUSSIAN, ENGLISH_CORRECT_DEFINITION, englishExamples(), EnumSet.allOf(Field.class), true)),
                    arguments(new ValidationInput(1, ENGLISH, ENGLISH_CORRECT_NAME_CHINESE, null, ENGLISH_CORRECT_NAME_ENGLISH, ENGLISH_CORRECT_NAME_RUSSIAN, ENGLISH_CORRECT_DEFINITION, englishExamples(), EnumSet.allOf(Field.class), true)),
                    arguments(new ValidationInput(1, ENGLISH, ENGLISH_CORRECT_NAME_CHINESE, "word/", ENGLISH_CORRECT_NAME_ENGLISH, ENGLISH_CORRECT_NAME_RUSSIAN, ENGLISH_CORRECT_DEFINITION, englishExamples(), EnumSet.allOf(Field.class), true)),
                    arguments(new ValidationInput(1, ENGLISH, ENGLISH_CORRECT_NAME_CHINESE, "/word", ENGLISH_CORRECT_NAME_ENGLISH, ENGLISH_CORRECT_NAME_RUSSIAN, ENGLISH_CORRECT_DEFINITION, englishExamples(), EnumSet.allOf(Field.class), true)),
                    arguments(new ValidationInput(1, ENGLISH, ENGLISH_CORRECT_NAME_CHINESE, "/ /", ENGLISH_CORRECT_NAME_ENGLISH, ENGLISH_CORRECT_NAME_RUSSIAN, ENGLISH_CORRECT_DEFINITION, englishExamples(), EnumSet.allOf(Field.class), true)),
                    arguments(new ValidationInput(1, ENGLISH, ENGLISH_CORRECT_NAME_CHINESE, "/w0rd/", ENGLISH_CORRECT_NAME_ENGLISH, ENGLISH_CORRECT_NAME_RUSSIAN, ENGLISH_CORRECT_DEFINITION, englishExamples(), EnumSet.allOf(Field.class), true)),
                    arguments(new ValidationInput(1, ENGLISH, ENGLISH_CORRECT_NAME_CHINESE, "/wɜːd/ , /wɝd/", ENGLISH_CORRECT_NAME_ENGLISH, ENGLISH_CORRECT_NAME_RUSSIAN, ENGLISH_CORRECT_DEFINITION, englishExamples(), EnumSet.allOf(Field.class), true)),
                    arguments(new ValidationInput(1, ENGLISH, ENGLISH_CORRECT_NAME_CHINESE, "/wɜːd  /", ENGLISH_CORRECT_NAME_ENGLISH, ENGLISH_CORRECT_NAME_RUSSIAN, ENGLISH_CORRECT_DEFINITION, englishExamples(), EnumSet.allOf(Field.class), true)),
                    arguments(new ValidationInput(1, ENGLISH, ENGLISH_CORRECT_NAME_CHINESE, "/" + "a".repeat(MAX_TRANSCRIPTION_LENGTH - 1) + "/", ENGLISH_CORRECT_NAME_ENGLISH, ENGLISH_CORRECT_NAME_RUSSIAN, ENGLISH_CORRECT_DEFINITION, englishExamples(), EnumSet.allOf(Field.class), true)),
                    arguments(new ValidationInput(1, ENGLISH, ENGLISH_CORRECT_NAME_CHINESE, ENGLISH_CORRECT_TRANSCRIPTION, ENGLISH_CORRECT_NAME_ENGLISH, null, ENGLISH_CORRECT_DEFINITION, englishExamples(), EnumSet.allOf(Field.class), true)),
                    arguments(new ValidationInput(1, ENGLISH, ENGLISH_CORRECT_NAME_CHINESE, ENGLISH_CORRECT_TRANSCRIPTION, ENGLISH_CORRECT_NAME_ENGLISH, "a".repeat(MAX_NAME_RUSSIAN_LENGTH + 1), ENGLISH_CORRECT_DEFINITION, englishExamples(), EnumSet.allOf(Field.class), true)),
                    arguments(new ValidationInput(1, ENGLISH, ENGLISH_CORRECT_NAME_CHINESE, ENGLISH_CORRECT_TRANSCRIPTION, ENGLISH_CORRECT_NAME_ENGLISH, ENGLISH_CORRECT_NAME_RUSSIAN + ";", ENGLISH_CORRECT_DEFINITION, englishExamples(), EnumSet.allOf(Field.class), true)),
                    arguments(new ValidationInput(1, ENGLISH, ENGLISH_CORRECT_NAME_CHINESE, ENGLISH_CORRECT_TRANSCRIPTION, ENGLISH_CORRECT_NAME_ENGLISH, ENGLISH_CORRECT_NAME_RUSSIAN + "  " + ENGLISH_CORRECT_NAME_RUSSIAN, ENGLISH_CORRECT_DEFINITION, englishExamples(), EnumSet.allOf(Field.class), true)),
                    arguments(new ValidationInput(1, ENGLISH, ENGLISH_CORRECT_NAME_CHINESE, ENGLISH_CORRECT_TRANSCRIPTION, ENGLISH_CORRECT_NAME_ENGLISH, "hello", ENGLISH_CORRECT_DEFINITION, englishExamples(), EnumSet.allOf(Field.class), true)),
                    arguments(new ValidationInput(1, ENGLISH, null, ENGLISH_CORRECT_TRANSCRIPTION, ENGLISH_CORRECT_NAME_ENGLISH, ENGLISH_CORRECT_NAME_RUSSIAN, ENGLISH_CORRECT_DEFINITION, englishExamples(), EnumSet.allOf(Field.class), true)),
                    arguments(new ValidationInput(1, ENGLISH, "你".repeat(MAX_NAME_CHINESE_EN_PLATFORM_LENGTH + 1), ENGLISH_CORRECT_TRANSCRIPTION, ENGLISH_CORRECT_NAME_ENGLISH, ENGLISH_CORRECT_NAME_RUSSIAN, ENGLISH_CORRECT_DEFINITION, englishExamples(), EnumSet.allOf(Field.class), true)),
                    arguments(new ValidationInput(1, ENGLISH, ENGLISH_CORRECT_NAME_CHINESE + " ", ENGLISH_CORRECT_TRANSCRIPTION, ENGLISH_CORRECT_NAME_ENGLISH, ENGLISH_CORRECT_NAME_RUSSIAN, ENGLISH_CORRECT_DEFINITION, englishExamples(), EnumSet.allOf(Field.class), true)),
                    arguments(new ValidationInput(1, ENGLISH, "hello", ENGLISH_CORRECT_TRANSCRIPTION, ENGLISH_CORRECT_NAME_ENGLISH, ENGLISH_CORRECT_NAME_RUSSIAN, ENGLISH_CORRECT_DEFINITION, englishExamples(), EnumSet.allOf(Field.class), true)),
                    arguments(new ValidationInput(1, ENGLISH, ENGLISH_CORRECT_NAME_CHINESE, ENGLISH_CORRECT_TRANSCRIPTION, ENGLISH_CORRECT_NAME_ENGLISH, ENGLISH_CORRECT_NAME_RUSSIAN, null, englishExamples(), EnumSet.allOf(Field.class), true)),
                    arguments(new ValidationInput(1, ENGLISH, ENGLISH_CORRECT_NAME_CHINESE, ENGLISH_CORRECT_TRANSCRIPTION, ENGLISH_CORRECT_NAME_ENGLISH, ENGLISH_CORRECT_NAME_RUSSIAN, "hello.", englishExamples(), EnumSet.allOf(Field.class), true)),
                    arguments(new ValidationInput(1, ENGLISH, ENGLISH_CORRECT_NAME_CHINESE, ENGLISH_CORRECT_TRANSCRIPTION, ENGLISH_CORRECT_NAME_ENGLISH, ENGLISH_CORRECT_NAME_RUSSIAN, "Hello?", englishExamples(), EnumSet.allOf(Field.class), true)),
                    arguments(new ValidationInput(1, ENGLISH, ENGLISH_CORRECT_NAME_CHINESE, ENGLISH_CORRECT_TRANSCRIPTION, ENGLISH_CORRECT_NAME_ENGLISH, ENGLISH_CORRECT_NAME_RUSSIAN, "Hello@.", englishExamples(), EnumSet.allOf(Field.class), true)),
                    arguments(new ValidationInput(1, ENGLISH, ENGLISH_CORRECT_NAME_CHINESE, ENGLISH_CORRECT_TRANSCRIPTION, ENGLISH_CORRECT_NAME_ENGLISH, ENGLISH_CORRECT_NAME_RUSSIAN, "A" + "a".repeat(MAX_DEFINITION_LENGTH - 1) + ".", englishExamples(), EnumSet.allOf(Field.class), true)),
                    arguments(new ValidationInput(1, ENGLISH, ENGLISH_CORRECT_NAME_CHINESE, ENGLISH_CORRECT_TRANSCRIPTION, ENGLISH_CORRECT_NAME_ENGLISH, ENGLISH_CORRECT_NAME_RUSSIAN, ENGLISH_CORRECT_DEFINITION, null, EnumSet.allOf(Field.class), false)),
                    arguments(new ValidationInput(1, ENGLISH, ENGLISH_CORRECT_NAME_CHINESE, ENGLISH_CORRECT_TRANSCRIPTION, ENGLISH_CORRECT_NAME_ENGLISH, ENGLISH_CORRECT_NAME_RUSSIAN, ENGLISH_CORRECT_DEFINITION, List.of(example, example, example, example), EnumSet.allOf(Field.class), true)),
                    arguments(new ValidationInput(1, ENGLISH, ENGLISH_CORRECT_NAME_CHINESE, ENGLISH_CORRECT_TRANSCRIPTION, ENGLISH_CORRECT_NAME_ENGLISH, ENGLISH_CORRECT_NAME_RUSSIAN, ENGLISH_CORRECT_DEFINITION, List.of(example, example, example, example, example, example), EnumSet.allOf(Field.class), true)),
                    arguments(new ValidationInput(1, ENGLISH, ENGLISH_CORRECT_NAME_CHINESE, ENGLISH_CORRECT_TRANSCRIPTION, ENGLISH_CORRECT_NAME_ENGLISH, ENGLISH_CORRECT_NAME_RUSSIAN, ENGLISH_CORRECT_DEFINITION, List.of(Map.of("ch", ENGLISH_CORRECT_EXAMPLE_CH, "en", ENGLISH_CORRECT_EXAMPLE_EN, "ru", ENGLISH_CORRECT_EXAMPLE_RU, "extra", "value"), example, example, example, example), EnumSet.allOf(Field.class), true)),
                    arguments(new ValidationInput(1, ENGLISH, ENGLISH_CORRECT_NAME_CHINESE, ENGLISH_CORRECT_TRANSCRIPTION, ENGLISH_CORRECT_NAME_ENGLISH, ENGLISH_CORRECT_NAME_RUSSIAN, ENGLISH_CORRECT_DEFINITION, List.of(Map.of("ch", ENGLISH_CORRECT_EXAMPLE_CH, "en", ENGLISH_CORRECT_EXAMPLE_EN), Map.of("ch", ENGLISH_CORRECT_EXAMPLE_CH, "en", ENGLISH_CORRECT_EXAMPLE_EN), Map.of("ch", ENGLISH_CORRECT_EXAMPLE_CH, "en", ENGLISH_CORRECT_EXAMPLE_EN), Map.of("ch", ENGLISH_CORRECT_EXAMPLE_CH, "en", ENGLISH_CORRECT_EXAMPLE_EN), Map.of("ch", ENGLISH_CORRECT_EXAMPLE_CH, "en", ENGLISH_CORRECT_EXAMPLE_EN)), EnumSet.allOf(Field.class), true)),
                    arguments(new ValidationInput(1, ENGLISH, ENGLISH_CORRECT_NAME_CHINESE, ENGLISH_CORRECT_TRANSCRIPTION, ENGLISH_CORRECT_NAME_ENGLISH, ENGLISH_CORRECT_NAME_RUSSIAN, ENGLISH_CORRECT_DEFINITION, List.of(Map.of("ch", "", "en", ENGLISH_CORRECT_EXAMPLE_EN, "ru", ENGLISH_CORRECT_EXAMPLE_RU), example, example, example, example), EnumSet.allOf(Field.class), true)),
                    arguments(new ValidationInput(1, ENGLISH, ENGLISH_CORRECT_NAME_CHINESE, ENGLISH_CORRECT_TRANSCRIPTION, ENGLISH_CORRECT_NAME_ENGLISH, ENGLISH_CORRECT_NAME_RUSSIAN, ENGLISH_CORRECT_DEFINITION, List.of(Map.of("ch", " ", "en", ENGLISH_CORRECT_EXAMPLE_EN, "ru", ENGLISH_CORRECT_EXAMPLE_RU), example, example, example, example), EnumSet.allOf(Field.class), true)),
                    arguments(new ValidationInput(1, ENGLISH, ENGLISH_CORRECT_NAME_CHINESE, ENGLISH_CORRECT_TRANSCRIPTION, ENGLISH_CORRECT_NAME_ENGLISH, ENGLISH_CORRECT_NAME_RUSSIAN, ENGLISH_CORRECT_DEFINITION, List.of(Map.of("ch", ENGLISH_CORRECT_EXAMPLE_CH + ".", "en", ENGLISH_CORRECT_EXAMPLE_EN, "ru", ENGLISH_CORRECT_EXAMPLE_RU), example, example, example, example), EnumSet.allOf(Field.class), true)),
                    arguments(new ValidationInput(1, ENGLISH, ENGLISH_CORRECT_NAME_CHINESE, ENGLISH_CORRECT_TRANSCRIPTION, ENGLISH_CORRECT_NAME_ENGLISH, ENGLISH_CORRECT_NAME_RUSSIAN, ENGLISH_CORRECT_DEFINITION, List.of(Map.of("ch", "hello", "en", ENGLISH_CORRECT_EXAMPLE_EN, "ru", ENGLISH_CORRECT_EXAMPLE_RU), example, example, example, example), EnumSet.allOf(Field.class), true)),
                    arguments(new ValidationInput(1, ENGLISH, ENGLISH_CORRECT_NAME_CHINESE, ENGLISH_CORRECT_TRANSCRIPTION, ENGLISH_CORRECT_NAME_ENGLISH, ENGLISH_CORRECT_NAME_RUSSIAN, ENGLISH_CORRECT_DEFINITION, List.of(Map.of("ch", "你".repeat(MAX_EXAMPLE_LENGTH + 1), "en", ENGLISH_CORRECT_EXAMPLE_EN, "ru", ENGLISH_CORRECT_EXAMPLE_RU), example, example, example, example), EnumSet.allOf(Field.class), true)),
                    arguments(new ValidationInput(1, ENGLISH, ENGLISH_CORRECT_NAME_CHINESE, ENGLISH_CORRECT_TRANSCRIPTION, ENGLISH_CORRECT_NAME_ENGLISH, ENGLISH_CORRECT_NAME_RUSSIAN, ENGLISH_CORRECT_DEFINITION, List.of(Map.of("ch", ENGLISH_CORRECT_EXAMPLE_CH, "en", "", "ru", ENGLISH_CORRECT_EXAMPLE_RU), example, example, example, example), EnumSet.allOf(Field.class), true)),
                    arguments(new ValidationInput(1, ENGLISH, ENGLISH_CORRECT_NAME_CHINESE, ENGLISH_CORRECT_TRANSCRIPTION, ENGLISH_CORRECT_NAME_ENGLISH, ENGLISH_CORRECT_NAME_RUSSIAN, ENGLISH_CORRECT_DEFINITION, List.of(Map.of("ch", ENGLISH_CORRECT_EXAMPLE_CH, "en", " ", "ru", ENGLISH_CORRECT_EXAMPLE_RU), example, example, example, example), EnumSet.allOf(Field.class), true)),
                    arguments(new ValidationInput(1, ENGLISH, ENGLISH_CORRECT_NAME_CHINESE, ENGLISH_CORRECT_TRANSCRIPTION, ENGLISH_CORRECT_NAME_ENGLISH, ENGLISH_CORRECT_NAME_RUSSIAN, ENGLISH_CORRECT_DEFINITION, List.of(Map.of("ch", ENGLISH_CORRECT_EXAMPLE_CH, "en", "A" + "a".repeat(MAX_EXAMPLE_LENGTH), "ru", ENGLISH_CORRECT_EXAMPLE_RU), example, example, example, example), EnumSet.allOf(Field.class), true)),
                    arguments(new ValidationInput(1, ENGLISH, ENGLISH_CORRECT_NAME_CHINESE, ENGLISH_CORRECT_TRANSCRIPTION, ENGLISH_CORRECT_NAME_ENGLISH, ENGLISH_CORRECT_NAME_RUSSIAN, ENGLISH_CORRECT_DEFINITION, List.of(Map.of("ch", ENGLISH_CORRECT_EXAMPLE_CH, "en", ENGLISH_CORRECT_EXAMPLE_CH, "ru", ENGLISH_CORRECT_EXAMPLE_RU), example, example, example, example), EnumSet.allOf(Field.class), true)),
                    arguments(new ValidationInput(1, ENGLISH, ENGLISH_CORRECT_NAME_CHINESE, ENGLISH_CORRECT_TRANSCRIPTION, ENGLISH_CORRECT_NAME_ENGLISH, ENGLISH_CORRECT_NAME_RUSSIAN, ENGLISH_CORRECT_DEFINITION, List.of(Map.of("ch", ENGLISH_CORRECT_EXAMPLE_CH, "en", ENGLISH_CORRECT_EXAMPLE_EN, "ru", ""), example, example, example, example), EnumSet.allOf(Field.class), true)),
                    arguments(new ValidationInput(1, ENGLISH, ENGLISH_CORRECT_NAME_CHINESE, ENGLISH_CORRECT_TRANSCRIPTION, ENGLISH_CORRECT_NAME_ENGLISH, ENGLISH_CORRECT_NAME_RUSSIAN, ENGLISH_CORRECT_DEFINITION, List.of(Map.of("ch", ENGLISH_CORRECT_EXAMPLE_CH, "en", ENGLISH_CORRECT_EXAMPLE_EN, "ru", " "), example, example, example, example), EnumSet.allOf(Field.class), true)),
                    arguments(new ValidationInput(1, ENGLISH, ENGLISH_CORRECT_NAME_CHINESE, ENGLISH_CORRECT_TRANSCRIPTION, ENGLISH_CORRECT_NAME_ENGLISH, ENGLISH_CORRECT_NAME_RUSSIAN, ENGLISH_CORRECT_DEFINITION, List.of(Map.of("ch", ENGLISH_CORRECT_EXAMPLE_CH, "en", ENGLISH_CORRECT_EXAMPLE_EN, "ru", "а".repeat(MAX_EXAMPLE_LENGTH + 1)), example, example, example, example), EnumSet.allOf(Field.class), true)),
                    arguments(new ValidationInput(1, ENGLISH, ENGLISH_CORRECT_NAME_CHINESE, ENGLISH_CORRECT_TRANSCRIPTION, ENGLISH_CORRECT_NAME_ENGLISH, ENGLISH_CORRECT_NAME_RUSSIAN, ENGLISH_CORRECT_DEFINITION, List.of(Map.of("ch", ENGLISH_CORRECT_EXAMPLE_CH, "en", ENGLISH_CORRECT_EXAMPLE_EN, "ru", "hello"), example, example, example, example), EnumSet.allOf(Field.class), true))
            );
        }

        public static Stream<Arguments> validate_chinese_returnsErrors() {
            Map<String, String> example = chineseExample(CHINESE_CORRECT_NAME_CHINESE);
            return Stream.of(
                    arguments(new ValidationInput(1, CHINESE, null, CHINESE_CORRECT_TRANSCRIPTION, CHINESE_CORRECT_NAME_ENGLISH, CHINESE_CORRECT_NAME_RUSSIAN, CHINESE_CORRECT_DEFINITION, chineseExamples(CHINESE_CORRECT_NAME_CHINESE), EnumSet.allOf(Field.class), true)),
                    arguments(new ValidationInput(1, CHINESE, "你".repeat(MAX_NAME_CHINESE_CH_PLATFORM_LENGTH + 1), CHINESE_CORRECT_TRANSCRIPTION, CHINESE_CORRECT_NAME_ENGLISH, CHINESE_CORRECT_NAME_RUSSIAN, CHINESE_CORRECT_DEFINITION, chineseExamples(CHINESE_CORRECT_NAME_CHINESE), EnumSet.allOf(Field.class), true)),
                    arguments(new ValidationInput(1, CHINESE, CHINESE_CORRECT_NAME_CHINESE + " ", CHINESE_CORRECT_TRANSCRIPTION, CHINESE_CORRECT_NAME_ENGLISH, CHINESE_CORRECT_NAME_RUSSIAN, CHINESE_CORRECT_DEFINITION, chineseExamples(CHINESE_CORRECT_NAME_CHINESE), EnumSet.allOf(Field.class), true)),
                    arguments(new ValidationInput(1, CHINESE, CHINESE_CORRECT_NAME_CHINESE + ",", CHINESE_CORRECT_TRANSCRIPTION, CHINESE_CORRECT_NAME_ENGLISH, CHINESE_CORRECT_NAME_RUSSIAN, CHINESE_CORRECT_DEFINITION, chineseExamples(CHINESE_CORRECT_NAME_CHINESE), EnumSet.allOf(Field.class), true)),
                    arguments(new ValidationInput(1, CHINESE, CHINESE_CORRECT_NAME_CHINESE + "，", CHINESE_CORRECT_TRANSCRIPTION, CHINESE_CORRECT_NAME_ENGLISH, CHINESE_CORRECT_NAME_RUSSIAN, CHINESE_CORRECT_DEFINITION, chineseExamples(CHINESE_CORRECT_NAME_CHINESE), EnumSet.allOf(Field.class), true)),
                    arguments(new ValidationInput(1, CHINESE, CHINESE_CORRECT_NAME_CHINESE + ";", CHINESE_CORRECT_TRANSCRIPTION, CHINESE_CORRECT_NAME_ENGLISH, CHINESE_CORRECT_NAME_RUSSIAN, CHINESE_CORRECT_DEFINITION, chineseExamples(CHINESE_CORRECT_NAME_CHINESE), EnumSet.allOf(Field.class), true)),
                    arguments(new ValidationInput(1, CHINESE, CHINESE_CORRECT_NAME_CHINESE + "；", CHINESE_CORRECT_TRANSCRIPTION, CHINESE_CORRECT_NAME_ENGLISH, CHINESE_CORRECT_NAME_RUSSIAN, CHINESE_CORRECT_DEFINITION, chineseExamples(CHINESE_CORRECT_NAME_CHINESE), EnumSet.allOf(Field.class), true)),
                    arguments(new ValidationInput(1, CHINESE, "hello", CHINESE_CORRECT_TRANSCRIPTION, CHINESE_CORRECT_NAME_ENGLISH, CHINESE_CORRECT_NAME_RUSSIAN, CHINESE_CORRECT_DEFINITION, chineseExamples(CHINESE_CORRECT_NAME_CHINESE), EnumSet.allOf(Field.class), true)),
                    arguments(new ValidationInput(1, CHINESE, CHINESE_CORRECT_NAME_CHINESE, null, CHINESE_CORRECT_NAME_ENGLISH, CHINESE_CORRECT_NAME_RUSSIAN, CHINESE_CORRECT_DEFINITION, chineseExamples(CHINESE_CORRECT_NAME_CHINESE), EnumSet.allOf(Field.class), true)),
                    arguments(new ValidationInput(1, CHINESE, CHINESE_CORRECT_NAME_CHINESE, "ni", CHINESE_CORRECT_NAME_ENGLISH, CHINESE_CORRECT_NAME_RUSSIAN, CHINESE_CORRECT_DEFINITION, chineseExamples(CHINESE_CORRECT_NAME_CHINESE), EnumSet.allOf(Field.class), true)),
                    arguments(new ValidationInput(1, CHINESE, CHINESE_CORRECT_NAME_CHINESE, "ni hao@", CHINESE_CORRECT_NAME_ENGLISH, CHINESE_CORRECT_NAME_RUSSIAN, CHINESE_CORRECT_DEFINITION, chineseExamples(CHINESE_CORRECT_NAME_CHINESE), EnumSet.allOf(Field.class), true)),
                    arguments(new ValidationInput(1, CHINESE, CHINESE_CORRECT_NAME_CHINESE, CHINESE_CORRECT_TRANSCRIPTION, null, CHINESE_CORRECT_NAME_RUSSIAN, CHINESE_CORRECT_DEFINITION, chineseExamples(CHINESE_CORRECT_NAME_CHINESE), EnumSet.allOf(Field.class), true)),
                    arguments(new ValidationInput(1, CHINESE, CHINESE_CORRECT_NAME_CHINESE, CHINESE_CORRECT_TRANSCRIPTION, "a".repeat(MAX_NAME_ENGLISH_CH_PLATFORM_LENGTH + 1), CHINESE_CORRECT_NAME_RUSSIAN, CHINESE_CORRECT_DEFINITION, chineseExamples(CHINESE_CORRECT_NAME_CHINESE), EnumSet.allOf(Field.class), true)),
                    arguments(new ValidationInput(1, CHINESE, CHINESE_CORRECT_NAME_CHINESE, CHINESE_CORRECT_TRANSCRIPTION, "hello;", CHINESE_CORRECT_NAME_RUSSIAN, CHINESE_CORRECT_DEFINITION, chineseExamples(CHINESE_CORRECT_NAME_CHINESE), EnumSet.allOf(Field.class), true)),
                    arguments(new ValidationInput(1, CHINESE, CHINESE_CORRECT_NAME_CHINESE, CHINESE_CORRECT_TRANSCRIPTION, "hello  world", CHINESE_CORRECT_NAME_RUSSIAN, CHINESE_CORRECT_DEFINITION, chineseExamples(CHINESE_CORRECT_NAME_CHINESE), EnumSet.allOf(Field.class), true)),
                    arguments(new ValidationInput(1, CHINESE, CHINESE_CORRECT_NAME_CHINESE, CHINESE_CORRECT_TRANSCRIPTION, CHINESE_CORRECT_NAME_CHINESE, CHINESE_CORRECT_NAME_RUSSIAN, CHINESE_CORRECT_DEFINITION, chineseExamples(CHINESE_CORRECT_NAME_CHINESE), EnumSet.allOf(Field.class), true)),
                    arguments(new ValidationInput(1, CHINESE, CHINESE_CORRECT_NAME_CHINESE, CHINESE_CORRECT_TRANSCRIPTION, CHINESE_CORRECT_NAME_ENGLISH, null, CHINESE_CORRECT_DEFINITION, chineseExamples(CHINESE_CORRECT_NAME_CHINESE), EnumSet.allOf(Field.class), true)),
                    arguments(new ValidationInput(1, CHINESE, CHINESE_CORRECT_NAME_CHINESE, CHINESE_CORRECT_TRANSCRIPTION, CHINESE_CORRECT_NAME_ENGLISH, "a".repeat(MAX_NAME_RUSSIAN_LENGTH + 1), CHINESE_CORRECT_DEFINITION, chineseExamples(CHINESE_CORRECT_NAME_CHINESE), EnumSet.allOf(Field.class), true)),
                    arguments(new ValidationInput(1, CHINESE, CHINESE_CORRECT_NAME_CHINESE, CHINESE_CORRECT_TRANSCRIPTION, CHINESE_CORRECT_NAME_ENGLISH, CHINESE_CORRECT_NAME_RUSSIAN + ";", CHINESE_CORRECT_DEFINITION, chineseExamples(CHINESE_CORRECT_NAME_CHINESE), EnumSet.allOf(Field.class), true)),
                    arguments(new ValidationInput(1, CHINESE, CHINESE_CORRECT_NAME_CHINESE, CHINESE_CORRECT_TRANSCRIPTION, CHINESE_CORRECT_NAME_ENGLISH, CHINESE_CORRECT_NAME_RUSSIAN + "  " + CHINESE_CORRECT_NAME_RUSSIAN, CHINESE_CORRECT_DEFINITION, chineseExamples(CHINESE_CORRECT_NAME_CHINESE), EnumSet.allOf(Field.class), true)),
                    arguments(new ValidationInput(1, CHINESE, CHINESE_CORRECT_NAME_CHINESE, CHINESE_CORRECT_TRANSCRIPTION, CHINESE_CORRECT_NAME_ENGLISH, "hello", CHINESE_CORRECT_DEFINITION, chineseExamples(CHINESE_CORRECT_NAME_CHINESE), EnumSet.allOf(Field.class), true)),
                    arguments(new ValidationInput(1, CHINESE, CHINESE_CORRECT_NAME_CHINESE, CHINESE_CORRECT_TRANSCRIPTION, CHINESE_CORRECT_NAME_ENGLISH, CHINESE_CORRECT_NAME_RUSSIAN, null, chineseExamples(CHINESE_CORRECT_NAME_CHINESE), EnumSet.allOf(Field.class), true)),
                    arguments(new ValidationInput(1, CHINESE, CHINESE_CORRECT_NAME_CHINESE, CHINESE_CORRECT_TRANSCRIPTION, CHINESE_CORRECT_NAME_ENGLISH, CHINESE_CORRECT_NAME_RUSSIAN, CHINESE_CORRECT_DEFINITION + ".", chineseExamples(CHINESE_CORRECT_NAME_CHINESE), EnumSet.allOf(Field.class), true)),
                    arguments(new ValidationInput(1, CHINESE, CHINESE_CORRECT_NAME_CHINESE, CHINESE_CORRECT_TRANSCRIPTION, CHINESE_CORRECT_NAME_ENGLISH, CHINESE_CORRECT_NAME_RUSSIAN, "Hello", chineseExamples(CHINESE_CORRECT_NAME_CHINESE), EnumSet.allOf(Field.class), true)),
                    arguments(new ValidationInput(1, CHINESE, CHINESE_CORRECT_NAME_CHINESE, CHINESE_CORRECT_TRANSCRIPTION, CHINESE_CORRECT_NAME_ENGLISH, CHINESE_CORRECT_NAME_RUSSIAN, "你".repeat(MAX_DEFINITION_LENGTH + 1), chineseExamples(CHINESE_CORRECT_NAME_CHINESE), EnumSet.allOf(Field.class), true)),
                    arguments(new ValidationInput(1, CHINESE, CHINESE_CORRECT_NAME_CHINESE, CHINESE_CORRECT_TRANSCRIPTION, CHINESE_CORRECT_NAME_ENGLISH, CHINESE_CORRECT_NAME_RUSSIAN, CHINESE_CORRECT_DEFINITION, null, EnumSet.allOf(Field.class), false)),
                    arguments(new ValidationInput(1, CHINESE, CHINESE_CORRECT_NAME_CHINESE, CHINESE_CORRECT_TRANSCRIPTION, CHINESE_CORRECT_NAME_ENGLISH, CHINESE_CORRECT_NAME_RUSSIAN, CHINESE_CORRECT_DEFINITION, List.of(example, example, example, example), EnumSet.allOf(Field.class), true)),
                    arguments(new ValidationInput(1, CHINESE, CHINESE_CORRECT_NAME_CHINESE, CHINESE_CORRECT_TRANSCRIPTION, CHINESE_CORRECT_NAME_ENGLISH, CHINESE_CORRECT_NAME_RUSSIAN, CHINESE_CORRECT_DEFINITION, List.of(example, example, example, example, example, example), EnumSet.allOf(Field.class), true)),
                    arguments(new ValidationInput(1, CHINESE, CHINESE_CORRECT_NAME_CHINESE, CHINESE_CORRECT_TRANSCRIPTION, CHINESE_CORRECT_NAME_ENGLISH, CHINESE_CORRECT_NAME_RUSSIAN, CHINESE_CORRECT_DEFINITION, List.of(Map.of("ch", CHINESE_CORRECT_EXAMPLE_CH, "pinyin", CHINESE_CORRECT_EXAMPLE_PINYIN, "en", CHINESE_CORRECT_EXAMPLE_EN, "ru", CHINESE_CORRECT_EXAMPLE_RU, "extra", "value"), example, example, example, example), EnumSet.allOf(Field.class), true)),
                    arguments(new ValidationInput(1, CHINESE, CHINESE_CORRECT_NAME_CHINESE, CHINESE_CORRECT_TRANSCRIPTION, CHINESE_CORRECT_NAME_ENGLISH, CHINESE_CORRECT_NAME_RUSSIAN, CHINESE_CORRECT_DEFINITION, List.of(Map.of("ch", CHINESE_CORRECT_EXAMPLE_CH, "pinyin", CHINESE_CORRECT_EXAMPLE_PINYIN, "en", CHINESE_CORRECT_EXAMPLE_EN), Map.of("ch", CHINESE_CORRECT_EXAMPLE_CH, "pinyin", CHINESE_CORRECT_EXAMPLE_PINYIN, "en", CHINESE_CORRECT_EXAMPLE_EN), Map.of("ch", CHINESE_CORRECT_EXAMPLE_CH, "pinyin", CHINESE_CORRECT_EXAMPLE_PINYIN, "en", CHINESE_CORRECT_EXAMPLE_EN), Map.of("ch", CHINESE_CORRECT_EXAMPLE_CH, "pinyin", CHINESE_CORRECT_EXAMPLE_PINYIN, "en", CHINESE_CORRECT_EXAMPLE_EN), Map.of("ch", CHINESE_CORRECT_EXAMPLE_CH, "pinyin", CHINESE_CORRECT_EXAMPLE_PINYIN, "en", CHINESE_CORRECT_EXAMPLE_EN)), EnumSet.allOf(Field.class), true)),
                    arguments(new ValidationInput(1, CHINESE, CHINESE_CORRECT_NAME_CHINESE, CHINESE_CORRECT_TRANSCRIPTION, CHINESE_CORRECT_NAME_ENGLISH, CHINESE_CORRECT_NAME_RUSSIAN, CHINESE_CORRECT_DEFINITION, List.of(Map.of("ch", "", "pinyin", CHINESE_CORRECT_EXAMPLE_PINYIN, "en", CHINESE_CORRECT_EXAMPLE_EN, "ru", CHINESE_CORRECT_EXAMPLE_RU), example, example, example, example), EnumSet.allOf(Field.class), true)),
                    arguments(new ValidationInput(1, CHINESE, CHINESE_CORRECT_NAME_CHINESE, CHINESE_CORRECT_TRANSCRIPTION, CHINESE_CORRECT_NAME_ENGLISH, CHINESE_CORRECT_NAME_RUSSIAN, CHINESE_CORRECT_DEFINITION, List.of(Map.of("ch", " ", "pinyin", CHINESE_CORRECT_EXAMPLE_PINYIN, "en", CHINESE_CORRECT_EXAMPLE_EN, "ru", CHINESE_CORRECT_EXAMPLE_RU), example, example, example, example), EnumSet.allOf(Field.class), true)),
                    arguments(new ValidationInput(1, CHINESE, CHINESE_CORRECT_NAME_CHINESE, CHINESE_CORRECT_TRANSCRIPTION, CHINESE_CORRECT_NAME_ENGLISH, CHINESE_CORRECT_NAME_RUSSIAN, CHINESE_CORRECT_DEFINITION, List.of(Map.of("ch", CHINESE_CORRECT_EXAMPLE_CH + ".", "pinyin", CHINESE_CORRECT_EXAMPLE_PINYIN, "en", CHINESE_CORRECT_EXAMPLE_EN, "ru", CHINESE_CORRECT_EXAMPLE_RU), example, example, example, example), EnumSet.allOf(Field.class), true)),
                    arguments(new ValidationInput(1, CHINESE, CHINESE_CORRECT_NAME_CHINESE, CHINESE_CORRECT_TRANSCRIPTION, CHINESE_CORRECT_NAME_ENGLISH, CHINESE_CORRECT_NAME_RUSSIAN, CHINESE_CORRECT_DEFINITION, List.of(Map.of("ch", "hello", "pinyin", CHINESE_CORRECT_EXAMPLE_PINYIN, "en", CHINESE_CORRECT_EXAMPLE_EN, "ru", CHINESE_CORRECT_EXAMPLE_RU), example, example, example, example), EnumSet.allOf(Field.class), true)),
                    arguments(new ValidationInput(1, CHINESE, CHINESE_CORRECT_NAME_CHINESE, CHINESE_CORRECT_TRANSCRIPTION, CHINESE_CORRECT_NAME_ENGLISH, CHINESE_CORRECT_NAME_RUSSIAN, CHINESE_CORRECT_DEFINITION, List.of(Map.of("ch", "天气", "pinyin", "tian qi", "en", CHINESE_CORRECT_EXAMPLE_EN, "ru", CHINESE_CORRECT_EXAMPLE_RU), example, example, example, example), EnumSet.allOf(Field.class), true)),
                    arguments(new ValidationInput(1, CHINESE, CHINESE_CORRECT_NAME_CHINESE, CHINESE_CORRECT_TRANSCRIPTION, CHINESE_CORRECT_NAME_ENGLISH, CHINESE_CORRECT_NAME_RUSSIAN, CHINESE_CORRECT_DEFINITION, List.of(Map.of("ch", "你好".repeat(MAX_EXAMPLE_LENGTH / 2) + "你", "pinyin", CHINESE_CORRECT_EXAMPLE_PINYIN, "en", CHINESE_CORRECT_EXAMPLE_EN, "ru", CHINESE_CORRECT_EXAMPLE_RU), example, example, example, example), EnumSet.allOf(Field.class), true)),
                    arguments(new ValidationInput(1, CHINESE, CHINESE_CORRECT_NAME_CHINESE, CHINESE_CORRECT_TRANSCRIPTION, CHINESE_CORRECT_NAME_ENGLISH, CHINESE_CORRECT_NAME_RUSSIAN, CHINESE_CORRECT_DEFINITION, List.of(Map.of("ch", CHINESE_CORRECT_EXAMPLE_CH, "pinyin", "", "en", CHINESE_CORRECT_EXAMPLE_EN, "ru", CHINESE_CORRECT_EXAMPLE_RU), example, example, example, example), EnumSet.allOf(Field.class), true)),
                    arguments(new ValidationInput(1, CHINESE, CHINESE_CORRECT_NAME_CHINESE, CHINESE_CORRECT_TRANSCRIPTION, CHINESE_CORRECT_NAME_ENGLISH, CHINESE_CORRECT_NAME_RUSSIAN, CHINESE_CORRECT_DEFINITION, List.of(Map.of("ch", CHINESE_CORRECT_EXAMPLE_CH, "pinyin", " ", "en", CHINESE_CORRECT_EXAMPLE_EN, "ru", CHINESE_CORRECT_EXAMPLE_RU), example, example, example, example), EnumSet.allOf(Field.class), true)),
                    arguments(new ValidationInput(1, CHINESE, CHINESE_CORRECT_NAME_CHINESE, CHINESE_CORRECT_TRANSCRIPTION, CHINESE_CORRECT_NAME_ENGLISH, CHINESE_CORRECT_NAME_RUSSIAN, CHINESE_CORRECT_DEFINITION, List.of(Map.of("ch", CHINESE_CORRECT_EXAMPLE_CH, "pinyin", "a".repeat(MAX_EXAMPLE_LENGTH + 1), "en", CHINESE_CORRECT_EXAMPLE_EN, "ru", CHINESE_CORRECT_EXAMPLE_RU), example, example, example, example), EnumSet.allOf(Field.class), true)),
                    arguments(new ValidationInput(1, CHINESE, CHINESE_CORRECT_NAME_CHINESE, CHINESE_CORRECT_TRANSCRIPTION, CHINESE_CORRECT_NAME_ENGLISH, CHINESE_CORRECT_NAME_RUSSIAN, CHINESE_CORRECT_DEFINITION, List.of(Map.of("ch", CHINESE_CORRECT_EXAMPLE_CH, "pinyin", "ni hao@", "en", CHINESE_CORRECT_EXAMPLE_EN, "ru", CHINESE_CORRECT_EXAMPLE_RU), example, example, example, example), EnumSet.allOf(Field.class), true)),
                    arguments(new ValidationInput(1, CHINESE, CHINESE_CORRECT_NAME_CHINESE, CHINESE_CORRECT_TRANSCRIPTION, CHINESE_CORRECT_NAME_ENGLISH, CHINESE_CORRECT_NAME_RUSSIAN, CHINESE_CORRECT_DEFINITION, List.of(Map.of("ch", CHINESE_CORRECT_EXAMPLE_CH, "pinyin", CHINESE_CORRECT_EXAMPLE_PINYIN, "en", "", "ru", CHINESE_CORRECT_EXAMPLE_RU), example, example, example, example), EnumSet.allOf(Field.class), true)),
                    arguments(new ValidationInput(1, CHINESE, CHINESE_CORRECT_NAME_CHINESE, CHINESE_CORRECT_TRANSCRIPTION, CHINESE_CORRECT_NAME_ENGLISH, CHINESE_CORRECT_NAME_RUSSIAN, CHINESE_CORRECT_DEFINITION, List.of(Map.of("ch", CHINESE_CORRECT_EXAMPLE_CH, "pinyin", CHINESE_CORRECT_EXAMPLE_PINYIN, "en", " ", "ru", CHINESE_CORRECT_EXAMPLE_RU), example, example, example, example), EnumSet.allOf(Field.class), true)),
                    arguments(new ValidationInput(1, CHINESE, CHINESE_CORRECT_NAME_CHINESE, CHINESE_CORRECT_TRANSCRIPTION, CHINESE_CORRECT_NAME_ENGLISH, CHINESE_CORRECT_NAME_RUSSIAN, CHINESE_CORRECT_DEFINITION, List.of(Map.of("ch", CHINESE_CORRECT_EXAMPLE_CH, "pinyin", CHINESE_CORRECT_EXAMPLE_PINYIN, "en", "A" + "a".repeat(MAX_EXAMPLE_LENGTH), "ru", CHINESE_CORRECT_EXAMPLE_RU), example, example, example, example), EnumSet.allOf(Field.class), true)),
                    arguments(new ValidationInput(1, CHINESE, CHINESE_CORRECT_NAME_CHINESE, CHINESE_CORRECT_TRANSCRIPTION, CHINESE_CORRECT_NAME_ENGLISH, CHINESE_CORRECT_NAME_RUSSIAN, CHINESE_CORRECT_DEFINITION, List.of(Map.of("ch", CHINESE_CORRECT_EXAMPLE_CH, "pinyin", CHINESE_CORRECT_EXAMPLE_PINYIN, "en", CHINESE_CORRECT_EXAMPLE_CH, "ru", CHINESE_CORRECT_EXAMPLE_RU), example, example, example, example), EnumSet.allOf(Field.class), true)),
                    arguments(new ValidationInput(1, CHINESE, CHINESE_CORRECT_NAME_CHINESE, CHINESE_CORRECT_TRANSCRIPTION, CHINESE_CORRECT_NAME_ENGLISH, CHINESE_CORRECT_NAME_RUSSIAN, CHINESE_CORRECT_DEFINITION, List.of(Map.of("ch", CHINESE_CORRECT_EXAMPLE_CH, "pinyin", CHINESE_CORRECT_EXAMPLE_PINYIN, "en", CHINESE_CORRECT_EXAMPLE_EN, "ru", ""), example, example, example, example), EnumSet.allOf(Field.class), true)),
                    arguments(new ValidationInput(1, CHINESE, CHINESE_CORRECT_NAME_CHINESE, CHINESE_CORRECT_TRANSCRIPTION, CHINESE_CORRECT_NAME_ENGLISH, CHINESE_CORRECT_NAME_RUSSIAN, CHINESE_CORRECT_DEFINITION, List.of(Map.of("ch", CHINESE_CORRECT_EXAMPLE_CH, "pinyin", CHINESE_CORRECT_EXAMPLE_PINYIN, "en", CHINESE_CORRECT_EXAMPLE_EN, "ru", " "), example, example, example, example), EnumSet.allOf(Field.class), true)),
                    arguments(new ValidationInput(1, CHINESE, CHINESE_CORRECT_NAME_CHINESE, CHINESE_CORRECT_TRANSCRIPTION, CHINESE_CORRECT_NAME_ENGLISH, CHINESE_CORRECT_NAME_RUSSIAN, CHINESE_CORRECT_DEFINITION, List.of(Map.of("ch", CHINESE_CORRECT_EXAMPLE_CH, "pinyin", CHINESE_CORRECT_EXAMPLE_PINYIN, "en", CHINESE_CORRECT_EXAMPLE_EN, "ru", "а".repeat(MAX_EXAMPLE_LENGTH + 1)), example, example, example, example), EnumSet.allOf(Field.class), true)),
                    arguments(new ValidationInput(1, CHINESE, CHINESE_CORRECT_NAME_CHINESE, CHINESE_CORRECT_TRANSCRIPTION, CHINESE_CORRECT_NAME_ENGLISH, CHINESE_CORRECT_NAME_RUSSIAN, CHINESE_CORRECT_DEFINITION, List.of(Map.of("ch", CHINESE_CORRECT_EXAMPLE_CH, "pinyin", CHINESE_CORRECT_EXAMPLE_PINYIN, "en", CHINESE_CORRECT_EXAMPLE_EN, "ru", "hello"), example, example, example, example), EnumSet.allOf(Field.class), true))
            );
        }
    }

    private static Map<String, String> englishExample() {
        return Map.of(
                "ch", ENGLISH_CORRECT_EXAMPLE_CH,
                "en", ENGLISH_CORRECT_EXAMPLE_EN,
                "ru", ENGLISH_CORRECT_EXAMPLE_RU
        );
    }

    private static Map<String, String> chineseExample(String nameChinese) {
        String chinese = nameChinese == null ? CHINESE_CORRECT_EXAMPLE_CH : nameChinese;
        return Map.of(
                "ch", chinese,
                "pinyin", CHINESE_CORRECT_EXAMPLE_PINYIN,
                "en", CHINESE_CORRECT_EXAMPLE_EN,
                "ru", CHINESE_CORRECT_EXAMPLE_RU
        );
    }

    private static List<Map<String, String>> englishExamples() {
        Map<String, String> example = englishExample();
        return List.of(example, example, example, example, example);
    }

    private static List<Map<String, String>> englishExamplesWithValues(String ch, String en, String ru) {
        Map<String, String> example = Map.of(
                "ch", ch,
                "en", en,
                "ru", ru
        );
        return List.of(example, example, example, example, example);
    }

    private static List<Map<String, String>> chineseExamples(String nameChinese) {
        Map<String, String> example = chineseExample(nameChinese);
        return List.of(example, example, example, example, example);
    }

    private static List<Map<String, String>> chineseExamplesWithValues(String ch, String pinyin, String en, String ru) {
        Map<String, String> example = Map.of(
                "ch", ch,
                "pinyin", pinyin,
                "en", en,
                "ru", ru
        );
        return List.of(example, example, example, example, example);
    }
}
