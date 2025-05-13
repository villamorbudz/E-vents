import React, { useState, useEffect, useRef } from 'react';
import AdminSidebar from '../AdminSidebar';
import { FaEdit, FaTrash, FaUndo, FaPlus, FaSort, FaSortUp, FaSortDown } from 'react-icons/fa';
import '../../styles/AdminEntityPage.css';
import './TableSort.css'; // CSS for sorting styles

const BaseEntityManagement = ({ 
  title, 
  entityName, 
  fetchItems, 
  displayFields, 
  getterParams,
  onCreateClick, 
  onEditClick, 
  onToggleClick, 
  onDeleteClick,
  refreshTrigger,
  className // Add className prop for custom styling
}) => {
  const [items, setItems] = useState([]);
  const [sortedItems, setSortedItems] = useState([]);
  const [loading, setLoading] = useState(true);
  const [sortConfig, setSortConfig] = useState({ key: null, direction: 'none' });
  const tableRef = useRef(null);
  
  // Helper function to get value from an item by parameter
  const getValue = (item, param) => {
    if (!item) return '';
    
    // Handle special cases for category and tags
    if (param === 'category') {
      // First try to use the categoryName property if available
      if (item.categoryName) {
        return item.categoryName;
      }
      // Then try to access category.name
      if (item.category && item.category.name) {
        return item.category.name;
      }
      // Then try categoryInfo
      if (item.categoryInfo && item.categoryInfo.name) {
        return item.categoryInfo.name;
      }
      return 'Unknown';
    }
    
    // Handle tags display
    if (param === 'tags') {
      // First try to use the tagNames property if available
      if (item.tagNames) {
        return item.tagNames;
      }
      // Then try to join tag names
      if (Array.isArray(item.tags)) {
        return item.tags.map(tag => tag.name).join(', ');
      }
      return '';
    }
    
    // Handle nested properties like 'role.name'
    if (param.includes('.')) {
      const parts = param.split('.');
      let value = item;
      for (const part of parts) {
        value = value?.[part];
        if (value === undefined) return '';
      }
      return value;
    }
    
    // Format date fields
    if (param === 'createdDate' || param === 'birthdate' || param.toLowerCase().includes('date')) {
      if (!item[param]) return 'N/A';
      try {
        const date = new Date(item[param]);
        if (isNaN(date.getTime())) return item[param]; // Return original if invalid date
        return date.toLocaleDateString('en-US', {
          year: 'numeric',
          month: 'short',
          day: 'numeric',
          hour: '2-digit',
          minute: '2-digit'
        });
      } catch (error) {
        console.error('Error formatting date:', error);
        return item[param];
      }
    }
    
    // Handle active status
    if (param === 'active') {
      return item[param] !== undefined ? item[param] : false;
    }
    
    return item[param] !== undefined ? item[param] : '';
  };

  useEffect(() => {
    const loadItems = async () => {
      setLoading(true);
      try {
        const data = await fetchItems();
        // Ensure data is an array
        const itemsArray = Array.isArray(data) ? data : [];
        setItems(itemsArray);
        setSortedItems(itemsArray);
      } catch (e) {
        console.error(e);
        setItems([]);
        setSortedItems([]);
      }
      setLoading(false);
    };
    loadItems();
  }, [fetchItems, refreshTrigger]);
  
  // Apply sorting whenever items or sortConfig changes
  useEffect(() => {
    let result = [...items];
    
    // Apply sorting
    if (sortConfig.key && sortConfig.direction !== 'none') {
      result.sort((a, b) => {
        const aValue = getValue(a, sortConfig.key);
        const bValue = getValue(b, sortConfig.key);
        
        // Handle string comparison
        if (typeof aValue === 'string' && typeof bValue === 'string') {
          if (sortConfig.direction === 'ascending') {
            return aValue.localeCompare(bValue);
          } else {
            return bValue.localeCompare(aValue);
          }
        }
        
        // Handle numeric comparison
        if (sortConfig.direction === 'ascending') {
          return aValue > bValue ? 1 : -1;
        } else {
          return aValue < bValue ? 1 : -1;
        }
      });
    }
    
    setSortedItems(result);
  }, [items, sortConfig]);
  
  // Function to handle sorting when a column header is clicked
  const requestSort = (key) => {
    let direction = 'ascending';
    if (sortConfig.key === key) {
      if (sortConfig.direction === 'ascending') {
        direction = 'descending';
      } else if (sortConfig.direction === 'descending') {
        direction = 'none';
      }
    }
    setSortConfig({ key, direction });
  };
  
  // No need for individual cell scroll handling as we're using a single table scroll

  return (
    <div className="admin-layout">
      <AdminSidebar />
      <div className="admin-container">
        <h1 className="admin-title">{title}</h1>
        <button 
          className="admin-button create-button" 
          onClick={onCreateClick}
        >
          <FaPlus /> New {entityName}
        </button>
        {loading ? (
          <div className="admin-loading">
            <div className="spinner"></div>
            <p>Loading {entityName} data...</p>
          </div>
        ) : (
          <div className={className || ''}>
            <table className="admin-table" ref={tableRef}>
            <thead>
              <tr>
                {displayFields.map((f, index) => {
                  const param = getterParams[index];
                  const getSortIcon = () => {
                    if (sortConfig.key === param) {
                      if (sortConfig.direction === 'ascending') {
                        return <FaSortUp />;
                      } else if (sortConfig.direction === 'descending') {
                        return <FaSortDown />;
                      }
                    }
                    return <FaSort />;
                  };
                  
                  return (
                    <th 
                      key={index}
                      className="sortable-header"
                      onClick={() => requestSort(param)}
                    >
                      <div className="header-content">
                        {f}
                        <span className="sort-icon">{getSortIcon()}</span>
                      </div>
                    </th>
                  );
                })}
                <th>Actions</th>
              </tr>
            </thead>
            <tbody>
              {sortedItems.map(item => (
                <tr key={item[getterParams[0]]}>
                  {getterParams.map((param, index) => {
                    // Format active status with colors
                    if (param === 'active') {
                      const isActive = getValue(item, param);
                      return (
                        <td key={index}>
                          <span className={`status-badge ${isActive ? 'status-active' : 'status-inactive'}`}>
                            {isActive ? 'Active' : 'Inactive'}
                          </span>
                        </td>
                      );
                    }
                    
                    return (
                      <td key={index} title={String(getValue(item, param))}>
                        {String(getValue(item, param))}
                      </td>
                    );
                  })}
                  <td>
                    <div className="action-buttons">
                      <button onClick={() => onEditClick(item)} title="Edit">
                        <FaEdit/>
                      </button>
                      <button onClick={() => onToggleClick(item)} title="Toggle Active Status">
                        <FaUndo/>
                      </button>
                      {/* Hide delete button for Category management */}
                      {entityName !== "Category" && (
                        <button onClick={() => onDeleteClick(item)} title="Delete">
                          <FaTrash/>
                        </button>
                      )}
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
          </div>
        )}
      </div>
    </div>
  );
};

export default BaseEntityManagement;
