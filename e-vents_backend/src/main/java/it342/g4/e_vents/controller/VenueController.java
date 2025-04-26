package it342.g4.e_vents.controller;

import it342.g4.e_vents.model.Venue;
import it342.g4.e_vents.service.VenueService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

/**
 * Controller for venue-related operations
 */
@RestController
@RequestMapping("/api/venues")
@CrossOrigin(origins = "*")
public class VenueController {

    private final VenueService venueService;
    
    @Autowired
    public VenueController(VenueService venueService) {
        this.venueService = venueService;
    }
    
    /**
     * Retrieves all venues
     * @return List of all venues
     */
    @GetMapping
    public ResponseEntity<List<Venue>> getAllVenues() {
        return ResponseEntity.ok(venueService.getAllVenues());
    }
    
    /**
     * Retrieves a venue by ID
     * @param id The venue ID
     * @return The venue or 404 if not found
     */
    @GetMapping("/{id}")
    public ResponseEntity<Venue> getVenueById(@PathVariable Long id) {
        return venueService.findVenueById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * Creates a new venue
     * @param venue Venue data from request body
     * @return The created venue
     */
    @PostMapping
    public ResponseEntity<Venue> createVenue(@RequestBody Venue venue) {
        try {
            Venue result = venueService.findOrCreateVenue(
                venue.getName(),
                venue.getAddress(),
                venue.getCity(),
                venue.getCountry()
            );
            // If the venue already existed, return 200 OK, else 201 Created
            boolean isNew = result.getVenueId().equals(venue.getVenueId()) == false;
            if (isNew) {
                return ResponseEntity.status(HttpStatus.CREATED).body(result);
            } else {
                return ResponseEntity.ok(result);
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Updates an existing venue
     * @param id The venue ID to update
     * @param venueDetails Updated venue data
     * @return The updated venue or error
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateVenue(@PathVariable Long id, @RequestBody Venue venueDetails) {
        try {
            // Set the ID from the path
            venueDetails.setVenueId(id);
            
            // Update and return
            Venue updatedVenue = venueService.updateVenue(venueDetails);
            return ResponseEntity.ok(updatedVenue);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Collections.singletonMap("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Collections.singletonMap("error", e.getMessage()));
        }
    }
    
    /**
     * Deletes a venue
     * @param id The venue ID to delete
     * @return Success message or error
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteVenue(@PathVariable Long id) {
        try {
            venueService.deleteVenue(id);
            return ResponseEntity.ok(Collections.singletonMap("message", "Venue deleted successfully"));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Collections.singletonMap("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Collections.singletonMap("error", e.getMessage()));
        }
    }
}
