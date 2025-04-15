package it342.g4.e_vents.controller;

import it342.g4.e_vents.model.Venue;
import it342.g4.e_vents.model.Act;
import it342.g4.e_vents.model.Event;
import it342.g4.e_vents.service.VenueService;
import it342.g4.e_vents.service.ActService;
import it342.g4.e_vents.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import org.springframework.web.servlet.ModelAndView;

@RestController
@RequestMapping("/api")
public class EventController {

    @GetMapping("/events/edit/{id}")
    public ModelAndView showEditEventPage(@PathVariable Long id) {
        ModelAndView mav = new ModelAndView("edit_event");
        mav.addObject("eventId", id);
        return mav;
    }

    @GetMapping("/events/{id}")
    public ResponseEntity<Event> getEventById(@PathVariable Long id) {
        Optional<Event> event = eventService.findEventById(id);
        return event.map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Autowired
    private VenueService venueService;

    @Autowired
    private ActService actService;

    @Autowired
    private EventService eventService;

    @GetMapping("/venues")
    public List<Venue> getVenues() {
        return venueService.getAllVenues();
    }

    @GetMapping("/acts")
    public List<Act> getActs() {
        return actService.getAllActs();
    }

    @GetMapping("/events")
    public List<Event> getAllEvents() {
        return eventService.getAllEvents();
    }

    @PostMapping("/events/create")
    public ModelAndView createEvent(@RequestParam String name,
                              @RequestParam String date,
                              @RequestParam String time,
                              @RequestParam Long venue,
                              @RequestParam(value = "lineup") List<Long> lineup,
                              @RequestParam String status) {
        Event event = new Event();
        event.setName(name);
        event.setDate(java.sql.Date.valueOf(date));
        event.setTime(java.sql.Time.valueOf(time+":00")); // HTML time is HH:mm, SQL Time expects HH:mm:ss
        event.setVenue(venueService.getAllVenues().stream().filter(v -> v.getVenueId().equals(venue)).findFirst().orElse(null));
        event.setLineup(
            actService.getAllActs().stream().filter(a -> lineup.contains(a.getActId())).toList()
        );
        event.setStatus(status);
        eventService.saveEvent(event);
        return new ModelAndView("redirect:/events/dashboard");
    }

    @PutMapping("/events/edit/{id}")
    public ResponseEntity<?> editEvent(@PathVariable Long id, @RequestBody Event eventDetails) {
        Optional<Event> eventOptional = eventService.findEventById(id);
        if (eventOptional.isPresent()) {
            Event event = eventOptional.get();
            event.setName(eventDetails.getName());
            event.setDate(eventDetails.getDate());
            event.setTime(eventDetails.getTime());
            event.setVenue(eventDetails.getVenue());
            event.setLineup(eventDetails.getLineup());
            event.setStatus(eventDetails.getStatus());
            eventService.saveEvent(event);
            return ResponseEntity.ok("Event updated successfully");
        } else {
            return ResponseEntity.status(404).body("Event not found");
        }
    }

    @DeleteMapping("/events/{id}")
    public ResponseEntity<?> deleteEvent(@PathVariable Long id) {
        Optional<Event> eventOptional = eventService.findEventById(id);
        if (eventOptional.isPresent()) {
            Event event = eventOptional.get();
            event.setStatus("cancelled");
            eventService.saveEvent(event);
            return ResponseEntity.ok("Event cancelled");
        } else {
            return ResponseEntity.status(404).body("Event not found");
        }
    }

    @PutMapping("/events/{id}/restore")
    public ResponseEntity<?> restoreEvent(@PathVariable Long id) {
        Optional<Event> eventOptional = eventService.findEventById(id);
        if (eventOptional.isPresent()) {
            Event event = eventOptional.get();
            event.setStatus("scheduled");
            eventService.saveEvent(event);
            return ResponseEntity.ok("Event restored successfully");
        } else {
            return ResponseEntity.status(404).body("Event not found");
        }
    }
}
