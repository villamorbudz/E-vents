package it342.g4.e_vents.controller;

import it342.g4.e_vents.model.Category;
import it342.g4.e_vents.model.Tags;
import it342.g4.e_vents.service.CategoryService;
import it342.g4.e_vents.service.TagsService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.Collections;
import java.util.List;

/**
 * REST Controller for tag-related operations
 */
@RestController
@RequestMapping("/api/tags")
@CrossOrigin(origins = "*")
@Tag(name = "Tag", description = "Tag management APIs")
public class TagController {

    private final TagsService tagsService;
    private final CategoryService categoryService;

    @Autowired
    public TagController(TagsService tagsService, CategoryService categoryService) {
        this.tagsService = tagsService;
        this.categoryService = categoryService;
    }

    /**
     * Retrieves all active tags
     * @return List of all active tags
     */
    @GetMapping
    @Operation(summary = "Get all active tags", description = "Retrieves a list of all active tags in the system")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved list of active tags", 
                     content = @Content(mediaType = "application/json", schema = @Schema(implementation = Tags.class)))
    })
    public ResponseEntity<List<Tags>> getAllActiveTags() {
        return ResponseEntity.ok(tagsService.getAllActiveTags());
    }

    /**
     * Retrieves all tags, including inactive ones
     * @return List of all tags
     */
    @GetMapping("/all")
    @Operation(summary = "Get all tags", description = "Retrieves a list of all tags in the system, including inactive ones")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved list of all tags", 
                     content = @Content(mediaType = "application/json", schema = @Schema(implementation = Tags.class)))
    })
    public ResponseEntity<List<Tags>> getAllTags() {
        return ResponseEntity.ok(tagsService.getAllTags());
    }

    /**
     * Retrieves an active tag by ID
     * @param id The tag ID
     * @return The tag or 404 if not found
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get tag by ID", description = "Retrieves a specific active tag by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved the tag", 
                     content = @Content(mediaType = "application/json", schema = @Schema(implementation = Tags.class))),
        @ApiResponse(responseCode = "404", description = "Tag not found", content = @Content)
    })
    public ResponseEntity<Tags> getTagById(
            @Parameter(description = "ID of the tag to retrieve", required = true) @PathVariable Long id) {
        return tagsService.getActiveTagById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Retrieves tags by category
     * @param categoryId The category ID
     * @return List of tags for the category
     */
    @GetMapping("/by-category/{categoryId}")
    @Operation(summary = "Get tags by category", description = "Retrieves all tags associated with a specific category")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved tags for the category", 
                     content = @Content(mediaType = "application/json", schema = @Schema(implementation = Tags.class))),
        @ApiResponse(responseCode = "404", description = "Category not found", content = @Content),
        @ApiResponse(responseCode = "400", description = "Bad request", content = @Content)
    })
    public ResponseEntity<List<Tags>> getTagsByCategory(
            @Parameter(description = "ID of the category to get tags for", required = true) @PathVariable Long categoryId) {
        try {
            Category category = categoryService.getCategoryById(categoryId)
                    .orElseThrow(() -> new EntityNotFoundException("Category not found with ID: " + categoryId));
            
            return ResponseEntity.ok(tagsService.getTagsByCategory(category));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Creates a new tag
     * @param tag Tag data from request body
     * @return The created tag
     */
    @PostMapping
    @Operation(summary = "Create a new tag", description = "Creates a new tag in the system")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Tag successfully created", 
                     content = @Content(mediaType = "application/json", schema = @Schema(implementation = Tags.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content)
    })
    public ResponseEntity<?> createTag(
            @Parameter(description = "Tag object to be created", required = true) @RequestBody Tags tag) {
        try {
            Tags createdTag = tagsService.saveTag(tag);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdTag);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Collections.singletonMap("error", e.getMessage()));
        }
    }

    /**
     * Updates an existing tag
     * @param id The tag ID to update
     * @param tagDetails Updated tag data
     * @return The updated tag or error
     */
    @PutMapping("/{id}")
    @Operation(summary = "Update a tag", description = "Updates an existing tag in the system")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Tag successfully updated", 
                     content = @Content(mediaType = "application/json", schema = @Schema(implementation = Tags.class))),
        @ApiResponse(responseCode = "404", description = "Tag not found", content = @Content),
        @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content)
    })
    public ResponseEntity<?> updateTag(
            @Parameter(description = "ID of the tag to update", required = true) @PathVariable Long id, 
            @Parameter(description = "Updated tag details", required = true) @RequestBody Tags tagDetails) {
        try {
            // Verify tag exists
            Tags existingTag = tagsService.getTagById(id)
                    .orElseThrow(() -> new EntityNotFoundException("Tag not found with ID: " + id));
            
            // Set the ID from the path
            tagDetails.setTagId(id);
            
            // Update and return
            Tags updatedTag = tagsService.saveTag(tagDetails);
            return ResponseEntity.ok(updatedTag);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Collections.singletonMap("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Collections.singletonMap("error", e.getMessage()));
        }
    }

    /**
     * Deactivates (soft-deletes) a tag
     * @param id The tag ID to deactivate
     * @return Success message or error
     */
    @DeleteMapping("/{id}/deactivate")
    @Operation(summary = "Deactivate a tag", description = "Soft-deletes a tag by setting its is_active attribute to false")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Tag successfully deactivated", 
                     content = @Content(mediaType = "application/json")),
        @ApiResponse(responseCode = "404", description = "Tag not found", content = @Content),
        @ApiResponse(responseCode = "400", description = "Bad request", content = @Content)
    })
    public ResponseEntity<?> deactivateTag(
            @Parameter(description = "ID of the tag to deactivate", required = true) @PathVariable Long id) {
        try {
            tagsService.deactivateTag(id);
            return ResponseEntity.ok(Collections.singletonMap("message", "Tag deactivated successfully"));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Collections.singletonMap("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Collections.singletonMap("error", e.getMessage()));
        }
    }

    /**
     * Restores a previously deactivated tag
     * @param id The tag ID to restore
     * @return Success message or error
     */
    @PutMapping("/restore/{id}")
    @Operation(summary = "Restore a tag", description = "Restores a previously deactivated tag by setting its is_active attribute to true")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Tag successfully restored", 
                     content = @Content(mediaType = "application/json")),
        @ApiResponse(responseCode = "404", description = "Tag not found", content = @Content),
        @ApiResponse(responseCode = "400", description = "Bad request", content = @Content)
    })
    public ResponseEntity<?> restoreTag(
            @Parameter(description = "ID of the tag to restore", required = true) @PathVariable Long id) {
        try {
            tagsService.restoreTag(id);
            return ResponseEntity.ok(Collections.singletonMap("message", "Tag restored successfully"));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Collections.singletonMap("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Collections.singletonMap("error", e.getMessage()));
        }
    }
    
    /**
     * Permanently deletes a tag from the database
     * @param id The tag ID to permanently delete
     * @return Success message or error
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Permanently delete a tag", description = "Permanently deletes a tag from the database")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Tag permanently deleted successfully", 
                     content = @Content(mediaType = "application/json")),
        @ApiResponse(responseCode = "404", description = "Tag not found", content = @Content),
        @ApiResponse(responseCode = "400", description = "Bad request", content = @Content)
    })
    public ResponseEntity<?> permanentlyDeleteTag(
            @Parameter(description = "ID of the tag to permanently delete", required = true) @PathVariable Long id) {
        try {
            // Verify tag exists before deletion
            if (!tagsService.getTagById(id).isPresent()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Collections.singletonMap("error", "Tag not found with ID: " + id));
            }
            
            tagsService.deleteTag(id);
            return ResponseEntity.ok(Collections.singletonMap("message", "Tag permanently deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Collections.singletonMap("error", e.getMessage()));
        }
    }
}
