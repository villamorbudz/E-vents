package it342.g4.e_vents.service;

import it342.g4.e_vents.model.Ticket;
import it342.g4.e_vents.repository.TicketRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * Service for managing Ticket entities
 */
@Service
public class TicketService {

    private final TicketRepository ticketRepository;
    
    @Autowired
    public TicketService(TicketRepository ticketRepository) {
        this.ticketRepository = ticketRepository;
    }
    
    /**
     * Retrieves all tickets from the database
     * @return List of all tickets
     */
    public List<Ticket> getAllTickets() {
        return ticketRepository.findAll();
    }
    
    /**
     * Finds a ticket by its ID
     * @param id The ticket ID to look up
     * @return Optional containing the ticket if found
     */
    public Optional<Ticket> findTicketById(Long id) {
        return ticketRepository.findById(id);
    }
    
    /**
     * Gets a ticket by ID or throws an exception if not found
     * @param id The ticket ID to look up
     * @return The ticket
     * @throws EntityNotFoundException if the ticket is not found
     */
    public Ticket getTicketById(Long id) {
        return ticketRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Ticket not found with ID: " + id));
    }
    
    /**
     * Creates a new ticket
     * @param ticket The ticket to create
     * @return The created ticket with ID
     */
    public Ticket createTicket(Ticket ticket) {
        // Set purchase date to current date if not provided
        if (ticket.getPurchaseDate() == null) {
            ticket.setPurchaseDate(new Date());
        }
        
        return ticketRepository.save(ticket);
    }
    
    /**
     * Updates an existing ticket
     * @param ticket The ticket with updated fields
     * @return The updated ticket
     */
    public Ticket updateTicket(Ticket ticket) {
        // Check if ticket exists
        if (!ticketRepository.existsById(ticket.getTicketId())) {
            throw new EntityNotFoundException("Ticket not found with ID: " + ticket.getTicketId());
        }
        return ticketRepository.save(ticket);
    }
    
    /**
     * Deletes a ticket by ID
     * @param id The ID of the ticket to delete
     * @throws EntityNotFoundException if the ticket is not found
     */
    public void deleteTicket(Long id) {
        if (!ticketRepository.existsById(id)) {
            throw new EntityNotFoundException("Ticket not found with ID: " + id);
        }
        ticketRepository.deleteById(id);
    }
    
    /**
     * Cancels a ticket by setting its status to 'cancelled'
     * @param id The ID of the ticket to cancel
     * @return The updated ticket
     * @throws EntityNotFoundException if the ticket is not found
     */
    public Ticket cancelTicket(Long id) {
        Ticket ticket = getTicketById(id);
        ticket.setStatus("cancelled");
        return ticketRepository.save(ticket);
    }
    
    /**
     * Finds tickets by user ID
     * @param userId The user ID
     * @return List of tickets for the user
     */
    public List<Ticket> findTicketsByUserId(Long userId) {
        return ticketRepository.findByUserId(userId);
    }
    
    /**
     * Finds tickets by event ID
     * @param eventId The event ID
     * @return List of tickets for the event
     */
    public List<Ticket> findTicketsByEventId(Long eventId) {
        return ticketRepository.findByEventId(eventId);
    }
    
    /**
     * Finds tickets by status
     * @param status The ticket status
     * @return List of tickets with the specified status
     */
    public List<Ticket> findTicketsByStatus(String status) {
        return ticketRepository.findByStatus(status);
    }
}
