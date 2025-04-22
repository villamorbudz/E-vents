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
     * Retrieves all users
     * @return List of all users
     */
    @GetMapping("/all")
    public ResponseEntity<?> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
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
     * Gets list of countries (placeholder until API integration)
     * @return Array of country names
     */
    @GetMapping("/countries")
    public ResponseEntity<?> getCountries() {
        return ResponseEntity.ok(userService.getCountries());
    }

    /**
     * Gets regions for a country (placeholder until API integration)
     * @param country The country to get regions for
     * @return Array of region names
     */
    @GetMapping("/regions/{country}")
    public ResponseEntity<?> getRegions(@PathVariable String country) {
        return ResponseEntity.ok(userService.getRegions(country));
    }

    /**
     * Gets cities for a region (placeholder until API integration)
     * @param country The country containing the region
     * @param region The region to get cities for
     * @return Array of city names
     */
    @GetMapping("/cities/{country}/{region}")
    public ResponseEntity<?> getCities(@PathVariable String country, @PathVariable String region) {
        return ResponseEntity.ok(userService.getCities(country, region));
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
     * Soft deletes a user by setting their is_active attribute to false
     * @param id The ID of the user to soft delete
     * @return Success message or error
     */
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> softDeleteUser(@PathVariable Long id) {
        try {
            userService.softDeleteUser(id);
            return ResponseEntity.ok(Collections.singletonMap("message", "User deactivated successfully"));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("error", e.getMessage()));
        }
    }
    
    /**
     * Restores a previously soft-deleted user by setting their is_active attribute back to true
     * @param id The ID of the user to restore
     * @return Success message or error
     */
    @PostMapping("/restore/{id}")
    public ResponseEntity<?> restoreUser(@PathVariable Long id) {
        try {
            userService.restoreUser(id);
            return ResponseEntity.ok(Collections.singletonMap("message", "User restored successfully"));
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
