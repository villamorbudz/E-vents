package it342.g4.e_vents.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import it342.g4.e_vents.model.Ticket;
import it342.g4.e_vents.service.TicketService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Controller for ticket-related operations
 */
@RestController
@RequestMapping("/api/tickets")
@CrossOrigin(origins = "*")
@Tag(name = "Ticket", description = "Ticket management APIs")
public class TicketController {

    private final TicketService ticketService;
    
    @Autowired
    public TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }
    
    /**
     * Retrieves all active tickets
     * @return List of all active tickets
     */
    @GetMapping
    @Operation(summary = "Get all active tickets", description = "Retrieves a list of all active tickets in the system")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved list of tickets", 
                     content = @Content(mediaType = "application/json", schema = @Schema(implementation = Ticket.class)))
    })
    public ResponseEntity<List<Ticket>> getAllActiveTickets() {
        return ResponseEntity.ok(ticketService.getAllActiveTickets());
    }
    
    /**
     * Retrieves all tickets, including inactive ones
     * @return List of all tickets
     */
    @GetMapping("/all")
    @Operation(summary = "Get all tickets", description = "Retrieves a list of all tickets in the system, including inactive ones")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved list of all tickets", 
                     content = @Content(mediaType = "application/json", schema = @Schema(implementation = Ticket.class)))
    })
    public ResponseEntity<List<Ticket>> getAllTickets() {
        return ResponseEntity.ok(ticketService.getAllTickets());
    }
    
    /**
     * Retrieves an active ticket by ID
     * @param id The ticket ID
     * @return The ticket or 404 if not found
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get ticket by ID", description = "Retrieves a specific active ticket by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved the ticket", 
                     content = @Content(mediaType = "application/json", schema = @Schema(implementation = Ticket.class))),
        @ApiResponse(responseCode = "404", description = "Ticket not found", content = @Content)
    })
    public ResponseEntity<Ticket> getActiveTicketById(
            @Parameter(description = "ID of the ticket to retrieve") @PathVariable Long id) {
        return ticketService.getActiveTicketById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * Retrieves tickets by user ID
     * @param userId The user ID
     * @return List of active tickets for the specified user
     */
    @GetMapping("/user/{userId}")
    @Operation(summary = "Get tickets by user ID", description = "Retrieves all active tickets for a specific user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved tickets for the user", 
                     content = @Content(mediaType = "application/json", schema = @Schema(implementation = Ticket.class)))
    })
    public ResponseEntity<List<Ticket>> getTicketsByUserId(
            @Parameter(description = "ID of the user to retrieve tickets for") @PathVariable Long userId) {
        return ResponseEntity.ok(ticketService.getTicketsByUserId(userId));
    }
    
    /**
     * Retrieves tickets by event ID
     * @param eventId The event ID
     * @return List of active tickets for the specified event
     */
    @GetMapping("/event/{eventId}")
    @Operation(summary = "Get tickets by event ID", description = "Retrieves all active tickets for a specific event")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved tickets for the event", 
                     content = @Content(mediaType = "application/json", schema = @Schema(implementation = Ticket.class)))
    })
    public ResponseEntity<List<Ticket>> getTicketsByEventId(
            @Parameter(description = "ID of the event to retrieve tickets for") @PathVariable Long eventId) {
        return ResponseEntity.ok(ticketService.getTicketsByEventId(eventId));
    }
    
    /**
     * Retrieves tickets by ticket category ID
     * @param ticketCategoryId The ticket category ID
     * @return List of active tickets for the specified ticket category
     */
    @GetMapping("/category/{ticketCategoryId}")
    @Operation(summary = "Get tickets by category ID", description = "Retrieves all active tickets for a specific ticket category")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved tickets for the category", 
                     content = @Content(mediaType = "application/json", schema = @Schema(implementation = Ticket.class)))
    })
    public ResponseEntity<List<Ticket>> getTicketsByTicketCategoryId(
            @Parameter(description = "ID of the ticket category to retrieve tickets for") @PathVariable Long ticketCategoryId) {
        return ResponseEntity.ok(ticketService.getTicketsByTicketCategoryId(ticketCategoryId));
    }
    
    /**
     * Retrieves tickets by status
     * @param status The status to filter by
     * @return List of tickets with the specified status
     */
    @GetMapping("/status/{status}")
    @Operation(summary = "Get tickets by status", description = "Retrieves all tickets with a specific status")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved tickets with the specified status", 
                     content = @Content(mediaType = "application/json", schema = @Schema(implementation = Ticket.class)))
    })
    public ResponseEntity<List<Ticket>> getTicketsByStatus(
            @Parameter(description = "Status to filter tickets by") @PathVariable String status) {
        return ResponseEntity.ok(ticketService.getTicketsByStatus(status));
    }
    
    /**
     * Get the count of active tickets
     * @return ResponseEntity with the count of active tickets
     */
    @Operation(summary = "Get count of active tickets", description = "Returns the total number of active tickets in the system")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved count", 
                    content = @Content(mediaType = "application/json", 
                    schema = @Schema(implementation = Long.class))),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/count")
    public ResponseEntity<Map<String, Long>> countActiveTickets() {
        try {
            long count = ticketService.countActiveTickets();
            Map<String, Long> response = Collections.singletonMap("count", count);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Creates a new ticket (purchases a ticket)
     * @param ticket Ticket data from request body
     * @return The created ticket or error
     */
    @PostMapping
    @Operation(summary = "Create a new ticket", description = "Creates a new ticket (purchase) in the system")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Ticket successfully created", 
                     content = @Content(mediaType = "application/json", schema = @Schema(implementation = Ticket.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input or no tickets available", content = @Content),
        @ApiResponse(responseCode = "404", description = "User or ticket category not found", content = @Content)
    })
    public ResponseEntity<?> createTicket(
            @Parameter(description = "Ticket object to be created", required = true) @RequestBody Ticket ticket) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ticketService.createTicket(ticket));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Collections.singletonMap("error", e.getMessage()));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Collections.singletonMap("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error", e.getMessage()));
        }
    }
    
    /**
     * Updates an existing ticket
     * @param id The ticket ID to update
     * @param ticketDetails Updated ticket data
     * @return The updated ticket or error
     */
    @PutMapping("/{id}")
    @Operation(summary = "Update a ticket", description = "Updates an existing ticket by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Ticket successfully updated", 
                     content = @Content(mediaType = "application/json", schema = @Schema(implementation = Ticket.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content),
        @ApiResponse(responseCode = "404", description = "Ticket not found", content = @Content)
    })
    public ResponseEntity<?> updateTicket(
            @Parameter(description = "ID of the ticket to update", required = true) @PathVariable Long id,
            @Parameter(description = "Updated ticket details", required = true) @RequestBody Ticket ticketDetails) {
        try {
            Ticket updatedTicket = ticketService.updateTicket(id, ticketDetails);
            return ResponseEntity.ok(updatedTicket);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Collections.singletonMap("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Collections.singletonMap("error", e.getMessage()));
        }
    }
    
    /**
     * Deactivates (soft-deletes) a ticket
     * @param id The ticket ID to deactivate
     * @return Success message or error
     */
    @DeleteMapping("/{id}/deactivate")
    @Operation(summary = "Deactivate a ticket", description = "Soft-deletes a ticket by setting it as inactive")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Ticket successfully deactivated", 
                     content = @Content(mediaType = "application/json")),
        @ApiResponse(responseCode = "404", description = "Ticket not found", content = @Content)
    })
    public ResponseEntity<?> deactivateTicket(
            @Parameter(description = "ID of the ticket to deactivate", required = true) @PathVariable Long id) {
        try {
            ticketService.deactivateTicket(id);
            return ResponseEntity.ok(Collections.singletonMap("message", "Ticket deactivated successfully"));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Collections.singletonMap("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Collections.singletonMap("error", e.getMessage()));
        }
    }
    
    /**
     * Restores a previously deactivated ticket
     * @param id The ticket ID to restore
     * @return Success message or error
     */
    @PutMapping("/restore/{id}")
    @Operation(summary = "Restore a ticket", description = "Restores a previously deactivated ticket")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Ticket successfully restored", 
                     content = @Content(mediaType = "application/json")),
        @ApiResponse(responseCode = "404", description = "Ticket not found", content = @Content)
    })
    public ResponseEntity<?> restoreTicket(
            @Parameter(description = "ID of the ticket to restore", required = true) @PathVariable Long id) {
        try {
            ticketService.restoreTicket(id);
            return ResponseEntity.ok(Collections.singletonMap("message", "Ticket restored successfully"));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Collections.singletonMap("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Collections.singletonMap("error", e.getMessage()));
        }
    }
}
