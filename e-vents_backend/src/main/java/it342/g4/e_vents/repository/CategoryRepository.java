package it342.g4.e_vents.repository;

import it342.g4.e_vents.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    Category findByName(String name);
    
    List<Category> findByIsActiveTrue();
    
    Optional<Category> findByIdAndIsActiveTrue(Long id);
    
    /**
     * Count active categories
     * @return Number of active categories
     */
    long countByIsActiveTrue();
}
