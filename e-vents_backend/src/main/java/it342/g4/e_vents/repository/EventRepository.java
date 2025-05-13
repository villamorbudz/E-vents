package it342.g4.e_vents.repository;

import it342.g4.e_vents.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventRepository extends JpaRepository<Event, Long> {
    /**
     * Count active events in the system
     * @return Number of active events
     */
    long countByIsActiveTrue();
}
