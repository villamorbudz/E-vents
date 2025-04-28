package it342.g4.e_vents.service;

import it342.g4.e_vents.model.Category;
import it342.g4.e_vents.model.Tags;
import it342.g4.e_vents.repository.TagsRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TagsService {

    private final TagsRepository tagsRepository;

    @Autowired
    public TagsService(TagsRepository tagsRepository) {
        this.tagsRepository = tagsRepository;
    }

    /**
     * Retrieves all tags
     * @return List of all tags
     */
    public List<Tags> getAllTags() {
        return tagsRepository.findAll();
    }
    
    /**
     * Retrieves all active tags
     * @return List of all active tags
     */
    public List<Tags> getAllActiveTags() {
        return tagsRepository.findByIsActiveTrue();
    }

    /**
     * Finds a tag by ID
     * @param id The tag ID
     * @return Optional containing the tag if found
     */
    public Optional<Tags> getTagById(Long id) {
        return tagsRepository.findById(id);
    }
    
    /**
     * Finds an active tag by ID
     * @param id The tag ID
     * @return Optional containing the tag if found and active
     */
    public Optional<Tags> getActiveTagById(Long id) {
        return tagsRepository.findByTagIdAndIsActiveTrue(id);
    }
    
    /**
     * Gets an active tag by ID or throws an exception if not found
     * @param id The tag ID
     * @return The tag
     * @throws EntityNotFoundException if the tag is not found or inactive
     */
    public Tags getActiveTagByIdOrThrow(Long id) {
        return tagsRepository.findByTagIdAndIsActiveTrue(id)
                .orElseThrow(() -> new EntityNotFoundException("Tag not found with ID: " + id));
    }
    
    /**
     * Gets any tag by ID (active or inactive) or throws an exception if not found
     * @param id The tag ID
     * @return The tag
     * @throws EntityNotFoundException if the tag is not found
     */
    public Tags getTagByIdOrThrow(Long id) {
        return tagsRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Tag not found with ID: " + id));
    }

    /**
     * Retrieves tags by category
     * @param category The category
     * @return List of tags for the category
     */
    public List<Tags> getTagsByCategory(Category category) {
        return tagsRepository.findByCategory(category);
    }
    
    /**
     * Retrieves active tags by category
     * @param category The category
     * @return List of active tags for the category
     */
    public List<Tags> getActiveTagsByCategory(Category category) {
        return tagsRepository.findByCategoryAndIsActiveTrue(category);
    }

    /**
     * Searches for tags by name
     * @param query The search query
     * @return List of tags matching the query
     */
    public List<Tags> searchTags(String query) {
        return tagsRepository.findByNameContainingIgnoreCase(query);
    }
    
    /**
     * Searches for active tags by name
     * @param query The search query
     * @return List of active tags matching the query
     */
    public List<Tags> searchActiveTags(String query) {
        return tagsRepository.findByNameContainingIgnoreCaseAndIsActiveTrue(query);
    }

    /**
     * Creates or updates a tag
     * @param tag The tag to save
     * @return The saved tag
     */
    public Tags saveTag(Tags tag) {
        // Ensure tag is active when saving
        tag.setActive(true);
        return tagsRepository.save(tag);
    }
    
    /**
     * Updates an existing tag
     * @param tag The tag with updated fields
     * @return The updated tag
     * @throws EntityNotFoundException if the tag is not found or inactive
     */
    public Tags updateTag(Tags tag) {
        // Check if tag exists and is active
        getActiveTagByIdOrThrow(tag.getTagId());
        return tagsRepository.save(tag);
    }

    /**
     * Permanently deletes a tag
     * @param id The tag ID to delete
     * @throws EntityNotFoundException if the tag is not found
     */
    public void deleteTag(Long id) {
        if (!tagsRepository.existsById(id)) {
            throw new EntityNotFoundException("Tag not found with ID: " + id);
        }
        tagsRepository.deleteById(id);
    }
    
    /**
     * Soft deletes a tag by setting isActive to false
     * @param id The tag ID to soft delete
     * @throws EntityNotFoundException if the tag is not found
     */
    public void softDeleteTag(Long id) {
        Tags tag = getTagByIdOrThrow(id);
        tag.setActive(false);
        tagsRepository.save(tag);
    }
    
    /**
     * Restores a soft-deleted tag by setting isActive to true
     * @param id The tag ID to restore
     * @return The restored tag
     * @throws EntityNotFoundException if the tag is not found
     */
    public Tags restoreTag(Long id) {
        Tags tag = getTagByIdOrThrow(id);
        tag.setActive(true);
        return tagsRepository.save(tag);
    }
    
    /**
     * Counts all tags in the database (active and inactive)
     * @return The total number of tags
     */
    public long countAllTags() {
        return tagsRepository.count();
    }
    
    /**
     * Counts all active tags in the database
     * @return The number of active tags
     */
    public long countActiveTags() {
        return tagsRepository.countByIsActiveTrue();
    }
}
