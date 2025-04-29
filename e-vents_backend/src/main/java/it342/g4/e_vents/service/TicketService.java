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
     * Retrieves all active tickets from the database
     * @return List of all active tickets
     */
    public List<Ticket> getAllTickets() {
        return ticketRepository.findByIsActiveTrue();
    }
    
    /**
     * Retrieves all tickets including inactive ones
     * @return List of all tickets
     */
    public List<Ticket> getAllTicketsIncludingInactive() {
        return ticketRepository.findAll();
    }
    
    /**
     * Finds an active ticket by its ID
     * @param id The ticket ID to look up
     * @return Optional containing the ticket if found and active
     */
    public Optional<Ticket> findTicketById(Long id) {
        return ticketRepository.findByTicketIdAndIsActiveTrue(id);
    }
    
    /**
     * Finds any ticket by its ID (active or inactive)
     * @param id The ticket ID to look up
     * @return Optional containing the ticket if found
     */
    public Optional<Ticket> findTicketByIdIncludingInactive(Long id) {
        return ticketRepository.findById(id);
    }
    
    /**
     * Gets an active ticket by ID or throws an exception if not found
     * @param id The ticket ID to look up
     * @return The ticket
     * @throws EntityNotFoundException if the ticket is not found or inactive
     */
    public Ticket getTicketById(Long id) {
        return ticketRepository.findByTicketIdAndIsActiveTrue(id)
                .orElseThrow(() -> new EntityNotFoundException("Ticket not found with ID: " + id));
    }
    
    /**
     * Gets any ticket by ID (active or inactive) or throws an exception if not found
     * @param id The ticket ID to look up
     * @return The ticket
     * @throws EntityNotFoundException if the ticket is not found
     */
    public Ticket getTicketByIdIncludingInactive(Long id) {
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
        
        // Ensure ticket is active
        ticket.setActive(true);
        
        return ticketRepository.save(ticket);
    }
    
    /**
     * Updates an existing ticket
     * @param ticket The ticket with updated fields
     * @return The updated ticket
     */
    public Ticket updateTicket(Ticket ticket) {
        // Check if ticket exists and is active
        Ticket existingTicket = findTicketById(ticket.getTicketId())
                .orElseThrow(() -> new EntityNotFoundException("Ticket not found with ID: " + ticket.getTicketId()));
        
        return ticketRepository.save(ticket);
    }
    
    /**
     * Permanently deletes a ticket by ID
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
     * Soft deletes a ticket by setting isActive to false
     * @param id The ID of the ticket to soft delete
     * @throws EntityNotFoundException if the ticket is not found
     */
    public void softDeleteTicket(Long id) {
        Ticket ticket = getTicketByIdIncludingInactive(id);
        ticket.setActive(false);
        ticketRepository.save(ticket);
    }
    
    /**
     * Restores a soft-deleted ticket by setting isActive to true
     * @param id The ID of the ticket to restore
     * @return The restored ticket
     * @throws EntityNotFoundException if the ticket is not found
     */
    public Ticket restoreTicket(Long id) {
        Ticket ticket = getTicketByIdIncludingInactive(id);
        ticket.setActive(true);
        return ticketRepository.save(ticket);
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
     * Finds active tickets by user ID
     * @param userId The user ID
     * @return List of active tickets for the user
     */
    public List<Ticket> findTicketsByUserId(Long userId) {
        return ticketRepository.findByUserIdAndIsActiveTrue(userId);
    }
    
    /**
     * Finds active tickets by event ID
     * @param eventId The event ID
     * @return List of active tickets for the event
     */
    public List<Ticket> findTicketsByEventId(Long eventId) {
        return ticketRepository.findByEventIdAndIsActiveTrue(eventId);
    }
    
    /**
     * Finds active tickets by status
     * @param status The ticket status
     * @return List of active tickets with the specified status
     */
    public List<Ticket> findTicketsByStatus(String status) {
        return ticketRepository.findByStatusAndIsActiveTrue(status);
    }
    
    /**
     * Counts all tickets in the database (active and inactive)
     * @return The total number of tickets
     */
    public long countAllTickets() {
        return ticketRepository.count();
    }
    
    /**
     * Counts all active tickets in the database
     * @return The number of active tickets
     */
    public long countActiveTickets() {
        return ticketRepository.countByIsActiveTrue();
    }
}
