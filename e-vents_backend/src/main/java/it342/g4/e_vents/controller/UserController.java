package it342.g4.e_vents.controller;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import it342.g4.e_vents.dto.LoginRequest;
import it342.g4.e_vents.model.User;
import it342.g4.e_vents.security.JwtUtils;
import it342.g4.e_vents.service.UserService;

/**
 * Controller for user-related operations
 */
@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
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
    public ResponseEntity<?> registerUser(@RequestBody User user) {
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
            response.put("role", registeredUser.getRole() != null ? registeredUser.getRole() : "USER");
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
     /*  @PostMapping("/login")
    public ResponseEntity<?> login(@RequestParam String email, @RequestParam String password) {
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
            response.put("role", user.getRole() != null ? user.getRole() : "USER");
            
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }*/

    @PostMapping("/login")
public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
    try {
        User user = userService.login(loginRequest.getEmail(), loginRequest.getPassword());

        String token = jwtUtils.generateToken(user);

        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("userId", user.getUserId());
        response.put("email", user.getEmail());
        response.put("firstName", user.getFirstName());
        response.put("lastName", user.getLastName());
        response.put("role", user.getRole() != null ? user.getRole() : "USER");

        return ResponseEntity.ok(response);
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
    public ResponseEntity<?> userExists(@RequestParam(required = false) String email) {
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
public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody User updatedUser) {
    try {
        // Log incoming request details
        System.out.println("Updating user with ID: " + id);
        
        // Authorization check
        if (!isAuthorizedForUser(id)) {
            System.out.println("Authorization failed for user update. ID: " + id);
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(Collections.singletonMap("error", "You are not authorized to update this user profile"));
        }
        
        // Ensure ID in path matches ID in body
        if (updatedUser.getUserId() != null && !updatedUser.getUserId().equals(id)) {
            return ResponseEntity.badRequest()
                .body(Collections.singletonMap("error", "User ID in request body doesn't match path parameter"));
        }
        
        // Set the ID from the path parameter
        updatedUser.setUserId(id);
        
        return userService.updateUser(id, updatedUser)
                .map(user -> {
                    System.out.println("User updated successfully: " + user.getUserId());
                    return ResponseEntity.ok(user);
                })
                .orElse(ResponseEntity.notFound().build());
    } catch (RuntimeException e) {
        System.out.println("Error updating user: " + e.getMessage());
        e.printStackTrace();
        return ResponseEntity.badRequest().body(Collections.singletonMap("error", e.getMessage()));
    }
}

    /**
 * Helper method to verify if the authenticated user has permission to access/modify 
 * the requested user profile
 */
    private boolean isAuthorizedForUser(Long userId) {
        try {
            // Get current authentication
            var authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null) {
                System.out.println("No authentication found in context");
                return false;
            }
            
            // Get email from authentication
            String email = authentication.getName();
            System.out.println("Authenticated email: " + email);
            
            // Check if user is admin or the same user
            boolean isAdmin = authentication.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
            
            if (isAdmin) {
                System.out.println("Admin access granted");
                return true;
            }
            
            // Get user by ID to check if it matches authenticated user
            var optionalUser = userService.getUser(userId);
            if (optionalUser.isEmpty()) {
                System.out.println("User ID not found: " + userId);
                return false;
            }
            
            var user = optionalUser.get();
            boolean isSameUser = user.getEmail().equals(email);
            System.out.println("User match check: " + isSameUser);
            
            return isSameUser;
        } catch (Exception e) {
            System.out.println("Error in authorization check: " + e.getMessage());
            return false;
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
