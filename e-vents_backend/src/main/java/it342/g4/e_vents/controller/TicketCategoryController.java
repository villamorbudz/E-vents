package it342.g4.e_vents.controller;

import it342.g4.e_vents.model.TicketCategory;
import it342.g4.e_vents.service.TicketCategoryService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Controller for ticket category-related operations
 */
@RestController
@RequestMapping("/api/ticket-categories")
@CrossOrigin(origins = "*")
@Tag(name = "Ticket Category", description = "Ticket category management APIs")
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
    @Operation(summary = "Get all active ticket categories", description = "Retrieves a list of all active ticket categories in the system")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved list of active ticket categories", 
                     content = @Content(mediaType = "application/json", schema = @Schema(implementation = TicketCategory.class)))
    })
    public ResponseEntity<List<TicketCategory>> getAllActiveTicketCategories() {
        return ResponseEntity.ok(ticketCategoryService.getAllActiveTicketCategories());
    }
    
    /**
     * Retrieves all ticket categories, including inactive ones
     * @return List of all ticket categories
     */
    @GetMapping("/all")
    @Operation(summary = "Get all ticket categories", description = "Retrieves a list of all ticket categories in the system, including inactive ones")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved list of all ticket categories", 
                     content = @Content(mediaType = "application/json", schema = @Schema(implementation = TicketCategory.class)))
    })
    public ResponseEntity<List<TicketCategory>> getAllTicketCategories() {
        return ResponseEntity.ok(ticketCategoryService.getAllTicketCategories());
    }
    
    /**
     * Retrieves an active ticket category by ID
     * @param id The ticket category ID
     * @return The ticket category or 404 if not found
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get ticket category by ID", description = "Retrieves a specific active ticket category by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved the ticket category", 
                     content = @Content(mediaType = "application/json", schema = @Schema(implementation = TicketCategory.class))),
        @ApiResponse(responseCode = "404", description = "Ticket category not found", content = @Content)
    })
    public ResponseEntity<TicketCategory> getActiveTicketCategoryById(
            @Parameter(description = "ID of the ticket category to retrieve", required = true) @PathVariable Long id) {
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
    @Operation(summary = "Get ticket categories by status", description = "Retrieves all ticket categories with a specific status")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved ticket categories with the specified status", 
                     content = @Content(mediaType = "application/json", schema = @Schema(implementation = TicketCategory.class)))
    })
    public ResponseEntity<List<TicketCategory>> getTicketCategoriesByStatus(
            @Parameter(description = "Status to filter ticket categories by", required = true) @PathVariable String status) {
        return ResponseEntity.ok(ticketCategoryService.getTicketCategoriesByStatus(status));
    }
    
    /**
     * Searches for ticket categories by name
     * @param name The name to search for
     * @return List of active ticket categories matching the search term
     */
    @GetMapping("/search")
    @Operation(summary = "Search ticket categories by name", description = "Searches for active ticket categories matching the provided name")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved search results", 
                     content = @Content(mediaType = "application/json", schema = @Schema(implementation = TicketCategory.class)))
    })
    public ResponseEntity<List<TicketCategory>> searchTicketCategoriesByName(
            @Parameter(description = "Name to search for", required = true) @RequestParam String name) {
        return ResponseEntity.ok(ticketCategoryService.searchTicketCategoriesByName(name));
    }
    
    /**
     * Retrieves ticket categories with available tickets
     * @return List of ticket categories with available tickets
     */
    @GetMapping("/available")
    @Operation(summary = "Get available ticket categories", description = "Retrieves all ticket categories that have available tickets")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved available ticket categories", 
                     content = @Content(mediaType = "application/json", schema = @Schema(implementation = TicketCategory.class)))
    })
    public ResponseEntity<List<TicketCategory>> getAvailableTicketCategories() {
        return ResponseEntity.ok(ticketCategoryService.getAvailableTicketCategories());
    }
    
    /**
     * Retrieves ticket categories by event ID
     * @param eventId The event ID
     * @return List of active ticket categories for the specified event
     */
    @GetMapping("/event/{eventId}")
    @Operation(summary = "Get ticket categories by event ID", description = "Retrieves all active ticket categories for a specific event")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved ticket categories for the event", 
                     content = @Content(mediaType = "application/json", schema = @Schema(implementation = TicketCategory.class)))
    })
    public ResponseEntity<List<TicketCategory>> getTicketCategoriesByEventId(
            @Parameter(description = "ID of the event to retrieve ticket categories for", required = true) @PathVariable Long eventId) {
        return ResponseEntity.ok(ticketCategoryService.getTicketCategoriesByEventId(eventId));
    }
    
    /**
     * Retrieves available ticket categories for an event
     * @param eventId The event ID
     * @return List of active ticket categories with available tickets for the specified event
     */
    @GetMapping("/event/{eventId}/available")
    @Operation(summary = "Get available ticket categories by event ID", 
               description = "Retrieves all active ticket categories with available tickets for a specific event")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved available ticket categories for the event", 
                     content = @Content(mediaType = "application/json", schema = @Schema(implementation = TicketCategory.class)))
    })
    public ResponseEntity<List<TicketCategory>> getAvailableTicketCategoriesByEventId(
            @Parameter(description = "ID of the event to retrieve available ticket categories for", required = true) @PathVariable Long eventId) {
        return ResponseEntity.ok(ticketCategoryService.getAvailableTicketCategoriesByEventId(eventId));
    }
    
    /**
     * Creates a new ticket category
     * @param ticketCategory Ticket category data from request body
     * @return The created ticket category or error
     */
    @PostMapping
    @Operation(summary = "Create a new ticket category", description = "Creates a new ticket category in the system")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Ticket category successfully created", 
                     content = @Content(mediaType = "application/json", schema = @Schema(implementation = TicketCategory.class))),
        @ApiResponse(responseCode = "409", description = "Ticket category with this name already exists", content = @Content),
        @ApiResponse(responseCode = "404", description = "Event not found", content = @Content),
        @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content)
    })
    public ResponseEntity<?> createTicketCategory(
            @Parameter(description = "Ticket category object to be created", required = true) @RequestBody TicketCategory ticketCategory) {
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
    @Operation(summary = "Update a ticket category", description = "Updates an existing ticket category by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Ticket category successfully updated", 
                     content = @Content(mediaType = "application/json", schema = @Schema(implementation = TicketCategory.class))),
        @ApiResponse(responseCode = "404", description = "Ticket category or event not found", content = @Content),
        @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content)
    })
    public ResponseEntity<?> updateTicketCategory(
            @Parameter(description = "ID of the ticket category to update", required = true) @PathVariable Long id, 
            @Parameter(description = "Updated ticket category details", required = true) @RequestBody TicketCategory ticketCategoryDetails) {
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
    @Operation(summary = "Deactivate a ticket category", description = "Deactivates a ticket category by setting it as inactive (soft delete)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Ticket category successfully deactivated", 
                     content = @Content(mediaType = "application/json")),
        @ApiResponse(responseCode = "404", description = "Ticket category or event not found", content = @Content),
        @ApiResponse(responseCode = "400", description = "Bad request", content = @Content)
    })
    public ResponseEntity<?> deactivateTicketCategory(
            @Parameter(description = "ID of the ticket category to deactivate", required = true) @PathVariable Long id) {
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
    @PutMapping("/restore/{id}")
    @Operation(summary = "Restore a ticket category", description = "Activates a previously deactivated ticket category")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Ticket category successfully restored", 
                     content = @Content(mediaType = "application/json")),
        @ApiResponse(responseCode = "404", description = "Ticket category or event not found", content = @Content),
        @ApiResponse(responseCode = "400", description = "Bad request", content = @Content)
    })
    public ResponseEntity<?> restoreTicketCategory(
            @Parameter(description = "ID of the ticket category to restore", required = true) @PathVariable Long id) {
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
    
    /**
     * Get the count of active ticket categories
     * @return ResponseEntity with the count of active ticket categories
     */
    @Operation(summary = "Get count of active ticket categories", description = "Returns the total number of active ticket categories in the system")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved count", 
                    content = @Content(mediaType = "application/json", 
                    schema = @Schema(implementation = Long.class))),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/count")
    public ResponseEntity<Map<String, Long>> countActiveTicketCategories() {
        try {
            long count = ticketCategoryService.countActiveTicketCategories();
            Map<String, Long> response = Collections.singletonMap("count", count);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
