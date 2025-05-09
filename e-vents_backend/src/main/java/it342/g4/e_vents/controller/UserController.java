package it342.g4.e_vents.controller;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

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
            response.put("city", registeredUser.getCity());
            response.put("region", registeredUser.getRegion());
            response.put("country", registeredUser.getCountry());
            response.put("postalCode", registeredUser.getPostalCode());
            response.put("birthdate", registeredUser.getBirthdate());
            response.put("contactNumber", registeredUser.getContactNumber());
            
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
                     content = @Content(mediaType = "application/json"))
    })
    public ResponseEntity<?> getCountries() {
        return ResponseEntity.ok(userService.getCountries());
    }

    /**
     * Gets regions for a country (placeholder until API integration)
     * @param country The country to get regions for
     * @return Array of region names
     */
    @GetMapping("/regions/{country}")
    @Operation(summary = "Get regions by country", description = "Retrieves a list of regions for a specific country")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved list of regions", 
                     content = @Content(mediaType = "application/json"))
    })
    public ResponseEntity<?> getRegions(
            @Parameter(description = "Country to get regions for", required = true) @PathVariable String country) {
        return ResponseEntity.ok(userService.getRegions(country));
    }

    /**
     * Gets cities for a region (placeholder until API integration)
     * @param country The country containing the region
     * @param region The region to get cities for
     * @return Array of city names
     */
    @GetMapping("/cities/{country}/{region}")
    @Operation(summary = "Get cities by region", description = "Retrieves a list of cities for a specific region in a country")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved list of cities", 
                     content = @Content(mediaType = "application/json"))
    })
    public ResponseEntity<?> getCities(
            @Parameter(description = "Country containing the region", required = true) @PathVariable String country, 
            @Parameter(description = "Region to get cities for", required = true) @PathVariable String region) {
        return ResponseEntity.ok(userService.getCities(country, region));
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
            return userService.updateUser(id, updatedUser)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("error", e.getMessage()));
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
