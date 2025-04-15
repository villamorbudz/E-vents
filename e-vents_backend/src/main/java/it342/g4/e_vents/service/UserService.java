package it342.g4.e_vents.service;

import it342.g4.e_vents.model.User;
import it342.g4.e_vents.model.Role;
import it342.g4.e_vents.repository.UserRepository;
import it342.g4.e_vents.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private RoleRepository roleRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    public User registerUser(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Error: User already exists");
        }

        // Encrypt the password before saving
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        
        // Set default role to 'attendee' with roleId 1
        Role defaultRole = roleRepository.findById(1L).orElseThrow(() -> new RuntimeException("Default role not found"));
        user.setRole(defaultRole);

        return userRepository.save(user);
    }

    public User login(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Error: User not found"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        return user;
    }

    // Method to get list of countries (placeholder until API integration)
    public String[] getCountries() {
        return new String[]{"---"};
    }

    // Method to get regions for a country (placeholder until API integration)
    public String[] getRegions(String country) {
        return new String[]{"---"};
    }

    // Method to get cities for a region (placeholder until API integration)
    public String[] getCities(String country, String region) {
        return new String[]{"---"};
    }

    public boolean userExists(String email) {
        return userRepository.existsByEmail(email);
    }

    public boolean changePasswordByEmail(String email, String newPassword) {
        java.util.Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setPassword(passwordEncoder.encode(newPassword));
            editUser(user);
            return true;
        }
        return false;
    }

    public User editUser(User user) {
        // Assumes user.id is set and valid
        return userRepository.save(user);
    }
}
