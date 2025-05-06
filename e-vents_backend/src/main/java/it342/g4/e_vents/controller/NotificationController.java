package it342.g4.e_vents.controller;

import it342.g4.e_vents.model.Notification;
import it342.g4.e_vents.service.NotificationService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

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
     * Retrieves all active notifications
     * @return List of all active notifications
     */
    @GetMapping
    public ResponseEntity<List<Notification>> getAllActiveNotifications() {
        return ResponseEntity.ok(notificationService.getAllActiveNotifications());
    }
    
    /**
     * Retrieves all notifications, including inactive ones
     * @return List of all notifications
     */
    @GetMapping("/all")
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
     * @return List of active notifications for the specified user
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Notification>> getNotificationsByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(notificationService.getNotificationsByUserId(userId));
    }
    
    /**
     * Retrieves unread notifications for a specific user
     * @param userId The user ID
     * @return List of unread active notifications for the specified user
     */
    @GetMapping("/user/{userId}/unread")
    public ResponseEntity<List<Notification>> getUnreadNotificationsByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(notificationService.getUnreadNotificationsByUserId(userId));
    }
    
    /**
     * Creates a new notification
     * @param notification Notification data from request body
     * @return The created notification or error
     */
    @PostMapping
    public ResponseEntity<?> createNotification(@RequestBody Notification notification) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(notificationService.createNotification(notification));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Collections.singletonMap("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Collections.singletonMap("error", e.getMessage()));
        }
    }
    
    /**
     * Updates an existing notification
     * @param id The notification ID to update
     * @param notificationDetails Updated notification data
     * @return The updated notification or error
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateNotification(@PathVariable Long id, @RequestBody Notification notificationDetails) {
        try {
            Notification updatedNotification = notificationService.updateNotification(id, notificationDetails);
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
     * Marks a notification as read
     * @param id The notification ID to mark as read
     * @return The updated notification or error
     */
    @PutMapping("/{id}/read")
    public ResponseEntity<?> markNotificationAsRead(@PathVariable Long id) {
        try {
            Notification updatedNotification = notificationService.markNotificationAsRead(id);
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
     * Deactivates (soft-deletes) a notification
     * @param id The notification ID to deactivate
     * @return Success message or error
     */
    @DeleteMapping("/{id}/deactivate")
    public ResponseEntity<?> deactivateNotification(@PathVariable Long id) {
        try {
            notificationService.deactivateNotification(id);
            return ResponseEntity.ok(Collections.singletonMap("message", "Notification deactivated successfully"));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Collections.singletonMap("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Collections.singletonMap("error", e.getMessage()));
        }
    }
    
    /**
     * Restores a previously deactivated notification
     * @param id The notification ID to restore
     * @return Success message or error
     */
    @PostMapping("/restore/{id}")
    public ResponseEntity<?> restoreNotification(@PathVariable Long id) {
        try {
            notificationService.restoreNotification(id);
            return ResponseEntity.ok(Collections.singletonMap("message", "Notification restored successfully"));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Collections.singletonMap("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Collections.singletonMap("error", e.getMessage()));
        }
    }
}
