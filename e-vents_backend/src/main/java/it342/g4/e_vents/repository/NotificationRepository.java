package it342.g4.e_vents.repository;

import it342.g4.e_vents.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    // Find all notifications for a specific user
    List<Notification> findByUserUserIdOrderByCreatedAtDesc(Long userId);
    
    // Find unread notifications for a specific user
    List<Notification> findByUserUserIdAndReadFalseOrderByCreatedAtDesc(Long userId);
    
    // Find notifications by type for a specific user
    List<Notification> findByUserUserIdAndTypeOrderByCreatedAtDesc(Long userId, String type);
    
    // Find notifications related to a specific event
    List<Notification> findByEventEventIdOrderByCreatedAtDesc(Long eventId);
    
    // Count unread notifications for a user
    long countByUserUserIdAndReadFalse(Long userId);
}
