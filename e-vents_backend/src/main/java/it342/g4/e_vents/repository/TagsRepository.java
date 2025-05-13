package it342.g4.e_vents.repository;

import it342.g4.e_vents.model.Category;
import it342.g4.e_vents.model.Tags;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for Tags entity
 */
@Repository
public interface TagsRepository extends JpaRepository<Tags, Long> {
    /**
     * Count active tags in the system
     * @return Number of active tags
     */
    long countByIsActiveTrue();
    
    /**
     * Find all active tags
     * @return List of active tags
     */
    List<Tags> findByIsActiveTrue();
    
    /**
     * Find an active tag by ID
     * @param id Tag ID
     * @return Optional containing the tag if found
     */
    Optional<Tags> findByTagIdAndIsActiveTrue(Long id);
    
    /**
     * Find tags by category
     * @param category Category to filter by
     * @return List of tags in the category
     */
    List<Tags> findByCategory(Category category);
    
    /**
     * Search tags by name (case insensitive)
     * @param query Search query
     * @return List of matching tags
     */
    List<Tags> findByNameContainingIgnoreCase(String query);
    
    /**
     * Check if a tag with the given name exists
     * @param name Tag name
     * @return True if exists, false otherwise
     */
    boolean existsByName(String name);
    
    /**
     * Find a tag by its exact name
     * @param name Tag name
     * @return Optional containing the tag if found
     */
    Optional<Tags> findByName(String name);
}
