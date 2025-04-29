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

import java.util.Collections;
import java.util.List;

/**
 * Controller for tag-related operations
 */
@RestController
@RequestMapping("/api/tags")
@CrossOrigin(origins = "*")
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
    public ResponseEntity<List<Tags>> getAllTags() {
        return ResponseEntity.ok(tagsService.getAllActiveTags());
    }
    
    /**
     * Retrieves all tags including inactive ones
     * @return List of all tags
     */
    @GetMapping("/all")
    public ResponseEntity<List<Tags>> getAllTagsIncludingInactive() {
        return ResponseEntity.ok(tagsService.getAllTags());
    }
    
    /**
     * Retrieves an active tag by ID
     * @param id The tag ID
     * @return The tag or 404 if not found
     */
    @GetMapping("/{id}")
    public ResponseEntity<Tags> getTagById(@PathVariable Long id) {
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
    public ResponseEntity<?> getTagsByCategory(@PathVariable Long categoryId) {
        try {
            Category category = categoryService.getCategoryById(categoryId)
                    .orElseThrow(() -> new EntityNotFoundException("Category not found with ID: " + categoryId));
            
            return ResponseEntity.ok(tagsService.getActiveTagsByCategory(category));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Collections.singletonMap("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Collections.singletonMap("error", e.getMessage()));
        }
    }
    
    /**
     * Searches for tags by name
     * @param name The search query
     * @return List of tags matching the query
     */
    @GetMapping("/search")
    public ResponseEntity<List<Tags>> searchTags(@RequestParam String name) {
        return ResponseEntity.ok(tagsService.searchActiveTags(name));
    }
    
    /**
     * Creates a new tag
     * @param tag The tag to create
     * @return The created tag
     */
    @PostMapping("/create")
    public ResponseEntity<?> createTag(@RequestBody Tags tag) {
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
     * @param id The tag ID
     * @param tagDetails The updated tag details
     * @return The updated tag
     */
    @PutMapping("/{id}/edit")
    public ResponseEntity<?> updateTag(@PathVariable Long id, @RequestBody Tags tagDetails) {
        try {
            // Set the ID from the path
            tagDetails.setTagId(id);
            
            // Update and return
            Tags updatedTag = tagsService.updateTag(tagDetails);
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
     * Soft deletes a tag
     * @param id The tag ID to delete
     * @return Success message or error
     */
    @DeleteMapping("/{id}/deactivate")
    public ResponseEntity<?> deactivateTag(@PathVariable Long id) {
        try {
            tagsService.softDeleteTag(id);
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
     * Permanently deletes a tag
     * @param id The tag ID to permanently delete
     * @return Success message or error
     */
    @DeleteMapping("/{id}/delete")
    public ResponseEntity<?> permanentlyDeleteTag(@PathVariable Long id) {
        try {
            tagsService.deleteTag(id);
            return ResponseEntity.ok(Collections.singletonMap("message", "Tag permanently deleted successfully"));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Collections.singletonMap("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Collections.singletonMap("error", e.getMessage()));
        }
    }
    
    /**
     * Restores a soft-deleted tag
     * @param id The tag ID to restore
     * @return The restored tag or error
     */
    @PostMapping("/{id}/restore")
    public ResponseEntity<?> restoreTag(@PathVariable Long id) {
        try {
            Tags restoredTag = tagsService.restoreTag(id);
            return ResponseEntity.ok(restoredTag);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Collections.singletonMap("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Collections.singletonMap("error", e.getMessage()));
        }
    }
}
