package it342.g4.e_vents.repository;

import it342.g4.e_vents.model.Category;
import it342.g4.e_vents.model.Tags;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TagsRepository extends JpaRepository<Tags, Long> {
    List<Tags> findByCategory(Category category);
    List<Tags> findByNameContainingIgnoreCase(String name);
    
    /**
     * Find active tags
     * @return List of active tags
     */
    List<Tags> findByIsActiveTrue();
    
    /**
     * Find an active tag by ID
     * @param tagId The tag ID
     * @return Optional containing the tag if found and active
     */
    Optional<Tags> findByTagIdAndIsActiveTrue(Long tagId);
    
    /**
     * Find active tags by category
     * @param category The category
     * @return List of active tags for the category
     */
    List<Tags> findByCategoryAndIsActiveTrue(Category category);
    
    /**
     * Find active tags by name (case insensitive)
     * @param name The tag name
     * @return List of active tags with the specified name
     */
    List<Tags> findByNameContainingIgnoreCaseAndIsActiveTrue(String name);
    
    /**
     * Count active tags
     * @return Number of active tags
     */
    long countByIsActiveTrue();
}
