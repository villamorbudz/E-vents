package it342.g4.e_vents.service;

import it342.g4.e_vents.model.Rating;
import it342.g4.e_vents.model.User;
import it342.g4.e_vents.repository.RatingRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class RatingService {
    private final RatingRepository ratingRepository;
    private final UserService userService;

    @Autowired
    public RatingService(RatingRepository ratingRepository, UserService userService) {
        this.ratingRepository = ratingRepository;
        this.userService = userService;
    }

    @Transactional
    public Rating createRating(int ratingValue, Long entityId, Rating.EntityType entityType, String message, Long userId) {
        User user = userService.getUser(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));
        
        Rating rating = Rating.builder()
                .ratingValue(ratingValue)
                .ratedEntityId(entityId)
                .ratedEntityType(entityType)
                .message(message)
                .user(user)
                .build();

        return ratingRepository.save(rating);
    }

    @Transactional(readOnly = true)
    public List<Rating> getRatingsForEntity(Long entityId, Rating.EntityType entityType) {
        return ratingRepository.findByRatedEntityIdAndRatedEntityTypeAndIsActiveTrue(entityId, entityType);
    }

    @Transactional(readOnly = true)
    public List<Rating> getUserRatings(Long userId) {
        User user = userService.getUser(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));
        return ratingRepository.findByUserAndIsActiveTrue(user);
    }

    @Transactional
    public Rating updateRating(Long ratingId, String message) {
        Rating rating = ratingRepository.findById(ratingId)
                .orElseThrow(() -> new EntityNotFoundException("Rating not found with id: " + ratingId));

        rating.setMessage(message);
        return ratingRepository.save(rating);
    }

    @Transactional
    public void deleteRating(Long ratingId) {
        Rating rating = ratingRepository.findById(ratingId)
                .orElseThrow(() -> new EntityNotFoundException("Rating not found with id: " + ratingId));
        
        rating.setActive(false);
        ratingRepository.save(rating);
    }

    @Transactional(readOnly = true)
    public double getAverageRating(Long entityId, Rating.EntityType entityType) {
        List<Rating> ratings = ratingRepository.findByRatedEntityIdAndRatedEntityType(entityId, entityType);
        if (ratings.isEmpty()) {
            return 0.0;
        }
        return ratings.stream()
                .filter(Rating::isActive)
                .mapToInt(Rating::getRatingValue)
                .average()
                .orElse(0.0);
    }
}
