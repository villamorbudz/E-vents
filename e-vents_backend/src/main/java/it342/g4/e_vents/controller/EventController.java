package it342.g4.e_vents.controller;

import it342.g4.e_vents.model.Act;
import it342.g4.e_vents.model.Event;
import it342.g4.e_vents.model.Venue;
import it342.g4.e_vents.service.ActService;
import it342.g4.e_vents.service.EventService;
import it342.g4.e_vents.service.VenueService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.sql.Date;
import java.sql.Time;
import java.util.Collections;
import java.util.List;

/**
 * Controller for event-related operations
 */
@RestController
@RequestMapping("/api/events")
@CrossOrigin(origins = "*")
public class EventController {

    private final EventService eventService;
    private final VenueService venueService;
    private final ActService actService;
    
    @Autowired
    public EventController(EventService eventService, VenueService venueService, ActService actService) {
        this.eventService = eventService;
        this.venueService = venueService;
        this.actService = actService;
    }
    
    /**
     * Retrieves all events
     * @return List of all events
     */
    @GetMapping()
    public ResponseEntity<List<Event>> getAllEvents() {
        return ResponseEntity.ok(eventService.getAllEvents());
    }
    
    /**
     * Retrieves an event by ID
     * @param id The event ID
     * @return The event or 404 if not found
     */
    @GetMapping("/{id}")
    public ResponseEntity<Event> getEventById(@PathVariable Long id) {
        return eventService.findEventById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * Shows the edit event page
     * @param id The event ID to edit
     * @return ModelAndView for the edit page
     */
    @GetMapping("/edit/{id}")
    public ModelAndView showEditEventPage(@PathVariable Long id) {
        ModelAndView mav = new ModelAndView("edit_event");
        mav.addObject("eventId", id);
        return mav;
    }

    /**
     * Retrieves all venues
     * @return List of all venues
     */
    @GetMapping("/venues")
    public ResponseEntity<List<Venue>> getVenues() {
        return ResponseEntity.ok(venueService.getAllVenues());
    }

    /**
     * Retrieves all acts
     * @return List of all acts
     */
    @GetMapping("/acts")
    public ResponseEntity<List<Act>> getActs() {
        return ResponseEntity.ok(actService.getAllActs());
    }

    /**
     * Creates a new event with form data
     * @param name Event name
     * @param date Event date
     * @param time Event time
     * @param venue Venue ID
     * @param lineup List of Act IDs
     * @param status Event status
     * @return Redirect to dashboard
     */
    @PostMapping("/create")
    public ModelAndView createEventForm(@RequestParam String name,
                                   @RequestParam String date,
                                   @RequestParam String time,
                                   @RequestParam Long venue,
                                   @RequestParam(value = "lineup") List<Long> lineup,
                                   @RequestParam String status) {
        Event event = new Event();
        event.setName(name);
        event.setDate(Date.valueOf(date));
        event.setTime(Time.valueOf(time + ":00")); // HTML time is HH:mm, SQL Time expects HH:mm:ss
        
        // Find venue by ID
        Venue venueObj = venueService.getAllVenues().stream()
                .filter(v -> v.getVenueId().equals(venue))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Venue not found with ID: " + venue));
        event.setVenue(venueObj);
        
        // Find acts by IDs
        List<Act> acts = actService.getAllActs().stream()
                .filter(a -> lineup.contains(a.getActId()))
                .toList();
        event.setLineup(acts);
        
        event.setStatus(status);
        eventService.createEvent(event);
        return new ModelAndView("redirect:/events/dashboard");
    }
    
    /**
     * Creates a new event with JSON data
     * @param event Event data from request body
     * @return The created event
     */
    @PostMapping
    public ResponseEntity<Event> createEvent(@RequestBody Event event) {
        try {
            Event createdEvent = eventService.createEvent(event);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdEvent);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Updates an existing event
     * @param id The event ID to update
     * @param eventDetails Updated event data
     * @return Success message or error
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateEvent(@PathVariable Long id, @RequestBody Event eventDetails) {
        try {
            // Verify event exists
            Event existingEvent = eventService.getEventById(id);
            
            // Update fields
            existingEvent.setName(eventDetails.getName());
            existingEvent.setDate(eventDetails.getDate());
            existingEvent.setTime(eventDetails.getTime());
            existingEvent.setVenue(eventDetails.getVenue());
            existingEvent.setLineup(eventDetails.getLineup());
            existingEvent.setStatus(eventDetails.getStatus());
            
            // Save and return
            Event updatedEvent = eventService.updateEvent(existingEvent);
            return ResponseEntity.ok(updatedEvent);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Collections.singletonMap("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Collections.singletonMap("error", e.getMessage()));
        }
    }

    /**
     * Cancels an event by setting its status to 'cancelled'
     * @param id The event ID to cancel
     * @return Success message or error
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> cancelEvent(@PathVariable Long id) {
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
     * Permanently deletes an event from the database
     * @param id The event ID to delete
     * @return Success message or error
     */
    @DeleteMapping("/{id}/permanent")
    public ResponseEntity<?> deleteEventPermanently(@PathVariable Long id) {
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
     * Restores a cancelled event by setting its status to 'scheduled'
     * @param id The event ID to restore
     * @return Success message or error
     */
    @PutMapping("/{id}/restore")
    public ResponseEntity<?> restoreEvent(@PathVariable Long id) {
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
}
