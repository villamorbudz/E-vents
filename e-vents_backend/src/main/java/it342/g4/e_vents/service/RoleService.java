package it342.g4.e_vents.service;

import it342.g4.e_vents.model.Role;
import it342.g4.e_vents.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Service for managing Role entities
 */
@Service
public class RoleService {

    private final RoleRepository roleRepository;
    
    @Autowired
    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    /**
     * Retrieves all roles from the database
     * @return List of all roles
     */
    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }
    
    /**
     * Finds a role by its ID
     * @param id The role ID to look up
     * @return Optional containing the role if found
     */
    public Optional<Role> findRoleById(Long id) {
        return roleRepository.findById(id);
    }
    
    /**
     * Finds a role by its name
     * @param name The role name to look up
     * @return Optional containing the role if found
     */
    public Optional<Role> findRoleByName(String name) {
        return roleRepository.findByName(name);
    }
}
