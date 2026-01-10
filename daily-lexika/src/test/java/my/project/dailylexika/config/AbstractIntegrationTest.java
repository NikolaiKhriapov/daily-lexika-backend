package my.project.dailylexika.config;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.TestSecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.DockerClientFactory;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import org.springframework.beans.factory.annotation.Autowired;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.MOCK;

@ActiveProfiles("test")
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = MOCK)
@Testcontainers
public abstract class AbstractIntegrationTest {

    private static final String POSTGRES_IMAGE = "postgres:16.2";

    private static final String TRUNCATE_ALL_TABLES_SQL = """
            DO $$
            DECLARE
              stmt TEXT;
            BEGIN
              SELECT 'TRUNCATE TABLE ' || string_agg(format('%I.%I', schemaname, tablename), ', ') || ' RESTART IDENTITY CASCADE'
              INTO stmt
              FROM pg_tables
              WHERE schemaname = 'public'
                AND tablename <> 'flyway_schema_history';
              IF stmt IS NOT NULL THEN
                EXECUTE stmt;
              END IF;
            END $$;
            """;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Container
    @ServiceConnection
    static final PostgreSQLContainer<?> postgres = new SharedPostgresContainer()
            .withDatabaseName("test_database")
            .withUsername("test")
            .withPassword("test");

    @BeforeAll
    static void requireDocker() {
        Assertions.assertTrue(
                DockerClientFactory.instance().isDockerAvailable(),
                "Docker is required to run integration tests."
        );
    }

    @BeforeEach
    void cleanDatabase() {
        jdbcTemplate.execute(TRUNCATE_ALL_TABLES_SQL);
    }

    @AfterEach
    void clearSecurityContext() {
        TestSecurityContextHolder.clearContext();
        SecurityContextHolder.clearContext();
    }

    private static final class SharedPostgresContainer extends PostgreSQLContainer<SharedPostgresContainer> {

        private SharedPostgresContainer() {
            super(DockerImageName.parse(POSTGRES_IMAGE));
        }

        @Override
        public void stop() {
            // Keep container running across test classes; Ryuk will clean it up at JVM shutdown.
        }
    }
}
