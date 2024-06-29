package my.project.dailybudget;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(
        scanBasePackages = "my.project.dailybudget",
        scanBasePackageClasses = {
                my.project.library.util.i18n.I18nConfig.class,
                my.project.library.util.security.AdminRoleContainer.class,
                my.project.library.util.security.JwtService.class,
                my.project.library.util.serialization.SerializationConfig.class,
        }
)
public class DailyBudgetApp {
    public static void main(String[] args) {
        SpringApplication.run(DailyBudgetApp.class, args);
    }
}
