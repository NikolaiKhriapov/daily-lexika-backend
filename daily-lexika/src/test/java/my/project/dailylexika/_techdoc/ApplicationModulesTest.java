package my.project.dailylexika._techdoc;

import my.project.dailylexika.DailyLexikaApp;
import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;

class ApplicationModulesTest {

    @Test
    void verifyAndPrintApplicationModules() {
        ApplicationModules modules = ApplicationModules.of(DailyLexikaApp.class);
        modules.verify();
        modules.forEach(System.out::println);
    }
}
