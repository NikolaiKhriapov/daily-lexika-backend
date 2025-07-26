package my.project.dailylexika.notification.persistence;

import my.project.dailylexika.notification.model.entities.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Integer> {

    List<Notification> findAllByToUserId(Integer userId);

    void deleteAllByToUserId(Integer userId);
}
