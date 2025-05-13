package it342.g4.e_vents.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import it342.g4.e_vents.model.Category;
import it342.g4.e_vents.service.CategoryService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Controller for category-related operations
 */
@RestController
@RequestMapping("/api/categories")
@CrossOrigin(origins = "*")
@Tag(name = "Category", description = "Category management APIs")
public class CategoryController {

    private final CategoryService categoryService;
    
    @Autowired
    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }
    
    /**
     * Retrieves all active categories
     * @return List of all active categories
     */
    @GetMapping
    @Operation(summary = "Get all active categories", description = "Retrieves a list of all active categories in the system")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved list of active categories", 
                     content = @Content(mediaType = "application/json", schema = @Schema(implementation = Category.class)))
    })
    public ResponseEntity<List<Category>> getAllActiveCategories() {
        return ResponseEntity.ok(categoryService.getAllActiveCategories());
    }
    
    /**
     * Retrieves all categories, including inactive ones
     * @return List of all categories
     */
    @GetMapping("/all")
    @Operation(summary = "Get all categories including inactive", description = "Retrieves a list of all categories in the system, including inactive ones")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved list of all categories", 
                     content = @Content(mediaType = "application/json", schema = @Schema(implementation = Category.class)))
    })
    public ResponseEntity<List<Category>> getAllCategories() {
        return ResponseEntity.ok(categoryService.getAllCategories());
    }
    
    /**
     * Retrieves an active category by ID
     * @param id The category ID
     * @return The category or 404 if not found
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get active category by ID", description = "Retrieves a specific active category by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved the category", 
                     content = @Content(mediaType = "application/json", schema = @Schema(implementation = Category.class))),
        @ApiResponse(responseCode = "404", description = "Category not found", content = @Content)
    })
    public ResponseEntity<Category> getActiveCategoryById(
            @Parameter(description = "ID of the category to retrieve") @PathVariable Long id) {
        return categoryService.getActiveCategoryById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * Retrieves a category by name
     * @param name The category name
     * @return The category or 404 if not found
     */
    @GetMapping("/name/{name}")
    @Operation(summary = "Get category by name", description = "Retrieves a specific category by its name")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved the category", 
                     content = @Content(mediaType = "application/json", schema = @Schema(implementation = Category.class))),
        @ApiResponse(responseCode = "404", description = "Category not found", content = @Content)
    })
    public ResponseEntity<Category> getCategoryByName(
            @Parameter(description = "Name of the category to retrieve") @PathVariable String name) {
        return categoryService.getCategoryByName(name)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * Creates a new category
     * @param category Category data from request body
     * @return The created category or error
     */
    @PostMapping("/create")
    @Operation(summary = "Create a new category", description = "Creates a new category in the system")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Category successfully created", 
                     content = @Content(mediaType = "application/json", schema = @Schema(implementation = Category.class))),
        @ApiResponse(responseCode = "409", description = "Category with this name already exists", content = @Content),
        @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content)
    })
    public ResponseEntity<?> createCategory(
            @Parameter(description = "Category object to be created", required = true) @RequestBody Category category) {
        if (categoryService.existsByName(category.getName())) {
            return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(Collections.singletonMap("message", "Category with this name already exists"));
        }
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(categoryService.saveCategory(category));
    }
    
    /**
     * Updates an existing category
     * @param id The category ID to update
     * @param categoryDetails Updated category data
     * @return The updated category or error
     */
    @PutMapping("/{id}/edit")
    @Operation(summary = "Update a category", description = "Updates an existing category by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Category successfully updated", 
                     content = @Content(mediaType = "application/json", schema = @Schema(implementation = Category.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content),
        @ApiResponse(responseCode = "404", description = "Category not found", content = @Content)
    })
    public ResponseEntity<?> updateCategory(
            @Parameter(description = "ID of the category to update", required = true) @PathVariable Long id, 
            @Parameter(description = "Updated category details", required = true) @RequestBody Category categoryDetails) {
        try {
            // Verify category exists
            categoryService.getCategoryById(id)
                    .orElseThrow(() -> new EntityNotFoundException("Category not found with ID: " + id));
            
            // Set the ID from the path
            categoryDetails.setCategoryId(id);
            
            // Update and return
            Category updatedCategory = categoryService.saveCategory(categoryDetails);
            return ResponseEntity.ok(updatedCategory);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Collections.singletonMap("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Collections.singletonMap("error", e.getMessage()));
        }
    }
    
    /**
     * Deactivates (soft-deletes) a category
     * @param id The category ID to deactivate
     * @return Success message or error
     */
    @DeleteMapping("/{id}/deactivate")
    @Operation(summary = "Deactivate a category", description = "Deactivates a category by setting it as inactive (soft delete)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Category successfully deactivated", 
                     content = @Content(mediaType = "application/json")),
        @ApiResponse(responseCode = "404", description = "Category not found", content = @Content),
        @ApiResponse(responseCode = "400", description = "Bad request", content = @Content)
    })
    public ResponseEntity<?> deactivateCategory(
            @Parameter(description = "ID of the category to deactivate", required = true) @PathVariable Long id) {
        try {
            // Verify category exists
            categoryService.getCategoryById(id)
                    .orElseThrow(() -> new EntityNotFoundException("Category not found with ID: " + id));
            
            categoryService.deactivateCategory(id);
            return ResponseEntity.ok(Collections.singletonMap("message", "Category deactivated successfully"));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Collections.singletonMap("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Collections.singletonMap("error", e.getMessage()));
        }
    }
    
    /**
     * Restores a previously deactivated category
     * @param id The category ID to restore
     * @return Success message or error
     */
    @PutMapping("/restore/{id}")
    @Operation(summary = "Restore a category", description = "Activates a previously deactivated category")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Category successfully restored", 
                     content = @Content(mediaType = "application/json")),
        @ApiResponse(responseCode = "404", description = "Category not found", content = @Content),
        @ApiResponse(responseCode = "400", description = "Bad request", content = @Content)
    })
    public ResponseEntity<?> restoreCategory(
            @Parameter(description = "ID of the category to restore", required = true) @PathVariable Long id) {
        try {
            categoryService.restoreCategory(id);
            return ResponseEntity.ok(Collections.singletonMap("message", "Category restored successfully"));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Collections.singletonMap("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Collections.singletonMap("error", e.getMessage()));
        }
    }
    
    /**
     * Permanently deletes a category
     * @param id The category ID to permanently delete
     * @return Success message or error
     */
    @DeleteMapping("/{id}/delete")
    @Operation(summary = "Delete a category permanently", description = "Permanently deletes a category from the system")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Category permanently deleted", 
                     content = @Content(mediaType = "application/json")),
        @ApiResponse(responseCode = "404", description = "Category not found", content = @Content),
        @ApiResponse(responseCode = "400", description = "Bad request", content = @Content)
    })
    public ResponseEntity<?> deleteCategoryPermanently(
            @Parameter(description = "ID of the category to permanently delete", required = true) @PathVariable Long id) {
        try {
            // Verify category exists
            categoryService.getCategoryById(id)
                    .orElseThrow(() -> new EntityNotFoundException("Category not found with ID: " + id));
            
            categoryService.deleteCategory(id);
            return ResponseEntity.ok(Collections.singletonMap("message", "Category permanently deleted"));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Collections.singletonMap("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Collections.singletonMap("error", e.getMessage()));
        }
    }
    
    /**
     * Get the count of active categories
     * @return ResponseEntity with the count of active categories
     */
    @Operation(summary = "Get count of active categories", description = "Returns the total number of active categories in the system")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved count", 
                    content = @Content(mediaType = "application/json", 
                    schema = @Schema(implementation = Long.class))),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/count")
    public ResponseEntity<Map<String, Long>> countActiveCategories() {
        try {
            long count = categoryService.countActiveCategories();
            Map<String, Long> response = Collections.singletonMap("count", count);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
