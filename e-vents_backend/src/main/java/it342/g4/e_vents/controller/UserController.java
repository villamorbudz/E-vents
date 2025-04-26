package it342.g4.e_vents.controller;

import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import it342.g4.e_vents.model.User;
import it342.g4.e_vents.service.UserService;

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
public ResponseEntity<?> userExists(@RequestParam(required = false) String email) {
    System.out.println("Incoming email: " + email);
    if (email == null || email.trim().isEmpty()) {
        return ResponseEntity.badRequest().body("Email parameter is required");
    }

    boolean exists = userService.userExists(email);
    return ResponseEntity.ok(Collections.singletonMap("exists", exists));
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
