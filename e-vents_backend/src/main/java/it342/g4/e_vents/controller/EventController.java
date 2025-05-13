package it342.g4.e_vents.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import it342.g4.e_vents.model.Act;
import it342.g4.e_vents.model.Event;
import it342.g4.e_vents.service.ActService;
import it342.g4.e_vents.service.EventService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for event-related operations
 */
@RestController
@RequestMapping("/api/events")
@CrossOrigin(origins = "*")
@Tag(name = "Event", description = "Event management APIs")
public class EventController {

    private final EventService eventService;
    private final ActService actService;

    @Autowired
    public EventController(EventService eventService, ActService actService) {
        this.eventService = eventService;
        this.actService = actService;
    }

    /**
     * Retrieves all events
     * @return List of all events
     */
    @GetMapping()
    @Operation(summary = "Get all events", description = "Retrieves a list of all events in the system")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved list of events",
                     content = @Content(mediaType = "application/json", schema = @Schema(implementation = Event.class)))
    })
    public ResponseEntity<List<Event>> getAllEvents() {
        return ResponseEntity.ok(eventService.getAllEvents());
    }

    /**
     * Retrieves all scheduled events
     * @return List of all scheduled events
     */
    @GetMapping("/scheduled")
    @Operation(summary = "Get all scheduled events", description = "Retrieves a list of all events with SCHEDULED status")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved list of scheduled events",
                     content = @Content(mediaType = "application/json", schema = @Schema(implementation = Event.class)))
    })
    public ResponseEntity<List<Event>> getScheduledEvents() {
        return ResponseEntity.ok(eventService.getEventsByStatus("SCHEDULED"));
    }

    /**
     * Retrieves all postponed events
     * @return List of all postponed events
     */
    @GetMapping("/postponed")
    @Operation(summary = "Get all postponed events", description = "Retrieves a list of all events with POSTPONED status")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved list of postponed events",
                     content = @Content(mediaType = "application/json", schema = @Schema(implementation = Event.class)))
    })
    public ResponseEntity<List<Event>> getPostponedEvents() {
        return ResponseEntity.ok(eventService.getEventsByStatus("POSTPONED"));
    }

    /**
     * Retrieves all cancelled events
     * @return List of all cancelled events
     */
    @GetMapping("/cancelled")
    @Operation(summary = "Get all cancelled events", description = "Retrieves a list of all events with CANCELLED status")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved list of cancelled events",
                     content = @Content(mediaType = "application/json", schema = @Schema(implementation = Event.class)))
    })
    public ResponseEntity<List<Event>> getCancelledEvents() {
        return ResponseEntity.ok(eventService.getEventsByStatus("CANCELLED"));
    }

    /**
     * Retrieves an event by ID
     * @param id The event ID
     * @return The event or 404 if not found
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get event by ID", description = "Retrieves a specific event by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved the event",
                     content = @Content(mediaType = "application/json", schema = @Schema(implementation = Event.class))),
        @ApiResponse(responseCode = "404", description = "Event not found", content = @Content)
    })
    public ResponseEntity<Event> getEventById(
            @Parameter(description = "ID of the event to retrieve") @PathVariable Long id) {
        try {
            return ResponseEntity.ok(eventService.getEventById(id));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Shows the edit event page
     * @param id The event ID to edit
     * @return ModelAndView for the edit page
     */
    @GetMapping("/edit/{id}")
    @Operation(summary = "Show edit event page", description = "Returns a view for editing an event")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully returned edit page view")
    })
    public ModelAndView showEditEventPage(
            @Parameter(description = "ID of the event to edit") @PathVariable Long id) {
        ModelAndView mav = new ModelAndView("edit_event");
        mav.addObject("eventId", id);
        return mav;
    }

    /**
     * Retrieves all acts
     * @return List of all acts
     */
    @GetMapping("/acts")
    @Operation(summary = "Get all acts", description = "Retrieves a list of all acts for event lineup selection")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved list of acts",
                     content = @Content(mediaType = "application/json", schema = @Schema(implementation = Act.class)))
    })
    public ResponseEntity<List<Act>> getActs() {
        return ResponseEntity.ok(actService.getAllActs());
    }

    /**
     * Creates a new event with JSON data
     * @param event Event data from request body
     * @return The created event or error
     */
    @PostMapping(value = "/create", consumes = "application/json", produces = "application/json")
    @Operation(summary = "Create a new event", description = "Creates a new event in the system using JSON data")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Event successfully created",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Event.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content),
            @ApiResponse(responseCode = "500", description = "Server error", content = @Content)
    })
    public ResponseEntity<Object> createEvent(
            @Parameter(description = "Event object to be created", required = true)
            @RequestBody Event event) {
        try {
            // Log the incoming request for debugging
            System.out.println("Received event creation request: " + event.getName());

            // Validate required fields
            if (event.getName() == null || event.getName().trim().isEmpty()) {
                return ResponseEntity
                    .badRequest()
                    .body(Map.of("error", "Event name is required"));
            }

            if (event.getVenue() == null || event.getVenue().trim().isEmpty()) {
                return ResponseEntity
                    .badRequest()
                    .body(Map.of("error", "Event venue is required"));
            }

            if (event.getDate() == null) {
                return ResponseEntity
                    .badRequest()
                    .body(Map.of("error", "Event date is required"));
            }

            if (event.getTime() == null) {
                return ResponseEntity
                    .badRequest()
                    .body(Map.of("error", "Event time is required"));
            }

            if (event.getUser() == null || event.getUser().getUserId() == null) {
                return ResponseEntity
                    .badRequest()
                    .body(Map.of("error", "User ID is required"));
            }

            // Validate lineup
            if (event.getLineup() == null || event.getLineup().isEmpty()) {
                return ResponseEntity
                    .badRequest()
                    .body(Map.of("error", "At least one act/lineup is required"));
            }

            // Fetch and set Act entities based on actId(s) provided in lineup
            List<Act> resolvedActs = new ArrayList<>();
            for (Act actStub : event.getLineup()) {
                if (actStub.getActId() == null) {
                    return ResponseEntity.badRequest().body(Map.of("error", "Each act must have an actId"));
                }
                Act act = actService.getActById(actStub.getActId());
                if (act == null) {
                    return ResponseEntity.badRequest().body(Map.of("error", "Act with ID " + actStub.getActId() + " not found"));
                }
                resolvedActs.add(act);
            }
            event.setLineup(resolvedActs);

            // Set default values if not provided
            if (event.getStatus() == null) {
                event.setStatus("SCHEDULED");
            }

            // Create the event
            Event createdEvent = eventService.createEvent(event);

            // Log the created event for debugging
            System.out.println("Created event with ID: " + createdEvent.getEventId());
            System.out.println("Event name: " + createdEvent.getName());

            // Return with status 201 Created and the complete event object
            return ResponseEntity.status(HttpStatus.CREATED).body(createdEvent);
        } catch (Exception e) {
            e.printStackTrace(); // Log the full stack trace for debugging
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to create event", "message", e.getMessage()));
        }
    }

    /**
     * Updates an existing event
     * @param id The event ID to update
     * @param eventDetails Updated event data
     * @return Success message or error
     */
    @PutMapping("/{id}")
    @Operation(summary = "Update an event", description = "Updates an existing event by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Event successfully updated", 
                     content = @Content(mediaType = "application/json", schema = @Schema(implementation = Event.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content),
        @ApiResponse(responseCode = "404", description = "Event not found", content = @Content)
    })
    public ResponseEntity<Event> updateEvent(
            @Parameter(description = "ID of the event to update", required = true) @PathVariable Long id, 
            @Parameter(description = "Updated event details", required = true) @RequestBody Event eventDetails) {
        try {
            // Verify event exists
            Event existingEvent = eventService.getEventById(id);
            
            // Update fields
            existingEvent.setName(eventDetails.getName());
            existingEvent.setDate(eventDetails.getDate());
            existingEvent.setTime(eventDetails.getTime());
            existingEvent.setLineup(eventDetails.getLineup());
            existingEvent.setStatus(eventDetails.getStatus());
            
            // Save and return
            Event updatedEvent = eventService.updateEvent(existingEvent);
            return ResponseEntity.ok(updatedEvent);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Permanently deletes an event from the database
     * @param id The event ID to delete
     * @return Success message or error
     */
    @DeleteMapping("/{id}/delete")
    @Operation(summary = "Delete an event permanently", description = "Permanently deletes an event from the system")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Event permanently deleted", 
                     content = @Content(mediaType = "application/json")),
        @ApiResponse(responseCode = "404", description = "Event not found", content = @Content),
        @ApiResponse(responseCode = "400", description = "Bad request", content = @Content)
    })
    public ResponseEntity<?> deleteEventPermanently(
            @Parameter(description = "ID of the event to permanently delete", required = true) @PathVariable Long id) {
        try {
            eventService.deleteEvent(id);
            return ResponseEntity.ok(Collections.singletonMap("message", "Event permanently deleted"));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Collections.singletonMap("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Collections.singletonMap("error", e.getMessage()));
        }
    }

    /**
     * Restores a cancelled or postponed event
     * @param id The event ID to restore
     * @return Success message or error
     */
    @PutMapping("/restore/{id}")
    @Operation(summary = "Restore an event", description = "Restores a cancelled or postponed event to SCHEDULED status")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Event successfully restored", 
                     content = @Content(mediaType = "application/json")),
        @ApiResponse(responseCode = "404", description = "Event not found", content = @Content),
        @ApiResponse(responseCode = "400", description = "Bad request", content = @Content)
    })
    public ResponseEntity<?> restoreEvent(
            @Parameter(description = "ID of the event to restore", required = true) @PathVariable Long id) {
        try {
            eventService.restoreEvent(id);
            return ResponseEntity.ok(Collections.singletonMap("message", "Event restored successfully"));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Collections.singletonMap("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Collections.singletonMap("error", e.getMessage()));
        }
    }
    
    /**
     * Cancels an event by setting its status to CANCELLED
     * @param id The event ID to cancel
     * @return Success message or error
     */
    @DeleteMapping("/{id}/cancel")
    @Operation(summary = "Cancel an event", description = "Cancels an event by setting its status to CANCELLED")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Event successfully cancelled", 
                     content = @Content(mediaType = "application/json")),
        @ApiResponse(responseCode = "404", description = "Event not found", content = @Content),
        @ApiResponse(responseCode = "400", description = "Bad request", content = @Content)
    })
    public ResponseEntity<?> cancelEvent(
            @Parameter(description = "ID of the event to cancel", required = true) @PathVariable Long id) {
        try {
            eventService.cancelEvent(id);
            return ResponseEntity.ok(Collections.singletonMap("message", "Event cancelled successfully"));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Collections.singletonMap("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Collections.singletonMap("error", e.getMessage()));
        }
    }
    
    /**
     * Postpones an event by setting its status to POSTPONED
     * @param id The event ID to postpone
     * @return Success message or error
     */
    @PutMapping("/{id}/postpone")
    @Operation(summary = "Postpone an event", description = "Postpones an event by setting its status to POSTPONED")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Event successfully postponed", 
                     content = @Content(mediaType = "application/json")),
        @ApiResponse(responseCode = "404", description = "Event not found", content = @Content),
        @ApiResponse(responseCode = "400", description = "Bad request", content = @Content)
    })
    public ResponseEntity<?> postponeEvent(
            @Parameter(description = "ID of the event to postpone", required = true) @PathVariable Long id) {
        try {
            eventService.postponeEvent(id);
            return ResponseEntity.ok(Collections.singletonMap("message", "Event postponed successfully"));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Collections.singletonMap("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Collections.singletonMap("error", e.getMessage()));
        }
    }
    
    /**
     * Get the count of active events
     * @return ResponseEntity with the count of active events
     */
    @GetMapping("/count")
    @Operation(summary = "Count active events", description = "Get the count of active events")
    public ResponseEntity<Map<String, Long>> countActiveEvents() {
        Long count = eventService.countActiveEvents();
        return ResponseEntity.ok(Map.of("count", count));
    }
}
