package my.project.dailylexika.flashcard.persistence;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import my.project.dailylexika.flashcard.model.entities.WordData;
import my.project.library.dailylexika.enumerations.Language;
import my.project.library.dailylexika.enumerations.Platform;

import java.util.ArrayList;
import java.util.List;

public class WordDataRepositoryImpl implements WordDataRepositoryCustom {

    private static final String PINYIN_TONES = "\u0101\u00E1\u01CE\u00E0\u0113\u00E9\u011B\u00E8\u012B\u00ED\u01D0\u00EC\u014D\u00F3\u01D2\u00F2\u016B\u00FA\u01D4\u00F9\u01D6\u01D8\u01DA\u01DC\u00FC";
    private static final String PINYIN_TONES_REPLACEMENT = "aaaaeeeeiiiioooouuuuuuuuu";

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<WordData> searchByPlatformAndQuery(Platform platform, Language translationLanguage, String query, String transcriptionQuery, int limit) {
        List<FieldSpec> fields = resolveOrderedFields(platform, translationLanguage);
        List<FieldSpec> searchFields = fields;
        List<FieldSpec> orderFields = fields;
        boolean usesTranscription = fields.stream().anyMatch(FieldSpec::usesTranscription);

        StringBuilder jpql = new StringBuilder("""
                SELECT wd FROM word_data wd
                WHERE wd.platform = :platform
                AND (
                """);
        for (int i = 0; i < searchFields.size(); i++) {
            if (i > 0) {
                jpql.append(" OR ");
            }
            FieldSpec field = searchFields.get(i);
            jpql.append(fieldExpression(field))
                    .append(" LIKE CONCAT('%', ")
                    .append(queryParameter(field))
                    .append(", '%')");
        }
        String rankCase = buildRankCase(orderFields);
        String bucketSortCase = buildBucketSortCase(orderFields);
        jpql.append(") ORDER BY ")
                .append(rankCase)
                .append(", ")
                .append(bucketSortCase)
                .append(", wd.id");

        var typedQuery = entityManager.createQuery(jpql.toString(), WordData.class)
                .setParameter("platform", platform)
                .setParameter("query", query);
        if (usesTranscription) {
            typedQuery.setParameter("transcriptionQuery", transcriptionQuery);
            typedQuery.setParameter("pinyinFrom", PINYIN_TONES);
            typedQuery.setParameter("pinyinTo", PINYIN_TONES_REPLACEMENT);
        }

        return typedQuery.setMaxResults(limit).getResultList();
    }

    private static List<FieldSpec> resolveOrderedFields(Platform platform, Language translationLanguage) {
        List<FieldSpec> fields = new ArrayList<>();
        if (platform == Platform.CHINESE) {
            fields.add(new FieldSpec("nameChinese", false));
            fields.add(new FieldSpec("transcription", true));
            if (translationLanguage == Language.ENGLISH) {
                fields.add(new FieldSpec("nameEnglish", false));
            } else if (translationLanguage == Language.RUSSIAN) {
                fields.add(new FieldSpec("nameRussian", false));
            }
        } else {
            fields.add(new FieldSpec("nameEnglish", false));
            if (translationLanguage == Language.CHINESE) {
                fields.add(new FieldSpec("nameChinese", false));
            } else if (translationLanguage == Language.RUSSIAN) {
                fields.add(new FieldSpec("nameRussian", false));
            }
        }
        return fields;
    }

    private static String buildRankCase(List<FieldSpec> orderFields) {
        StringBuilder rankCase = new StringBuilder("CASE ");
        int rank = 0;
        for (FieldSpec field : orderFields) {
            rankCase.append("WHEN ")
                    .append(fieldExpression(field))
                    .append(" LIKE CONCAT(")
                    .append(queryParameter(field))
                    .append(", '%') THEN ")
                    .append(rank++)
                    .append(" ");
            rankCase.append("WHEN ")
                    .append(fieldExpression(field))
                    .append(" LIKE CONCAT('%', ")
                    .append(queryParameter(field))
                    .append(", '%') THEN ")
                    .append(rank++)
                    .append(" ");
        }
        rankCase.append("ELSE ").append(rank).append(" END");
        return rankCase.toString();
    }

    private static String buildBucketSortCase(List<FieldSpec> orderFields) {
        StringBuilder sortCase = new StringBuilder("CASE ");
        for (FieldSpec field : orderFields) {
            String fieldExpr = fieldExpression(field);
            sortCase.append("WHEN ")
                    .append(fieldExpr)
                    .append(" LIKE CONCAT(")
                    .append(queryParameter(field))
                    .append(", '%') THEN ")
                    .append(fieldExpr)
                    .append(" ");
            sortCase.append("WHEN ")
                    .append(fieldExpr)
                    .append(" LIKE CONCAT('%', ")
                    .append(queryParameter(field))
                    .append(", '%') THEN ")
                    .append(fieldExpr)
                    .append(" ");
        }
        sortCase.append("ELSE ").append(fieldExpression(orderFields.get(0))).append(" END");
        return sortCase.toString();
    }

    private static String fieldExpression(FieldSpec field) {
        if (field.usesTranscription()) {
            return "FUNCTION('translate', LOWER(wd.transcription), :pinyinFrom, :pinyinTo)";
        }
        return "LOWER(wd." + field.name() + ")";
    }

    private static String queryParameter(FieldSpec field) {
        return field.usesTranscription() ? "LOWER(:transcriptionQuery)" : "LOWER(:query)";
    }

    private record FieldSpec(String name, boolean usesTranscription) {
    }
}
