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
     * Retrieves all active tags
     * @return List of all active tags
     */
    public List<Tags> getAllActiveTags() {
        return tagsRepository.findByIsActiveTrue();
    }

    /**
     * Retrieves all tags, including inactive ones
     * @return List of all tags
     */
    public List<Tags> getAllTags() {
        return tagsRepository.findAll();
    }
    
    /**
     * Retrieves an active tag by ID
     * @param id The tag ID
     * @return Optional containing the tag if found
     */
    public Optional<Tags> getActiveTagById(Long id) {
        return tagsRepository.findByTagIdAndIsActiveTrue(id);
    }

    /**
     * Retrieves a tag by ID
     * @param id The tag ID
     * @return Optional containing the tag if found
     */
    public Optional<Tags> getTagById(Long id) {
        return tagsRepository.findById(id);
    }

    public List<Tags> getTagsByCategory(Category category) {
        return tagsRepository.findByCategory(category);
    }

    public List<Tags> searchTags(String query) {
        return tagsRepository.findByNameContainingIgnoreCase(query);
    }

    public Tags saveTag(Tags tag) {
        if (tag.getCategory() == null) {
            throw new IllegalArgumentException("Tag must have a category");
        }
        if (tagsRepository.existsByName(tag.getName())) {
            throw new IllegalArgumentException("Tag with this name already exists");
        }
        // Set active by default
        tag.setActive(true);
        return tagsRepository.save(tag);
    }
    
    /**
     * Updates an existing tag
     * @param tag Updated tag data
     * @return The updated tag
     * @throws EntityNotFoundException if tag not found
     */
    public Tags updateTag(Tags tag) {
        // Check if exists
        if (!tagsRepository.existsById(tag.getTagId())) {
            throw new EntityNotFoundException("Tag not found with ID: " + tag.getTagId());
        }
        
        return tagsRepository.save(tag);
    }
    
    /**
     * Deactivates (soft-deletes) a tag
     * @param id The tag ID to deactivate
     * @throws EntityNotFoundException if tag not found
     */
    public void deactivateTag(Long id) {
        Tags tag = tagsRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Tag not found with ID: " + id));
        
        tag.setActive(false);
        tagsRepository.save(tag);
    }
    
    /**
     * Restores a previously deactivated tag
     * @param id The tag ID to restore
     * @throws EntityNotFoundException if tag not found
     */
    public void restoreTag(Long id) {
        Tags tag = tagsRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Tag not found with ID: " + id));
        
        tag.setActive(true);
        tagsRepository.save(tag);
    }

    public boolean existsByName(String name) {
        return tagsRepository.existsByName(name);
    }

    public boolean validateTagsForCategory(List<Tags> tags, Category category) {
        if (tags == null || category == null) {
            return false;
        }
        return tags.stream().allMatch(tag -> tag.getCategory().getCategoryId().equals(category.getCategoryId()));
    }

    public void deleteTag(Long id) {
        tagsRepository.deleteById(id);
    }
}
