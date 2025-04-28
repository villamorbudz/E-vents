package it342.g4.e_vents.controller;

import it342.g4.e_vents.model.Category;
import it342.g4.e_vents.service.CategoryService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

/**
 * Controller for category-related operations
 */
@RestController
@RequestMapping("/api/categories")
@CrossOrigin(origins = "*")
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
    public ResponseEntity<List<Category>> getAllCategories() {
        return ResponseEntity.ok(categoryService.getAllCategories());
    }
    
    /**
     * Retrieves all categories including inactive ones
     * @return List of all categories
     */
    @GetMapping("/all")
    public ResponseEntity<List<Category>> getAllCategoriesIncludingInactive() {
        return ResponseEntity.ok(categoryService.getAllCategoriesIncludingInactive());
    }
    
    /**
     * Retrieves an active category by ID
     * @param id The category ID
     * @return The category or 404 if not found
     */
    @GetMapping("/{id}")
    public ResponseEntity<Category> getCategoryById(@PathVariable Long id) {
        return categoryService.getCategoryById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * Creates a new category
     * @param category Category data from request body
     * @return The created category
     */
    @PostMapping("/create")
    public ResponseEntity<Category> createCategory(@RequestBody Category category) {
        try {
            Category createdCategory = categoryService.saveCategory(category);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdCategory);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Updates an existing category
     * @param id The category ID to update
     * @param categoryDetails Updated category data
     * @return The updated category or error
     */
    @PutMapping("/{id}/edit")
    public ResponseEntity<?> updateCategory(@PathVariable Long id, @RequestBody Category categoryDetails) {
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
     * Soft deletes a category by setting isActive to false
     * @param id The category ID to soft delete
     * @return Success message or error
     */
    @DeleteMapping("/{id}/deactivate")
    public ResponseEntity<?> softDeleteCategory(@PathVariable Long id) {
        try {
            categoryService.softDeleteCategory(id);
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
     * Restores a soft-deleted category by setting isActive to true
     * @param id The category ID to restore
     * @return The restored category or error
     */
    @PostMapping("/{id}/restore")
    public ResponseEntity<?> restoreCategory(@PathVariable Long id) {
        try {
            Category restoredCategory = categoryService.restoreCategory(id);
            return ResponseEntity.ok(restoredCategory);
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
    public ResponseEntity<?> permanentlyDeleteCategory(@PathVariable Long id) {
        try {
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
}
