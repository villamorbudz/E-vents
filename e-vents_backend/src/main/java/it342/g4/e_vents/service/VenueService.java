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
        return venueRepository.findByIsActiveTrue();
    }
    
    /**
     * Retrieves all venues including inactive ones
     * @return List of all venues
     */
    public List<Venue> getAllVenuesIncludingInactive() {
        return venueRepository.findAll();
    }
    
    /**
     * Finds a venue by its ID
     * @param id The venue ID to look up
     * @return Optional containing the venue if found
     */
    public Optional<Venue> findVenueById(Long id) {
        return venueRepository.findByVenueIdAndIsActiveTrue(id);
    }
    
    /**
     * Gets a venue by ID or throws an exception if not found
     * @param id The venue ID to look up
     * @return The venue
     * @throws EntityNotFoundException if the venue is not found
     */
    public Venue getVenueById(Long id) {
        return venueRepository.findByVenueIdAndIsActiveTrue(id)
                .orElseThrow(() -> new EntityNotFoundException("Venue not found with ID: " + id));
    }
    
    /**
     * Creates a new venue
     * @param venue The venue to create
     * @return The created venue with ID
     */
    public Venue createVenue(Venue venue) {
        venue.setActive(true);
        return venueRepository.save(venue);
    }

    /**
     * Finds a venue by normalized name and address, or creates a new one if not found.
     * @param name Venue name
     * @param address Venue address
     * @param city Venue city (optional for further normalization)
     * @param country Venue country (optional for further normalization)
     * @return Existing or newly created Venue
     */
    public Venue findOrCreateVenue(String name, String address, String city, String country) {
        String normalizedName = name.trim();
        String normalizedAddress = address.trim();
        Optional<Venue> existing = venueRepository.findByNameIgnoreCaseAndAddressIgnoreCaseAndIsActiveTrue(normalizedName, normalizedAddress);
        if (existing.isPresent()) {
            return existing.get();
        }
        Venue newVenue = new Venue();
        newVenue.setName(normalizedName);
        newVenue.setAddress(normalizedAddress);
        if (city != null) newVenue.setCity(city.trim());
        if (country != null) newVenue.setCountry(country.trim());
        newVenue.setActive(true);
        return venueRepository.save(newVenue);
    }
    
    /**
     * Updates an existing venue
     * @param venue The venue with updated fields
     * @return The updated venue
     */
    public Venue updateVenue(Venue venue) {
        // Check if venue exists and is active
        Venue existingVenue = venueRepository.findByVenueIdAndIsActiveTrue(venue.getVenueId())
                .orElseThrow(() -> new EntityNotFoundException("Venue not found with ID: " + venue.getVenueId()));
        
        // Preserve the active status
        venue.setActive(existingVenue.isActive());
        return venueRepository.save(venue);
    }
    
    /**
     * Soft deletes a venue by ID (sets isActive to false)
     * @param id The ID of the venue to soft delete
     * @throws EntityNotFoundException if the venue is not found
     */
    public void softDeleteVenue(Long id) {
        Venue venue = venueRepository.findByVenueIdAndIsActiveTrue(id)
                .orElseThrow(() -> new EntityNotFoundException("Venue not found with ID: " + id));
        venue.setActive(false);
        venueRepository.save(venue);
    }
    
    /**
     * Restores a soft-deleted venue by ID (sets isActive to true)
     * @param id The ID of the venue to restore
     * @throws EntityNotFoundException if the venue is not found
     */
    public Venue restoreVenue(Long id) {
        Venue venue = venueRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Venue not found with ID: " + id));
        venue.setActive(true);
        return venueRepository.save(venue);
    }
    
    /**
     * Permanently deletes a venue by ID
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
     * Counts all venues in the database (active and inactive)
     * @return The total number of venues
     */
    public long countAllVenues() {
        return venueRepository.count();
    }
    
    /**
     * Counts all active venues in the database
     * @return The number of active venues
     */
    public long countActiveVenues() {
        return venueRepository.countByIsActiveTrue();
    }
}
