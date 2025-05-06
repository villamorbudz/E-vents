package it342.g4.e_vents.repository;

import it342.g4.e_vents.model.TicketCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface TicketCategoryRepository extends JpaRepository<TicketCategory, Long> {
    List<TicketCategory> findByIsActiveTrue();
    List<TicketCategory> findByStatus(String status);
    List<TicketCategory> findByNameContainingIgnoreCaseAndIsActiveTrue(String name);
    
    @Query("SELECT tc FROM TicketCategory tc WHERE tc.totalTickets > tc.ticketsSold AND tc.isActive = true")
    List<TicketCategory> findAvailableTicketCategories();
    
    boolean existsByName(String name);
    
    // Find ticket categories by event ID
    List<TicketCategory> findByEventEventIdAndIsActiveTrue(Long eventId);
    
    // Find available ticket categories for an event
    @Query("SELECT tc FROM TicketCategory tc WHERE tc.event.eventId = ?1 AND tc.totalTickets > tc.ticketsSold AND tc.isActive = true")
    List<TicketCategory> findAvailableTicketCategoriesByEventId(Long eventId);
}
