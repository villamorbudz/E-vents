package it342.g4.e_vents.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import it342.g4.e_vents.model.Act;
import it342.g4.e_vents.service.ActService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Controller for act-related operations
 */
@RestController
@RequestMapping("/api/acts")
@CrossOrigin(origins = "*")
@Tag(name = "Act", description = "Act management APIs")
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
    @Operation(summary = "Get all active acts", description = "Retrieves a list of all active acts in the system")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved list of active acts", 
                     content = @Content(mediaType = "application/json", schema = @Schema(implementation = Act.class)))
    })
    public ResponseEntity<List<Act>> getAllActs() {
        return ResponseEntity.ok(actService.getAllActiveActs());
    }
    
    /**
     * Retrieves all acts, including inactive ones
     * @return List of all acts
     */
    @GetMapping("/all")
    @Operation(summary = "Get all acts including inactive", description = "Retrieves a list of all acts in the system, including inactive ones")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved list of all acts", 
                     content = @Content(mediaType = "application/json", schema = @Schema(implementation = Act.class)))
    })
    public ResponseEntity<List<Act>> getAllActsIncludingInactive() {
        return ResponseEntity.ok(actService.getAllActs());
    }
    
    /**
     * Retrieves an active act by ID
     * @param id The act ID
     * @return The act or 404 if not found
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get act by ID", description = "Retrieves a specific active act by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved the act", 
                     content = @Content(mediaType = "application/json", schema = @Schema(implementation = Act.class))),
        @ApiResponse(responseCode = "404", description = "Act not found", content = @Content)
    })
    public ResponseEntity<Act> getActById(
            @Parameter(description = "ID of the act to retrieve") @PathVariable Long id) {
        return actService.findActiveActById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * Retrieves the category name for an act
     * @param id The act ID
     * @return The category name or 404 if act not found
     */
    @GetMapping("/{id}/category-name")
    @Operation(summary = "Get category name for an act", description = "Retrieves the category name for a specific act")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved the category name", 
                     content = @Content(mediaType = "application/json")),
        @ApiResponse(responseCode = "404", description = "Act not found", content = @Content)
    })
    public ResponseEntity<Map<String, String>> getCategoryNameByActId(
            @Parameter(description = "ID of the act") @PathVariable Long id) {
        return actService.findActiveActById(id)
                .map(act -> ResponseEntity.ok(Collections.singletonMap("categoryName", act.getCategoryName())))
                .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * Retrieves the tag names for an act as a comma-separated string
     * @param id The act ID
     * @return The tag names or 404 if act not found
     */
    @GetMapping("/{id}/tag-names")
    @Operation(summary = "Get tag names for an act", description = "Retrieves the tag names for a specific act as a comma-separated string")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved the tag names", 
                     content = @Content(mediaType = "application/json")),
        @ApiResponse(responseCode = "404", description = "Act not found", content = @Content)
    })
    public ResponseEntity<Map<String, String>> getTagNamesByActId(
            @Parameter(description = "ID of the act") @PathVariable Long id) {
        return actService.findActiveActById(id)
                .map(act -> ResponseEntity.ok(Collections.singletonMap("tagNames", act.getTagNames())))
                .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * Creates a new act
     * @param act Act data from request body
     * @return The created act
     */
    @PostMapping("/create")
    @Operation(summary = "Create a new act", description = "Creates a new act in the system")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Act successfully created", 
                     content = @Content(mediaType = "application/json", schema = @Schema(implementation = Act.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content)
    })
    public ResponseEntity<Act> createAct(
            @Parameter(description = "Act object to be created", required = true) @RequestBody Act act) {
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
    @Operation(summary = "Update an act", description = "Updates an existing act by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Act successfully updated", 
                     content = @Content(mediaType = "application/json", schema = @Schema(implementation = Act.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content),
        @ApiResponse(responseCode = "404", description = "Act not found", content = @Content)
    })
    public ResponseEntity<?> updateAct(
            @Parameter(description = "ID of the act to update", required = true) @PathVariable Long id, 
            @Parameter(description = "Updated act details", required = true) @RequestBody Act actDetails) {
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
     * Deactivates (soft-deletes) an act
     * @param id The act ID to deactivate
     * @return Success message or error
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Deactivate an act", description = "Deactivates an act by setting it as inactive (soft delete)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Act successfully deactivated", 
                     content = @Content(mediaType = "application/json")),
        @ApiResponse(responseCode = "404", description = "Act not found", content = @Content),
        @ApiResponse(responseCode = "400", description = "Bad request", content = @Content)
    })
    public ResponseEntity<?> deactivateAct(
            @Parameter(description = "ID of the act to deactivate", required = true) @PathVariable Long id) {
        try {
            actService.deactivateAct(id);
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
     * Restores a previously deactivated act
     * @param id The act ID to restore
     * @return Success message or error
     */
    @PutMapping("/restore/{id}")
    @Operation(summary = "Restore an act", description = "Activates a previously deactivated act")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Act successfully restored", 
                     content = @Content(mediaType = "application/json")),
        @ApiResponse(responseCode = "404", description = "Act not found", content = @Content),
        @ApiResponse(responseCode = "400", description = "Bad request", content = @Content)
    })
    public ResponseEntity<?> restoreAct(
            @Parameter(description = "ID of the act to restore", required = true) @PathVariable Long id) {
        try {
            actService.restoreAct(id);
            return ResponseEntity.ok(Collections.singletonMap("message", "Act restored successfully"));
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
    @Operation(summary = "Delete an act permanently", description = "Permanently deletes an act from the system")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Act permanently deleted", 
                     content = @Content(mediaType = "application/json")),
        @ApiResponse(responseCode = "404", description = "Act not found", content = @Content),
        @ApiResponse(responseCode = "400", description = "Bad request", content = @Content)
    })
    public ResponseEntity<?> deleteActPermanently(
            @Parameter(description = "ID of the act to permanently delete", required = true) @PathVariable Long id) {
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
    
    /**
     * Get the count of active acts
     * @return ResponseEntity with the count of active acts
     */
    @Operation(summary = "Get count of active acts", description = "Returns the total number of active acts in the system")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved count", 
                    content = @Content(mediaType = "application/json", 
                    schema = @Schema(implementation = Long.class))),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/count")
    public ResponseEntity<Map<String, Long>> countActiveActs() {
        try {
            long count = actService.countActiveActs();
            Map<String, Long> response = Collections.singletonMap("count", count);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
