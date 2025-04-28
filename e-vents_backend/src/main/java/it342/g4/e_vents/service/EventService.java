package it342.g4.e_vents.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it342.g4.e_vents.model.Event;
import it342.g4.e_vents.repository.EventRepository;
import jakarta.persistence.EntityNotFoundException;

/**
 * Service for managing Event entities
 */
@Service
public class EventService {

    private final EventRepository eventRepository;
    
    @Autowired
    public EventService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
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
        return updateEventStatus(id, "cancelled");
    }
    
    /**
     * Restores a cancelled event by setting its status to 'scheduled'
     * @param id The ID of the event to restore
     * @return The updated event
     * @throws EntityNotFoundException if the event is not found
     */
    public Event restoreEvent(Long id) {
        return updateEventStatus(id, "scheduled");
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
     * Counts all events in the system
     * @return The total number of events
     */
    public long countAllEvents() {
        return eventRepository.count();
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
}
