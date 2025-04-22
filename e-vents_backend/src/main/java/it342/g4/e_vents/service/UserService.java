package it342.g4.e_vents.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import it342.g4.e_vents.model.Role;
import it342.g4.e_vents.model.User;
import it342.g4.e_vents.repository.RoleRepository;
import it342.g4.e_vents.repository.UserRepository;

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
    //public String[] getCountries() {
        //return new String[]{"---"};
    //}

    public String[] getCountries() {
        return new String[]{
            "United States", "Canada", "United Kingdom", "Australia", 
            "Germany", "France", "Japan", "Brazil", "India", "China"
        };
    }

    /*  Method to get regions for a country (placeholder until API integration)
    public String[] getRegions(String country) {
        return new String[]{"---"};
    }*/

    public String[] getRegions(String country) {
        switch (country) {
            case "United States":
                return new String[]{
                    "Alabama", "Alaska", "Arizona", "Arkansas", "California", 
                    "Colorado", "Connecticut", "Delaware", "Florida", "Georgia"
                };
            case "Canada":
                return new String[]{
                    "Alberta", "British Columbia", "Manitoba", "New Brunswick", 
                    "Newfoundland and Labrador", "Nova Scotia", "Ontario", "Quebec"
                };
            case "United Kingdom":
                return new String[]{
                    "England", "Scotland", "Wales", "Northern Ireland"
                };
            default:
                return new String[]{"Other"};
        }
    }

    /*  Method to get cities for a region (placeholder until API integration)
    public String[] getCities(String country, String region) {
        return new String[]{"---"};
    }*/

    public String[] getCities(String country, String region) {
        if (country.equals("United States")) {
            if (region.equals("California")) {
                return new String[]{
                    "Los Angeles", "San Francisco", "San Diego", "Sacramento", "San Jose"
                };
            } else if (region.equals("New York")) {
                return new String[]{
                    "New York City", "Buffalo", "Rochester", "Albany", "Syracuse"
                };
            }
        } else if (country.equals("Canada")) {
            if (region.equals("Ontario")) {
                return new String[]{
                    "Toronto", "Ottawa", "Hamilton", "London", "Windsor"
                };
            }
        }
        return new String[]{"City 1", "City 2", "City 3"};
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
