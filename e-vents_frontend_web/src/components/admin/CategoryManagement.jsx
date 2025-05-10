import React from 'react';
import BaseEntityManagement from './BaseEntityManagement';
import { categoryService } from '../../services/apiService';

const CategoryManagement = () => {
  const displayFields = ['categoryId', 'name'];
  
  const fetchCategories = async () => {
    try {
      const data = await categoryService.getAllIncludingInactive();
      return data;
    } catch (error) {
      console.error('Error fetching categories:', error);
      return [];
    }
  };
  
  const handleCreateCategory = async (categoryData) => {
    return await categoryService.createCategory(categoryData);
  };
  
  const handleEditCategory = async (categoryData) => {
    return await categoryService.updateCategory(categoryData.categoryId, categoryData);
  };
  
  const handleToggleCategory = async (categoryData) => {
    if (categoryData.active) {
      return await categoryService.deactivateCategory(categoryData.categoryId);
    } else {
      return await categoryService.restoreCategory(categoryData.categoryId);
    }
  };
  
  const handleDeleteCategory = async (categoryData) => {
    return await categoryService.deleteCategory(categoryData.categoryId);
  };
  
  return (
    <BaseEntityManagement
      title="Category Management"
      entityName="Category"
      fetchItems={fetchCategories}
      displayFields={displayFields}
      handleCreate={handleCreateCategory}
      handleEdit={handleEditCategory}
      handleToggle={handleToggleCategory}
      handleDelete={handleDeleteCategory}
    />
  );
};

export default CategoryManagement;
