import React from 'react';
import BaseEntityManagement from './BaseEntityManagement';
import { userService } from '../../services/apiService';

const UserManagement = () => {
  // Display fields for the table
  const displayFields = ['userId', 'firstName', 'lastName', 'email', 'active'];
  
  const fetchUsers = async () => {
    try {
      const data = await userService.getAllUsers();
      return data;
    } catch (error) {
      console.error('Error fetching users:', error);
      return [];
    }
  };
  
  const handleCreateUser = async (userData) => {
    try {
      return await userService.adminCreateUser(userData);
    } catch (error) {
      console.error('Error creating user:', error);
      throw error;
    }
  };
  
  const handleEditUser = async (userData) => {
    try {
      return await userService.updateUser(userData.userId, userData);
    } catch (error) {
      console.error('Error updating user:', error);
      throw error;
    }
  };
  
  const handleToggleUser = async (userData) => {
    try {
      if (userData.active) {
        return await userService.deactivateUser(userData.userId);
      } else {
        return await userService.activateUser(userData.userId);
      }
    } catch (error) {
      console.error('Error toggling user status:', error);
      throw error;
    }
  };
  
  const handleDeleteUser = async (userData) => {
    try {
      return await userService.deleteUser(userData.userId);
    } catch (error) {
      console.error('Error deleting user:', error);
      throw error;
    }
  };

  return (
    <BaseEntityManagement
      title="User Management"
      entityName="User"
      fetchItems={fetchUsers}
      displayFields={displayFields}
      handleCreate={handleCreateUser}
      handleEdit={handleEditUser}
      handleToggle={handleToggleUser}
      handleDelete={handleDeleteUser}
    />
  );
};

export default UserManagement;
