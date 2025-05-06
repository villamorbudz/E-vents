package it342.g4.e_vents.repository;

import it342.g4.e_vents.model.Category;
import it342.g4.e_vents.model.Tags;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TagsRepository extends JpaRepository<Tags, Long> {
    /**
     * Find all active tags
     * @return List of active tags
     */
    List<Tags> findByIsActiveTrue();
    
    /**
     * Find an active tag by ID
     * @param id The tag ID
     * @return Optional containing the tag if found
     */
    Optional<Tags> findByTagIdAndIsActiveTrue(Long id);
    
    /**
     * Find tags by category
     * @param category The category to filter by
     * @return List of tags for the category
     */
    List<Tags> findByCategory(Category category);
    
    /**
     * Search tags by name (case-insensitive)
     * @param name The name to search for
     * @return List of matching tags
     */
    List<Tags> findByNameContainingIgnoreCase(String name);
    
    /**
     * Find a tag by its exact name
     * @param name The exact name of the tag
     * @return Optional containing the tag if found
     */
    Optional<Tags> findByName(String name);
    
    /**
     * Check if a tag with the given name exists
     * @param name The name to check
     * @return True if exists, false otherwise
     */
    boolean existsByName(String name);
}
