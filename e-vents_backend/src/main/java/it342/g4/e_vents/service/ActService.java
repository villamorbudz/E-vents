package it342.g4.e_vents.service;

import it342.g4.e_vents.model.Act;
import it342.g4.e_vents.model.Tags;
import it342.g4.e_vents.repository.ActRepository;
import it342.g4.e_vents.repository.TagsRepository;
import it342.g4.e_vents.service.TagsService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service for managing Act entities
 */
@Service
public class ActService {

    private final ActRepository actRepository;
    private final TagsRepository tagsRepository;
    private final TagsService tagsService;
    
    @Autowired
    public ActService(ActRepository actRepository, TagsRepository tagsRepository, TagsService tagsService) {
        this.actRepository = actRepository;
        this.tagsRepository = tagsRepository;
        this.tagsService = tagsService;
    }

    /**
     * Retrieves all acts from the database
     * @return List of all acts
     */
    public List<Act> getAllActs() {
        return actRepository.findAll();
    }
    
    /**
     * Retrieves all active acts from the database
     * @return List of all active acts
     */
    public List<Act> getAllActiveActs() {
        return actRepository.findAll().stream()
                .filter(act -> act.isActive())
                .collect(Collectors.toList());
    }
    
    /**
     * Finds an act by its ID
     * @param id The act ID to look up
     * @return Optional containing the act if found
     */
    public Optional<Act> findActById(Long id) {
        return actRepository.findById(id);
    }
    
    /**
     * Finds an active act by its ID
     * @param id The act ID to look up
     * @return Optional containing the act if found and active
     */
    public Optional<Act> findActiveActById(Long id) {
        Optional<Act> act = actRepository.findById(id);
        return act.isPresent() && act.get().isActive() ? act : Optional.empty();
    }
    
    /**
     * Gets an act by ID or throws an exception if not found
     * @param id The act ID to look up
     * @return The act
     * @throws EntityNotFoundException if the act is not found
     */
    public Act getActById(Long id) {
        return actRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Act not found with ID: " + id));
    }
    
    /**
     * Creates a new act
     * @param act The act to create
     * @return The created act with ID
     */
    public Act createAct(Act act) {
        // Set act as active by default
        act.setActive(true);
        return saveAct(act);
    }
    
    /**
     * Saves an act (either creates or updates)
     * @param act The act to save
     * @return The saved act
     */
    public Act saveAct(Act act) {
        if (act.getCategory() == null) {
            throw new IllegalArgumentException("Act must have a category");
        }
        
        if (act.getTags() != null && !act.getTags().isEmpty()) {
            // Validate that all tags belong to the act's category
            if (!tagsService.validateTagsForCategory(act.getTags(), act.getCategory())) {
                throw new IllegalArgumentException("All tags must belong to the act's category");
            }
        }
        
        // Process tags if they exist
        processTags(act);
        
        return actRepository.save(act);
    }
    
    /**
     * Process tags for an act - resolves tag IDs to actual Tag entities
     * @param act The act with tags to process
     */
    private void processTags(Act act) {
        if (act.getTags() != null && !act.getTags().isEmpty()) {
            List<Tags> resolvedTags = new ArrayList<>();
            
            // Get tag IDs and fetch actual tag entities
            for (Tags tag : act.getTags()) {
                if (tag.getTagId() != null) {
                    tagsRepository.findById(tag.getTagId())
                        .ifPresent(resolvedTags::add);
                }
            }
            
            act.setTags(resolvedTags);
        }
    }
    
    /**
     * Updates an existing act
     * @param act The act with updated fields
     * @return The updated act
     */
    public Act updateAct(Act act) {
        // Check if act exists
        if (!actRepository.existsById(act.getActId())) {
            throw new EntityNotFoundException("Act not found with ID: " + act.getActId());
        }
        
        // Preserve the active status from the existing record
        Act existingAct = getActById(act.getActId());
        act.setActive(existingAct.isActive());
        
        return saveAct(act);
    }
    
    /**
     * Deactivates (soft-deletes) an act
     * @param id The ID of the act to deactivate
     * @throws EntityNotFoundException if the act is not found
     */
    public void deactivateAct(Long id) {
        Act act = getActById(id);
        act.setActive(false);
        actRepository.save(act);
    }
    
    /**
     * Restores a previously deactivated act
     * @param id The ID of the act to restore
     * @throws EntityNotFoundException if the act is not found
     */
    public void restoreAct(Long id) {
        Act act = getActById(id);
        act.setActive(true);
        actRepository.save(act);
    }
    
    /**
     * Deletes an act by ID
     * @param id The ID of the act to delete
     * @throws EntityNotFoundException if the act is not found
     */
    public void deleteAct(Long id) {
        if (!actRepository.existsById(id)) {
            throw new EntityNotFoundException("Act not found with ID: " + id);
        }
        actRepository.deleteById(id);
    }
    
    /**
     * Counts all acts in the system
     * @return The total number of acts
     */
    public long countAllActs() {
        return actRepository.count();
    }
    
    /**
     * Counts the number of active acts in the system
     * @return The count of active acts
     */
    public long countActiveActs() {
        return actRepository.countByIsActiveTrue();
    }
}