package it342.g4.e_vents.service;

import it342.g4.e_vents.model.TicketCategory;
import it342.g4.e_vents.repository.TicketCategoryRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Service for managing TicketCategory entities
 */
@Service
public class TicketCategoryService {

    private final TicketCategoryRepository ticketCategoryRepository;
    
    @Autowired
    public TicketCategoryService(TicketCategoryRepository ticketCategoryRepository) {
        this.ticketCategoryRepository = ticketCategoryRepository;
    }
    
    /**
     * Retrieves all active ticket categories
     * @return List of all active ticket categories
     */
    public List<TicketCategory> getAllTicketCategories() {
        return ticketCategoryRepository.findByIsActiveTrue();
    }
    
    /**
     * Retrieves all ticket categories including inactive ones
     * @return List of all ticket categories
     */
    public List<TicketCategory> getAllTicketCategoriesIncludingInactive() {
        return ticketCategoryRepository.findAll();
    }
    
    /**
     * Finds an active ticket category by its ID
     * @param id The ticket category ID to look up
     * @return Optional containing the ticket category if found and active
     */
    public Optional<TicketCategory> findTicketCategoryById(Long id) {
        return ticketCategoryRepository.findByTicketCategoryIdAndIsActiveTrue(id);
    }
    
    /**
     * Finds any ticket category by its ID (active or inactive)
     * @param id The ticket category ID to look up
     * @return Optional containing the ticket category if found
     */
    public Optional<TicketCategory> findTicketCategoryByIdIncludingInactive(Long id) {
        return ticketCategoryRepository.findById(id);
    }
    
    /**
     * Gets an active ticket category by ID or throws an exception if not found
     * @param id The ticket category ID to look up
     * @return The ticket category
     * @throws EntityNotFoundException if the ticket category is not found or inactive
     */
    public TicketCategory getTicketCategoryById(Long id) {
        return ticketCategoryRepository.findByTicketCategoryIdAndIsActiveTrue(id)
                .orElseThrow(() -> new EntityNotFoundException("Ticket category not found with ID: " + id));
    }
    
    /**
     * Gets any ticket category by ID (active or inactive) or throws an exception if not found
     * @param id The ticket category ID to look up
     * @return The ticket category
     * @throws EntityNotFoundException if the ticket category is not found
     */
    public TicketCategory getTicketCategoryByIdIncludingInactive(Long id) {
        return ticketCategoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Ticket category not found with ID: " + id));
    }
    
    /**
     * Creates a new ticket category
     * @param ticketCategory The ticket category to create
     * @return The created ticket category with ID
     */
    public TicketCategory createTicketCategory(TicketCategory ticketCategory) {
        // Ensure ticket category is active
        ticketCategory.setActive(true);
        
        // Set default status if not provided
        if (ticketCategory.getStatus() == null || ticketCategory.getStatus().isEmpty()) {
            ticketCategory.setStatus("active");
        }
        
        // Initialize ticketsSold to 0 if not set
        if (ticketCategory.getTicketsSold() < 0) {
            ticketCategory.setTicketsSold(0);
        }
        
        return ticketCategoryRepository.save(ticketCategory);
    }
    
    /**
     * Updates an existing ticket category
     * @param ticketCategory The ticket category with updated fields
     * @return The updated ticket category
     */
    public TicketCategory updateTicketCategory(TicketCategory ticketCategory) {
        // Check if ticket category exists and is active
        TicketCategory existingTicketCategory = findTicketCategoryById(ticketCategory.getTicketCategoryId())
                .orElseThrow(() -> new EntityNotFoundException("Ticket category not found with ID: " + ticketCategory.getTicketCategoryId()));
        
        // Validate that totalTickets is not less than ticketsSold
        if (ticketCategory.getTotalTickets() < ticketCategory.getTicketsSold()) {
            throw new IllegalArgumentException("Total tickets cannot be less than tickets sold");
        }
        
        return ticketCategoryRepository.save(ticketCategory);
    }
    
    /**
     * Permanently deletes a ticket category by ID
     * @param id The ID of the ticket category to delete
     * @throws EntityNotFoundException if the ticket category is not found
     */
    public void deleteTicketCategory(Long id) {
        if (!ticketCategoryRepository.existsById(id)) {
            throw new EntityNotFoundException("Ticket category not found with ID: " + id);
        }
        ticketCategoryRepository.deleteById(id);
    }
    
    /**
     * Soft deletes a ticket category by setting isActive to false
     * @param id The ID of the ticket category to soft delete
     * @throws EntityNotFoundException if the ticket category is not found
     */
    public void softDeleteTicketCategory(Long id) {
        TicketCategory ticketCategory = getTicketCategoryByIdIncludingInactive(id);
        ticketCategory.setActive(false);
        ticketCategoryRepository.save(ticketCategory);
    }
    
    /**
     * Restores a soft-deleted ticket category by setting isActive to true
     * @param id The ID of the ticket category to restore
     * @return The restored ticket category
     * @throws EntityNotFoundException if the ticket category is not found
     */
    public TicketCategory restoreTicketCategory(Long id) {
        TicketCategory ticketCategory = getTicketCategoryByIdIncludingInactive(id);
        ticketCategory.setActive(true);
        return ticketCategoryRepository.save(ticketCategory);
    }
    
    /**
     * Finds active ticket categories by status
     * @param status The ticket category status
     * @return List of active ticket categories with the specified status
     */
    public List<TicketCategory> findTicketCategoriesByStatus(String status) {
        return ticketCategoryRepository.findByStatusAndIsActiveTrue(status);
    }
    
    /**
     * Finds active ticket categories by name (case insensitive)
     * @param name The ticket category name
     * @return List of active ticket categories with the specified name
     */
    public List<TicketCategory> findTicketCategoriesByName(String name) {
        return ticketCategoryRepository.findByNameContainingIgnoreCaseAndIsActiveTrue(name);
    }
    
    /**
     * Finds active ticket categories with available tickets
     * @return List of active ticket categories with available tickets
     */
    public List<TicketCategory> findAvailableTicketCategories() {
        return ticketCategoryRepository.findAvailableTicketCategories();
    }
}
