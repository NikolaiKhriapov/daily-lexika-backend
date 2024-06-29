package my.project.admin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(
        scanBasePackages = "my.project.admin",
        scanBasePackageClasses = {
                my.project.library.util.api.BaseUrlConfig.class,
                my.project.library.util.api.RestTemplateConfig.class,
                my.project.library.util.api.RestTemplateService.class,
                my.project.library.util.i18n.I18nConfig.class,
                my.project.library.util.security.AdminRoleContainer.class,
                my.project.library.util.security.JwtService.class,
                my.project.library.util.serialization.SerializationConfig.class,
        }
)
public class AdminApp {
    public static void main(String[] args) {
        SpringApplication.run(AdminApp.class, args);
    }
}
