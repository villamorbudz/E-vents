package it342.g4.e_vents.controller;

import it342.g4.e_vents.model.Act;
import it342.g4.e_vents.model.Event;
import it342.g4.e_vents.model.Role;
import it342.g4.e_vents.model.Tags;
import it342.g4.e_vents.model.User;
import it342.g4.e_vents.service.ActService;
import it342.g4.e_vents.service.CategoryService;
import it342.g4.e_vents.service.EventService;
import it342.g4.e_vents.service.RoleService;
import it342.g4.e_vents.service.TagsService;
import it342.g4.e_vents.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller for admin dashboard
 */
@Controller
@RequestMapping("/admin")
public class AdminController {
    
    @InitBinder
    public void initBinder(WebDataBinder binder) {
        // No need for custom date/time binding as Spring handles LocalDate/LocalTime natively
    }

    private final UserService userService;
    private final EventService eventService;
    private final ActService actService;
    private final RoleService roleService;
    private final TagsService tagsService;
    private final CategoryService categoryService;
    
    @Autowired
    public AdminController(UserService userService, EventService eventService, 
                          ActService actService,
                          RoleService roleService, TagsService tagsService,
                          CategoryService categoryService) {
        this.userService = userService;
        this.eventService = eventService;
        this.actService = actService;
        this.roleService = roleService;
        this.tagsService = tagsService;
        this.categoryService = categoryService;
    }

    /**
     * Admin dashboard home page with dynamic entity management
     */
    @GetMapping
    public String dashboard(Model model, @RequestParam(required = false, defaultValue = "dashboard") String entityType) {
        // Get counts for dashboard cards
        long userCount = userService.countAllUsers();
        long eventCount = eventService.countAllEvents();
        long actCount = actService.countAllActs();
        
        // Add counts to model
        model.addAttribute("userCount", userCount);
        model.addAttribute("eventCount", eventCount);
        model.addAttribute("actCount", actCount);
        
        // Set active tab and page title
        model.addAttribute("activeTab", entityType);
        model.addAttribute("pageTitle", "Admin Dashboard");
        
        // Load entity data based on the selected tab
        if ("users".equals(entityType)) {
            List<User> users = userService.getAllUsers();
            model.addAttribute("users", users);
            model.addAttribute("entityName", "Users");
            model.addAttribute("entityIcon", "bi-people");
            model.addAttribute("entityColor", "primary");
            
            // Define table columns for users
            Map<String, String> columns = new HashMap<>();
            columns.put("userId", "ID");
            columns.put("firstName,lastName", "Name");
            columns.put("email", "Email");
            columns.put("role.name", "Role");
            columns.put("active", "Status");
            model.addAttribute("columns", columns);
            
        } else if ("events".equals(entityType)) {
            List<Event> events = eventService.getAllEvents();
            model.addAttribute("events", events);
            model.addAttribute("entityName", "Events");
            model.addAttribute("entityIcon", "bi-calendar-event");
            model.addAttribute("entityColor", "success");
            
            // Define table columns for events
            Map<String, String> columns = new HashMap<>();
            columns.put("eventId", "ID");
            columns.put("name", "Name");
            columns.put("date", "Date");
            columns.put("time", "Time");
            columns.put("status", "Status");
            model.addAttribute("columns", columns);
            
        } else if ("acts".equals(entityType)) {
            List<Act> acts = actService.getAllActs();
            model.addAttribute("acts", acts);
            model.addAttribute("entityName", "Acts");
            model.addAttribute("entityIcon", "bi-music-note-beamed");
            model.addAttribute("entityColor", "warning");
            
            // Define table columns for acts
            Map<String, String> columns = new HashMap<>();
            columns.put("actId", "ID");
            columns.put("name", "Name");
            columns.put("description", "Description");
            columns.put("category", "Genre");
            model.addAttribute("columns", columns);
        } else if ("tags".equals(entityType)) {
            List<Tags> tags = tagsService.getAllTags();
            model.addAttribute("tags", tags);
            model.addAttribute("entityName", "Tags");
            model.addAttribute("entityIcon", "bi-tags");
            model.addAttribute("entityColor", "info");
            
            // Define table columns for tags
            Map<String, String> columns = new HashMap<>();
            columns.put("tagId", "ID");
            columns.put("name", "Name");
            columns.put("category.name", "Category");
            model.addAttribute("columns", columns);
            
        } else {
            // Dashboard view - show recent data
            List<User> recentUsers = userService.getRecentUsers(5);
            List<Event> upcomingEvents = eventService.getUpcomingEvents(5);
            List<Act> acts = actService.getAllActs();
            
            model.addAttribute("recentUsers", recentUsers);
            model.addAttribute("upcomingEvents", upcomingEvents);
            model.addAttribute("acts", acts);
        }
        
        return "admin/dashboard";
    }
    
