package it342.g4.e_vents.controller;

import it342.g4.e_vents.model.Venue;
import it342.g4.e_vents.service.VenueService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

/**
 * REST Controller for venue-related operations
 */
@RestController
@RequestMapping("/api/venues")
@CrossOrigin(origins = "*")
@Tag(name = "Venue", description = "Venue management APIs")
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
    @Operation(summary = "Get all venues", description = "Retrieves a list of all venues in the system")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved list of venues", 
                     content = @Content(mediaType = "application/json", schema = @Schema(implementation = Venue.class)))
    })
    public ResponseEntity<List<Venue>> getAllVenues() {
        return ResponseEntity.ok(venueService.getAllVenues());
    }
    
    /**
     * Get venue by ID
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get venue by ID", description = "Retrieves a specific venue by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved the venue", 
                     content = @Content(mediaType = "application/json", schema = @Schema(implementation = Venue.class))),
        @ApiResponse(responseCode = "404", description = "Venue not found", content = @Content)
    })
    public ResponseEntity<Venue> getVenueById(
            @Parameter(description = "ID of the venue to retrieve", required = true) @PathVariable Long id) {
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
    @Operation(summary = "Create a new venue", description = "Creates a new venue in the system")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Venue successfully created", 
                     content = @Content(mediaType = "application/json", schema = @Schema(implementation = Venue.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content)
    })
    public ResponseEntity<Venue> createVenue(
            @Parameter(description = "Venue object to be created", required = true) @RequestBody Venue venue) {
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
    @Operation(summary = "Find or create venue from Google Places", 
               description = "Finds an existing venue by Google Place ID or creates a new one using Google Places data")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Venue found or successfully created", 
                     content = @Content(mediaType = "application/json", schema = @Schema(implementation = Venue.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content)
    })
    public ResponseEntity<Venue> findOrCreateVenue(
            @Parameter(description = "Google Places data for venue creation", required = true) 
            @RequestBody Map<String, Object> placeData) {
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
    @Operation(summary = "Update a venue", description = "Updates an existing venue by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Venue successfully updated", 
                     content = @Content(mediaType = "application/json", schema = @Schema(implementation = Venue.class))),
        @ApiResponse(responseCode = "404", description = "Venue not found", content = @Content),
        @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content)
    })
    public ResponseEntity<Venue> updateVenue(
            @Parameter(description = "ID of the venue to update", required = true) @PathVariable Long id, 
            @Parameter(description = "Updated venue details", required = true) @RequestBody Venue venue) {
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
    @Operation(summary = "Search venues", description = "Searches for venues matching the provided query string")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved search results", 
                     content = @Content(mediaType = "application/json", schema = @Schema(implementation = Venue.class)))
    })
    public ResponseEntity<List<Venue>> searchVenues(
            @Parameter(description = "Search query string", required = true) @RequestParam String query) {
        return ResponseEntity.ok(venueService.searchVenues(query));
    }

    /**
     * Get venue by Google Place ID
     */
    @GetMapping("/places/{placeId}")
    @Operation(summary = "Get venue by Google Place ID", description = "Retrieves a venue by its Google Place ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved the venue", 
                     content = @Content(mediaType = "application/json", schema = @Schema(implementation = Venue.class))),
        @ApiResponse(responseCode = "404", description = "Venue not found with this Place ID", content = @Content)
    })
    public ResponseEntity<Venue> getVenueByPlaceId(
            @Parameter(description = "Google Place ID of the venue", required = true) @PathVariable String placeId) {
        return venueService.findByGooglePlaceId(placeId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
