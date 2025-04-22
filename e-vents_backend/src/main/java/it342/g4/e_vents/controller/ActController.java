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
     * Retrieves all acts
     * @return List of all acts
     */
    @GetMapping
    public ResponseEntity<List<Act>> getAllActs() {
        return ResponseEntity.ok(actService.getAllActs());
    }
    
    /**
     * Retrieves an act by ID
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
    @PostMapping
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
    @PutMapping("/{id}")
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
     * Deletes an act
     * @param id The act ID to delete
     * @return Success message or error
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteAct(@PathVariable Long id) {
        try {
            actService.deleteAct(id);
            return ResponseEntity.ok(Collections.singletonMap("message", "Act deleted successfully"));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Collections.singletonMap("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Collections.singletonMap("error", e.getMessage()));
        }
    }
}
