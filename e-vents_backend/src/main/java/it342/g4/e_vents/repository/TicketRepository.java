package it342.g4.e_vents.repository;

import it342.g4.e_vents.model.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

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
    
    /**
     * Find active tickets
     * @return List of active tickets
     */
    List<Ticket> findByIsActiveTrue();
    
    /**
     * Find active tickets by user ID
     * @param userId The user ID
     * @return List of active tickets for the user
     */
    List<Ticket> findByUserIdAndIsActiveTrue(Long userId);
    
    /**
     * Find active tickets by event ID
     * @param eventId The event ID
     * @return List of active tickets for the event
     */
    List<Ticket> findByEventIdAndIsActiveTrue(Long eventId);
    
    /**
     * Find active tickets by status
     * @param status The ticket status
     * @return List of active tickets with the specified status
     */
    List<Ticket> findByStatusAndIsActiveTrue(String status);
    
    /**
     * Find an active ticket by ID
     * @param ticketId The ticket ID
     * @return Optional containing the ticket if found and active
     */
    Optional<Ticket> findByTicketIdAndIsActiveTrue(Long ticketId);
    
    /**
     * Count active tickets
     * @return Number of active tickets
     */
    long countByIsActiveTrue();
}
