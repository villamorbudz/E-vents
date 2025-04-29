package it342.g4.e_vents.repository;

import it342.g4.e_vents.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    /**
     * Find active events
     * @return List of active events
     */
    List<Event> findByIsActiveTrue();
    
    /**
     * Find an active event by ID
     * @param eventId The event ID
     * @return Optional containing the event if found and active
     */
    Optional<Event> findByEventIdAndIsActiveTrue(Long eventId);
    
    /**
     * Count active events
     * @return Number of active events
     */
    long countByIsActiveTrue();
}
