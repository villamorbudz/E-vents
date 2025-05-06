package it342.g4.e_vents.controller;

import it342.g4.e_vents.model.Venue;
import it342.g4.e_vents.service.VenueService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * REST Controller for venue-related operations
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
     * Get all venues
     */
    @GetMapping
    public ResponseEntity<List<Venue>> getAllVenues() {
        return ResponseEntity.ok(venueService.getAllVenues());
    }
    
    /**
     * Get venue by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<Venue> getVenueById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(venueService.getVenueById(id));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * Create or update a venue
     */
    @PostMapping
    public ResponseEntity<Venue> createVenue(@RequestBody Venue venue) {
        try {
            Venue savedVenue = venueService.createVenue(venue);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedVenue);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    /**
     * Find or create a venue using Google Places data
     */
    @PostMapping("/places")
    public ResponseEntity<Venue> findOrCreateVenue(@RequestBody Map<String, Object> placeData) {
        try {
            String name = (String) placeData.get("name");
            String formattedAddress = (String) placeData.get("formattedAddress");
            String googlePlaceId = (String) placeData.get("placeId");
            double latitude = ((Number) placeData.get("latitude")).doubleValue();
            double longitude = ((Number) placeData.get("longitude")).doubleValue();

            Venue venue = venueService.findOrCreateVenue(
                name,
                formattedAddress,
                googlePlaceId,
                latitude,
                longitude
            );
            return ResponseEntity.ok(venue);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    /**
     * Update an existing venue
     */
    @PutMapping("/{id}")
    public ResponseEntity<Venue> updateVenue(@PathVariable Long id, @RequestBody Venue venue) {
        try {
            venue.setVenueId(id);
            return ResponseEntity.ok(venueService.updateVenue(venue));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    /**
     * Search venues by query
     */
    @GetMapping("/search")
    public ResponseEntity<List<Venue>> searchVenues(@RequestParam String query) {
        return ResponseEntity.ok(venueService.searchVenues(query));
    }

    /**
     * Get venue by Google Place ID
     */
    @GetMapping("/places/{placeId}")
    public ResponseEntity<Venue> getVenueByPlaceId(@PathVariable String placeId) {
        return venueService.findByGooglePlaceId(placeId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
