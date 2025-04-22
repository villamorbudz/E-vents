package it342.g4.e_vents.service;

import it342.g4.e_vents.model.Venue;
import it342.g4.e_vents.repository.VenueRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Service for managing Venue entities
 */
@Service
public class VenueService {

    private final VenueRepository venueRepository;
    
    @Autowired
    public VenueService(VenueRepository venueRepository) {
        this.venueRepository = venueRepository;
    }

    /**
     * Retrieves all venues from the database
     * @return List of all venues
     */
    public List<Venue> getAllVenues() {
        return venueRepository.findAll();
    }
    
    /**
     * Finds a venue by its ID
     * @param id The venue ID to look up
     * @return Optional containing the venue if found
     */
    public Optional<Venue> findVenueById(Long id) {
        return venueRepository.findById(id);
    }
    
    /**
     * Gets a venue by ID or throws an exception if not found
     * @param id The venue ID to look up
     * @return The venue
     * @throws EntityNotFoundException if the venue is not found
     */
    public Venue getVenueById(Long id) {
        return venueRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Venue not found with ID: " + id));
    }
    
    /**
     * Creates a new venue
     * @param venue The venue to create
     * @return The created venue with ID
     */
    public Venue createVenue(Venue venue) {
        return venueRepository.save(venue);
    }
    
    /**
     * Updates an existing venue
     * @param venue The venue with updated fields
     * @return The updated venue
     */
    public Venue updateVenue(Venue venue) {
        // Check if venue exists
        if (!venueRepository.existsById(venue.getVenueId())) {
            throw new EntityNotFoundException("Venue not found with ID: " + venue.getVenueId());
        }
        return venueRepository.save(venue);
    }
    
    /**
     * Deletes a venue by ID
     * @param id The ID of the venue to delete
     * @throws EntityNotFoundException if the venue is not found
     */
    public void deleteVenue(Long id) {
        if (!venueRepository.existsById(id)) {
            throw new EntityNotFoundException("Venue not found with ID: " + id);
        }
        venueRepository.deleteById(id);
    }
    
    /**
     * Counts all venues in the system
     * @return The total number of venues
     */
    public long countAllVenues() {
        return venueRepository.count();
    }
}
