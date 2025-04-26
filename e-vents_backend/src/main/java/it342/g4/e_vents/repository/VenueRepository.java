package it342.g4.e_vents.repository;

import it342.g4.e_vents.model.Venue;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.List;

public interface VenueRepository extends JpaRepository<Venue, Long> {
    Optional<Venue> findByNameIgnoreCaseAndAddressIgnoreCase(String name, String address);
    List<Venue> findByNameContainingIgnoreCase(String namePart);
}
