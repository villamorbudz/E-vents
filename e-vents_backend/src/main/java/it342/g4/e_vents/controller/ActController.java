package it342.g4.e_vents.controller;

import it342.g4.e_vents.model.Act;
import it342.g4.e_vents.service.ActService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

/**
 * Controller for act-related operations
 */
@RestController
@RequestMapping("/api/acts")
@CrossOrigin(origins = "*")
public class ActController {

    private final ActService actService;
    
    @Autowired
    public ActController(ActService actService) {
        this.actService = actService;
    }
    
    /**
     * Retrieves all active acts
     * @return List of all active acts
     */
    @GetMapping
    public ResponseEntity<List<Act>> getAllActs() {
        return ResponseEntity.ok(actService.getAllActs());
    }
    
    /**
     * Retrieves all acts including inactive ones
     * @return List of all acts
     */
    @GetMapping("/all")
    public ResponseEntity<List<Act>> getAllActsIncludingInactive() {
        return ResponseEntity.ok(actService.getAllActsIncludingInactive());
    }
    
    /**
     * Retrieves an active act by ID
     * @param id The act ID
     * @return The act or 404 if not found
     */
    @GetMapping("/{id}")
    public ResponseEntity<Act> getActById(@PathVariable Long id) {
        return actService.findActById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * Creates a new act
     * @param act Act data from request body
     * @return The created act
     */
    @PostMapping("/create")
    public ResponseEntity<Act> createAct(@RequestBody Act act) {
        try {
            Act createdAct = actService.createAct(act);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdAct);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Updates an existing act
     * @param id The act ID to update
     * @param actDetails Updated act data
     * @return The updated act or error
     */
    @PutMapping("/{id}/edit")
    public ResponseEntity<?> updateAct(@PathVariable Long id, @RequestBody Act actDetails) {
        try {
            // Set the ID from the path
            actDetails.setActId(id);
            
            // Update and return
            Act updatedAct = actService.updateAct(actDetails);
            return ResponseEntity.ok(updatedAct);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Collections.singletonMap("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Collections.singletonMap("error", e.getMessage()));
        }
    }
    
    /**
     * Soft deletes an act by setting isActive to false
     * @param id The act ID to soft delete
     * @return Success message or error
     */
    @DeleteMapping("/{id}/deactivate")
    public ResponseEntity<?> softDeleteAct(@PathVariable Long id) {
        try {
            actService.softDeleteAct(id);
            return ResponseEntity.ok(Collections.singletonMap("message", "Act deactivated successfully"));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Collections.singletonMap("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Collections.singletonMap("error", e.getMessage()));
        }
    }
    
    /**
     * Restores a soft-deleted act by setting isActive to true
     * @param id The act ID to restore
     * @return The restored act or error
     */
    @PostMapping("/{id}/restore")
    public ResponseEntity<?> restoreAct(@PathVariable Long id) {
        try {
            Act restoredAct = actService.restoreAct(id);
            return ResponseEntity.ok(restoredAct);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Collections.singletonMap("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Collections.singletonMap("error", e.getMessage()));
        }
    }
    
    /**
     * Permanently deletes an act
     * @param id The act ID to permanently delete
     * @return Success message or error
     */
    @DeleteMapping("/{id}/delete")
    public ResponseEntity<?> permanentlyDeleteAct(@PathVariable Long id) {
        try {
            actService.deleteAct(id);
            return ResponseEntity.ok(Collections.singletonMap("message", "Act permanently deleted"));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Collections.singletonMap("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Collections.singletonMap("error", e.getMessage()));
        }
    }
}
