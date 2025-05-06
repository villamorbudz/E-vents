package it342.g4.e_vents.service;

import it342.g4.e_vents.model.Venue;
import it342.g4.e_vents.repository.VenueRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;
import java.text.Normalizer;
import java.util.regex.Pattern;

/**
 * Service for managing Venue entities with Google Places API integration
 */
@Service
public class VenueService {

    private final VenueRepository venueRepository;
    private static final Pattern NORMALIZE_PATTERN = Pattern.compile("[^\\p{ASCII}]");
    
    @Autowired
    public VenueService(VenueRepository venueRepository) {
        this.venueRepository = venueRepository;
    }

    @Cacheable(value = "venues")
    public List<Venue> getAllVenues() {
        return venueRepository.findAll();
    }
    
    @Cacheable(value = "venues", key = "#id")
    public Venue getVenueById(Long id) {
        return venueRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Venue not found with ID: " + id));
    }

    @Cacheable(value = "venues", key = "'place-' + #placeId")
    public Optional<Venue> findByGooglePlaceId(String placeId) {
        return venueRepository.findByGooglePlaceId(placeId);
    }
    
    @CacheEvict(value = "venues", allEntries = true)
    public Venue createVenue(Venue venue) {
        if (venue.getGooglePlaceId() == null || venue.getName() == null || 
            venue.getFormattedAddress() == null || venue.getLatitude() == 0 || 
            venue.getLongitude() == 0) {
            throw new IllegalArgumentException("Missing required venue data");
        }
        return venueRepository.save(venue);
    }

    /**
     * Finds or creates a venue using Google Places data
     */
    @Transactional
    public Venue findOrCreateVenue(String name, String formattedAddress, String googlePlaceId, 
                                 double latitude, double longitude) {
        // First try to find by Google Place ID if available
        if (googlePlaceId != null && !googlePlaceId.isEmpty()) {
            Optional<Venue> existingByPlaceId = venueRepository.findByGooglePlaceId(googlePlaceId);
            if (existingByPlaceId.isPresent()) {
                return existingByPlaceId.get();
            }
        }

        // Normalize inputs
        String normalizedName = normalizeString(name);
        String normalizedAddress = normalizeString(formattedAddress);

        // Try to find by exact name and address
        Optional<Venue> existing = venueRepository.findByExactNameAndAddress(normalizedName, normalizedAddress);
        if (existing.isPresent()) {
            Venue venue = existing.get();
            // Update Google Place ID if it was missing
            if (googlePlaceId != null && (venue.getGooglePlaceId() == null || venue.getGooglePlaceId().isEmpty())) {
                venue.setGooglePlaceId(googlePlaceId);
                venue.setLatitude(latitude);
                venue.setLongitude(longitude);
                return venueRepository.save(venue);
            }
            return venue;
        }

        // Create new venue
        Venue newVenue = new Venue();
        newVenue.setName(normalizedName);
        newVenue.setFormattedAddress(normalizedAddress);
        newVenue.setGooglePlaceId(googlePlaceId);
        newVenue.setLatitude(latitude);
        newVenue.setLongitude(longitude);
        
        return venueRepository.save(newVenue);
    }
    
    /**
     * Search venues by query string
     */
    @Cacheable(value = "venueSearches", key = "#query")
    public List<Venue> searchVenues(String query) {
        return venueRepository.searchVenues(normalizeString(query));
    }
    
    @CacheEvict(value = "venues", key = "#venue.venueId")
    public Venue updateVenue(Venue venue) {
        if (!venueRepository.existsById(venue.getVenueId())) {
            throw new EntityNotFoundException("Venue not found with ID: " + venue.getVenueId());
        }
        if (venue.getGooglePlaceId() == null || venue.getName() == null || 
            venue.getFormattedAddress() == null || venue.getLatitude() == 0 || 
            venue.getLongitude() == 0) {
            throw new IllegalArgumentException("Missing required venue data");
        }
        return venueRepository.save(venue);
    }

    /**
     * Count total number of venues in the system
     */
    @Cacheable(value = "venueStats", key = "'count'")
    public long countAllVenues() {
        return venueRepository.count();
    }

    /**
     * Normalize string for consistent comparison
     */
    private String normalizeString(String input) {
        if (input == null) return null;
        String normalized = Normalizer.normalize(input.trim(), Normalizer.Form.NFD);
        normalized = NORMALIZE_PATTERN.matcher(normalized).replaceAll("");
        return normalized.toLowerCase();
    }
}
