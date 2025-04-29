package it342.g4.e_vents.service;

import it342.g4.e_vents.model.Category;
import it342.g4.e_vents.repository.CategoryRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    @Autowired
    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    /**
     * Retrieves all active categories
     * @return List of all active categories
     */
    public List<Category> getAllCategories() {
        return categoryRepository.findByIsActiveTrue();
    }

    /**
     * Retrieves all categories including inactive ones
     * @return List of all categories
     */
    public List<Category> getAllCategoriesIncludingInactive() {
        return categoryRepository.findAll();
    }

    /**
     * Retrieves an active category by ID
     * @param id The category ID
     * @return Optional containing the category if found and active
     */
    public Optional<Category> getCategoryById(Long id) {
        return categoryRepository.findByIdAndIsActiveTrue(id);
    }

    /**
     * Retrieves any category by ID (active or inactive)
     * @param id The category ID
     * @return Optional containing the category if found
     */
    public Optional<Category> getCategoryByIdIncludingInactive(Long id) {
        return categoryRepository.findById(id);
    }

    /**
     * Retrieves a category by name
     * @param name The category name
     * @return The category if found
     */
    public Category getCategoryByName(String name) {
        return categoryRepository.findByName(name);
    }

    /**
     * Saves a category
     * @param category The category to save
     * @return The saved category
     */
    public Category saveCategory(Category category) {
        return categoryRepository.save(category);
    }

    /**
     * Soft deletes a category by setting isActive to false
     * @param id The ID of the category to soft delete
     * @throws EntityNotFoundException if the category is not found
     */
    public void softDeleteCategory(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Category not found with ID: " + id));
        
        category.setActive(false);
        categoryRepository.save(category);
    }

    /**
     * Restores a soft-deleted category by setting isActive to true
     * @param id The ID of the category to restore
     * @return The restored category
     * @throws EntityNotFoundException if the category is not found
     */
    public Category restoreCategory(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Category not found with ID: " + id));
        
        category.setActive(true);
        return categoryRepository.save(category);
    }

    /**
     * Permanently deletes a category
     * @param id The ID of the category to delete
     * @throws EntityNotFoundException if the category is not found
     */
    public void deleteCategory(Long id) {
        if (!categoryRepository.existsById(id)) {
            throw new EntityNotFoundException("Category not found with ID: " + id);
        }
        categoryRepository.deleteById(id);
    }
    
    /**
     * Counts all categories in the database (active and inactive)
     * @return The total number of categories
     */
    public long countAllCategories() {
        return categoryRepository.count();
    }
    
    /**
     * Counts all active categories in the database
     * @return The number of active categories
     */
    public long countActiveCategories() {
        return categoryRepository.countByIsActiveTrue();
    }
}
