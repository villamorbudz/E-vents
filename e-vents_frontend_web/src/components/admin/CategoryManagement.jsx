import React, { useState } from 'react';
import BaseEntityManagement from './BaseEntityManagement';
import EntityModal from './EntityModal';
import { categoryService } from '../../services/apiService';

const CategoryManagement = () => {
  // Display fields for the table headers
  const displayFields = ['ID', 'Name', 'Active'];
  
  // Getter parameters that match the attribute names in the data
  const getterParams = ['categoryId', 'name', 'active'];
  
  const [modalOpen, setModalOpen] = useState(false);
  const [modalMode, setModalMode] = useState('create');
  const [selectedItem, setSelectedItem] = useState({});
  const [formData, setFormData] = useState({});
  const [error, setError] = useState(null);
  const [successMessage, setSuccessMessage] = useState(null);
  const [refreshTrigger, setRefreshTrigger] = useState(0);
  const [isLoading, setIsLoading] = useState(false);
  const [validationErrors, setValidationErrors] = useState({});
  
  const fetchCategories = async () => {
    console.log('CategoryManagement: Starting to fetch all categories...');
    try {
      const categoriesData = await categoryService.getAllIncludingInactive();
      console.log('CategoryManagement: Categories data received:', categoriesData);
      
      // Ensure categoriesData is an array before returning it
      if (Array.isArray(categoriesData)) {
        console.log(`CategoryManagement: Returning ${categoriesData.length} categories`);
        
        // Simplify the category objects to avoid circular references
        const simplifiedCategories = categoriesData.map(category => ({
          categoryId: category.categoryId,
          name: category.name,
          active: category.active !== undefined ? category.active : true
        }));
        
        console.log('CategoryManagement: Simplified categories:', simplifiedCategories);
        return simplifiedCategories;
      } else {
        console.error('CategoryManagement: Categories data is not an array:', categoriesData);
        return [];
      }
    } catch (error) {
      console.error('CategoryManagement: Error fetching categories:', error);
      return [];
    }
  };
  
  const handleInputChange = (e) => {
    const { name, value, type, checked } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: type === 'checkbox' ? checked : value
    }));
  };

  const handleCreateClick = () => {
    setModalMode('create');
    setSelectedItem({});
    setFormData({
      name: ''
    });
    setError(null);
    setSuccessMessage(null);
    setModalOpen(true);
  };

  const handleEditClick = (item) => {
    setModalMode('edit');
    setSelectedItem(item);
    setFormData({
      name: item.name || ''
    });
    setError(null);
    setSuccessMessage(null);
    setModalOpen(true);
  };

  const handleToggleClick = (item) => {
    setModalMode('toggle');
    setSelectedItem(item);
    setError(null);
    setSuccessMessage(null);
    setModalOpen(true);
  };

  const handleDeleteClick = (item) => {
    setModalMode('delete');
    setSelectedItem(item);
    setError(null);
    setSuccessMessage(null);
    setModalOpen(true);
  };

  const validateForm = () => {
    const errors = {};
    
    if (!formData.name || formData.name.trim() === '') {
      errors.name = 'Name is required';
    }
    
    setValidationErrors(errors);
    return Object.keys(errors).length === 0;
  };

  const handleConfirm = async () => {
    // For create and edit modes, validate the form first
    if (modalMode === 'create' || modalMode === 'edit') {
      if (!validateForm()) {
        return; // Stop submission if validation fails
      }
    }
    
    setIsLoading(true);
    setError(null);
    setSuccessMessage(null);
    
    try {
      let result;
      let successMsg = '';
      
      switch (modalMode) {
        case 'create':
          result = await categoryService.createCategory(formData);
          successMsg = `Category '${formData.name}' created successfully!`;
          // Exit form on successful creation
          setModalOpen(false);
          setRefreshTrigger(prev => prev + 1);
          break;
        case 'edit':
          result = await categoryService.updateCategory(selectedItem.categoryId, formData);
          successMsg = `Category '${formData.name}' updated successfully!`;
          break;
        case 'toggle':
          if (selectedItem.active) {
            result = await categoryService.deactivateCategory(selectedItem.categoryId);
            successMsg = `Category '${selectedItem.name}' deactivated successfully!`;
          } else {
            result = await categoryService.restoreCategory(selectedItem.categoryId);
            successMsg = `Category '${selectedItem.name}' activated successfully!`;
          }
          // Close modal for toggle operations
          setModalOpen(false);
          setRefreshTrigger(prev => prev + 1);
          break;
        case 'delete':
          await categoryService.deleteCategory(selectedItem.categoryId);
          successMsg = `Category '${selectedItem.name}' deleted successfully!`;
          // Close modal for delete operations
          setModalOpen(false);
          setRefreshTrigger(prev => prev + 1);
          break;
        default:
          break;
      }
      
      // Set success message
      setSuccessMessage(successMsg);
      
      // For edit mode, don't close automatically
      if (modalMode === 'edit') {
        // Just show success message, don't close
      }
    } catch (error) {
      console.error(`Error ${modalMode} category:`, error);
      
      // Extract error message from response
      let errorMessage;
      if (error.response?.data) {
        // Handle different error response formats
        if (typeof error.response.data === 'string') {
          errorMessage = error.response.data;
        } else if (error.response.data.message) {
          errorMessage = error.response.data.message;
        } else if (error.response.data.error) {
          errorMessage = error.response.data.error;
        } else {
          errorMessage = JSON.stringify(error.response.data);
        }
      } else if (error.message) {
        errorMessage = error.message;
      } else {
        errorMessage = error.toString();
      }
      
      setError(errorMessage);
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <>
      <BaseEntityManagement
        title="Category Management"
        entityName="Category"
        fetchItems={fetchCategories}
        displayFields={displayFields}
        getterParams={getterParams}
        onCreateClick={handleCreateClick}
        onEditClick={handleEditClick}
        onToggleClick={handleToggleClick}
        onDeleteClick={handleDeleteClick}
        refreshTrigger={refreshTrigger}
      />
      
      <EntityModal
        isOpen={modalOpen}
        onClose={() => {
          setModalOpen(false);
          setRefreshTrigger(prev => prev + 1); // Refresh table when modal is closed
          setValidationErrors({}); // Clear validation errors when closing
        }}
        title={`${modalMode.charAt(0).toUpperCase() + modalMode.slice(1)} Category`}
        mode={modalMode}
        onConfirm={handleConfirm}
        error={error}
        successMessage={successMessage}
        isLoading={isLoading}
        item={selectedItem}
      >
        {(modalMode === 'create' || modalMode === 'edit') && (
          <div className="form-group">
            <label htmlFor="name">Name *</label>
            <input
              type="text"
              id="name"
              name="name"
              value={formData.name || ''}
              onChange={handleInputChange}
              className={validationErrors.name ? 'input-error' : ''}
              required
            />
            {validationErrors.name && (
              <div className="validation-error">{validationErrors.name}</div>
            )}
          </div>
        )}
      </EntityModal>
    </>
  );
};

export default CategoryManagement;
