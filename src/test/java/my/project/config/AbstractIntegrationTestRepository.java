package my.project.config;

import my.project.config.datahandler.ExcelDataHandler;
import my.project.config.datahandler.WordsLoader;
import my.project.util.DatabaseContainer;
import org.junit.ClassRule;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace.NONE;

@ActiveProfiles("test")
@DataJpaTest
@Import({WordsLoader.class, ExcelDataHandler.class})
@AutoConfigureTestDatabase(replace = NONE)
public abstract class AbstractIntegrationTestRepository {

    @ClassRule
    public static final DatabaseContainer databaseContainer = DatabaseContainer.getInstance();
}
