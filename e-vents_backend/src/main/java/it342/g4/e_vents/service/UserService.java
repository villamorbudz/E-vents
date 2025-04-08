package it342.g4.e_vents.service;

import it342.g4.e_vents.model.User;
import it342.g4.e_vents.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    public User registerUser(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        // Encrypt the password before saving
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        
        return userRepository.save(user);
    }

    public User login(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        return user;
    }

    // Method to get list of countries (placeholder until API integration)
    public String[] getCountries() {
        return new String[]{"Select Country"};
    }

    // Method to get regions for a country (placeholder until API integration)
    public String[] getRegions(String country) {
        return new String[]{"Select Region"};
    }

    // Method to get cities for a region (placeholder until API integration)
    public String[] getCities(String country, String region) {
        return new String[]{"Select City"};
    }
}
