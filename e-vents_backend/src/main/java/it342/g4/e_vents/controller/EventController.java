package it342.g4.e_vents.controller;

import it342.g4.e_vents.model.Event;
import it342.g4.e_vents.repository.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/events")
public class EventController {

    @Autowired
    private EventRepository eventRepository;

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("event", new Event());
        return "createEvent";
    }

    @PostMapping("/create")
    public String createEvent(@ModelAttribute Event event) {
        eventRepository.save(event);
        return "redirect:/events/dashboard";
    }

    @GetMapping("/dashboard")
    public String showDashboard(Model model) {
        List<Event> events = eventRepository.findAll();
        model.addAttribute("events", events);
        return "dashboard";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Event event = eventRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid event Id:" + id));
        model.addAttribute("event", event);
        return "editEvent";
    }

    @PostMapping("/edit/{id}")
    public String updateEvent(@PathVariable Long id, @ModelAttribute Event event) {
        event.setId(id);
        eventRepository.save(event);
        return "redirect:/events/dashboard";
    }

    @GetMapping("/delete/{id}")
    public String deleteEvent(@PathVariable Long id, Model model) {
        Event event = eventRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid event Id:" + id));
        model.addAttribute("event", event);
        return "confirmDelete";
    }

    @PostMapping("/delete/{id}")
    public String confirmDeleteEvent(@PathVariable Long id) {
        eventRepository.deleteById(id);
        return "redirect:/events/dashboard";
    }
}
