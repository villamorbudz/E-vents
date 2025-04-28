package it342.g4.e_vents.repository;

import it342.g4.e_vents.model.Act;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ActRepository extends JpaRepository<Act, Long> {
    List<Act> findByIsActiveTrue();
    
    Optional<Act> findByActIdAndIsActiveTrue(Long id);
    
    /**
     * Count active acts
     * @return Number of active acts
     */
    long countByIsActiveTrue();
}
