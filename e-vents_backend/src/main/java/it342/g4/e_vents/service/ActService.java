package it342.g4.e_vents.service;

import it342.g4.e_vents.model.Act;
import it342.g4.e_vents.model.Tags;
import it342.g4.e_vents.repository.ActRepository;
import it342.g4.e_vents.repository.TagsRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Service for managing Act entities
 */
@Service
public class ActService {

    private final ActRepository actRepository;
    private final TagsRepository tagsRepository;
    
    @Autowired
    public ActService(ActRepository actRepository, TagsRepository tagsRepository) {
        this.actRepository = actRepository;
        this.tagsRepository = tagsRepository;
    }

    /**
     * Retrieves all active acts from the database
     * @return List of all active acts
     */
    public List<Act> getAllActs() {
        return actRepository.findByIsActiveTrue();
    }
    
    /**
     * Retrieves all acts including inactive ones
     * @return List of all acts
     */
    public List<Act> getAllActsIncludingInactive() {
        return actRepository.findAll();
    }
    
    /**
     * Finds an active act by its ID
     * @param id The act ID to look up
     * @return Optional containing the act if found and active
     */
    public Optional<Act> findActById(Long id) {
        return actRepository.findByActIdAndIsActiveTrue(id);
    }
    
    /**
     * Finds any act by its ID (active or inactive)
     * @param id The act ID to look up
     * @return Optional containing the act if found
     */
    public Optional<Act> findActByIdIncludingInactive(Long id) {
        return actRepository.findById(id);
    }
    
    /**
     * Gets an active act by ID or throws an exception if not found
     * @param id The act ID to look up
     * @return The act
     * @throws EntityNotFoundException if the act is not found or inactive
     */
    public Act getActById(Long id) {
        return actRepository.findByActIdAndIsActiveTrue(id)
                .orElseThrow(() -> new EntityNotFoundException("Act not found with ID: " + id));
    }
    
    /**
     * Gets any act by ID (active or inactive) or throws an exception if not found
     * @param id The act ID to look up
     * @return The act
     * @throws EntityNotFoundException if the act is not found
     */
    public Act getActByIdIncludingInactive(Long id) {
        return actRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Act not found with ID: " + id));
    }
    
    /**
     * Creates a new act
     * @param act The act to create
     * @return The created act with ID
     */
    public Act createAct(Act act) {
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
     * @throws EntityNotFoundException if the act is not found or inactive
     */
    public Act updateAct(Act act) {
        // Check if act exists and is active
        Act existingAct = findActById(act.getActId())
                .orElseThrow(() -> new EntityNotFoundException("Act not found with ID: " + act.getActId()));
        
        // Process tags if they exist
        processTags(act);
        
        return actRepository.save(act);
    }
    
    /**
     * Soft deletes an act by setting isActive to false
     * @param id The ID of the act to soft delete
     * @throws EntityNotFoundException if the act is not found
     */
    public void softDeleteAct(Long id) {
        Act act = actRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Act not found with ID: " + id));
        
        act.setActive(false);
        actRepository.save(act);
    }
    
    /**
     * Restores a soft-deleted act by setting isActive to true
     * @param id The ID of the act to restore
     * @return The restored act
     * @throws EntityNotFoundException if the act is not found
     */
    public Act restoreAct(Long id) {
        Act act = actRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Act not found with ID: " + id));
        
        act.setActive(true);
        return actRepository.save(act);
    }
    
    /**
     * Permanently deletes an act by ID
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
     * Counts all acts in the database (active and inactive)
     * @return The total number of acts
     */
    public long countAllActs() {
        return actRepository.count();
    }
    
    /**
     * Counts all active acts in the database
     * @return The number of active acts
     */
    public long countActiveActs() {
        return actRepository.countByIsActiveTrue();
    }
}