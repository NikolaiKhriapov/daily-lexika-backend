package my.project.admin._techdoc;

import lombok.RequiredArgsConstructor;
import my.project.admin.AdminApp;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.modulith.core.ApplicationModules;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.MOCK;

@SpringBootTest(webEnvironment = MOCK)
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ApplicationModulesTest {

    @Test
    void verifyAndPrintApplicationModules() {
        ApplicationModules modules = ApplicationModules.of(AdminApp.class);
        modules.verify();
        modules.forEach(System.out::println);
    }
}
