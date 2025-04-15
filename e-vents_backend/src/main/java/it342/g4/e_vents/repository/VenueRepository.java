package it342.g4.e_vents.repository;

import it342.g4.e_vents.model.Venue;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VenueRepository extends JpaRepository<Venue, Long> {
}
