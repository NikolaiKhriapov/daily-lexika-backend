package my.project.util.data;

import com.github.javafaker.Faker;
import my.project.models.dtos.flashcards.WordPackDto;
import my.project.models.entities.enumerations.Category;
import my.project.models.entities.enumerations.Platform;

import java.util.List;

public class FakerUtil {

    private static final Faker FAKER = new Faker();

    // Common

    public static Long generateLongId() {
        return generateRandomLong(999_999L);
    }

    public static Integer generateIntId() {
        return generateRandomInt(999_999);
    }

    // Authentication

    public static String generateName() {
        return FAKER.name().firstName();
    }

    public static String generateEmail() {
        return FAKER.internet().emailAddress();
    }

    public static String generatePassword() {
        return FAKER.internet().password();
    }

    // WordPack

    public static WordPackDto generateWordPackDTO(Platform platform) {
        return new WordPackDto(
                generateWordPackName(platform),
                generateWordPackDescription(),
                generateWordPackCategory(platform),
                platform,
                100L,
                null
        );
    }

    public static String generateWordPackName(Platform platform) {
        List<String> chineseWordPackNames = List.of("CH__HSK 1", "CH__HSK 2", "CH__HSK 3", "CH__HSK 4", "CH__HSK 5", "CH__HSK 6");
        List<String> englishWordPackNames = List.of("EN__A1", "EN__A2", "EN__B1", "EN__B2", "EN__C1");

        return switch (platform) {
            case CHINESE -> chineseWordPackNames.get(generateRandomInt(chineseWordPackNames.size()));
            case ENGLISH -> englishWordPackNames.get(generateRandomInt(englishWordPackNames.size()));
        };
    }

    public static String generateWordPackDescription() {
        return FAKER.letterify(generateStringEn(20));
    }

    public static Category generateWordPackCategory(Platform platform) {
        List<Category> chineseCategories = List.of(Category.HSK, Category.CUSTOM);
        List<Category> englishCategories = List.of(Category.CEFR, Category.CUSTOM);

        return switch (platform) {
            case CHINESE -> chineseCategories.get(generateRandomInt(chineseCategories.size()));
            case ENGLISH -> englishCategories.get(generateRandomInt(englishCategories.size()));
        };
    }

    // WordData

    public static String generateNameChinese() {
        return generateStringCh(3);
    }

    public static String generateTranscription() {
        return generateStringEn(10);
    }

    public static String generateNameEnglish() {
        return generateStringEn(20);
    }

    public static String generateNameRussian() {
        return generateStringRu(20);
    }

    public static String generateDefinition() {
        return generateStringEn(50);
    }

    public static String generateExamples() {
        return generateStringEn(20) + ";"
                + generateStringEn(20) + ";"
                + generateStringEn(20) + ";"
                + generateStringEn(20) + ";"
                + generateStringEn(20);
    }

    // helper methods

    public static int generateRandomInt(int to) {
        return FAKER.number().numberBetween(1, to);
    }

    public static Long generateRandomLong(Long to) {
        return FAKER.number().numberBetween(1L, to);
    }

    public static Long generateRandomLong(Long from, Long to) {
        return FAKER.number().numberBetween(from, to);
    }

    private static String generateStringEn(int length) {
        return FAKER.letterify("?".repeat(length)).toLowerCase();
    }

    private static String generateStringCh(int length) {
        StringBuilder chineseCharacters = new StringBuilder();
        for (int i = 0; i < length; i++) {
            chineseCharacters.append((char) FAKER.number().numberBetween(0x4e00, 0x9fff + 1));
        }

        return chineseCharacters.toString();
    }

    private static String generateStringRu(int length) {
        StringBuilder russianCharacters = new StringBuilder();
        for (int i = 0; i < length; i++) {
            russianCharacters.append((char) FAKER.number().numberBetween(0x0410, 0x044F + 1));
        }

        return russianCharacters.toString().toLowerCase();
    }
}
