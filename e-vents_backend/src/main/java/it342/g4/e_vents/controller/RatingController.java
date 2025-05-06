package it342.g4.e_vents.controller;

import it342.g4.e_vents.model.Rating;
import it342.g4.e_vents.service.RatingService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/ratings")
@CrossOrigin(origins = "*")
@Validated
public class RatingController {
    private final RatingService ratingService;

    @Autowired
    public RatingController(RatingService ratingService) {
        this.ratingService = ratingService;
    }

    /**
     * Creates a new rating for an entity
     * 
     * @param ratingValue The numeric value of the rating (0-5)
     * @param entityId The ID of the entity being rated
     * @param entityType The type of entity (EVENT, VENUE, etc.)
     * @param message Optional feedback message accompanying the rating
     * @param userId The ID of the user submitting the rating (from request header)
     * @return The created Rating object with HTTP 201 status, or HTTP 400 if creation fails
     */
    @PostMapping
    public ResponseEntity<Rating> createRating(
            @RequestParam @Min(0) @Max(5) int ratingValue,
            @RequestParam Long entityId,
            @RequestParam Rating.EntityType entityType,
            @RequestParam(required = false) String message,
            @RequestHeader("User-Id") Long userId) {
        try {
            Rating rating = ratingService.createRating(ratingValue, entityId, entityType, message, userId);
            return ResponseEntity.status(HttpStatus.CREATED).body(rating);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Retrieves all ratings for a specific entity
     * 
     * @param entityId The ID of the entity to get ratings for
     * @param entityType The type of entity (EVENT, VENUE, etc.)
     * @return List of ratings for the specified entity
     */
    @GetMapping("/entity/{entityId}")
    public ResponseEntity<List<Rating>> getRatingsForEntity(
            @PathVariable Long entityId,
            @RequestParam Rating.EntityType entityType) {
        List<Rating> ratings = ratingService.getRatingsForEntity(entityId, entityType);
        return ResponseEntity.ok(ratings);
    }

    /**
     * Retrieves all ratings submitted by a specific user
     * 
     * @param userId The ID of the user whose ratings to retrieve
     * @return List of ratings submitted by the specified user
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Rating>> getUserRatings(@PathVariable Long userId) {
        List<Rating> ratings = ratingService.getUserRatings(userId);
        return ResponseEntity.ok(ratings);
    }

    /**
     * Updates an existing rating's message
     * 
     * @param ratingId The ID of the rating to update
     * @param message The new message content
     * @return The updated Rating object, or HTTP 404 if rating not found
     */
    @PutMapping("/{ratingId}")
    public ResponseEntity<?> updateRating(
            @PathVariable Long ratingId,
            @RequestParam String message) {
        try {
            Rating rating = ratingService.updateRating(ratingId, message);
            return ResponseEntity.ok(rating);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Deletes a rating by its ID
     * 
     * @param ratingId The ID of the rating to delete
     * @return Success message, or HTTP 404 if rating not found
     */
    @DeleteMapping("/{ratingId}")
    public ResponseEntity<?> deleteRating(@PathVariable Long ratingId) {
        try {
            ratingService.deleteRating(ratingId);
            return ResponseEntity.ok(Map.of("message", "Rating deleted successfully"));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Calculates and returns the average rating value for a specific entity
     * 
     * @param entityId The ID of the entity to get the average rating for
     * @param entityType The type of entity (EVENT, VENUE, etc.)
     * @return Map containing the average rating value
     */
    @GetMapping("/average/{entityId}")
    public ResponseEntity<Map<String, Double>> getAverageRating(
            @PathVariable Long entityId,
            @RequestParam Rating.EntityType entityType) {
        double average = ratingService.getAverageRating(entityId, entityType);
        return ResponseEntity.ok(Map.of("average", average));
    }
}
