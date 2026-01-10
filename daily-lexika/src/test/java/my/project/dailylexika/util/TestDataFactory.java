package my.project.dailylexika.util;

import my.project.library.util.security.JwtService;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import static my.project.library.admin.enumerations.RoleName.SUPER_ADMIN;

public final class TestDataFactory {

    private TestDataFactory() {
    }

    private static final String ADMIN_SUBJECT = "admin@test.com";

    public static String buildBearerToken(String token) {
        return "Bearer " + token;
    }

    public static String buildAdminBearerToken(JwtService jwtService) {
        String adminToken = jwtService.generateToken(ADMIN_SUBJECT, SUPER_ADMIN.name());
        return buildBearerToken(adminToken);
    }

    public static String buildUniqueEmail() {
        return "user-" + UUID.randomUUID() + "@test.com";
    }

    public static String buildUniqueWordEnglish() {
        return "testworden" + UUID.randomUUID().toString().replace("-", "").substring(0, 6);
    }

    public static String buildUniqueWordChinese() {
        int length = ThreadLocalRandom.current().nextInt(2, 3);
        StringBuilder builder = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int codePoint = ThreadLocalRandom.current().nextInt(0x4E00, 0x9FFF + 1);
            builder.appendCodePoint(codePoint);
        }
        return builder.toString();
    }

    public static Integer missingWordDataId() {
        return Integer.MAX_VALUE;
    }

    public static Long missingWordPackId() {
        return Long.MAX_VALUE;
    }
}
