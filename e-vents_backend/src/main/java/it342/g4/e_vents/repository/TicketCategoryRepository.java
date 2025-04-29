package it342.g4.e_vents.repository;

import it342.g4.e_vents.model.TicketCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TicketCategoryRepository extends JpaRepository<TicketCategory, Long> {
    
    /**
     * Find active ticket categories
     * @return List of active ticket categories
     */
    List<TicketCategory> findByIsActiveTrue();
    
    /**
     * Find an active ticket category by ID
     * @param ticketCategoryId The ticket category ID
     * @return Optional containing the ticket category if found and active
     */
    Optional<TicketCategory> findByTicketCategoryIdAndIsActiveTrue(Long ticketCategoryId);
    
    /**
     * Find active ticket categories by status
     * @param status The ticket category status
     * @return List of active ticket categories with the specified status
     */
    List<TicketCategory> findByStatusAndIsActiveTrue(String status);
    
    /**
     * Find active ticket categories by name (case insensitive)
     * @param name The ticket category name
     * @return List of active ticket categories with the specified name
     */
    List<TicketCategory> findByNameContainingIgnoreCaseAndIsActiveTrue(String name);
    
    /**
     * Find active ticket categories with available tickets (totalTickets > ticketsSold)
     * @return List of active ticket categories with available tickets
     */
    @Query("SELECT tc FROM TicketCategory tc WHERE tc.isActive = true AND tc.totalTickets > tc.ticketsSold")
    List<TicketCategory> findAvailableTicketCategories();
}
