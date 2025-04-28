package it342.g4.e_vents.repository;

import it342.g4.e_vents.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    
    // Methods for active/inactive users
    List<User> findByIsActiveTrue();
    Optional<User> findByUserIdAndIsActiveTrue(Long userId);
    
    /**
     * Count active users
     * @return Number of active users
     */
    long countByIsActiveTrue();
}
