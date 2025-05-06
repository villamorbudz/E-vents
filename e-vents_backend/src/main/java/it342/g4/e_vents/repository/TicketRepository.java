package it342.g4.e_vents.repository;

import it342.g4.e_vents.model.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {
    List<Ticket> findByIsActiveTrue();
    List<Ticket> findByUserUserIdAndIsActiveTrue(Long userId);
    List<Ticket> findByTicketCategoryEventEventIdAndIsActiveTrue(Long eventId);
    List<Ticket> findByTicketCategoryTicketCategoryIdAndIsActiveTrue(Long ticketCategoryId);
    List<Ticket> findByStatus(String status);
    Optional<Ticket> findByTicketIdAndIsActiveTrue(Long ticketId);
}
