package it342.g4.e_vents.service;

import it342.g4.e_vents.model.Event;
import it342.g4.e_vents.model.Notification;
import it342.g4.e_vents.model.User;
import it342.g4.e_vents.repository.EventRepository;
import it342.g4.e_vents.repository.NotificationRepository;
import it342.g4.e_vents.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    @Autowired
    public NotificationService(NotificationRepository notificationRepository, UserRepository userRepository, EventRepository eventRepository) {
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
        this.eventRepository = eventRepository;
    }

    /**
     * Retrieves all active notifications
     * @return List of all active notifications
     */
    public List<Notification> getAllActiveNotifications() {
        return notificationRepository.findByIsActiveTrue();
    }

    /**
     * Retrieves all notifications, including inactive ones
     * @return List of all notifications
     */
    public List<Notification> getAllNotifications() {
        return notificationRepository.findAll();
    }

    /**
     * Retrieves a notification by ID
     * @param id The notification ID
     * @return Optional containing the notification if found
     */
    public Optional<Notification> getNotificationById(Long id) {
        return notificationRepository.findById(id);
    }

    /**
     * Retrieves all notifications for a specific user
     * @param userId The user ID
     * @return List of active notifications for the specified user
     */
    public List<Notification> getNotificationsByUserId(Long userId) {
        return notificationRepository.findByUserUserIdAndIsActiveTrue(userId);
    }

    /**
     * Retrieves unread notifications for a specific user
     * @param userId The user ID
     * @return List of unread active notifications for the specified user
     */
    public List<Notification> getUnreadNotificationsByUserId(Long userId) {
        return notificationRepository.findByUserUserIdAndReadFalseAndIsActiveTrue(userId);
    }

    /**
     * Creates a new notification
     * @param notification The notification to create
     * @return The created notification with ID
     * @throws EntityNotFoundException if the user or event is not found
     */
    public Notification createNotification(Notification notification) {
        // Verify user exists
        if (notification.getUser() != null) {
            User user = userRepository.findById(notification.getUser().getUserId())
                    .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + notification.getUser().getUserId()));
            notification.setUser(user);
        }
        
        // Verify event exists if provided
        if (notification.getEvent() != null) {
            Event event = eventRepository.findById(notification.getEvent().getEventId())
                    .orElseThrow(() -> new EntityNotFoundException("Event not found with ID: " + notification.getEvent().getEventId()));
            notification.setEvent(event);
        }
        
        // Set default values
        notification.setActive(true);
        notification.setCreatedAt(LocalDateTime.now());
        notification.setRead(false);
        
        return notificationRepository.save(notification);
    }

    /**
     * Updates an existing notification
     * @param id The ID of the notification to update
     * @param notificationDetails Updated notification data
     * @return The updated notification
     * @throws EntityNotFoundException if the notification is not found
     */
    public Notification updateNotification(Long id, Notification notificationDetails) {
        Notification existingNotification = notificationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Notification not found with ID: " + id));
        
        // Update fields
        existingNotification.setTitle(notificationDetails.getTitle());
        existingNotification.setMessage(notificationDetails.getMessage());
        existingNotification.setType(notificationDetails.getType());
        
        return notificationRepository.save(existingNotification);
    }

    /**
     * Marks a notification as read
     * @param id The ID of the notification to mark as read
     * @return The updated notification
     * @throws EntityNotFoundException if the notification is not found
     */
    public Notification markNotificationAsRead(Long id) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Notification not found with ID: " + id));
        
        notification.setRead(true);
        notification.setReadAt(LocalDateTime.now());
        
        return notificationRepository.save(notification);
    }

    /**
     * Deactivates (soft-deletes) a notification
     * @param id The ID of the notification to deactivate
     * @throws EntityNotFoundException if the notification is not found
     */
    public void deactivateNotification(Long id) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Notification not found with ID: " + id));
        
        notification.setActive(false);
        notificationRepository.save(notification);
    }

    /**
     * Restores a previously deactivated notification
     * @param id The ID of the notification to restore
     * @throws EntityNotFoundException if the notification is not found
     */
    public void restoreNotification(Long id) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Notification not found with ID: " + id));
        
        notification.setActive(true);
        notificationRepository.save(notification);
    }
}
