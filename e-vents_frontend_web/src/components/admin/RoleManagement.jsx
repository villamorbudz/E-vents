import React from 'react';
import BaseEntityManagement from './BaseEntityManagement';
import { roleService } from '../../services/apiService';

const RoleManagement = () => {
  const displayFields = ['roleId', 'name', 'active'];
  
  const fetchRoles = async () => {
    try {
      const data = await roleService.getAllRoles();
      return data;
    } catch (error) {
      console.error('Error fetching roles:', error);
      return [];
    }
  };
  
  const handleCreateRole = async (roleData) => {
    return await roleService.createRole(roleData);
  };
  
  const handleEditRole = async (roleData) => {
    return await roleService.updateRole(roleData.roleId, roleData);
  };
  
  const handleToggleRole = async (roleData) => {
    if (roleData.active) {
      return await roleService.deactivateRole(roleData.roleId);
    } else {
      return await roleService.restoreRole(roleData.roleId);
    }
  };
  
  const handleDeleteRole = async (roleData) => {
    return await roleService.deactivateRole(roleData.roleId);
  };
  
  return (
    <BaseEntityManagement
      title="Role Management"
      entityName="Role"
      fetchItems={fetchRoles}
      displayFields={displayFields}
      getterParams={['roleId', 'name', 'active']} // Match with displayFields
      onCreateClick={() => console.log('Create role clicked')} // Placeholder
      onEditClick={(item) => console.log('Edit role clicked', item)} // Placeholder
      onToggleClick={(item) => console.log('Toggle role clicked', item)} // Placeholder
      onDeleteClick={(item) => console.log('Delete role clicked', item)} // Placeholder
      refreshTrigger={0} // Add a refresh trigger
    />
  );
};

export default RoleManagement;
