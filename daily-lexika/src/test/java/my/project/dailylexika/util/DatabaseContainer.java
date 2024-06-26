package my.project.dailylexika.util;

import org.testcontainers.containers.PostgreSQLContainer;

public class DatabaseContainer extends PostgreSQLContainer<DatabaseContainer> {

    private static final String DOCKER_IMAGE = "postgres:latest";

    private static DatabaseContainer databaseContainer;

    private DatabaseContainer() {
        super(DOCKER_IMAGE);
    }

    public static DatabaseContainer getInstance() {
        if (databaseContainer == null) {
            databaseContainer = new DatabaseContainer();
        }
        return databaseContainer;
    }
}
