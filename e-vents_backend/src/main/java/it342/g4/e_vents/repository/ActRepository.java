package it342.g4.e_vents.repository;

import it342.g4.e_vents.model.Act;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ActRepository extends JpaRepository<Act, Long> {
    /**
     * Find an act by its exact name
     * @param name The exact name of the act
     * @return Optional containing the act if found
     */
    Optional<Act> findByName(String name);
    
    /**
     * Check if an act with the given name exists
     * @param name The name to check
     * @return True if exists, false otherwise
     */
    boolean existsByName(String name);
}
