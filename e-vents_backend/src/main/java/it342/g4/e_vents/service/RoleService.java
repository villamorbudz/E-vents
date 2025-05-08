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
    
    /**
     * Creates a new role
     * @param role The role to create
     * @return The created role
     */
    public Role createRole(Role role) {
        return roleRepository.save(role);
    }
    
    /**
     * Updates an existing role
     * @param id The ID of the role to update
     * @param roleDetails The updated role details
     * @return The updated role, or empty if the role was not found
     */
    public Optional<Role> updateRole(Long id, Role roleDetails) {
        return roleRepository.findById(id)
                .map(existingRole -> {
                    existingRole.setName(roleDetails.getName());
                    return roleRepository.save(existingRole);
                });
    }
    
    /**
     * Deactivates a role by setting its active flag to false
     * @param id The ID of the role to deactivate
     * @return True if the role was deactivated, false if not found
     */
    public boolean softDeleteRole(Long id) {
        return roleRepository.findById(id)
                .map(role -> {
                    role.setActive(false);
                    roleRepository.save(role);
                    return true;
                })
                .orElse(false);
    }
    
    /**
     * Activates a deactivated role
     * @param id The ID of the role to activate
     * @return True if the role was activated, false if not found
     */
    public boolean restoreRole(Long id) {
        return roleRepository.findById(id)
                .map(role -> {
                    role.setActive(true);
                    roleRepository.save(role);
                    return true;
                })
                .orElse(false);
    }
}
