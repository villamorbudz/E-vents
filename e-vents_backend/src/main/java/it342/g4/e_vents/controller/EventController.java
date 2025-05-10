package it342.g4.e_vents.controller;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalTime;
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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import it342.g4.e_vents.model.Act;
import it342.g4.e_vents.model.Event;
import it342.g4.e_vents.model.TicketCategory;
import it342.g4.e_vents.model.User;
import it342.g4.e_vents.model.Venue;
import it342.g4.e_vents.service.ActService;
import it342.g4.e_vents.service.EventService;
import it342.g4.e_vents.service.TicketCategoryService;
import it342.g4.e_vents.service.UserService;
import it342.g4.e_vents.service.VenueService;
import jakarta.persistence.EntityNotFoundException;

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
    private final Path bannerStorageLocation;
    private final UserService userService;
    private final TicketCategoryService ticketCategoryService;
    
    @Autowired
    public EventController(EventService eventService, VenueService venueService, ActService actService, UserService userService, TicketCategoryService ticketCategoryService) {
        this.eventService = eventService;
        this.venueService = venueService;
        this.userService = userService;
        this.ticketCategoryService = ticketCategoryService;
        this.actService = actService;
        this.bannerStorageLocation = Paths.get("uploads/banners").toAbsolutePath().normalize();
        
        // Create directory if it doesn't exist
        try {
            Files.createDirectories(this.bannerStorageLocation);
        } catch (IOException ex) {
            throw new RuntimeException("Could not create the directory where banners will be stored.", ex);
        }
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
     * Retrieves all scheduled events
     * @return List of all scheduled events
     */
    @GetMapping("/scheduled")
    public ResponseEntity<List<Event>> getScheduledEvents() {
        return ResponseEntity.ok(eventService.getEventsByStatus("SCHEDULED"));
    }
    
    /**
     * Retrieves all postponed events
     * @return List of all postponed events
     */
    @GetMapping("/postponed")
    public ResponseEntity<List<Event>> getPostponedEvents() {
        return ResponseEntity.ok(eventService.getEventsByStatus("POSTPONED"));
    }
    
    /**
     * Retrieves all cancelled events
     * @return List of all cancelled events
     */
    @GetMapping("/cancelled")
    public ResponseEntity<List<Event>> getCancelledEvents() {
        return ResponseEntity.ok(eventService.getEventsByStatus("CANCELLED"));
    }
    
    /**
     * Retrieves an event by ID
     * @param id The event ID
     * @return The event or 404 if not found
     */
    @GetMapping("/{id}")
    public ResponseEntity<Event> getEventById(@PathVariable Long id) {
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
    @PostMapping("/create-form")
    public ModelAndView createEventForm(@RequestParam String name,
                                   @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
                                   @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime time,
                                   @RequestParam Long venue,
                                   @RequestParam(value = "lineup") List<Long> lineup,
                                   @RequestParam String status) {
        Event event = new Event();
        event.setName(name);
        event.setDate(date);
        event.setTime(time);
        
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
    @PostMapping("/create")
public ResponseEntity<Event> createEvent(@RequestBody Map<String, Object> requestData) {
    try {
        // Extract event data and ticket categories from the request
        @SuppressWarnings("unchecked")
        Map<String, Object> eventData = (Map<String, Object>) requestData.get("event");
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> ticketCategoriesData = (List<Map<String, Object>>) requestData.get("ticketCategories");
        
        // Get user ID
        Long userId = Long.valueOf(requestData.get("userId").toString());
        User user = userService.getUserById(userId);
        
        // Create event object from the data
        Event event = new Event();
        event.setName((String) eventData.get("name"));
        event.setDescription((String) eventData.get("description"));
        // Parse date and time
        event.setDate(LocalDate.parse((String) eventData.get("date")));
        event.setTime(LocalTime.parse((String) eventData.get("time")));
        
        // Set venue information
        Venue venue = new Venue();
        venue.setName((String) eventData.get("venue"));
        // You might need to add more venue details here
        
        event.setVenue(venue);
        event.setUser(user);
        
        // Set default status
        event.setStatus(Event.STATUS_SCHEDULED);
        
        // Save the event first
        Event createdEvent = eventService.createEvent(event);
        
        // Now create and associate ticket categories
        if (ticketCategoriesData != null && !ticketCategoriesData.isEmpty()) {
            for (Map<String, Object> categoryData : ticketCategoriesData) {
                TicketCategory category = new TicketCategory();
                category.setName((String) categoryData.get("name"));
                category.setPrice(Double.valueOf(categoryData.get("price").toString()));
                category.setDescription((String) categoryData.get("description"));
                category.setTotalTickets(Integer.valueOf(categoryData.get("capacity").toString()));
                category.setTicketsSold(0); // Initially no tickets sold
                category.setStatus("AVAILABLE");
                category.setActive(true);
                category.setEvent(createdEvent);
                
                // Add service method to save ticket category
                ticketCategoryService.createTicketCategory(category);
            }
        }
        
        return ResponseEntity.status(HttpStatus.CREATED).body(createdEvent);
    } catch (Exception e) {
        e.printStackTrace();
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
    public ResponseEntity<Event> updateEvent(@PathVariable Long id, @RequestBody Event eventDetails) {
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
     * Restores a cancelled or postponed event
     * @param id The event ID to restore
     * @return Success message or error
     */
    @PostMapping("/restore/{id}")
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
    
    /**
     * Cancels an event by setting its status to CANCELLED
     * @param id The event ID to cancel
     * @return Success message or error
     */
    @DeleteMapping("/{id}/cancel")
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
     * Postpones an event by setting its status to POSTPONED
     * @param id The event ID to postpone
     * @return Success message or error
     */
    @PutMapping("/{id}/postpone")
    public ResponseEntity<?> postponeEvent(@PathVariable Long id) {
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
     * Uploads a banner image for an event
     * @param id The event ID
     * @param file The image file to upload
     * @return Success message or error
     */
    @PostMapping("/{id}/banner")
    public ResponseEntity<?> uploadBanner(@PathVariable Long id, @RequestParam("file") MultipartFile file) {
        try {
            // Verify event exists
            Event event = eventService.getEventById(id);
            
            // Generate unique filename
            String fileExtension = getFileExtension(file.getOriginalFilename());
            String fileName = UUID.randomUUID().toString() + fileExtension;
            
            // Save file
            Path targetLocation = this.bannerStorageLocation.resolve(fileName);
            Files.copy(file.getInputStream(), targetLocation);
            
            // Update event with banner path
            event.setBannerImage(fileName);
            eventService.updateEvent(event);
            
            return ResponseEntity.ok(Collections.singletonMap("message", "Banner uploaded successfully"));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Collections.singletonMap("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Collections.singletonMap("error", e.getMessage()));
        }
    }
    
    /**
     * Deletes the banner image for an event
     * @param id The event ID
     * @return Success message or error
     */
    @DeleteMapping("/{id}/banner")
    public ResponseEntity<?> deleteBanner(@PathVariable Long id) {
        try {
            // Verify event exists
            Event event = eventService.getEventById(id);
            
            // Delete file if exists
            if (event.getBannerImage() != null && !event.getBannerImage().isEmpty()) {
                Path filePath = this.bannerStorageLocation.resolve(event.getBannerImage()).normalize();
                Files.deleteIfExists(filePath);
                
                // Update event
                event.setBannerImage(null);
                eventService.updateEvent(event);
            }
            
            return ResponseEntity.ok(Collections.singletonMap("message", "Banner deleted successfully"));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Collections.singletonMap("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Collections.singletonMap("error", e.getMessage()));
        }
    }
    
    /**
     * Serves image files from the file system
     * @param filename The image filename
     * @return The image file
     */
    @GetMapping("/images/{filename:.+}")
    public ResponseEntity<Resource> serveImage(@PathVariable String filename) {
        try {
            Path filePath = this.bannerStorageLocation.resolve(filename).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            
            if (resource.exists()) {
                return ResponseEntity.ok()
                        .contentType(MediaType.IMAGE_JPEG)
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (MalformedURLException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Helper method to extract file extension
     */
    private String getFileExtension(String filename) {
        if (filename == null) {
            return "";
        }
        int lastIndexOf = filename.lastIndexOf(".");
        if (lastIndexOf == -1) {
            return "";
        }
        return filename.substring(lastIndexOf);
    }
}
