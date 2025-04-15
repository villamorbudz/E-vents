package it342.g4.e_vents.service;

import it342.g4.e_vents.model.Event;
import it342.g4.e_vents.repository.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EventService {

    @Autowired
    private EventRepository eventRepository;

    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }

    public void saveEvent(Event event) {
        eventRepository.save(event);
    }

    public Optional<Event> findEventById(Long id) {
        return eventRepository.findById(id);
    }
}
