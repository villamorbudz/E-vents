package it342.g4.e_vents.service;

import java.util.List;
import java.util.Optional;
import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import it342.g4.e_vents.model.Role;
import it342.g4.e_vents.model.User;
import it342.g4.e_vents.repository.RoleRepository;
import it342.g4.e_vents.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Date;
import java.text.SimpleDateFormat;

@Service
public class UserService {
    
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    
    @Autowired
    public UserService(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Retrieves all users from the database
     * @return List of all users
     */
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    /**
     * Retrieves a user by their ID
     * @param id The user ID to look up
     * @return Optional containing the user if found
     */
    public Optional<User> getUser(Long id) {
        return userRepository.findById(id);
    }

    /**
     * Registers a new user with encrypted password and default role
     * @param user The user to register
     * @return The registered user with ID
     * @throws RuntimeException if user already exists or default role not found
     */
    public User registerUser(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Error: User already exists");
        }

        // Encrypt the password before saving
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        
        // Set default role to 'attendee' with roleId 1
        Role defaultRole = roleRepository.findById(2L)
                .orElseThrow(() -> new RuntimeException("Default role not found"));
        user.setRole(defaultRole);

        // Set creation date to current time (same handling as birthdate)
        user.setDateCreated(new Date());

        return userRepository.save(user);
    }

    public User getUserById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    /**
     * Authenticates a user by email and password
     * @param email The user's email
     * @param password The user's password (plain text)
     * @return The authenticated user
     * @throws RuntimeException if user not found or password invalid
     */
    public User login(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Error: User not found"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        return user;
    }

    /**
     * Gets list of countries from a Countries API
     * @return Array of country names
     */
    public String[] getCountries() {
        try {
            // Using restcountries.com API to fetch countries
            String url = "https://restcountries.com/v3.1/all?fields=name";
            return fetchCountriesFromAPI(url);
        } catch (Exception e) {
            e.printStackTrace();
            // Fallback to N/A if API fails
            return new String[]{"N/A"};
        }
    }

    /**
     * Fetch countries from the REST Countries API
     * @param apiUrl The API URL to fetch countries
     * @return Array of country names
     */
    private String[] fetchCountriesFromAPI(String apiUrl) throws Exception {
        java.net.URL url = new java.net.URL(apiUrl);
        java.net.HttpURLConnection conn = (java.net.HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        
        if (conn.getResponseCode() != 200) {
            throw new Exception("Failed to fetch countries: " + conn.getResponseCode());
        }
        
        java.io.BufferedReader reader = new java.io.BufferedReader(
                new java.io.InputStreamReader(conn.getInputStream()));
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            response.append(line);
        }
        reader.close();
        
        // Parse JSON response to get country names
        JSONArray jsonArray = new JSONArray(response.toString());
        List<String> countries = new ArrayList<>();
        
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject countryObj = jsonArray.getJSONObject(i);
            JSONObject nameObj = countryObj.getJSONObject("name");
            String commonName = nameObj.getString("common");
            countries.add(commonName);
        }
        
        // Sort countries alphabetically
        countries.sort(String::compareTo);
        
        return countries.toArray(new String[0]);
    }
    
    /**
     * Checks if a user with the given email exists
     * @param email The email to check
     * @return true if user exists, false otherwise
     */
    public boolean userExists(String email) {
        return userRepository.existsByEmail(email);
    }

    /**
     * Changes a user's password by email
     * @param email The email of the user
     * @param newPassword The new password (plain text)
     * @return true if password changed successfully, false if user not found
     */
    public boolean changePasswordByEmail(String email, String newPassword) {
        return userRepository.findByEmail(email)
                .map(user -> {
                    user.setPassword(passwordEncoder.encode(newPassword));
                    userRepository.save(user);
                    return true;
                })
                .orElse(false);
    }

    /**
     * Updates a user's profile information
     * @param id User ID to update
     * @param updatedUser Updated user data
     * @return Optional containing the updated user or empty if not found
     */
    public Optional<User> updateUser(Long id, User updatedUser) {
        Optional<User> existingUser = getUser(id);

        if (existingUser.isPresent()) {
            User user = existingUser.get();

            // Update fields only if they are not null
            if (updatedUser.getFirstName() != null) {
                user.setFirstName(updatedUser.getFirstName());
            }
            
            if (updatedUser.getLastName() != null) {
                user.setLastName(updatedUser.getLastName());
            }
            
            if (updatedUser.getContactNumber() != null) {
                user.setContactNumber(updatedUser.getContactNumber());
            }
            
            // Birthdate can be null
            user.setBirthdate(updatedUser.getBirthdate());
            
            if (updatedUser.getCountry() != null) {
                user.setCountry(updatedUser.getCountry());
            }

            // Handle role update if provided
            if (updatedUser.getRole() != null && updatedUser.getRole().getRoleId() != null) {
                try {
                    Role role = roleRepository.findById(updatedUser.getRole().getRoleId())
                        .orElseThrow(() -> new EntityNotFoundException("Role not found with ID: " + updatedUser.getRole().getRoleId()));
                    user.setRole(role);
                } catch (Exception e) {
                    throw new RuntimeException("Error updating user role: " + e.getMessage());
                }
            }

            // Save and return updated user
            return Optional.of(userRepository.save(user));
        }

        return Optional.empty();
    }
    
    /**
     * Gets the most recently registered users
     * @param limit The maximum number of users to return
     * @return List of recent users
     */
    public List<User> getRecentUsers(int limit) {
        // This is a simple implementation that returns the first N users
        // In a real application, you would order by registration date
        return userRepository.findAll().stream()
                .sorted((u1, u2) -> u2.getUserId().compareTo(u1.getUserId()))
                .limit(limit)
                .toList();
    }
    
    /**
     * Soft deletes a user by setting their is_active attribute to false
     * @param userId The ID of the user to soft delete
     * @return The updated User object
     * @throws EntityNotFoundException if the user is not found
     */
    public User softDeleteUser(Long userId) {
        return updateUserActiveStatus(userId, false);
    }
    
    /**
     * Restores a previously soft-deleted user by setting their is_active attribute back to true
     * @param userId The ID of the user to restore
     * @return The updated User object
     * @throws EntityNotFoundException if the user is not found
     */
    public User restoreUser(Long userId) {
        return updateUserActiveStatus(userId, true);
    }
    
    /**
     * Helper method to update a user's active status
     * @param userId The ID of the user to update
     * @param activeStatus The new active status
     * @return The updated User object
     * @throws EntityNotFoundException if the user is not found
     */
    private User updateUserActiveStatus(Long userId, boolean activeStatus) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + userId));
        
        user.setActive(activeStatus);
        return userRepository.save(user);
    }
    
    /**
     * Counts all users in the system
     * @return The total number of users
     */
    public long countAllUsers() {
        return userRepository.count();
    }
    
    /**
     * Deletes a user by ID
     * @param id The user ID
     * @return true if deleted, false if not found
     */
    public boolean deleteUser(Long id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
            return true;
        }
        return false;
    }

    /**
     * Counts the number of active users in the system
     * @return The count of active users
     */
    public long countActiveUsers() {
        return userRepository.countByIsActiveTrue();
    }
}
