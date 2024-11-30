package my.project.dailylexika;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication(
        scanBasePackages = "my.project.dailylexika",
        scanBasePackageClasses = {
                my.project.library.util.i18n.I18nConfig.class,
                my.project.library.util.security.AdminRoleContainer.class,
                my.project.library.util.security.JwtService.class,
                my.project.library.util.serialization.SerializationConfig.class,
        }
)
@EnableCaching
public class DailyLexikaApp {
    public static void main(String[] args) {
        SpringApplication.run(DailyLexikaApp.class, args);
    }
}
