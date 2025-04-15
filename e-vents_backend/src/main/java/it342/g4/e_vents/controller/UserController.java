package it342.g4.e_vents.controller;

import it342.g4.e_vents.model.User;
import it342.g4.e_vents.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody User user) {
        try {
            User registeredUser = userService.registerUser(user);
            return ResponseEntity.ok(registeredUser);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestParam String email, @RequestParam String password) {
        try {
            User user = userService.login(email, password);
            return ResponseEntity.ok(user);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Endpoint to get list of countries (placeholder until API integration)
    @GetMapping("/countries")
    public ResponseEntity<?> getCountries() {
        return ResponseEntity.ok(userService.getCountries());
    }

    // Endpoint to get regions for a country (placeholder until API integration)
    @GetMapping("/regions/{country}")
    public ResponseEntity<?> getRegions(@PathVariable String country) {
        return ResponseEntity.ok(userService.getRegions(country));
    }

    // Endpoint to get cities for a region (placeholder until API integration)
    @GetMapping("/cities/{country}/{region}")
    public ResponseEntity<?> getCities(@PathVariable String country, @PathVariable String region) {
        return ResponseEntity.ok(userService.getCities(country, region));
    }

    @GetMapping("/exists")
    public ResponseEntity<?> userExists(@RequestParam String email) {
        boolean exists = userService.userExists(email);
        return ResponseEntity.ok(java.util.Collections.singletonMap("exists", exists));
    }

    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody ChangePasswordRequest req) {
        try {
            boolean changed = userService.changePasswordByEmail(req.getEmail(), req.getPassword());
            if (changed) {
                return ResponseEntity.ok("Password changed successfully");
            } else {
                return ResponseEntity.badRequest().body("User not found");
            }
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    public static class ChangePasswordRequest {
        private String email;
        private String password;
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }
}
