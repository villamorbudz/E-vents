import React, { useState, useEffect } from 'react';
import BaseEntityManagement from './BaseEntityManagement';
import EntityModal from './EntityModal';
import { tagService, categoryService } from '../../services/apiService';

const TagManagement = () => {
  // Display fields for the table headers
  const displayFields = ['ID', 'Name', 'Category', 'Active'];
  
  // Getter parameters that match the attribute names in the data
  const getterParams = ['tagId', 'name', 'category', 'active'];
  
  const [modalOpen, setModalOpen] = useState(false);
  const [modalMode, setModalMode] = useState('create');
  const [selectedItem, setSelectedItem] = useState({});
  const [formData, setFormData] = useState({});
  const [error, setError] = useState(null);
  const [successMessage, setSuccessMessage] = useState(null);
  const [refreshTrigger, setRefreshTrigger] = useState(0);
  const [validationErrors, setValidationErrors] = useState({});
  
  // State for select boxes
  const [categories, setCategories] = useState([]);
  const [isLoading, setIsLoading] = useState(false);

  // Fetch categories for select box
  useEffect(() => {
    const fetchCategories = async () => {
      console.log('TagManagement: Starting to fetch categories...');
      try {
        const categoriesData = await categoryService.getAllCategories();
        console.log('TagManagement: Categories data received:', categoriesData);
        
        // Ensure categoriesData is an array before setting it
        if (Array.isArray(categoriesData)) {
          console.log(`TagManagement: Setting ${categoriesData.length} categories to state`);
          
          // Simplify the category objects to avoid circular references
          const simplifiedCategories = categoriesData.map(category => ({
            categoryId: category.categoryId,
            name: category.name,
            // Only include essential tag data if needed
            tags: category.tags ? category.tags.map(tag => ({
              tagId: tag.tagId,
              name: tag.name
            })) : []
          }));
          
          console.log('TagManagement: Simplified categories:', simplifiedCategories);
          setCategories(simplifiedCategories);
        } else {
          console.error('TagManagement: Categories data is not an array:', categoriesData);
          setCategories([]); // Set to empty array as fallback
        }
      } catch (error) {
        console.error('TagManagement: Error fetching categories:', error);
        setCategories([]); // Set to empty array on error
      }
    };
    
    fetchCategories();
  }, []);
  
  const fetchTags = async () => {
    try {
      const data = await tagService.getAllIncludingInactive();
      
      // Process each tag to ensure category name is available
      const processedData = await Promise.all(data.map(async (tag) => {
        // If category name is not already available, fetch it
        if (!tag.categoryName && (tag.category || tag.categoryInfo)) {
          try {
            const categoryName = await tagService.getCategoryNameByTagId(tag.tagId);
            tag.categoryName = categoryName;
          } catch (error) {
            console.error(`Error fetching category name for tag ${tag.tagId}:`, error);
            // Try to get the name from category or categoryInfo
            tag.categoryName = 
              (tag.category ? tag.category.name : null) || 
              (tag.categoryInfo ? tag.categoryInfo.name : 'Unknown');
          }
        }
        
        return tag;
      }));
      
      return processedData;
    } catch (error) {
      console.error('Error fetching tags:', error);
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
      name: '',
      category: ''
    });
    setError(null);
    setModalOpen(true);
  };

  const handleEditClick = (item) => {
    setModalMode('edit');
    setSelectedItem(item);
    
    // Extract category ID
    const categoryId = item.category?.categoryId || '';
    
    const formattedItem = {
      ...item,
      category: categoryId
    };
    
    setFormData(formattedItem);
    setError(null);
    setModalOpen(true);
  };

  const handleToggleClick = (item) => {
    setModalMode('toggle');
    setSelectedItem(item);
    setError(null);
    setModalOpen(true);
  };

  const handleDeleteClick = (item) => {
    setModalMode('delete');
    setSelectedItem(item);
    setError(null);
    setModalOpen(true);
  };

  const validateForm = () => {
    const errors = {};
    
    if (!formData.name || formData.name.trim() === '') {
      errors.name = 'Name is required';
    }
    
    if (!formData.category) {
      errors.category = 'Category is required';
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
    try {
      let result;
      let successMsg = '';
      
      // Prepare data for API
      const tagData = {
        ...formData
      };
      
      // Format category as object if needed
      if (tagData.category && typeof tagData.category === 'string') {
        tagData.category = { categoryId: tagData.category };
      }
      
      switch (modalMode) {
        case 'create':
          result = await tagService.createTag(tagData);
          successMsg = `Tag '${formData.name}' created successfully!`;
          // Exit form on successful creation
          setModalOpen(false);
          setRefreshTrigger(prev => prev + 1);
          break;
        case 'edit':
          result = await tagService.updateTag(selectedItem.tagId, tagData);
          successMsg = `Tag '${formData.name}' updated successfully!`;
          break;
        case 'toggle':
          if (selectedItem.active) {
            result = await tagService.deactivateTag(selectedItem.tagId);
            successMsg = `Tag '${selectedItem.name}' deactivated successfully!`;
          } else {
            result = await tagService.restoreTag(selectedItem.tagId);
            successMsg = `Tag '${selectedItem.name}' activated successfully!`;
          }
          // Close modal for toggle operations
          setModalOpen(false);
          setRefreshTrigger(prev => prev + 1);
          break;
        case 'delete':
          await tagService.deleteTag(selectedItem.tagId);
          successMsg = `Tag '${selectedItem.name}' deleted successfully!`;
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
      console.error(`Error ${modalMode} tag:`, error);
      setError(error.response?.data || `Error performing ${modalMode} operation`);
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <>
      <BaseEntityManagement
        title="Tag Management"
        entityName="Tag"
        fetchItems={fetchTags}
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
        title={`${modalMode.charAt(0).toUpperCase() + modalMode.slice(1)} Tag`}
        mode={modalMode}
        onConfirm={handleConfirm}
        error={error}
        successMessage={successMessage}
        isLoading={isLoading}
        item={selectedItem}
      >
        {(modalMode === 'create' || modalMode === 'edit') && (
          <>
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
            
            <div className="form-group">
              <label htmlFor="category">Category *</label>
              <select
                id="category"
                name="category"
                value={formData.category?.categoryId || formData.category || ''}
                onChange={handleInputChange}
                className={validationErrors.category ? 'input-error' : ''}
                required
              >
                <option value="">Select Category</option>
                {Array.isArray(categories) && categories.map(category => (
                  <option key={category.categoryId} value={category.categoryId}>
                    {category.name}
                  </option>
                ))}
              </select>
              {validationErrors.category ? (
                <div className="validation-error">{validationErrors.category}</div>
              ) : (
                <small>Tags are required to be related to a category</small>
              )}
            </div>
          </>
        )}
      </EntityModal>
    </>
  );
};

export default TagManagement;
