package my.project.chineseflashcards;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"my.project.chineseflashcards", "my.project.amqp"})
public class ChineseFlashcardsApplication {
    public static void main(String[] args) {
        SpringApplication.run(ChineseFlashcardsApplication.class, args);
    }
}
