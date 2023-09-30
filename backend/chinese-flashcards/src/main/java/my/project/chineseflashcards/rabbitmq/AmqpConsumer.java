package my.project.chineseflashcards.rabbitmq;

import lombok.AllArgsConstructor;
import my.project.clients.chineseflashcards.DeleteChineseFlashcardsRequest;
import my.project.chineseflashcards.service.ChineseFlashcardsService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class AmqpConsumer {

    private final ChineseFlashcardsService chineseFlashcardsService;

    @RabbitListener(queues = "${rabbitmq.queues.delete-chinese-flashcards}")
    public void consumer(DeleteChineseFlashcardsRequest deleteChineseFlashcardsRequest) {
        chineseFlashcardsService.deleteChineseFlashcardsForUser(deleteChineseFlashcardsRequest.userId());
    }
}
