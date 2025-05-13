package it342.g4.e_vents.service;

import it342.g4.e_vents.model.Ticket;
import it342.g4.e_vents.model.TicketCategory;
import it342.g4.e_vents.model.User;
import it342.g4.e_vents.repository.TicketCategoryRepository;
import it342.g4.e_vents.repository.TicketRepository;
import it342.g4.e_vents.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class TicketService {

    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;
    private final TicketCategoryRepository ticketCategoryRepository;

    @Autowired
    public TicketService(TicketRepository ticketRepository, UserRepository userRepository, 
                        TicketCategoryRepository ticketCategoryRepository) {
        this.ticketRepository = ticketRepository;
        this.userRepository = userRepository;
        this.ticketCategoryRepository = ticketCategoryRepository;
    }

    /**
     * Retrieves all active tickets
     * @return List of all active tickets
     */
    public List<Ticket> getAllActiveTickets() {
        return ticketRepository.findByIsActiveTrue();
    }

    /**
     * Retrieves all tickets, including inactive ones
     * @return List of all tickets
     */
    public List<Ticket> getAllTickets() {
        return ticketRepository.findAll();
    }

    /**
     * Retrieves an active ticket by ID
     * @param id The ticket ID
     * @return Optional containing the ticket if found and active
     */
    public Optional<Ticket> getActiveTicketById(Long id) {
        return ticketRepository.findByTicketIdAndIsActiveTrue(id);
    }

    /**
     * Retrieves a ticket by ID, regardless of active status
     * @param id The ticket ID
     * @return Optional containing the ticket if found
     */
    public Optional<Ticket> getTicketById(Long id) {
        return ticketRepository.findById(id);
    }

    /**
     * Retrieves tickets by user ID
     * @param userId The user ID
     * @return List of active tickets for the specified user
     */
    public List<Ticket> getTicketsByUserId(Long userId) {
        return ticketRepository.findByUserUserIdAndIsActiveTrue(userId);
    }

    /**
     * Retrieves tickets by event ID
     * @param eventId The event ID
     * @return List of active tickets for the specified event
     */
    public List<Ticket> getTicketsByEventId(Long eventId) {
        return ticketRepository.findByTicketCategoryEventEventIdAndIsActiveTrue(eventId);
    }

    /**
     * Retrieves tickets by ticket category ID
     * @param ticketCategoryId The ticket category ID
     * @return List of active tickets for the specified ticket category
     */
    public List<Ticket> getTicketsByTicketCategoryId(Long ticketCategoryId) {
        return ticketRepository.findByTicketCategoryTicketCategoryIdAndIsActiveTrue(ticketCategoryId);
    }

    /**
     * Retrieves tickets by status
     * @param status The status to filter by
     * @return List of tickets with the specified status
     */
    public List<Ticket> getTicketsByStatus(String status) {
        return ticketRepository.findByStatus(status);
    }

    /**
     * Creates a new ticket (purchases a ticket)
     * @param ticket The ticket to create
     * @return The created ticket with ID
     * @throws EntityNotFoundException if the user or ticket category is not found
     * @throws IllegalStateException if there are no available tickets in the category
     */
    @Transactional
    public Ticket createTicket(Ticket ticket) {
        // Verify user exists
        User user = userRepository.findById(ticket.getUser().getUserId())
                .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + ticket.getUser().getUserId()));
        
        // Verify ticket category exists
        TicketCategory ticketCategory = ticketCategoryRepository.findById(ticket.getTicketCategory().getTicketCategoryId())
                .orElseThrow(() -> new EntityNotFoundException("Ticket category not found with ID: " + 
                        ticket.getTicketCategory().getTicketCategoryId()));
        
        // Check if tickets are available
        if (ticketCategory.getAvailableTickets() <= 0) {
            throw new IllegalStateException("No tickets available in this category");
        }
        
        // Validate that ticketsSold will not exceed totalTickets
        if (ticketCategory.getTicketsSold() >= ticketCategory.getTotalTickets()) {
            throw new IllegalStateException("Cannot create ticket: All tickets in this category have been sold");
        }
        
        // Set references
        ticket.setUser(user);
        ticket.setTicketCategory(ticketCategory);
        
        // Set default values
        ticket.setActive(true);
        ticket.setPurchaseDate(LocalDateTime.now());
        ticket.setStatus("PURCHASED");
        
        // Increment ticketsSold in the ticket category
        ticketCategory.incrementTicketsSold();
        ticketCategoryRepository.save(ticketCategory);
        
        // Save and return the ticket
        return ticketRepository.save(ticket);
    }

    /**
     * Updates an existing ticket
     * @param id The ID of the ticket to update
     * @param ticketDetails Updated ticket data
     * @return The updated ticket
     * @throws EntityNotFoundException if the ticket is not found
     */
    public Ticket updateTicket(Long id, Ticket ticketDetails) {
        Ticket existingTicket = ticketRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Ticket not found with ID: " + id));
        
        // Update fields (only status can be updated)
        existingTicket.setStatus(ticketDetails.getStatus());
        
        return ticketRepository.save(existingTicket);
    }

    /**
     * Deactivates (soft-deletes) a ticket
     * @param id The ID of the ticket to deactivate
     * @throws EntityNotFoundException if the ticket is not found
     */
    public void deactivateTicket(Long id) {
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Ticket not found with ID: " + id));
        
        ticket.setActive(false);
        ticketRepository.save(ticket);
    }

    /**
     * Restores a previously deactivated ticket
     * @param id The ID of the ticket to restore
     * @throws EntityNotFoundException if the ticket is not found
     */
    public void restoreTicket(Long id) {
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Ticket not found with ID: " + id));
        
        ticket.setActive(true);
        ticketRepository.save(ticket);
    }

    /**
     * Counts the number of active tickets in the system
     * @return The count of active tickets
     */
    public long countActiveTickets() {
        return ticketRepository.countByIsActiveTrue();
    }
}