    // ==================== USER MANAGEMENT ====================
    
    /**
     * Display form to create a new user
     */
    @GetMapping("/users/new")
    public String newUserForm(Model model) {
        model.addAttribute("user", new User());
        model.addAttribute("roles", roleService.getAllRoles());
        model.addAttribute("pageTitle", "Create New User");
        return "admin/user-form";
    }
    
    /**
     * Process new user form submission
     */
    @PostMapping("/users/new")
    public String createUser(@ModelAttribute User user, RedirectAttributes redirectAttributes) {
        try {
            userService.registerUser(user);
            redirectAttributes.addFlashAttribute("successMessage", "User created successfully");
            return "redirect:/admin?entityType=users";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error creating user: " + e.getMessage());
            return "redirect:/admin/users/new";
        }
    }
    
    /**
     * Display form to edit an existing user
     */
    @GetMapping("/users/{id}/edit")
    public String editUserForm(@PathVariable("id") Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            User user = userService.getUser(id).orElseThrow(() -> 
                new EntityNotFoundException("User not found with ID: " + id));
            
            model.addAttribute("user", user);
            model.addAttribute("roles", roleService.getAllRoles());
            model.addAttribute("pageTitle", "Edit User");
            return "admin/user-form";
        } catch (EntityNotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/admin?entityType=users";
        }
    }
    
    /**
     * Delete a user
     */
    @GetMapping("/users/{id}/delete")
    public String deleteUser(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            userService.softDeleteUser(id);
            redirectAttributes.addFlashAttribute("successMessage", "User deleted successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error deleting user: " + e.getMessage());
        }
        return "redirect:/admin?entityType=users";
    }
    
    /**
     * Restore a previously deleted user
     */
    @GetMapping("/users/{id}/restore")
    public String restoreUser(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            userService.restoreUser(id);
            redirectAttributes.addFlashAttribute("successMessage", "User restored successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error restoring user: " + e.getMessage());
        }
        return "redirect:/admin?entityType=users";
    }
    
    // ==================== EVENT MANAGEMENT ====================
    
    /**
     * Display form to create a new event
     */
    @GetMapping("/events/new")
    public String newEventForm(Model model) {
        model.addAttribute("event", new Event());
        model.addAttribute("acts", actService.getAllActs());
        model.addAttribute("pageTitle", "Create New Event");
        return "admin/event-form";
    }
    
    /**
     * Process new event form submission
     */
    @PostMapping("/events/new")
    public String createEvent(@ModelAttribute Event event, RedirectAttributes redirectAttributes) {
        try {
            eventService.createEvent(event);
            redirectAttributes.addFlashAttribute("successMessage", "Event created successfully");
            return "redirect:/admin?entityType=events";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error creating event: " + e.getMessage());
            return "redirect:/admin/events/new";
        }
    }
    
    /**
     * Display form to edit an existing event
     */
    @GetMapping("/events/{id}/edit")
    public String editEventForm(@PathVariable("id") Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            Event event = eventService.getEventById(id);
            
            // Format date for HTML5 date input
            if (event.getDate() != null) {
                String formattedDate = event.getDate().toString();
                model.addAttribute("formattedDate", formattedDate);
                
                // For debugging
                System.out.println("Formatted date: " + formattedDate);
            } else {
                System.out.println("Event date is null");
            }
            
            // Format time for HTML5 time input
            if (event.getTime() != null) {
                String formattedTime = event.getTime().toString();
                model.addAttribute("formattedTime", formattedTime);
                
                // For debugging
                System.out.println("Formatted time: " + formattedTime);
            } else {
                System.out.println("Event time is null");
            }
            
            model.addAttribute("event", event);
            model.addAttribute("acts", actService.getAllActs());
            model.addAttribute("pageTitle", "Edit Event");
            return "admin/event-form";
        } catch (EntityNotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/admin?entityType=events";
        }
    }
    
    /**
     * Process edit event form submission
     */
    @PostMapping("/events/{id}/edit")
    public String updateEvent(@PathVariable("id") Long id, @ModelAttribute Event event, 
                            RedirectAttributes redirectAttributes) {
        try {
            // Ensure the ID is set correctly
            event.setEventId(id);
            eventService.updateEvent(event);
            redirectAttributes.addFlashAttribute("successMessage", "Event updated successfully");
            return "redirect:/admin?entityType=events";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error updating event: " + e.getMessage());
            return "redirect:/admin/events/" + id + "/edit";
        }
    }
    
    /**
     * Delete an event
     */
    @GetMapping("/events/{id}/delete")
    public String deleteEvent(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            eventService.deleteEvent(id);
            redirectAttributes.addFlashAttribute("successMessage", "Event deleted successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error deleting event: " + e.getMessage());
        }
        return "redirect:/admin?entityType=events";
    }
    
    // ==================== ACT MANAGEMENT ====================
    
    /**
     * Display form to create a new act
     */
    @GetMapping("/acts/new")
    public String newActForm(Model model) {
        model.addAttribute("act", new Act());
        model.addAttribute("pageTitle", "Create New Act");
        model.addAttribute("allTags", tagsService.getAllTags());
        return "admin/act-form";
    }
    
    /**
     * Process new act form submission
     */
    @PostMapping("/acts/new")
    public String createAct(@ModelAttribute Act act, RedirectAttributes redirectAttributes) {
        try {
            actService.createAct(act);
            redirectAttributes.addFlashAttribute("successMessage", "Act created successfully");
            return "redirect:/admin?entityType=acts";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error creating act: " + e.getMessage());
            return "redirect:/admin/acts/new";
        }
    }
    
    /**
     * Display form to edit an existing act
     */
    @GetMapping("/acts/{id}/edit")
    public String editActForm(@PathVariable("id") Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            Act act = actService.getActById(id);
            model.addAttribute("act", act);
            model.addAttribute("pageTitle", "Edit Act");
            model.addAttribute("allTags", tagsService.getAllTags());
            return "admin/act-form";
        } catch (EntityNotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/admin?entityType=acts";
        }
    }
    
    /**
     * Process edit act form submission
     */
    @PostMapping("/acts/{id}/edit")
    public String updateAct(@PathVariable("id") Long id, @ModelAttribute Act act, 
                          RedirectAttributes redirectAttributes) {
        try {
            // Ensure the ID is set correctly
            act.setActId(id);
            actService.updateAct(act);
            redirectAttributes.addFlashAttribute("successMessage", "Act updated successfully");
            return "redirect:/admin?entityType=acts";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error updating act: " + e.getMessage());
            return "redirect:/admin/acts/" + id + "/edit";
        }
    }
    
    /**
     * Delete an act
     */
    @GetMapping("/acts/{id}/delete")
    public String deleteAct(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            actService.deleteAct(id);
            redirectAttributes.addFlashAttribute("successMessage", "Act deleted successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error deleting act: " + e.getMessage());
        }
        return "redirect:/admin?entityType=acts";
    }
    
    // ==================== TAG MANAGEMENT ====================
    
    /**
     * Display form to create a new tag
     */
    @GetMapping("/tags/new")
    public String newTagForm(Model model) {
        model.addAttribute("tag", new Tags());
        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("pageTitle", "Create New Tag");
        return "admin/tag-form";
    }
    
    /**
     * Process new tag form submission
     */
    @PostMapping("/tags/new")
    public String createTag(@ModelAttribute Tags tag, RedirectAttributes redirectAttributes) {
        try {
            tagsService.saveTag(tag);
            redirectAttributes.addFlashAttribute("successMessage", "Tag created successfully");
            return "redirect:/admin?entityType=tags";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error creating tag: " + e.getMessage());
            return "redirect:/admin/tags/new";
        }
    }
    
    /**
     * Display form to edit an existing tag
     */
    @GetMapping("/tags/{id}/edit")
    public String editTagForm(@PathVariable("id") Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            Tags tag = tagsService.getTagById(id)
                    .orElseThrow(() -> new EntityNotFoundException("Tag not found with ID: " + id));
            model.addAttribute("tag", tag);
            model.addAttribute("categories", categoryService.getAllCategories());
            model.addAttribute("pageTitle", "Edit Tag");
            return "admin/tag-form";
        } catch (EntityNotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/admin?entityType=tags";
        }
    }
    
    /**
     * Process edit tag form submission
     */
    @PostMapping("/tags/{id}/edit")
    public String updateTag(@PathVariable("id") Long id, @ModelAttribute Tags tag, 
                          RedirectAttributes redirectAttributes) {
        try {
            // Ensure the ID is set correctly
            tag.setTagId(id);
            tagsService.saveTag(tag);
            redirectAttributes.addFlashAttribute("successMessage", "Tag updated successfully");
            return "redirect:/admin?entityType=tags";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error updating tag: " + e.getMessage());
            return "redirect:/admin/tags/" + id + "/edit";
        }
    }
    
    /**
     * Delete a tag
     */
    @GetMapping("/tags/{id}/delete")
    public String deleteTag(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            tagsService.deleteTag(id);
            redirectAttributes.addFlashAttribute("successMessage", "Tag deleted successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error deleting tag: " + e.getMessage());
        }
        return "redirect:/admin?entityType=tags";
    }
}