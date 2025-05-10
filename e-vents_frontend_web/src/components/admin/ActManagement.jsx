import React from 'react';
import BaseEntityManagement from './BaseEntityManagement';
import { actService } from '../../services/apiService';

const ActManagement = () => {
  const displayFields = ['actId', 'name'];
  
  const fetchActs = async () => {
    try {
      const data = await actService.getAllIncludingInactive();
      return data;
    } catch (error) {
      console.error('Error fetching acts:', error);
      return [];
    }
  };
  
  const handleCreateAct = async (actData) => {
    try {
      return await actService.createAct(actData);
    } catch (error) {
      console.error('Error creating act:', error);
      throw error;
    }
  };
  
  const handleEditAct = async (actData) => {
    try {
      return await actService.updateAct(actData.actId, actData);
    } catch (error) {
      console.error('Error updating act:', error);
      throw error;
    }
  };
  
  const handleToggleAct = async (actData) => {
    try {
      if (actData.active) {
        return await actService.deactivateAct(actData.actId);
      } else {
        return await actService.restoreAct(actData.actId);
      }
    } catch (error) {
      console.error('Error toggling act status:', error);
      throw error;
    }
  };
  
  const handleDeleteAct = async (actData) => {
    try {
      return await actService.deactivateAct(actData.actId);
    } catch (error) {
      console.error('Error deleting act:', error);
      throw error;
    }
  };
  
  // No need for local state management as BaseEntityManagement handles it

  return (
    <BaseEntityManagement
      title="Act Management"
      entityName="Act"
      fetchItems={fetchActs}
      displayFields={displayFields}
      handleCreate={handleCreateAct}
      handleEdit={handleEditAct}
      handleToggle={handleToggleAct}
      handleDelete={handleDeleteAct}
    />
  );
};

export default ActManagement;
