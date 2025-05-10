import React from 'react';
import BaseEntityManagement from './BaseEntityManagement';
import { tagService } from '../../services/apiService';

const TagManagement = () => {
  const displayFields = ['tagId', 'name'];
  
  const fetchTags = async () => {
    try {
      const data = await tagService.getAllIncludingInactive();
      return data;
    } catch (error) {
      console.error('Error fetching tags:', error);
      return [];
    }
  };
  
  const handleCreateTag = async (tagData) => {
    return await tagService.createTag(tagData);
  };
  
  const handleEditTag = async (tagData) => {
    return await tagService.updateTag(tagData.tagId, tagData);
  };
  
  const handleToggleTag = async (tagData) => {
    if (tagData.active) {
      return await tagService.deactivateTag(tagData.tagId);
    } else {
      return await tagService.restoreTag(tagData.tagId);
    }
  };
  
  const handleDeleteTag = async (tagData) => {
    return await tagService.deactivateTag(tagData.tagId);
  };
  
  return (
    <BaseEntityManagement
      title="Tag Management"
      entityName="Tag"
      fetchItems={fetchTags}
      displayFields={displayFields}
      handleCreate={handleCreateTag}
      handleEdit={handleEditTag}
      handleToggle={handleToggleTag}
      handleDelete={handleDeleteTag}
    />
  );
};

export default TagManagement;
