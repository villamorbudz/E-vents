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
    private final EventService eventService;
    
    @Autowired
    public NotificationService(NotificationRepository notificationRepository, 
                              UserRepository userRepository,
                              TicketService ticketService,
                              EventService eventService) {
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
        this.ticketService = ticketService;
        this.eventService = eventService;
    }
    
    /**
     * Get all notifications
     * @return List of all notifications
     */
    public List<Notification> getAllNotifications() {
        return notificationRepository.findAll();
    }
    
    /**
     * Get all active notifications
     * @return List of all active notifications
     */
    public List<Notification> getAllActiveNotifications() {
        return notificationRepository.findByIsActiveTrueOrderByCreatedAtDesc();
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
     * Get active notification by ID
     * @param id Notification ID
     * @return The active notification if found
     */
    public Optional<Notification> getActiveNotificationById(Long id) {
        return notificationRepository.findByNotificationIdAndIsActiveTrue(id);
    }
    
    /**
     * Get notification by ID or throw exception if not found
     * @param id Notification ID
     * @return The notification
     * @throws EntityNotFoundException if notification not found
     */
    public Notification getNotificationByIdOrThrow(Long id) {
        return notificationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Notification not found with ID: " + id));
    }
    
    /**
     * Get active notification by ID or throw exception if not found
     * @param id Notification ID
     * @return The active notification
     * @throws EntityNotFoundException if active notification not found
     */
    public Notification getActiveNotificationByIdOrThrow(Long id) {
        return notificationRepository.findByNotificationIdAndIsActiveTrue(id)
                .orElseThrow(() -> new EntityNotFoundException("Active notification not found with ID: " + id));
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
     * Get all active notifications for a specific user
     * @param userId User ID
     * @return List of active notifications for the user
     */
    public List<Notification> getActiveUserNotifications(Long userId) {
        return notificationRepository.findByUserUserIdAndIsActiveTrueOrderByCreatedAtDesc(userId);
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
     * Get active unread notifications for a specific user
     * @param userId User ID
     * @return List of active unread notifications
     */
    public List<Notification> getActiveUnreadNotifications(Long userId) {
        return notificationRepository.findByUserUserIdAndReadFalseAndIsActiveTrueOrderByCreatedAtDesc(userId);
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
     * Count active unread notifications for a user
     * @param userId User ID
     * @return Count of active unread notifications
     */
    public long countActiveUnreadNotifications(Long userId) {
        return notificationRepository.countByUserUserIdAndReadFalseAndIsActiveTrue(userId);
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
        notification.setActive(true);
        
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
        
        // Fetch the event using EventService
        Event event = null;
        try {
            event = eventService.getEventById(eventId);
        } catch (EntityNotFoundException e) {
            // Event not found, continue with null event
        }
        
        for (Ticket ticket : tickets) {
            if ("confirmed".equals(ticket.getStatus())) {
                // Fetch the user using UserRepository
                User ticketUser = userRepository.findById(ticket.getUserId())
                    .orElse(null);
                
                if (ticketUser != null) {
                    Notification notification = createNotification(
                        ticketUser.getUserId(),
                        title,
                        message,
                        type,
                        event
                    );
                    notifications.add(notification);
                }
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
     * Mark a notification as unread
     * @param notificationId Notification ID
     * @return The updated notification
     */
    public Notification markAsUnread(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
            .orElseThrow(() -> new EntityNotFoundException("Notification not found with ID: " + notificationId));
        notification.setRead(false);
        notification.setReadAt(null);
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
     * Soft delete a notification by setting isActive to false
     * @param notificationId Notification ID
     */
    public void softDeleteNotification(Long notificationId) {
        Notification notification = getNotificationByIdOrThrow(notificationId);
        notification.setActive(false);
        notificationRepository.save(notification);
    }
    
    /**
     * Restore a soft-deleted notification by setting isActive to true
     * @param notificationId Notification ID
     * @return The restored notification
     */
    public Notification restoreNotification(Long notificationId) {
        Notification notification = getNotificationByIdOrThrow(notificationId);
        notification.setActive(true);
        return notificationRepository.save(notification);
    }
    
    /**
     * Update an existing notification
     * @param notification The notification with updated fields
     * @return The updated notification
     */
    public Notification updateNotification(Notification notification) {
        getActiveNotificationByIdOrThrow(notification.getNotificationId());
        return notificationRepository.save(notification);
    }
    
    /**
     * Get notifications by user ID
     * @param userId User ID
     * @return List of notifications for the user
     */
    public List<Notification> getNotificationsByUserId(Long userId) {
        return notificationRepository.findByUserUserIdOrderByCreatedAtDesc(userId);
    }
    
    /**
     * Create a notification from a notification object
     * @param notification The notification object
     * @return The created notification
     */
    public Notification createNotification(Notification notification) {
        // Set created time if not provided
        if (notification.getCreatedAt() == null) {
            notification.setCreatedAt(LocalDateTime.now());
        }
        
        // Ensure notification is active
        notification.setActive(true);
        
        return notificationRepository.save(notification);
    }
}
