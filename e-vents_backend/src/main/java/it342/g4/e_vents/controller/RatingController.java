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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/ratings")
@CrossOrigin(origins = "*")
@Validated
@Tag(name = "Rating", description = "Rating management APIs")
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
    @Operation(summary = "Create a new rating", description = "Creates a new rating for an entity (event, venue, etc.)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Rating successfully created", 
                     content = @Content(mediaType = "application/json", schema = @Schema(implementation = Rating.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content)
    })
    public ResponseEntity<Rating> createRating(
            @Parameter(description = "Rating value (0-5)", required = true) 
            @RequestParam @Min(0) @Max(5) int ratingValue,
            @Parameter(description = "ID of the entity being rated", required = true) 
            @RequestParam Long entityId,
            @Parameter(description = "Type of entity (EVENT, VENUE, etc.)", required = true) 
            @RequestParam Rating.EntityType entityType,
            @Parameter(description = "Optional feedback message") 
            @RequestParam(required = false) String message,
            @Parameter(description = "ID of the user submitting the rating", required = true) 
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
    @Operation(summary = "Get ratings for entity", description = "Retrieves all ratings for a specific entity")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved ratings", 
                     content = @Content(mediaType = "application/json", schema = @Schema(implementation = Rating.class)))
    })
    public ResponseEntity<List<Rating>> getRatingsForEntity(
            @Parameter(description = "ID of the entity to get ratings for", required = true) 
            @PathVariable Long entityId,
            @Parameter(description = "Type of entity (EVENT, VENUE, etc.)", required = true) 
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
    @Operation(summary = "Get user ratings", description = "Retrieves all ratings submitted by a specific user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved user ratings", 
                     content = @Content(mediaType = "application/json", schema = @Schema(implementation = Rating.class)))
    })
    public ResponseEntity<List<Rating>> getUserRatings(
            @Parameter(description = "ID of the user whose ratings to retrieve", required = true) 
            @PathVariable Long userId) {
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
    @Operation(summary = "Update rating message", description = "Updates an existing rating's message")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Rating successfully updated", 
                     content = @Content(mediaType = "application/json", schema = @Schema(implementation = Rating.class))),
        @ApiResponse(responseCode = "404", description = "Rating not found", content = @Content)
    })
    public ResponseEntity<?> updateRating(
            @Parameter(description = "ID of the rating to update", required = true) 
            @PathVariable Long ratingId,
            @Parameter(description = "New message content", required = true) 
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
    @Operation(summary = "Delete rating", description = "Deletes a rating by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Rating successfully deleted", 
                     content = @Content(mediaType = "application/json")),
        @ApiResponse(responseCode = "404", description = "Rating not found", content = @Content)
    })
    public ResponseEntity<?> deleteRating(
            @Parameter(description = "ID of the rating to delete", required = true) 
            @PathVariable Long ratingId) {
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
    @Operation(summary = "Get average rating", description = "Calculates and returns the average rating value for a specific entity")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully calculated average rating", 
                     content = @Content(mediaType = "application/json"))
    })
    public ResponseEntity<Map<String, Double>> getAverageRating(
            @Parameter(description = "ID of the entity to get the average rating for", required = true) 
            @PathVariable Long entityId,
            @Parameter(description = "Type of entity (EVENT, VENUE, etc.)", required = true) 
            @RequestParam Rating.EntityType entityType) {
        double average = ratingService.getAverageRating(entityId, entityType);
        return ResponseEntity.ok(Map.of("average", average));
    }
}
