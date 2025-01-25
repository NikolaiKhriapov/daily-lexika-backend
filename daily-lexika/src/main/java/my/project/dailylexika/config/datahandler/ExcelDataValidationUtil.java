package my.project.dailylexika.config.datahandler;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import my.project.dailylexika.entities.flashcards.WordData;

import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

@Slf4j
public class ExcelDataValidationUtil {

    private static final String REGEX_CH = "^[\\u4e00-\\u9fa5CDT0-9\\s“”《》，、。？！；：%—/（）【】$]+$";
    private static final String REGEX_CH_PINYIN = "^[a-zāáǎàēéěèīíǐìōóǒòūúǔùǖǘǚǜ0-9\\s“”,.?!;:’—$]+$";
    private static final String REGEX_EN = "^[a-zA-Zé0-9\\s“”,.?!;:+%’/()$—–-]+$";
    private static final String REGEX_RU = "^[а-яА-ЯёЁ0-9\\s№«»,.?!;:%/()$—–-]+$";

    private static final List<Integer> CHINESE_EXCEPTION_PINYIN = List.of(2000555);
    private static final List<Integer> CHINESE_EXCEPTION_EXAMPLES_PINYIN = List.of(2000110, 2000275, 2000555, 2011144);

    public static void validateExcelWordDataEnglish(WordData wordData) {
        validateEnglishNameEnglish(wordData);
        validateEnglishTranscription(wordData);
        validateEnglishNameRussian(wordData);
        validateEnglishNameChinese(wordData);
        validateEnglishDefinition(wordData);
        validateEnglishExamples(wordData);
    }

    public static void validateExcelWordDataChinese(WordData wordData) {
        validateChineseNameChinese(wordData);
        validateChineseTranscription(wordData);
        validateChineseNameEnglish(wordData);
        validateChineseNameRussian(wordData);
        validateChineseDefinition(wordData);
        validateChineseExamples(wordData);
    }



    private static void validateEnglishNameEnglish(WordData wordData) {
        if (wordData.getNameEnglish().length() > 100) {
            log.error("Excel (EN_Words): Validation failed (name_english): {}", wordData.getId());
        }
    }

    private static void validateEnglishTranscription(WordData wordData) {
        if (!wordData.getTranscription().startsWith("/") || !wordData.getTranscription().endsWith("/") || wordData.getTranscription().contains("/ /")) {
            log.error("Excel (EN_Words): Validation failed (transcription): {}", wordData.getId());
        }
    }

    private static void validateEnglishNameRussian(WordData wordData) {
        if (wordData.getNameRussian().length() > 100) {
            log.error("Excel (EN_Words): Validation failed (name_russian): {}", wordData.getId());
        }
    }

    private static void validateEnglishNameChinese(WordData wordData) {
        if (wordData.getNameChinese().length() > 19 || wordData.getNameChinese().contains(",") || wordData.getNameChinese().contains(";") || wordData.getNameChinese().contains("；") || wordData.getNameChinese().contains(" ")) {
            log.error("Excel (EN_Words): Validation failed (name_chinese): {}", wordData.getId());
        }
    }

    private static void validateEnglishDefinition(WordData wordData) {
        if (wordData.getDefinition().equals("[TODO]")) return;

        if (!Pattern.compile(REGEX_EN).matcher(wordData.getDefinition()).matches()) {
            log.error("Excel (EN_Words): Validation failed (definition): {}: {}", wordData.getId(), wordData.getDefinition());
        }
    }

    private static void validateEnglishExamples(WordData wordData) {
        if (wordData.getExamples().equals("[TODO]")) return;

        try {
            List<Map<String, String>> list = new ObjectMapper().readValue(wordData.getExamples(), List.class);
            if (list.size() != 5) {
                log.error("Excel (EN_Words): Validation failed (examples: incorrect number of examples): {}", wordData.getId());
            }
            for (Map<String, String> element : list) {
                if (element.size() != 3) {
                    log.error("Excel (EN_Words): Validation failed (examples: incorrect number of translations): {}", wordData.getId());
                }
                String exampleCh = element.get("ch");
                if (!Pattern.compile(REGEX_CH).matcher(exampleCh).matches()) {
                    log.error("Excel (EN_Words): Validation failed (examples: Chinese): {}: {}: {}", wordData.getId(), wordData.getNameEnglish(), exampleCh);
                }
                String exampleEn = element.get("en");
                if (!Pattern.compile(REGEX_EN).matcher(exampleEn).matches()) {
                    log.error("Excel (EN_Words): Validation failed (examples: English): {}: {}: {}", wordData.getId(), wordData.getNameEnglish(), exampleEn);
                }
                String exampleRu = element.get("ru");
                if (!Pattern.compile(REGEX_RU).matcher(exampleRu).matches()) {
                    log.error("Excel (EN_Words): Validation failed (examples: Russian): {}: {}: {}", wordData.getId(), wordData.getNameEnglish(), exampleRu);
                }
            }
        } catch (Exception e) {
            log.error("Excel (EN_Words): Validation failed (examples: JSON): {}", wordData.getId());
        }
    }



