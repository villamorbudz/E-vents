import React, { useState, useEffect } from 'react';
import BaseEntityManagement from './BaseEntityManagement';
import EntityModal from './EntityModal';
import { actService, categoryService, tagService } from '../../services/apiService';
import '../../styles/TableScroll.css';

const ActManagement = () => {
  // Display fields for the table headers
  const displayFields = ['ID', 'Name', 'Description', 'Category', 'Tags', 'Active'];
  
  // Getter parameters that match the attribute names in the data
  const getterParams = ['actId', 'name', 'description', 'category', 'tags', 'active'];
  
  const [modalOpen, setModalOpen] = useState(false);
  const [modalMode, setModalMode] = useState('create');
  const [selectedItem, setSelectedItem] = useState({});
  const [formData, setFormData] = useState({});
  const [error, setError] = useState(null);
  const [refreshTrigger, setRefreshTrigger] = useState(0);
  
  // State for select boxes
  const [categories, setCategories] = useState([]);
  const [tags, setTags] = useState([]);
  const [selectedTags, setSelectedTags] = useState([]);
  const [isLoading, setIsLoading] = useState(false);

  // Fetch categories for select box
  useEffect(() => {
    const fetchCategories = async () => {
      try {
        const categoriesData = await categoryService.getAllCategories();
        setCategories(Array.isArray(categoriesData) ? categoriesData : []);
      } catch (error) {
        console.error('Error fetching categories:', error);
        setCategories([]);
      }
    };
    
    fetchCategories();
  }, []);

  // Fetch tags when category changes
  useEffect(() => {
    const fetchTags = async () => {
      if (formData.category) {
        try {
          const categoryId = typeof formData.category === 'object' ? 
            formData.category.categoryId : formData.category;
          
          const tagsData = await tagService.getTagsByCategory(categoryId);
          setTags(tagsData);
          
          // Validate selected tags against the new category
          if (selectedTags.length > 0) {
            const validTagIds = tagsData.map(tag => tag.tagId);
            const validSelectedTags = selectedTags.filter(tagId => 
              validTagIds.includes(tagId)
            );
            
            if (validSelectedTags.length !== selectedTags.length) {
              setSelectedTags(validSelectedTags);
              setFormData(prev => ({
                ...prev,
                tags: validSelectedTags.map(tagId => ({ tagId }))
              }));
            }
          }
        } catch (error) {
          console.error('Error fetching tags:', error);
        }
      } else {
        setTags([]);
      }
    };
    
    fetchTags();
  }, [formData.category]);
  
  const fetchActs = async () => {
    try {
      const data = await actService.getAllIncludingInactive();
      
      // Process each act to ensure category and tag names are available
      const processedData = await Promise.all(data.map(async (act) => {
        // If category name is not already available, fetch it
        if (!act.categoryName && act.category) {
          try {
            const categoryName = await actService.getCategoryNameByActId(act.actId);
            act.categoryName = categoryName;
          } catch (error) {
            console.error(`Error fetching category name for act ${act.actId}:`, error);
            act.categoryName = act.category.name || 'Unknown';
          }
        }
        
        // If tag names are not already available, fetch them
        if (!act.tagNames && act.tags) {
          try {
            const tagNames = await actService.getTagNamesByActId(act.actId);
            act.tagNames = tagNames;
          } catch (error) {
            console.error(`Error fetching tag names for act ${act.actId}:`, error);
            act.tagNames = act.tags.map(tag => tag.name).join(', ') || '';
          }
        }
        
        return act;
      }));
      
      return processedData;
    } catch (error) {
      console.error('Error fetching acts:', error);
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

  const handleTagsChange = (e) => {
    const selectedOptions = Array.from(e.target.selectedOptions, option => option.value);
    setSelectedTags(selectedOptions);
    
    // Update formData with selected tags
    setFormData(prev => ({
      ...prev,
      tags: selectedOptions.map(tagId => ({ tagId }))
    }));
  };

  const handleCreateClick = () => {
    setModalMode('create');
    setSelectedItem({});
    setFormData({
      name: '',
      description: '',
      category: '',
      tags: []
    });
    setSelectedTags([]);
    setError(null);
    setModalOpen(true);
  };

  const handleEditClick = (item) => {
    setModalMode('edit');
    setSelectedItem(item);
    
    // Extract category and tag IDs
    const categoryId = item.category?.categoryId || '';
    const tagIds = item.tags?.map(tag => tag.tagId) || [];
    
    setSelectedTags(tagIds);
    
    const formattedItem = {
      ...item,
      category: categoryId,
      tags: item.tags || []
    };
    
    setFormData(formattedItem);
    setError(null);
    setModalOpen(true);
    
    // Fetch tags for the selected category
    if (categoryId) {
      tagService.getTagsByCategory(categoryId)
        .then(tagsData => setTags(tagsData))
        .catch(error => console.error('Error fetching tags:', error));
    }
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

  const handleConfirm = async () => {
    setIsLoading(true);
    try {
      let result;
      
      // Prepare data for API
      const actData = {
        ...formData
      };
      
      // Format category as object if needed
      if (actData.category && typeof actData.category === 'string') {
        actData.category = { categoryId: actData.category };
      }
      
      switch (modalMode) {
        case 'create':
          result = await actService.createAct(actData);
          break;
        case 'edit':
          result = await actService.updateAct(selectedItem.actId, actData);
          break;
        case 'toggle':
          if (selectedItem.active) {
            result = await actService.deactivateAct(selectedItem.actId);
          } else {
            result = await actService.restoreAct(selectedItem.actId);
          }
          break;
        case 'delete':
          await actService.deleteAct(selectedItem.actId);
          break;
        default:
          break;
      }
      
      // Close modal and refresh data
      setModalOpen(false);
      setRefreshTrigger(prev => prev + 1);
    } catch (error) {
      console.error(`Error ${modalMode} act:`, error);
      setError(error.response?.data || `Error performing ${modalMode} operation`);
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <>
      <BaseEntityManagement
        title="Act Management"
        entityName="Act"
        fetchItems={fetchActs}
        displayFields={displayFields}
        getterParams={getterParams}
        onCreateClick={handleCreateClick}
        onEditClick={handleEditClick}
        onToggleClick={handleToggleClick}
        onDeleteClick={handleDeleteClick}
        refreshTrigger={refreshTrigger}
        className="table-scroll-wrapper"
      />
      
      <EntityModal
        isOpen={modalOpen}
        onClose={() => setModalOpen(false)}
        title={`${modalMode.charAt(0).toUpperCase() + modalMode.slice(1)} Act`}
        mode={modalMode}
        onConfirm={handleConfirm}
        error={error}
        item={selectedItem}
      >
        {(modalMode === 'create' || modalMode === 'edit') && (
          <>
            <div className="form-group">
              <label htmlFor="name">Name</label>
              <input
                type="text"
                id="name"
                name="name"
                value={formData.name || ''}
                onChange={handleInputChange}
                required
              />
            </div>
            
            <div className="form-group">
              <label htmlFor="description">Description</label>
              <textarea
                id="description"
                name="description"
                value={formData.description || ''}
                onChange={handleInputChange}
                rows="4"
              />
            </div>
            
            <div className="form-group">
              <label htmlFor="category">Category</label>
              <select
                id="category"
                name="category"
                value={formData.category?.categoryId || formData.category || ''}
                onChange={handleInputChange}
                required
              >
                <option value="">Select Category</option>
                {categories.map(category => (
                  <option key={category.categoryId} value={category.categoryId}>
                    {category.name}
                  </option>
                ))}
              </select>
            </div>
            
            <div className="form-group">
              <label htmlFor="tags">Tags</label>
              <select
                id="tags"
                name="tags"
                multiple
                value={selectedTags}
                onChange={handleTagsChange}
                className="multiselect"
                disabled={!formData.category}
              >
                {tags.map(tag => (
                  <option key={tag.tagId} value={tag.tagId}>{tag.name}</option>
                ))}
              </select>
              <small>
                {formData.category ? 
                  'Hold Ctrl/Cmd to select multiple tags' : 
                  'Select a category first to see available tags'}
              </small>
            </div>
          </>
        )}
      </EntityModal>
    </>
  );
};

export default ActManagement;
