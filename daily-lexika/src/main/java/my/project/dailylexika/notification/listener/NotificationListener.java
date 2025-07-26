package my.project.dailylexika.notification.listener;

import lombok.AllArgsConstructor;
import my.project.dailylexika.notification.model.entities.Notification;
import my.project.dailylexika.notification.service.NotificationService;
import my.project.library.dailylexika.events.user.AccountDeletedEvent;
import my.project.library.dailylexika.events.user.AccountRegisteredEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@AllArgsConstructor
public class NotificationListener {

    private final NotificationService notificationService;

    @EventListener
    @Transactional
    public void on(AccountRegisteredEvent event) {
        if (event.isUserAlreadyExists()) return;

        notificationService.sendNotification(
                new Notification(
                        event.userId(),
                        event.userEmail(),
                        "Welcome to Daily Lexika!",
                        "Hello %s,\n\n".formatted(event.userName())
                                + "Congratulations on joining our vibrant community of language learners! We're thrilled to have you on board. Get ready for an exciting journey of daily vocabulary learning using the powerful spaced repetition approach.\n\n"
                                + "📚 <b>What to Expect:</b>\n"
                                + "<b>Personalized Learning</b>: Our app tailors the experience just for you, ensuring that your learning journey is effective and enjoyable.\n"
                                + "<b>Spaced Repetition Magic</b>: Say goodbye to cramming! Our spaced repetition technique will help you master new words and solidify your vocabulary in the most efficient way.\n\n"
                                + "🚀 <b>How to Get Started:</b>\n"
                                + "<b>Explore the Dashboard</b>: Take a tour of your personalized dashboard, where you'll find your daily reviews, word packs, and statistics.\n"
                                + "<b>Set Your Goals</b>: Define your language learning goals. Whether it's acing exams, improving communication, or just having fun, we're here to support you.\n"
                                + "<b>Daily Check-ins</b>: Make it a habit to check in daily. Consistency is key to language mastery.\n"
                                + "<b>Install App</b>: Once installed, you can access the app directly from your home screen like any other app, without needing to open your browser.\n"
                                + "<b>Create Custom Word Packs</b>: Take your learning to the next level by creating custom word packs tailored to your specific interests, needs, or learning objectives. Add words that you encounter in your daily life, textbooks, or conversations to personalize your learning experience further.\n\n"
                                + "Remember, the journey of a thousand words begins with a single step. We're here to make each step enjoyable and impactful.\n\n"
                                + "Happy learning!\n\n"
                                + "Best,\n"
                                + "The Daily Lexika Team"
                )
        );
    }

    @EventListener
    @Transactional
    public void on(AccountDeletedEvent event) {
        if (!event.isDeleteUser()) return;
        notificationService.deleteAllByUserId(event.userId());
    }
}
