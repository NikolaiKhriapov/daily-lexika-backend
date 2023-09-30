package my.project.vocabulary;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"my.project.vocabulary", "my.project.amqp"})
public class VocabularyApplication {
    public static void main(String[] args) {
        SpringApplication.run(VocabularyApplication.class, args);
    }
}
