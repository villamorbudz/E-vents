package it342.g4.e_vents.controller;

import it342.g4.e_vents.model.TicketCategory;
import it342.g4.e_vents.service.TicketCategoryService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

/**
 * Controller for ticket category-related operations
 */
@RestController
@RequestMapping("/api/ticket-categories")
@CrossOrigin(origins = "*")
public class TicketCategoryController {

    private final TicketCategoryService ticketCategoryService;
    
    @Autowired
    public TicketCategoryController(TicketCategoryService ticketCategoryService) {
        this.ticketCategoryService = ticketCategoryService;
    }
    
    /**
     * Retrieves all active ticket categories
     * @return List of all active ticket categories
     */
    @GetMapping
    public ResponseEntity<List<TicketCategory>> getAllActiveTicketCategories() {
        return ResponseEntity.ok(ticketCategoryService.getAllActiveTicketCategories());
    }
    
    /**
     * Retrieves all ticket categories, including inactive ones
     * @return List of all ticket categories
     */
    @GetMapping("/all")
    public ResponseEntity<List<TicketCategory>> getAllTicketCategories() {
        return ResponseEntity.ok(ticketCategoryService.getAllTicketCategories());
    }
    
    /**
     * Retrieves an active ticket category by ID
     * @param id The ticket category ID
     * @return The ticket category or 404 if not found
     */
    @GetMapping("/{id}")
    public ResponseEntity<TicketCategory> getActiveTicketCategoryById(@PathVariable Long id) {
        return ticketCategoryService.getActiveTicketCategoryById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * Retrieves ticket categories by status
     * @param status The status to filter by
     * @return List of ticket categories with the specified status
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<List<TicketCategory>> getTicketCategoriesByStatus(@PathVariable String status) {
        return ResponseEntity.ok(ticketCategoryService.getTicketCategoriesByStatus(status));
    }
    
    /**
     * Searches for ticket categories by name
     * @param name The name to search for
     * @return List of active ticket categories matching the search term
     */
    @GetMapping("/search")
    public ResponseEntity<List<TicketCategory>> searchTicketCategoriesByName(@RequestParam String name) {
        return ResponseEntity.ok(ticketCategoryService.searchTicketCategoriesByName(name));
    }
    
    /**
     * Retrieves ticket categories with available tickets
     * @return List of ticket categories with available tickets
     */
    @GetMapping("/available")
    public ResponseEntity<List<TicketCategory>> getAvailableTicketCategories() {
        return ResponseEntity.ok(ticketCategoryService.getAvailableTicketCategories());
    }
    
    /**
     * Retrieves ticket categories by event ID
     * @param eventId The event ID
     * @return List of active ticket categories for the specified event
     */
    @GetMapping("/event/{eventId}")
    public ResponseEntity<List<TicketCategory>> getTicketCategoriesByEventId(@PathVariable Long eventId) {
        return ResponseEntity.ok(ticketCategoryService.getTicketCategoriesByEventId(eventId));
    }
    
    /**
     * Retrieves available ticket categories for an event
     * @param eventId The event ID
     * @return List of active ticket categories with available tickets for the specified event
     */
    @GetMapping("/event/{eventId}/available")
    public ResponseEntity<List<TicketCategory>> getAvailableTicketCategoriesByEventId(@PathVariable Long eventId) {
        return ResponseEntity.ok(ticketCategoryService.getAvailableTicketCategoriesByEventId(eventId));
    }
    
    /**
     * Creates a new ticket category
     * @param ticketCategory Ticket category data from request body
     * @return The created ticket category or error
     */
    @PostMapping
    public ResponseEntity<?> createTicketCategory(@RequestBody TicketCategory ticketCategory) {
        try {
            if (ticketCategoryService.existsByName(ticketCategory.getName())) {
                return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(Collections.singletonMap("message", "Ticket category with this name already exists"));
            }
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ticketCategoryService.createTicketCategory(ticketCategory));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Collections.singletonMap("error", "Event not found for the given ticket category"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Collections.singletonMap("error", e.getMessage()));
        }
    }
    
    /**
     * Updates an existing ticket category
     * @param id The ticket category ID to update
     * @param ticketCategoryDetails Updated ticket category data
     * @return The updated ticket category or error
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateTicketCategory(@PathVariable Long id, @RequestBody TicketCategory ticketCategoryDetails) {
        try {
            TicketCategory updatedTicketCategory = ticketCategoryService.updateTicketCategory(id, ticketCategoryDetails);
            return ResponseEntity.ok(updatedTicketCategory);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Collections.singletonMap("error", "Event not found for the given ticket category"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Collections.singletonMap("error", e.getMessage()));
        }
    }
    
    /**
     * Deactivates (soft-deletes) a ticket category
     * @param id The ticket category ID to deactivate
     * @return Success message or error
     */
    @DeleteMapping("/{id}/deactivate")
    public ResponseEntity<?> deactivateTicketCategory(@PathVariable Long id) {
        try {
            ticketCategoryService.deactivateTicketCategory(id);
            return ResponseEntity.ok(Collections.singletonMap("message", "Ticket category deactivated successfully"));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Collections.singletonMap("error", "Event not found for the given ticket category"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Collections.singletonMap("error", e.getMessage()));
        }
    }
    
    /**
     * Restores a previously deactivated ticket category
     * @param id The ticket category ID to restore
     * @return Success message or error
     */
    @PostMapping("/restore/{id}")
    public ResponseEntity<?> restoreTicketCategory(@PathVariable Long id) {
        try {
            ticketCategoryService.restoreTicketCategory(id);
            return ResponseEntity.ok(Collections.singletonMap("message", "Ticket category restored successfully"));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Collections.singletonMap("error", "Event not found for the given ticket category"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Collections.singletonMap("error", e.getMessage()));
        }
    }
}
