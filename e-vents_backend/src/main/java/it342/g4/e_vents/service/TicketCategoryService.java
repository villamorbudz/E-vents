package it342.g4.e_vents.service;

import it342.g4.e_vents.model.Event;
import it342.g4.e_vents.model.TicketCategory;
import it342.g4.e_vents.repository.EventRepository;
import it342.g4.e_vents.repository.TicketCategoryRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TicketCategoryService {

    private final TicketCategoryRepository ticketCategoryRepository;
    private final EventRepository eventRepository;

    @Autowired
    public TicketCategoryService(TicketCategoryRepository ticketCategoryRepository, EventRepository eventRepository) {
        this.ticketCategoryRepository = ticketCategoryRepository;
        this.eventRepository = eventRepository;
    }

    /**
     * Retrieves all active ticket categories
     * @return List of all active ticket categories
     */
    public List<TicketCategory> getAllActiveTicketCategories() {
        return ticketCategoryRepository.findByIsActiveTrue();
    }

    /**
     * Retrieves all ticket categories, including inactive ones
     * @return List of all ticket categories
     */
    public List<TicketCategory> getAllTicketCategories() {
        return ticketCategoryRepository.findAll();
    }

    /**
     * Retrieves an active ticket category by ID
     * @param id The ticket category ID
     * @return Optional containing the ticket category if found and active
     */
    public Optional<TicketCategory> getActiveTicketCategoryById(Long id) {
        Optional<TicketCategory> ticketCategory = ticketCategoryRepository.findById(id);
        return ticketCategory.isPresent() && ticketCategory.get().isActive() ? ticketCategory : Optional.empty();
    }

    /**
     * Retrieves a ticket category by ID, regardless of active status
     * @param id The ticket category ID
     * @return Optional containing the ticket category if found
     */
    public Optional<TicketCategory> getTicketCategoryById(Long id) {
        return ticketCategoryRepository.findById(id);
    }

    /**
     * Retrieves ticket categories by status
     * @param status The status to filter by
     * @return List of ticket categories with the specified status
     */
    public List<TicketCategory> getTicketCategoriesByStatus(String status) {
        return ticketCategoryRepository.findByStatus(status);
    }

    /**
     * Searches for ticket categories by name
     * @param name The name to search for
     * @return List of active ticket categories matching the search term
     */
    public List<TicketCategory> searchTicketCategoriesByName(String name) {
        return ticketCategoryRepository.findByNameContainingIgnoreCaseAndIsActiveTrue(name);
    }

    /**
     * Retrieves ticket categories with available tickets
     * @return List of ticket categories with available tickets
     */
    public List<TicketCategory> getAvailableTicketCategories() {
        return ticketCategoryRepository.findAvailableTicketCategories();
    }

    /**
     * Retrieves ticket categories by event ID
     * @param eventId The event ID
     * @return List of active ticket categories for the specified event
     */
    public List<TicketCategory> getTicketCategoriesByEventId(Long eventId) {
        return ticketCategoryRepository.findByEventEventIdAndIsActiveTrue(eventId);
    }

    /**
     * Retrieves available ticket categories for an event
     * @param eventId The event ID
     * @return List of active ticket categories with available tickets for the specified event
     */
    public List<TicketCategory> getAvailableTicketCategoriesByEventId(Long eventId) {
        return ticketCategoryRepository.findAvailableTicketCategoriesByEventId(eventId);
    }

    /**
     * Creates a new ticket category
     * @param ticketCategory The ticket category to create
     * @return The created ticket category with ID
     * @throws EntityNotFoundException if the event is not found
     */
    public TicketCategory createTicketCategory(TicketCategory ticketCategory) {
        // Verify event exists
        Event event = eventRepository.findById(ticketCategory.getEvent().getEventId())
                .orElseThrow(() -> new EntityNotFoundException("Event not found with ID: " + ticketCategory.getEvent().getEventId()));
        
        ticketCategory.setEvent(event);
        ticketCategory.setActive(true);
        ticketCategory.setTicketsSold(0); // Initialize ticketsSold to 0
        
        return ticketCategoryRepository.save(ticketCategory);
    }

    /**
     * Updates an existing ticket category
     * @param id The ID of the ticket category to update
     * @param ticketCategoryDetails Updated ticket category data
     * @return The updated ticket category
     * @throws EntityNotFoundException if the ticket category is not found
     */
    public TicketCategory updateTicketCategory(Long id, TicketCategory ticketCategoryDetails) {
        TicketCategory existingTicketCategory = ticketCategoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Ticket category not found with ID: " + id));
        
        // Update fields
        existingTicketCategory.setName(ticketCategoryDetails.getName());
        existingTicketCategory.setPrice(ticketCategoryDetails.getPrice());
        existingTicketCategory.setDescription(ticketCategoryDetails.getDescription());
        existingTicketCategory.setStatus(ticketCategoryDetails.getStatus());
        
        // Only update totalTickets if it's valid (>= ticketsSold)
        if (ticketCategoryDetails.getTotalTickets() >= existingTicketCategory.getTicketsSold()) {
            existingTicketCategory.setTotalTickets(ticketCategoryDetails.getTotalTickets());
        } else {
            throw new IllegalArgumentException("Total tickets cannot be less than tickets sold");
        }
        
        // If event is being updated, verify it exists
        if (ticketCategoryDetails.getEvent() != null && 
            (existingTicketCategory.getEvent() == null || 
             !existingTicketCategory.getEvent().getEventId().equals(ticketCategoryDetails.getEvent().getEventId()))) {
            
            Event event = eventRepository.findById(ticketCategoryDetails.getEvent().getEventId())
                    .orElseThrow(() -> new EntityNotFoundException("Event not found with ID: " + 
                            ticketCategoryDetails.getEvent().getEventId()));
            
            existingTicketCategory.setEvent(event);
        }
        
        return ticketCategoryRepository.save(existingTicketCategory);
    }

    /**
     * Deactivates (soft-deletes) a ticket category
     * @param id The ID of the ticket category to deactivate
     * @throws EntityNotFoundException if the ticket category is not found
     */
    public void deactivateTicketCategory(Long id) {
        TicketCategory ticketCategory = ticketCategoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Ticket category not found with ID: " + id));
        
        ticketCategory.setActive(false);
        ticketCategoryRepository.save(ticketCategory);
    }

    /**
     * Restores a previously deactivated ticket category
     * @param id The ID of the ticket category to restore
     * @throws EntityNotFoundException if the ticket category is not found
     */
    public void restoreTicketCategory(Long id) {
        TicketCategory ticketCategory = ticketCategoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Ticket category not found with ID: " + id));
        
        ticketCategory.setActive(true);
        ticketCategoryRepository.save(ticketCategory);
    }

    /**
     * Checks if a ticket category with the given name exists
     * @param name The name to check
     * @return true if a ticket category with the name exists, false otherwise
     */
    public boolean existsByName(String name) {
        return ticketCategoryRepository.existsByName(name);
    }

    /**
     * Counts the number of active ticket categories in the system
     * @return The count of active ticket categories
     */
    public long countActiveTicketCategories() {
        return ticketCategoryRepository.countByIsActiveTrue();
    }
}
