package my.project.dailylexika.config;

import my.project.dailylexika.util.DatabaseContainer;
import org.junit.ClassRule;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.MOCK;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = MOCK)
public abstract class AbstractIntegrationTest {

    @ClassRule
    public static final DatabaseContainer databaseContainer = DatabaseContainer.getInstance();
}
