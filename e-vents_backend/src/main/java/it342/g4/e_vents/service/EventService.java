package it342.g4.e_vents.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
     * Retrieves all events with a specific status
     * @param status The status to filter by
     * @return List of events with the specified status
     */
    public List<Event> getEventsByStatus(String status) {
        return eventRepository.findAll().stream()
                .filter(event -> status.equals(event.getStatus()))
                .collect(Collectors.toList());
    }
    
    /**
     * Gets all scheduled events
     * @return List of scheduled events
     */
    public List<Event> getScheduledEvents() {
        return getEventsByStatus(Event.STATUS_SCHEDULED);
    }
    
    /**
     * Gets all postponed events
     * @return List of postponed events
     */
    public List<Event> getPostponedEvents() {
        return getEventsByStatus(Event.STATUS_POSTPONED);
    }
    
    /**
     * Gets all cancelled events
     * @return List of cancelled events
     */
    public List<Event> getCancelledEvents() {
        return getEventsByStatus(Event.STATUS_CANCELLED);
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
    @Transactional
    public Event createEvent(Event event) {
        // Set default status if not specified
        if (event.getStatus() == null) {
            event.setStatus(Event.STATUS_SCHEDULED);
        }
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
     * Cancels an event by setting its status to CANCELLED
     * @param id The ID of the event to cancel
     * @return The updated event
     * @throws EntityNotFoundException if the event is not found
     */
    public Event cancelEvent(Long id) {
        return updateEventStatus(id, Event.STATUS_CANCELLED);
    }
    
    /**
     * Postpones an event by setting its status to POSTPONED
     * @param id The ID of the event to postpone
     * @return The updated event
     * @throws EntityNotFoundException if the event is not found
     */
    public Event postponeEvent(Long id) {
        return updateEventStatus(id, Event.STATUS_POSTPONED);
    }
    
    /**
     * Restores a cancelled or postponed event by setting its status to SCHEDULED
     * @param id The ID of the event to restore
     * @return The updated event
     * @throws EntityNotFoundException if the event is not found
     */
    public Event restoreEvent(Long id) {
        return updateEventStatus(id, Event.STATUS_SCHEDULED);
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
                .filter(event -> Event.STATUS_SCHEDULED.equals(event.getStatus()))
                .limit(limit)
                .toList();
    }

    /**
     * Counts the number of active events in the system
     * @return The count of active events
     */
    public long countActiveEvents() {
        return eventRepository.countByIsActiveTrue();
    }
}
