package it342.g4.e_vents.repository;

import it342.g4.e_vents.model.Venue;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.List;

public interface VenueRepository extends JpaRepository<Venue, Long> {
    Optional<Venue> findByNameIgnoreCaseAndAddressIgnoreCase(String name, String address);
    List<Venue> findByNameContainingIgnoreCase(String namePart);
    
    // Methods for active venues
    List<Venue> findByIsActiveTrue();
    Optional<Venue> findByVenueIdAndIsActiveTrue(Long id);
    List<Venue> findByNameContainingIgnoreCaseAndIsActiveTrue(String namePart);
    Optional<Venue> findByNameIgnoreCaseAndAddressIgnoreCaseAndIsActiveTrue(String name, String address);
    
    /**
     * Count active venues
     * @return Number of active venues
     */
    long countByIsActiveTrue();
}
