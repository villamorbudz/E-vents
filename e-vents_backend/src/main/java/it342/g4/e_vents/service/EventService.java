package it342.g4.e_vents.service;

import it342.g4.e_vents.model.Event;
import it342.g4.e_vents.repository.EventRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

/**
 * Service for managing Event entities
 */
@Service
public class EventService {

    private final EventRepository eventRepository;
    private final FileStorageService fileStorageService;
    
    @Autowired
    public EventService(EventRepository eventRepository, FileStorageService fileStorageService) {
        this.eventRepository = eventRepository;
        this.fileStorageService = fileStorageService;
    }

    /**
     * Retrieves all events from the database
     * @return List of all events
     */
    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }
    
    /**
     * Finds an event by its ID
     * @param id The event ID to look up
     * @return Optional containing the event if found
     */
    public Optional<Event> findEventById(Long id) {
        return eventRepository.findById(id);
    }
    
    /**
     * Gets an event by ID or throws an exception if not found
     * @param id The event ID to look up
     * @return The event
     * @throws EntityNotFoundException if the event is not found
     */
    public Event getEventById(Long id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Event not found with ID: " + id));
    }
    
    /**
     * Creates a new event
     * @param event The event to create
     * @return The created event with ID
     */
    public Event createEvent(Event event) {
        return eventRepository.save(event);
    }
    
    /**
     * Updates an existing event
     * @param event The event with updated fields
     * @return The updated event
     */
    public Event updateEvent(Event event) {
        return eventRepository.save(event);
    }
    
    /**
     * Updates an event's status
     * @param id The ID of the event to update
     * @param status The new status
     * @return The updated event
     * @throws EntityNotFoundException if the event is not found
     */
    public Event updateEventStatus(Long id, String status) {
        Event event = getEventById(id);
        event.setStatus(status);
        return eventRepository.save(event);
    }
    
    /**
     * Cancels an event by setting its status to 'cancelled'
     * @param id The ID of the event to cancel
     * @return The updated event
     * @throws EntityNotFoundException if the event is not found
     */
    public Event cancelEvent(Long id) {
        return updateEventStatus(id, "CANCELLED");
    }
    
    /**
     * Restores a cancelled event by setting its status to 'scheduled'
     * @param id The ID of the event to restore
     * @return The updated event
     * @throws EntityNotFoundException if the event is not found
     */
    public Event restoreEvent(Long id) {
        return updateEventStatus(id, "SCHEDULED");
    }

    /**
     * Postpones event by setting its status to 'postponed'
     * @param id The ID of the event to restore
     * @return The updated event
     * @throws EntityNotFoundException if the event is not found
     */
    public Event postponeEvent(Long id) {
        return updateEventStatus(id, "POSTPONED");
    }

    /**
     * Deletes an event permanently from the database
     * @param id The ID of the event to delete
     * @throws EntityNotFoundException if the event is not found
     */
    public void deleteEvent(Long id) {
        if (!eventRepository.existsById(id)) {
            throw new EntityNotFoundException("Event not found with ID: " + id);
        }
        eventRepository.deleteById(id);
    }
    
    /**
     * Counts all events in the database (active and inactive)
     * @return The total number of events
     */
    public long countAllEvents() {
        return eventRepository.count();
    }
    
    /**
     * Counts all active events in the database
     * @return The number of active events
     */
    public long countActiveEvents() {
        return eventRepository.countByIsActiveTrue();
    }
    
    /**
     * Gets upcoming events ordered by date
     * @param limit The maximum number of events to return
     * @return List of upcoming events
     */

    public List<Event> getUpcomingEvents(int limit) {
        // This is a simple implementation that returns the first N events
        // In a real application, you would filter by date > now and order by date
        return eventRepository.findAll().stream()
                .filter(event -> "scheduled".equalsIgnoreCase(event.getStatus()))
                .limit(limit)
                .toList();
    }
    
    /**
     * Uploads a banner image for an event
     * @param id The ID of the event
     * @param file The image file to upload
     * @return The updated event with banner image path
     * @throws EntityNotFoundException if the event is not found
     */
    public Event uploadBannerImage(Long id, MultipartFile file) {
        Event event = getEventById(id);
        
        // Delete old banner if exists
        if (event.getBannerImagePath() != null) {
            String oldFilename = fileStorageService.getFilenameFromPath(event.getBannerImagePath());
            if (oldFilename != null) {
                fileStorageService.deleteFile(oldFilename);
            }
        }
        
        // Store new file
        String filename = fileStorageService.storeFile(file);
        
        // Update event with new banner path
        event.setBannerImagePath("/api/events/images/" + filename);
        return eventRepository.save(event);
    }
    
    /**
     * Deletes the banner image for an event
     * @param id The ID of the event
     * @return The updated event without banner image
     * @throws EntityNotFoundException if the event is not found
     */
    public Event deleteBannerImage(Long id) {
        Event event = getEventById(id);
        
        // Delete file if exists
        if (event.getBannerImagePath() != null) {
            String filename = fileStorageService.getFilenameFromPath(event.getBannerImagePath());
            if (filename != null) {
                fileStorageService.deleteFile(filename);
            }
            
            // Update event to remove banner path
            event.setBannerImagePath(null);
            return eventRepository.save(event);
        }
        
        return event;
    }
}