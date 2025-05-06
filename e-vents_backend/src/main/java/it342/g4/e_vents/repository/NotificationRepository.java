package it342.g4.e_vents.repository;

import it342.g4.e_vents.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByIsActiveTrue();
    List<Notification> findByUserUserIdAndIsActiveTrue(Long userId);
    List<Notification> findByUserUserIdAndReadFalseAndIsActiveTrue(Long userId);
}
