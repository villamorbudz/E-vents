package it342.g4.e_vents.service;

import it342.g4.e_vents.model.Event;
import it342.g4.e_vents.model.Notification;
import it342.g4.e_vents.model.Ticket;
import it342.g4.e_vents.model.User;
import it342.g4.e_vents.repository.NotificationRepository;
import it342.g4.e_vents.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class NotificationService {
    
    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final TicketService ticketService;
    
    @Autowired
    public NotificationService(NotificationRepository notificationRepository, 
                              UserRepository userRepository,
                              TicketService ticketService) {
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
        this.ticketService = ticketService;
    }
    
    /**
     * Get all notifications
     * @return List of all notifications
     */
    public List<Notification> getAllNotifications() {
        return notificationRepository.findAll();
    }
    
    /**
     * Get notification by ID
     * @param id Notification ID
     * @return The notification if found
     */
    public Optional<Notification> getNotificationById(Long id) {
        return notificationRepository.findById(id);
    }
    
    /**
     * Get all notifications for a specific user
     * @param userId User ID
     * @return List of notifications for the user
     */
    public List<Notification> getUserNotifications(Long userId) {
        return notificationRepository.findByUserUserIdOrderByCreatedAtDesc(userId);
    }
    
    /**
     * Get unread notifications for a specific user
     * @param userId User ID
     * @return List of unread notifications
     */
    public List<Notification> getUnreadNotifications(Long userId) {
        return notificationRepository.findByUserUserIdAndReadFalseOrderByCreatedAtDesc(userId);
    }
    
    /**
     * Count unread notifications for a user
     * @param userId User ID
     * @return Count of unread notifications
     */
    public long countUnreadNotifications(Long userId) {
        return notificationRepository.countByUserUserIdAndReadFalse(userId);
    }
    
    /**
     * Create a notification for a specific user
     * @param userId User ID
     * @param title Notification title
     * @param message Notification message
     * @param type Notification type
     * @param event Related event (optional)
     * @return The created notification
     */
    public Notification createNotification(Long userId, String title, String message, String type, Event event) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + userId));
            
        Notification notification = new Notification();
        notification.setUser(user);
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setType(type);
        notification.setRead(false);
        notification.setCreatedAt(LocalDateTime.now());
        notification.setEvent(event);
        
        return notificationRepository.save(notification);
    }
    
    /**
     * Create a notification for a specific user (without event)
     * @param userId User ID
     * @param title Notification title
     * @param message Notification message
     * @param type Notification type
     * @return The created notification
     */
    public Notification createNotification(Long userId, String title, String message, String type) {
        return createNotification(userId, title, message, type, null);
    }
    
    /**
     * Create notifications for all users attending an event
     * @param eventId Event ID
     * @param title Notification title
     * @param message Notification message
     * @param type Notification type
     * @return List of created notifications
     */
    public List<Notification> notifyEventAttendees(Long eventId, String title, String message, String type) {
        List<Ticket> tickets = ticketService.findTicketsByEventId(eventId);
        List<Notification> notifications = new ArrayList<>();
        
        Event event = tickets.isEmpty() ? null : tickets.get(0).getEvent();
        
        for (Ticket ticket : tickets) {
            if ("confirmed".equals(ticket.getStatus())) {
                User user = ticket.getUser();
                Notification notification = createNotification(
                    user.getUserId(),
                    title,
                    message,
                    type,
                    event
                );
                notifications.add(notification);
            }
        }
        
        return notifications;
    }
    
    /**
     * Mark a notification as read
     * @param notificationId Notification ID
     * @return The updated notification
     */
    public Notification markAsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
            .orElseThrow(() -> new EntityNotFoundException("Notification not found with ID: " + notificationId));
            
        notification.setRead(true);
        notification.setReadAt(LocalDateTime.now());
        
        return notificationRepository.save(notification);
    }
    
    /**
     * Mark all notifications for a user as read
     * @param userId User ID
     * @return Number of notifications marked as read
     */
    public int markAllAsRead(Long userId) {
        List<Notification> unreadNotifications = notificationRepository.findByUserUserIdAndReadFalseOrderByCreatedAtDesc(userId);
        LocalDateTime now = LocalDateTime.now();
        
        for (Notification notification : unreadNotifications) {
            notification.setRead(true);
            notification.setReadAt(now);
        }
        
        notificationRepository.saveAll(unreadNotifications);
        return unreadNotifications.size();
    }
    
    /**
     * Delete a notification
     * @param notificationId Notification ID
     */
    public void deleteNotification(Long notificationId) {
        if (!notificationRepository.existsById(notificationId)) {
            throw new EntityNotFoundException("Notification not found with ID: " + notificationId);
        }
        notificationRepository.deleteById(notificationId);
    }
    
    /**
     * Update an existing notification
     * @param notification The notification with updated fields
     * @return The updated notification
     */
    public Notification updateNotification(Notification notification) {
        if (!notificationRepository.existsById(notification.getNotificationId())) {
            throw new EntityNotFoundException("Notification not found with ID: " + notification.getNotificationId());
        }
        return notificationRepository.save(notification);
    }
}
