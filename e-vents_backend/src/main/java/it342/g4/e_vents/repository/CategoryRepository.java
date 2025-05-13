package it342.g4.e_vents.repository;

import it342.g4.e_vents.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    boolean existsByName(String name);
    Optional<Category> findByName(String name);
    
    /**
     * Count active categories in the system
     * @return Number of active categories
     */
    long countByIsActiveTrue();
}
