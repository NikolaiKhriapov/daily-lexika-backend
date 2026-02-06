package my.project.dailylexika.config;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.TestSecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public abstract class AbstractUnitTest {

    @AfterEach
    void clearSecurityContext() {
        TestSecurityContextHolder.clearContext();
        SecurityContextHolder.clearContext();
    }
}
