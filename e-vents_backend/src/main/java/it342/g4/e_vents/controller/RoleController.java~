package it342.g4.e_vents.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import it342.g4.e_vents.model.Role;
import it342.g4.e_vents.service.RoleService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

/**
 * REST controller for managing roles
 */
@RestController
@RequestMapping("/api/roles")
@CrossOrigin(origins = "*")
@Tag(name = "Role", description = "Role management APIs")
public class RoleController {

    private final RoleService roleService;

    @Autowired
    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    /**
     * GET /api/roles : Get all roles
     * @return the ResponseEntity with status 200 (OK) and the list of roles in body
     */
    @GetMapping
    @Operation(summary = "Get all roles", description = "Retrieves a list of all roles in the system")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved list of roles", 
                     content = @Content(mediaType = "application/json", schema = @Schema(implementation = Role.class)))
    })
    public ResponseEntity<List<Role>> getAllRoles() {
        List<Role> roles = roleService.getAllRoles();
        return ResponseEntity.ok(roles);
    }

    /**
     * GET /api/roles/{id} : Get the "id" role
     * @param id the id of the role to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the role, or with status 404 (Not Found)
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get role by ID", description = "Retrieves a specific role by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved the role", 
                     content = @Content(mediaType = "application/json", schema = @Schema(implementation = Role.class))),
        @ApiResponse(responseCode = "404", description = "Role not found", content = @Content)
    })
    public ResponseEntity<Role> getRoleById(
            @Parameter(description = "ID of the role to retrieve") @PathVariable Long id) {
        return roleService.findRoleById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * POST /api/roles : Create a new role
     * @param role the role to create
     * @return the ResponseEntity with status 201 (Created) and with body the new role
     */
    @PostMapping
    @Operation(summary = "Create a new role", description = "Creates a new role in the system")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Role successfully created", 
                     content = @Content(mediaType = "application/json", schema = @Schema(implementation = Role.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content)
    })
    public ResponseEntity<?> createRole(
            @Parameter(description = "Role object to be created", required = true) @RequestBody Role role) {
        try {
            Role createdRole = roleService.createRole(role);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdRole);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Collections.singletonMap("error", e.getMessage()));
        }
    }

    /**
     * PUT /api/roles/{id} : Updates an existing role
     * @param id the id of the role to update
     * @param role the role to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated role,
     * or with status 404 (Not Found) if the role is not found
     */
    @PutMapping("/{id}")
    @Operation(summary = "Update a role", description = "Updates an existing role by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Role successfully updated", 
                     content = @Content(mediaType = "application/json", schema = @Schema(implementation = Role.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content),
        @ApiResponse(responseCode = "404", description = "Role not found", content = @Content)
    })
    public ResponseEntity<?> updateRole(
            @Parameter(description = "ID of the role to update", required = true) @PathVariable Long id,
            @Parameter(description = "Updated role details", required = true) @RequestBody Role role) {
        try {
            return roleService.updateRole(id, role)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Collections.singletonMap("error", e.getMessage()));
        }
    }

    /**
     * DELETE /api/roles/{id} : Deactivate the "id" role
     * @param id the id of the role to deactivate
     * @return the ResponseEntity with status 200 (OK) if successful,
     * or with status 404 (Not Found) if the role is not found
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Deactivate a role", description = "Deactivates a role by setting it as inactive")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Role successfully deactivated", 
                     content = @Content(mediaType = "application/json")),
        @ApiResponse(responseCode = "404", description = "Role not found", content = @Content)
    })
    public ResponseEntity<?> deactivateRole(
            @Parameter(description = "ID of the role to deactivate", required = true) @PathVariable Long id) {
        try {
            boolean deactivated = roleService.softDeleteRole(id);
            if (deactivated) {
                return ResponseEntity.ok(Collections.singletonMap("message", "Role deactivated successfully"));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Collections.singletonMap("error", "Role not found"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Collections.singletonMap("error", e.getMessage()));
        }
    }

    /**
     * POST /api/roles/restore/{id} : Activate the "id" role
     * @param id the id of the role to activate
     * @return the ResponseEntity with status 200 (OK) if successful,
     * or with status 404 (Not Found) if the role is not found
     */
    @PostMapping("/restore/{id}")
    @Operation(summary = "Activate a role", description = "Activates a previously deactivated role")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Role successfully activated", 
                     content = @Content(mediaType = "application/json")),
        @ApiResponse(responseCode = "404", description = "Role not found", content = @Content)
    })
    public ResponseEntity<?> activateRole(
            @Parameter(description = "ID of the role to activate", required = true) @PathVariable Long id) {
        try {
            boolean activated = roleService.restoreRole(id);
            if (activated) {
                return ResponseEntity.ok(Collections.singletonMap("message", "Role activated successfully"));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Collections.singletonMap("error", "Role not found"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Collections.singletonMap("error", e.getMessage()));
        }
    }
}
