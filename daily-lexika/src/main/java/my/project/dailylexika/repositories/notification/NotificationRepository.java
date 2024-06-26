package my.project.dailylexika.repositories.notification;

import my.project.dailylexika.entities.notification.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Integer> {

    List<Notification> findAllByToUserId(Integer userId);

    void deleteAllByToUserId(Integer userId);
}
