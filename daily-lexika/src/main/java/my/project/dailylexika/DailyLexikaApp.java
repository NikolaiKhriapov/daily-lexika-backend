package my.project.dailylexika;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(
        scanBasePackages = "my.project.dailylexika",
        scanBasePackageClasses = {
                my.project.library.util.security.AdminRoleContainer.class,
                my.project.library.util.security.JwtService.class
        }
)
public class DailyLexikaApp {
    public static void main(String[] args) {
        SpringApplication.run(DailyLexikaApp.class, args);
    }
}
