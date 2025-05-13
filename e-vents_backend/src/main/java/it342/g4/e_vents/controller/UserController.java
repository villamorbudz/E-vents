package it342.g4.e_vents.controller;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import it342.g4.e_vents.model.User;
import it342.g4.e_vents.security.JwtUtils;
import it342.g4.e_vents.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Controller for user-related operations
 */
@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
@Tag(name = "User", description = "User management APIs")
public class UserController {

    private final UserService userService;

    private final JwtUtils jwtUtils;
    
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    
    @Autowired
    public UserController(UserService userService, JwtUtils jwtUtils) {
        this.userService = userService;
        this.jwtUtils = jwtUtils;
    }
    
    /**
     * Registers a new user
     * @param user User data from request body
     * @return The registered user or error message
     */
    @PostMapping("/register")
    @Operation(summary = "Register a new user", description = "Creates a new user account in the system")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User successfully registered", 
                     content = @Content(mediaType = "application/json", schema = @Schema(implementation = User.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input or email already exists", content = @Content)
    })
    public ResponseEntity<?> registerUser(
        @Parameter(description = "User object to be registered", required = true) @RequestBody User user) {
        if (user.getFirstName() == null) user.setFirstName("");
        if (user.getLastName() == null) user.setLastName("");
        if (user.getEmail() == null) user.setEmail("");
        if (user.getContactNumber() == null) user.setContactNumber("");
        if (user.getCountry() == null) user.setCountry("");
        if (user.getPassword() == null) user.setPassword("");
        try {
            User registeredUser = userService.registerUser(user);
            
            // Generate JWT token
            String token = jwtUtils.generateToken(registeredUser);
            
            // Create response with token and user info
            Map<String, Object> response = new HashMap<>();
            response.put("token", token);
            response.put("userId", registeredUser.getUserId());
            response.put("email", registeredUser.getEmail());
            response.put("firstName", registeredUser.getFirstName());
            response.put("lastName", registeredUser.getLastName());
            response.put("role", registeredUser.getRole() != null ? registeredUser.getRole().getName() : "USER");
            response.put("country", registeredUser.getCountry());
            response.put("birthdate", registeredUser.getBirthdate());
            response.put("contactNumber", registeredUser.getContactNumber());
            response.put("dateCreated", registeredUser.getDateCreated());
            
            return ResponseEntity.ok(response);
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
     @Operation(summary = "Authenticate user", description = "Validates user credentials and returns a JWT token")
     @ApiResponses(value = {
         @ApiResponse(responseCode = "200", description = "Authentication successful", 
                      content = @Content(mediaType = "application/json")),
         @ApiResponse(responseCode = "401", description = "Invalid credentials", content = @Content)
     })
    public ResponseEntity<?> login(
            @Parameter(description = "User's email address", required = true) @RequestParam String email, 
            @Parameter(description = "User's password", required = true) @RequestParam String password) {
        try {
            User user = userService.login(email, password);
            
            // Generate JWT token
            String token = jwtUtils.generateToken(user);
            
            // Create response with token and user info
            Map<String, Object> response = new HashMap<>();
            response.put("token", token);
            response.put("userId", user.getUserId());
            response.put("email", user.getEmail());
            response.put("firstName", user.getFirstName());
            response.put("lastName", user.getLastName());
            response.put("role", user.getRole() != null ? user.getRole().getName() : "USER");
            
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

    /**
     * Retrieves all users
     * @return List of all users
     */
    @GetMapping("/all")
    @Operation(summary = "Get all users", description = "Retrieves a list of all users in the system")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved list of users", 
                     content = @Content(mediaType = "application/json", schema = @Schema(implementation = User.class)))
    })
    public ResponseEntity<?> getAllUsers() {
        // The @JsonFormat annotation on the dateCreated field in User class 
        // will handle the date formatting for all users
        return ResponseEntity.ok(userService.getAllUsers());
    }

    /**
     * Retrieves a user by ID
     * @param id The user ID
     * @return The user or 404 if not found
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get user by ID", description = "Retrieves a specific user by their ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved the user", 
                     content = @Content(mediaType = "application/json", schema = @Schema(implementation = User.class))),
        @ApiResponse(responseCode = "404", description = "User not found", content = @Content)
    })
    public ResponseEntity<?> getUserById(
            @Parameter(description = "ID of the user to retrieve", required = true) @PathVariable Long id) {
        return userService.getUser(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Deletes a user by ID
     * @param id The user ID
     * @return Success message or error
     */
    @DeleteMapping("/{id}/delete")
    @Operation(summary = "Delete a user permanently", description = "Permanently deletes a user from the system")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User successfully deleted", 
                     content = @Content(mediaType = "application/json")),
        @ApiResponse(responseCode = "404", description = "User not found", content = @Content),
        @ApiResponse(responseCode = "400", description = "Bad request", content = @Content)
    })
    public ResponseEntity<?> deleteUser(
            @Parameter(description = "ID of the user to delete", required = true) @PathVariable Long id) {
        try {
            boolean deleted = userService.deleteUser(id);
            if (deleted) {
                return ResponseEntity.ok(Collections.singletonMap("message", "User deleted successfully"));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.singletonMap("error", "User not found"));
            }
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("error", e.getMessage()));
        }
    }

    
    /**
     * Soft deletes a user by setting their is_active attribute to false
     * @param id The user ID
     * @return Success message or error
     */
    @PutMapping("/{id}/deactivate")
    @Operation(summary = "Deactivate a user", description = "Soft deletes a user by setting their is_active attribute to false")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User successfully deactivated", 
                     content = @Content(mediaType = "application/json")),
        @ApiResponse(responseCode = "404", description = "User not found", content = @Content),
        @ApiResponse(responseCode = "400", description = "Bad request", content = @Content)
    })
    public ResponseEntity<?> softDeleteUser(
            @Parameter(description = "ID of the user to deactivate", required = true) @PathVariable Long id) {
        try {
            User user = userService.softDeleteUser(id);
            return ResponseEntity.ok(Collections.singletonMap("message", "User soft deleted successfully"));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.singletonMap("error", e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("error", e.getMessage()));
        }
    }
    
    /**
     * Restores a previously soft-deleted user by setting their is_active attribute back to true
     * @param id The user ID
     * @return Success message or error
     */
    @PutMapping("/restore/{id}")
    @Operation(summary = "Restore a user", description = "Restores a previously deactivated user by setting their is_active attribute to true")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User successfully restored", 
                     content = @Content(mediaType = "application/json")),
        @ApiResponse(responseCode = "404", description = "User not found", content = @Content),
        @ApiResponse(responseCode = "400", description = "Bad request", content = @Content)
    })
    public ResponseEntity<?> restoreUser(
            @Parameter(description = "ID of the user to restore", required = true) @PathVariable Long id) {
        try {
            User user = userService.restoreUser(id);
            return ResponseEntity.ok(Collections.singletonMap("message", "User restored successfully"));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.singletonMap("error", e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("error", e.getMessage()));
        }
    }

    /**
     * Changes a user's password
     * @param req Request containing email and new password
     * @return Success message or error
     */
    @PostMapping("/change-password")
    @Operation(summary = "Change user password", description = "Updates a user's password by their email address")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Password successfully changed", 
                     content = @Content(mediaType = "application/json")),
        @ApiResponse(responseCode = "404", description = "User not found", content = @Content),
        @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content)
    })
    public ResponseEntity<?> changePassword(
            @Parameter(description = "Request containing email and new password", required = true) 
            @RequestBody ChangePasswordRequest req) {
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
    @Operation(summary = "Get countries list", description = "Retrieves a list of available countries")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved list of countries", 
                     content = @Content(array = @ArraySchema(schema = @Schema(implementation = String.class))))
    })
    public ResponseEntity<?> getCountries() {
        return ResponseEntity.ok(userService.getCountries());
    }

    /**
     * Checks if a user with the given email exists
     * @param email The email to check
     * @return JSON with exists flag
     */
    @GetMapping("/exists")
    @Operation(summary = "Check if email exists", description = "Checks if a user with the given email already exists in the system")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully checked email existence", 
                     content = @Content(mediaType = "application/json")),
        @ApiResponse(responseCode = "400", description = "Email parameter is required", content = @Content)
    })
    public ResponseEntity<?> userExists(
            @Parameter(description = "Email address to check", required = true) @RequestParam(required = false) String email) {
        System.out.println("Incoming email: " + email);
        if (email == null || email.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Email parameter is required");
        }

        boolean exists = userService.userExists(email);
        return ResponseEntity.ok(Collections.singletonMap("exists", exists));
    }

    /**
     * Updates a user's profile information
     * @param id The user ID
     * @param updatedUser Updated user data
     * @return The updated user or 404 if not found
     */
    @PutMapping("/{id}")
    @Operation(summary = "Update user profile", description = "Updates a user's profile information")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User successfully updated", 
                     content = @Content(mediaType = "application/json", schema = @Schema(implementation = User.class))),
        @ApiResponse(responseCode = "404", description = "User not found", content = @Content),
        @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content)
    })
    public ResponseEntity<?> updateUser(
            @Parameter(description = "ID of the user to update", required = true) @PathVariable Long id, 
            @Parameter(description = "Updated user details", required = true) @RequestBody User updatedUser) {
        try {
            logger.info("Updating user with ID: {}", id);
            logger.debug("Update payload: {}", updatedUser);
            
            return userService.updateUser(id, updatedUser)
                    .map(user -> {
                        logger.info("User {} updated successfully", id);
                        return ResponseEntity.ok(user);
                    })
                    .orElseGet(() -> {
                        logger.warn("User with ID {} not found for update", id);
                        return ResponseEntity.notFound().build();
                    });
        } catch (EntityNotFoundException e) {
            logger.error("Entity not found during user update: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Collections.singletonMap("error", e.getMessage()));
        } catch (RuntimeException e) {
            logger.error("Error updating user: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Collections.singletonMap("error", e.getMessage()));
        } catch (Exception e) {
            logger.error("Unexpected error updating user: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Collections.singletonMap("error", "An unexpected error occurred"));
        }
    }

    /**
     * Get the count of active users
     * @return ResponseEntity with the count of active users
     */
    @Operation(summary = "Get count of active users", description = "Returns the total number of active users in the system")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved count", 
                    content = @Content(mediaType = "application/json", 
                    schema = @Schema(implementation = Long.class))),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/count")
    public ResponseEntity<Map<String, Long>> countActiveUsers() {
        try {
            long count = userService.countActiveUsers();
            Map<String, Long> response = new HashMap<>();
            response.put("count", count);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * DTO for password change requests
     */
    @Schema(description = "Request object for changing a user's password")
    public static class ChangePasswordRequest {
        @Schema(description = "User's email address", required = true)
        private String email;
        
        @Schema(description = "New password", required = true)
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
