package it342.g4.e_vents.controller;

import it342.g4.e_vents.model.User;
import it342.g4.e_vents.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;

/**
 * Controller for user-related operations
 */
@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {

    private final UserService userService;
    
    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Registers a new user
     * @param user User data from request body
     * @return The registered user or error message
     */
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody User user) {
        try {
            User registeredUser = userService.registerUser(user);
            return ResponseEntity.ok(registeredUser);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Authenticates a user
     * @param email User's email
     * @param password User's password
     * @return The authenticated user or error message
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestParam String email, @RequestParam String password) {
        try {
            User user = userService.login(email, password);
            return ResponseEntity.ok(user);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

    /**
     * Retrieves all active users
     * @return List of all active users
     */
    @GetMapping
    public ResponseEntity<?> getAllUsers() {
        return ResponseEntity.ok(userService.getAllActiveUsers());
    }
    
    /**
     * Retrieves all users including inactive ones
     * @return List of all users
     */
    @GetMapping("/all")
    public ResponseEntity<?> getAllUsersIncludingInactive() {
        return ResponseEntity.ok(userService.getAllUsersIncludingInactive());
    }

    /**
     * Retrieves a user by ID
     * @param id The user ID
     * @return The user or 404 if not found
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        return userService.getUser(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Changes a user's password
     * @param req Request containing email and new password
     * @return Success message or error
     */
    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody ChangePasswordRequest req) {
        try {
            boolean changed = userService.changePasswordByEmail(req.getEmail(), req.getPassword());
            if (changed) {
                return ResponseEntity.ok(Collections.singletonMap("message", "Password changed successfully"));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.singletonMap("error", "User not found"));
            }
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("error", e.getMessage()));
        }
    }

    /**
     * Checks if a user with the given email exists
     * @param email The email to check
     * @return JSON with exists flag
     */
    @GetMapping("/exists")
    public ResponseEntity<?> userExists(@RequestParam String email) {
        boolean exists = userService.userExists(email);
        return ResponseEntity.ok(Collections.singletonMap("exists", exists));
    }

    /**
     * Updates an existing user
     * @param id The user ID to update
     * @param userDetails Updated user data
     * @return The updated user or error
     */
    @PutMapping("/{id}/edit")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody User userDetails) {
        try {
            // Verify user exists
            User existingUser = userService.getUser(id)
                    .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + id));
            
            // Update fields (preserve password and role)
            String currentPassword = existingUser.getPassword();
            String currentRole = existingUser.getRole();
            boolean isActive = existingUser.isActive();

            // Set ID from path
            userDetails.setUserId(id);
            
            // Preserve sensitive fields
            userDetails.setPassword(currentPassword);
            
            // Update and return
            User updatedUser = userService.editUser(userDetails);
            return ResponseEntity.ok(updatedUser);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Collections.singletonMap("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Collections.singletonMap("error", e.getMessage()));
        }
    }
    
    /**
     * Soft deletes a user by setting isActive to false
     * @param id The user ID to soft delete
     * @return Success message or error
     */
    @DeleteMapping("/{id}/deactivate")
    public ResponseEntity<?> softDeleteUser(@PathVariable Long id) {
        try {
            User user = userService.softDeleteUser(id);
            return ResponseEntity.ok(Collections.singletonMap("message", "User deactivated successfully"));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Collections.singletonMap("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Collections.singletonMap("error", e.getMessage()));
        }
    }
    
    /**
     * Restores a soft-deleted user by setting isActive to true
     * @param id The user ID to restore
     * @return The restored user or error
     */
    @PostMapping("/{id}/restore")
    public ResponseEntity<?> restoreUser(@PathVariable Long id) {
        try {
            User restoredUser = userService.restoreUser(id);
            return ResponseEntity.ok(restoredUser);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Collections.singletonMap("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Collections.singletonMap("error", e.getMessage()));
        }
    }

    /**
     * Permanently deletes a user
     * @param id The user ID to delete
     * @return Success message or error
     */
    @DeleteMapping("{id}/delete")
    public ResponseEntity<?> permanentlyDeleteUser(@PathVariable Long id) {
        try {
            userService.permanentlyDeleteUser(id);
            return ResponseEntity.ok(Collections.singletonMap("message", "User permanently deleted"));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("error", e.getMessage()));
        }
    }

    /**
     * DTO for password change requests
     */
    public static class ChangePasswordRequest {
        private String email;
        private String password;
        
        public String getEmail() { 
            return email; 
        }
        
        public void setEmail(String email) { 
            this.email = email; 
        }
        
        public String getPassword() { 
            return password; 
        }
        
        public void setPassword(String password) { 
            this.password = password; 
        }
    }
}
