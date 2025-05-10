package it342.g4.e_vents.controller;

import it342.g4.e_vents.model.Notification;
import it342.g4.e_vents.service.NotificationService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.Collections;
import java.util.List;

/**
 * Controller for notification-related operations
 */
@RestController
@RequestMapping("/api/notifications")
@CrossOrigin(origins = "*")
@Tag(name = "Notification", description = "Notification management APIs")
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
    @Operation(summary = "Get all active notifications", description = "Retrieves a list of all active notifications in the system")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved list of active notifications", 
                     content = @Content(mediaType = "application/json", schema = @Schema(implementation = Notification.class)))
    })
    public ResponseEntity<List<Notification>> getAllActiveNotifications() {
        return ResponseEntity.ok(notificationService.getAllActiveNotifications());
    }
    
    /**
     * Retrieves all notifications, including inactive ones
     * @return List of all notifications
     */
    @GetMapping("/all")
    @Operation(summary = "Get all notifications", description = "Retrieves a list of all notifications in the system, including inactive ones")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved list of all notifications", 
                     content = @Content(mediaType = "application/json", schema = @Schema(implementation = Notification.class)))
    })
    public ResponseEntity<List<Notification>> getAllNotifications() {
        return ResponseEntity.ok(notificationService.getAllNotifications());
    }
    
    /**
     * Retrieves a notification by ID
     * @param id The notification ID
     * @return The notification or 404 if not found
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get notification by ID", description = "Retrieves a specific notification by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved the notification", 
                     content = @Content(mediaType = "application/json", schema = @Schema(implementation = Notification.class))),
        @ApiResponse(responseCode = "404", description = "Notification not found", content = @Content)
    })
    public ResponseEntity<Notification> getNotificationById(
            @Parameter(description = "ID of the notification to retrieve", required = true) @PathVariable Long id) {
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
    @Operation(summary = "Get notifications by user ID", description = "Retrieves all active notifications for a specific user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved notifications for the user", 
                     content = @Content(mediaType = "application/json", schema = @Schema(implementation = Notification.class)))
    })
    public ResponseEntity<List<Notification>> getNotificationsByUserId(
            @Parameter(description = "ID of the user to get notifications for", required = true) @PathVariable Long userId) {
        return ResponseEntity.ok(notificationService.getNotificationsByUserId(userId));
    }
    
    /**
     * Retrieves unread notifications for a specific user
     * @param userId The user ID
     * @return List of unread active notifications for the specified user
     */
    @GetMapping("/user/{userId}/unread")
    @Operation(summary = "Get unread notifications by user ID", description = "Retrieves all unread active notifications for a specific user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved unread notifications for the user", 
                     content = @Content(mediaType = "application/json", schema = @Schema(implementation = Notification.class)))
    })
    public ResponseEntity<List<Notification>> getUnreadNotificationsByUserId(
            @Parameter(description = "ID of the user to get unread notifications for", required = true) @PathVariable Long userId) {
        return ResponseEntity.ok(notificationService.getUnreadNotificationsByUserId(userId));
    }
    
    /**
     * Creates a new notification
     * @param notification Notification data from request body
     * @return The created notification or error
     */
    @PostMapping
    @Operation(summary = "Create a new notification", description = "Creates a new notification in the system")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Notification successfully created", 
                     content = @Content(mediaType = "application/json", schema = @Schema(implementation = Notification.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content),
        @ApiResponse(responseCode = "404", description = "Referenced entity not found", content = @Content)
    })
    public ResponseEntity<?> createNotification(
            @Parameter(description = "Notification object to be created", required = true) @RequestBody Notification notification) {
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
    @Operation(summary = "Update a notification", description = "Updates an existing notification in the system")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Notification successfully updated", 
                     content = @Content(mediaType = "application/json", schema = @Schema(implementation = Notification.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content),
        @ApiResponse(responseCode = "404", description = "Notification not found", content = @Content)
    })
    public ResponseEntity<?> updateNotification(
            @Parameter(description = "ID of the notification to update", required = true) @PathVariable Long id, 
            @Parameter(description = "Updated notification details", required = true) @RequestBody Notification notificationDetails) {
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
    @Operation(summary = "Mark notification as read", description = "Marks a specific notification as read")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Notification successfully marked as read", 
                     content = @Content(mediaType = "application/json", schema = @Schema(implementation = Notification.class))),
        @ApiResponse(responseCode = "404", description = "Notification not found", content = @Content),
        @ApiResponse(responseCode = "400", description = "Bad request", content = @Content)
    })
    public ResponseEntity<?> markNotificationAsRead(
            @Parameter(description = "ID of the notification to mark as read", required = true) @PathVariable Long id) {
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
    @Operation(summary = "Deactivate a notification", description = "Soft-deletes a notification by setting its is_active attribute to false")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Notification successfully deactivated", 
                     content = @Content(mediaType = "application/json")),
        @ApiResponse(responseCode = "404", description = "Notification not found", content = @Content),
        @ApiResponse(responseCode = "400", description = "Bad request", content = @Content)
    })
    public ResponseEntity<?> deactivateNotification(
            @Parameter(description = "ID of the notification to deactivate", required = true) @PathVariable Long id) {
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
    @PutMapping("/restore/{id}")
    @Operation(summary = "Restore a notification", description = "Restores a previously deactivated notification by setting its is_active attribute to true")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Notification successfully restored", 
                     content = @Content(mediaType = "application/json")),
        @ApiResponse(responseCode = "404", description = "Notification not found", content = @Content),
        @ApiResponse(responseCode = "400", description = "Bad request", content = @Content)
    })
    public ResponseEntity<?> restoreNotification(
            @Parameter(description = "ID of the notification to restore", required = true) @PathVariable Long id) {
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