    private static void validateChineseNameChinese(WordData wordData) {
        if (wordData.getNameChinese().length() > 19) {
            log.error("Excel (CH_Words): Validation failed (name_chinese): {}", wordData.getId());
        }
    }

    private static void validateChineseTranscription(WordData wordData) {
        if ((wordData.getTranscription().split(" ").length != wordData.getNameChinese().length() || !Pattern.compile(REGEX_CH_PINYIN).matcher(wordData.getTranscription()).matches()) &&
                !CHINESE_EXCEPTION_PINYIN.contains(wordData.getId())) {
            if (wordData.getTranscription().endsWith("r")) {
                if (wordData.getTranscription().split(" ").length != (wordData.getNameChinese().length() - 1)) {
                    log.error("Excel (CH_Words): Validation failed (transcription): {}: {}: {} ::: {}", wordData.getId(), wordData.getTranscription(), wordData.getTranscription().split(" ").length, wordData.getNameChinese().length());
                }
            } else {
                log.error("Excel data (CH_Words) validation failed (transcription): {}: {}: {} ::: {}", wordData.getId(), wordData.getTranscription(), wordData.getTranscription().split(" ").length, wordData.getNameChinese().length());
            }
        }
    }

    private static void validateChineseNameEnglish(WordData wordData) {
        if (wordData.getNameEnglish().equals("[TODO]")) return;

        if (wordData.getNameEnglish().length() > 100 || wordData.getNameEnglish().contains(";") || wordData.getNameEnglish().contains("  ")) {
            log.error("Excel (CH_Words): Validation failed (name_english): {}", wordData.getId());
        }
    }

    private static void validateChineseNameRussian(WordData wordData) {
        if (wordData.getNameRussian().equals("[TODO]")) return;

        if (wordData.getNameRussian().length() > 100 || wordData.getNameRussian().contains(";") || wordData.getNameRussian().contains("  ")) {
            log.error("Excel (CH_Words): Validation failed (name_russian): {}", wordData.getId());
        }
    }

    private static void validateChineseDefinition(WordData wordData) {
        if (wordData.getDefinition().equals("[TODO]")) return;

        if (!Pattern.compile(REGEX_CH).matcher(wordData.getDefinition()).matches()) {
            log.error("Excel (CH_Words): Validation failed (definition): {}", wordData.getId());
        }
    }

    private static void validateChineseExamples(WordData wordData) {
        if (wordData.getExamples().equals("[TODO]")) return;

        try {
            List<Map<String, String>> list = new ObjectMapper().readValue(wordData.getExamples(), List.class);
            if (list.size() != 5) {
                log.error("Excel (CH_Words): Validation failed (examples: incorrect number of examples): {}", wordData.getId());
            }
            for (Map<String, String> element : list) {
                if (element.size() != 4) {
                    log.error("Excel (CH_Words): Validation failed (examples: incorrect number of translations): {}", wordData.getId());
                }
                String exampleCh = element.get("ch");
                if (!Pattern.compile(REGEX_CH).matcher(exampleCh).matches() || !exampleCh.contains(wordData.getNameChinese())) {
                    log.error("Excel (CH_Words): Validation failed (examples: Chinese): {}: {}: {}", wordData.getId(), wordData.getNameChinese(), exampleCh);
                }
                String examplePinyin = element.get("pinyin");
                if ((!Pattern.compile(REGEX_CH_PINYIN).matcher(examplePinyin).matches() ||
                        !examplePinyin.replaceAll(" ", "").contains(wordData.getTranscription().replaceAll(" ", ""))) &&
                            !CHINESE_EXCEPTION_EXAMPLES_PINYIN.contains(wordData.getId())) {
                    log.error("Excel (CH_Words): Validation failed (examples: Pinyin): {}: {}: {}", wordData.getId(), wordData.getTranscription(), examplePinyin);
                }
                String exampleEn = element.get("en");
                if (!Pattern.compile(REGEX_EN).matcher(exampleEn).matches()) {
                    log.error("Excel (CH_Words): Validation failed (examples: English): {}: {}: {}", wordData.getId(), wordData.getNameChinese(), exampleEn);
                }
                String exampleRu = element.get("ru");
                if (!Pattern.compile(REGEX_RU).matcher(exampleRu).matches()) {
                    log.error("Excel (CH_Words): Validation failed (examples: Russian): {}: {}: {}", wordData.getId(), wordData.getNameChinese(), exampleRu);
                }
            }
        } catch (Exception e) {
            log.error("Excel (CH_Words): Validation failed (examples: JSON): {}", wordData.getId());
        }
    }
}
