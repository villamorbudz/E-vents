import React from 'react';
import BaseEntityManagement from './BaseEntityManagement';
import { ticketCategoryService } from '../../services/apiService';

const TicketCategoryManagement = () => {
  const displayFields = ['ticketCategoryId', 'name'];
  
  const fetchTicketCategories = async () => {
    try {
      const data = await ticketCategoryService.getAllIncludingInactive();
      return data;
    } catch (error) {
      console.error('Error fetching ticket categories:', error);
      return [];
    }
  };
  
  const handleCreateTicketCategory = async (categoryData) => {
    return await ticketCategoryService.create(categoryData);
  };
  
  const handleEditTicketCategory = async (categoryData) => {
    return await ticketCategoryService.update(categoryData.ticketCategoryId, categoryData);
  };
  
  const handleToggleTicketCategory = async (categoryData) => {
    if (categoryData.active) {
      return await ticketCategoryService.deactivate(categoryData.ticketCategoryId);
    } else {
      return await ticketCategoryService.restore(categoryData.ticketCategoryId);
    }
  };
  
  const handleDeleteTicketCategory = async (categoryData) => {
    return await ticketCategoryService.deactivate(categoryData.ticketCategoryId);
  };
  
  return (
    <BaseEntityManagement
      title="Ticket Category Management"
      entityName="Ticket Category"
      fetchItems={fetchTicketCategories}
      displayFields={displayFields}
      handleCreate={handleCreateTicketCategory}
      handleEdit={handleEditTicketCategory}
      handleToggle={handleToggleTicketCategory}
      handleDelete={handleDeleteTicketCategory}
    />
  );
};

export default TicketCategoryManagement;
