package it342.g4.e_vents.controller;

import it342.g4.e_vents.model.Event;
import it342.g4.e_vents.model.Notification;
import it342.g4.e_vents.service.NotificationService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Controller for notification-related operations
 */
@RestController
@RequestMapping("/api/notifications")
@CrossOrigin(origins = "*")
public class NotificationController {
    
    private final NotificationService notificationService;
    
    @Autowired
    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }
    
    /**
     * Retrieves all notifications
     * @return List of all notifications
     */
    @GetMapping
    public ResponseEntity<List<Notification>> getAllNotifications() {
        return ResponseEntity.ok(notificationService.getAllNotifications());
    }
    
    /**
     * Retrieves a notification by ID
     * @param id The notification ID
     * @return The notification or 404 if not found
     */
    @GetMapping("/{id}")
    public ResponseEntity<Notification> getNotificationById(@PathVariable Long id) {
        return notificationService.getNotificationById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * Retrieves all notifications for a specific user
     * @param userId The user ID
     * @return List of notifications for the user
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Notification>> getUserNotifications(@PathVariable Long userId) {
        return ResponseEntity.ok(notificationService.getUserNotifications(userId));
    }
    
    /**
     * Retrieves unread notifications for a specific user
     * @param userId The user ID
     * @return List of unread notifications
     */
    @GetMapping("/user/{userId}/unread")
    public ResponseEntity<List<Notification>> getUnreadNotifications(@PathVariable Long userId) {
        return ResponseEntity.ok(notificationService.getUnreadNotifications(userId));
    }
    
    /**
     * Counts unread notifications for a specific user
     * @param userId The user ID
     * @return Count of unread notifications
     */
    @GetMapping("/user/{userId}/count")
    public ResponseEntity<Map<String, Long>> countUnreadNotifications(@PathVariable Long userId) {
        long count = notificationService.countUnreadNotifications(userId);
        return ResponseEntity.ok(Collections.singletonMap("count", count));
    }
    
    /**
     * Creates a new notification
     * @param request The notification request
     * @return The created notification
     */
    @PostMapping
    public ResponseEntity<Notification> createNotification(@RequestBody NotificationRequest request) {
        try {
            Notification notification = notificationService.createNotification(
                request.getUserId(),
                request.getTitle(),
                request.getMessage(),
                request.getType(),
                request.getEvent()
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(notification);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Creates notifications for all attendees of an event
     * @param request The event notification request
     * @return List of created notifications
     */
    @PostMapping("/event")
    public ResponseEntity<List<Notification>> notifyEventAttendees(@RequestBody EventNotificationRequest request) {
        try {
            List<Notification> notifications = notificationService.notifyEventAttendees(
                request.getEventId(),
                request.getTitle(),
                request.getMessage(),
                request.getType()
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(notifications);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Marks a notification as read
     * @param id The notification ID
     * @return The updated notification
     */
    @PutMapping("/{id}/read")
    public ResponseEntity<Notification> markAsRead(@PathVariable Long id) {
        try {
            Notification notification = notificationService.markAsRead(id);
            return ResponseEntity.ok(notification);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Marks all notifications for a user as read
     * @param userId The user ID
     * @return Number of notifications marked as read
     */
    @PutMapping("/user/{userId}/read-all")
    public ResponseEntity<Map<String, Integer>> markAllAsRead(@PathVariable Long userId) {
        try {
            int count = notificationService.markAllAsRead(userId);
            return ResponseEntity.ok(Collections.singletonMap("markedAsRead", count));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Updates an existing notification
     * @param id The notification ID
     * @param notification The updated notification data
     * @return The updated notification
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateNotification(@PathVariable Long id, @RequestBody Notification notification) {
        try {
            // Set the ID from the path
            notification.setNotificationId(id);
            
            // Update and return
            Notification updatedNotification = notificationService.updateNotification(notification);
            return ResponseEntity.ok(updatedNotification);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Collections.singletonMap("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Collections.singletonMap("error", e.getMessage()));
        }
    }
    
    /**
     * Deletes a notification
     * @param id The notification ID
     * @return Success message or error
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteNotification(@PathVariable Long id) {
        try {
            notificationService.deleteNotification(id);
            return ResponseEntity.ok(Collections.singletonMap("message", "Notification deleted successfully"));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Collections.singletonMap("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Collections.singletonMap("error", e.getMessage()));
        }
    }
    
    /**
     * Request class for creating a notification
     */
    public static class NotificationRequest {
        private Long userId;
        private String title;
        private String message;
        private String type;
        private Event event;
        
        // Getters and setters
        public Long getUserId() {
            return userId;
        }
        
        public void setUserId(Long userId) {
            this.userId = userId;
        }
        
        public String getTitle() {
            return title;
        }
        
        public void setTitle(String title) {
            this.title = title;
        }
        
        public String getMessage() {
            return message;
        }
        
        public void setMessage(String message) {
            this.message = message;
        }
        
        public String getType() {
            return type;
        }
        
        public void setType(String type) {
            this.type = type;
        }
        
        public Event getEvent() {
            return event;
        }
        
        public void setEvent(Event event) {
            this.event = event;
        }
    }
    
    /**
     * Request class for creating notifications for event attendees
     */
    public static class EventNotificationRequest {
        private Long eventId;
        private String title;
        private String message;
        private String type;
        
        // Getters and setters
        public Long getEventId() {
            return eventId;
        }
        
        public void setEventId(Long eventId) {
            this.eventId = eventId;
        }
        
        public String getTitle() {
            return title;
        }
        
        public void setTitle(String title) {
            this.title = title;
        }
        
        public String getMessage() {
            return message;
        }
        
        public void setMessage(String message) {
            this.message = message;
        }
        
        public String getType() {
            return type;
        }
        
        public void setType(String type) {
            this.type = type;
        }
    }
}
