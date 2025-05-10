import React from 'react';
import BaseEntityManagement from './BaseEntityManagement';
import { ratingService } from '../../services/apiService';

const RatingManagement = () => {
  const displayFields = ['ratingId', 'ratingValue', 'entityType'];
  
  const fetchRatings = async () => {
    try {
      // Since there's no getAllIncludingInactive method in the ratingService,
      // we'll need to adapt based on what's available
      const data = await ratingService.getAll();
      return data;
    } catch (error) {
      console.error('Error fetching ratings:', error);
      return [];
    }
  };
  
  const handleCreateRating = async (ratingData) => {
    return await ratingService.create(
      ratingData.ratingValue,
      ratingData.entityId,
      ratingData.entityType,
      ratingData.message
    );
  };
  
  const handleEditRating = async (ratingData) => {
    return await ratingService.update(ratingData.ratingId, ratingData.message);
  };
  
  const handleToggleRating = async (ratingData) => {
    // Since there's no specific toggle method, we'll just use delete/restore logic
    // This might need to be adjusted based on the actual API
    return ratingData;
  };
  
  const handleDeleteRating = async (ratingData) => {
    return await ratingService.delete(ratingData.ratingId);
  };
  
  return (
    <BaseEntityManagement
      title="Rating Management"
      entityName="Rating"
      fetchItems={fetchRatings}
      displayFields={displayFields}
      handleCreate={handleCreateRating}
      handleEdit={handleEditRating}
      handleToggle={handleToggleRating}
      handleDelete={handleDeleteRating}
    />
  );
};

export default RatingManagement;
