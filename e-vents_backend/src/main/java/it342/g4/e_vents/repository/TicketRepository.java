package it342.g4.e_vents.repository;

import it342.g4.e_vents.model.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {
    
    /**
     * Find tickets by user ID
     * @param userId The user ID
     * @return List of tickets for the user
     */
    List<Ticket> findByUserId(Long userId);
    
    /**
     * Find tickets by event ID
     * @param eventId The event ID
     * @return List of tickets for the event
     */
    List<Ticket> findByEventId(Long eventId);
    
    /**
     * Find tickets by status
     * @param status The ticket status
     * @return List of tickets with the specified status
     */
    List<Ticket> findByStatus(String status);
}
