package my.project.admin._techdoc;

import my.project.admin.AdminApp;
import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;

class ApplicationModulesTest {

    @Test
    void verifyAndPrintApplicationModules() {
        ApplicationModules modules = ApplicationModules.of(AdminApp.class);
        modules.verify();
        modules.forEach(System.out::println);
    }
}
