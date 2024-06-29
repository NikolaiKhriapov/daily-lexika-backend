package my.project.library.util.api;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "urls")
public class BaseUrlConfig {

    private String dailyLexika;
    private String dailyBudget;
    private String admin;
}
