package it342.g4.e_vents.service;

import it342.g4.e_vents.model.Act;
import it342.g4.e_vents.repository.ActRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Service for managing Act entities
 */
@Service
public class ActService {

    private final ActRepository actRepository;
    
    @Autowired
    public ActService(ActRepository actRepository) {
        this.actRepository = actRepository;
    }

    /**
     * Retrieves all acts from the database
     * @return List of all acts
     */
    public List<Act> getAllActs() {
        return actRepository.findAll();
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
        return actRepository.save(act);
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
        return actRepository.save(act);
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
}
