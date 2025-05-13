package it342.g4.e_vents.service;

import it342.g4.e_vents.model.Category;
import it342.g4.e_vents.repository.CategoryRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    @Autowired
    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    /**
     * Retrieves all categories from the database
     * @return List of all categories
     */
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    /**
     * Retrieves all active categories from the database
     * @return List of all active categories
     */
    public List<Category> getAllActiveCategories() {
        return categoryRepository.findAll().stream()
                .filter(category -> category.isActive())
                .collect(Collectors.toList());
    }

    /**
     * Finds a category by its ID
     * @param id The category ID to look up
     * @return Optional containing the category if found
     */
    public Optional<Category> getCategoryById(Long id) {
        return categoryRepository.findById(id);
    }

    /**
     * Finds an active category by its ID
     * @param id The category ID to look up
     * @return Optional containing the category if found and active
     */
    public Optional<Category> getActiveCategoryById(Long id) {
        Optional<Category> category = categoryRepository.findById(id);
        return category.isPresent() && category.get().isActive() ? category : Optional.empty();
    }

    /**
     * Finds a category by its name
     * @param name The category name to look up
     * @return Optional containing the category if found
     */
    public Optional<Category> getCategoryByName(String name) {
        return categoryRepository.findByName(name);
    }

    /**
     * Creates a new category
     * @param category The category to create
     * @return The created category with ID
     */
    public Category createCategory(Category category) {
        // Set category as active by default
        category.setActive(true);
        return saveCategory(category);
    }

    /**
     * Saves a category (either creates or updates)
     * @param category The category to save
     * @return The saved category
     */
    public Category saveCategory(Category category) {
        // If this is an update (ID exists), preserve active status
        if (category.getCategoryId() != null) {
            categoryRepository.findById(category.getCategoryId()).ifPresent(existingCategory -> {
                category.setActive(existingCategory.isActive());
            });
        } else {
            // New category, set active by default
            category.setActive(true);
        }
        
        return categoryRepository.save(category);
    }

    /**
     * Deactivates (soft-deletes) a category
     * @param id The ID of the category to deactivate
     * @throws EntityNotFoundException if the category is not found
     */
    public void deactivateCategory(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Category not found with ID: " + id));
        
        category.setActive(false);
        categoryRepository.save(category);
    }

    /**
     * Restores a previously deactivated category
     * @param id The ID of the category to restore
     * @throws EntityNotFoundException if the category is not found
     */
    public void restoreCategory(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Category not found with ID: " + id));
        
        category.setActive(true);
        categoryRepository.save(category);
    }

    /**
     * Permanently deletes a category by ID
     * @param id The ID of the category to delete
     */
    public void deleteCategory(Long id) {
        if (!categoryRepository.existsById(id)) {
            throw new EntityNotFoundException("Category not found with ID: " + id);
        }
        categoryRepository.deleteById(id);
    }

    /**
     * Checks if a category with the given name exists
     * @param name The name to check
     * @return true if a category with the name exists, false otherwise
     */
    public boolean existsByName(String name) {
        return categoryRepository.existsByName(name);
    }

    /**
     * Counts the number of active categories in the system
     * @return The count of active categories
     */
    public long countActiveCategories() {
        return categoryRepository.countByIsActiveTrue();
    }
}
