package it342.g4.e_vents.repository;

import it342.g4.e_vents.model.Rating;
import it342.g4.e_vents.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RatingRepository extends JpaRepository<Rating, Long> {
    List<Rating> findByRatedEntityIdAndRatedEntityTypeAndIsActiveTrue(Long entityId, Rating.EntityType entityType);
    List<Rating> findByUserAndIsActiveTrue(User user);
    List<Rating> findByRatedEntityIdAndRatedEntityType(Long entityId, Rating.EntityType entityType);
    
    /**
     * Count active ratings in the system
     * @return Number of active ratings
     */
    long countByIsActiveTrue();
}
