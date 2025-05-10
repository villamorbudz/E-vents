import React, { useState, useEffect } from 'react';
import AdminSidebar from '../AdminSidebar';
import { FaEdit, FaTrash, FaUndo, FaPlus, FaTimes } from 'react-icons/fa';
import '../../styles/AdminEntityPage.css';

const BaseEntityManagement = ({ 
  title, 
  entityName, 
  fetchItems, 
  displayFields, 
  handleCreate, 
  handleEdit, 
  handleToggle, 
  handleDelete 
}) => {
  const [items, setItems] = useState([]);
  const [loading, setLoading] = useState(true);
  const [modalOpen, setModalOpen] = useState(false);
  const [modalItem, setModalItem] = useState({});
  const [modalMode, setModalMode] = useState('view');
  const [formData, setFormData] = useState({});
  const [error, setError] = useState(null);
  const [success, setSuccess] = useState(null);

  useEffect(() => {
    const loadItems = async () => {
      setLoading(true);
      try {
        const data = await fetchItems();
        setItems(data);
      } catch (e) {
        console.error(e);
      }
      setLoading(false);
    };
    loadItems();
  }, [fetchItems]);
  
  // Initialize form data when modal opens
  useEffect(() => {
    if (modalOpen && modalItem) {
      setFormData({...modalItem});
      setError(null);
      setSuccess(null);
    }
  }, [modalOpen, modalItem]);

  const handleInputChange = (e) => {
    const { name, value, type, checked } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: type === 'checkbox' ? checked : value
    }));
  };

  const handleModalAction = async (action) => {
    try {
      setError(null);
      let result;
      // Get the ID field name from the first displayField (assuming it's the ID)
      const idField = displayFields[0];
      
      switch (action) {
        case 'create':
          result = await handleCreate(formData);
          if (result) {
            setItems([...items, result]);
            setSuccess(`${entityName} created successfully`);
          }
          break;
        case 'edit':
          result = await handleEdit(formData);
          if (result) {
            setItems(items.map(item => item[idField] === modalItem[idField] ? result : item));
            setSuccess(`${entityName} updated successfully`);
          }
          break;
        case 'toggle':
          result = await handleToggle(modalItem);
          if (result) {
            setItems(items.map(item => item[idField] === modalItem[idField] ? result : item));
            const action = modalItem.active ? 'deactivated' : 'activated';
            setSuccess(`${entityName} ${action} successfully`);
          }
          break;
        case 'delete':
          await handleDelete(modalItem);
          setItems(items.filter(item => item[idField] !== modalItem[idField]));
          setSuccess(`${entityName} deleted successfully`);
          break;
        default:
          break;
      }
      setModalOpen(false);
    } catch (error) {
      console.error('Error performing action:', error);
      setError(error.response?.data || `Error performing ${action} operation`);
    }
  };

  return (
    <div className="admin-layout">
      <AdminSidebar />
      <div className="admin-container" style={{ marginLeft: '260px' }}>
        <h1 className="admin-title">{title}</h1>
        <button 
          className="admin-button create-button" 
          onClick={() => { setModalItem({}); setModalMode('create'); setModalOpen(true); }}
        >
          <FaPlus /> New {entityName}
        </button>
        {loading ? (
          <div>Loading...</div>
        ) : (
          <table className="admin-table">
            <thead>
              <tr>
                {displayFields.map(f => <th key={f}>{f}</th>)}
                <th>Actions</th>
              </tr>
            </thead>
            <tbody>
              {items.map(item => (
                <tr key={item[displayFields[0]]}>
                  {displayFields.map(f => <td key={f}>{String(item[f] ?? '')}</td>)}
                  <td className="action-buttons">
                    <button onClick={() => { setModalItem(item); setModalMode('edit'); setModalOpen(true); }}>
                      <FaEdit/>
                    </button>
                    <button onClick={() => { setModalItem(item); setModalMode('toggle'); setModalOpen(true); }}>
                      <FaUndo/>
                    </button>
                    <button onClick={() => { setModalItem(item); setModalMode('delete'); setModalOpen(true); }}>
                      <FaTrash/>
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
        {modalOpen && (
          <div className="modal-overlay">
            <div className="modal">
              <div className="modal-header">
                <h2>{modalMode.charAt(0).toUpperCase() + modalMode.slice(1)} {entityName}</h2>
                <button className="close-button" onClick={() => setModalOpen(false)}>
                  <FaTimes />
                </button>
              </div>
              
              {error && <div className="error-message">{error}</div>}
              
              {modalMode === 'delete' ? (
                <div className="confirmation-message">
                  <p>Are you sure you want to delete this {entityName.toLowerCase()}?</p>
                  <pre>{JSON.stringify(modalItem, null, 2)}</pre>
                </div>
              ) : modalMode === 'toggle' ? (
                <div className="confirmation-message">
                  <p>
                    Are you sure you want to {modalItem.active ? 'deactivate' : 'activate'} this {entityName.toLowerCase()}?
                  </p>
                  <pre>{JSON.stringify(modalItem, null, 2)}</pre>
                </div>
              ) : (
                <div className="entity-form">
                  {typeof renderForm === 'function'
                    ? renderForm(formData, handleInputChange, setFormData, modalMode)
                    : displayFields.map(field => (
                      <div className="form-group" key={field}>
                        <label htmlFor={field}>{field}</label>
                        {field.toLowerCase().includes('active') ? (
                          <input
                            type="checkbox"
                            id={field}
                            name={field}
                            checked={formData[field] || false}
                            onChange={handleInputChange}
                            disabled={modalMode === 'view'}
                          />
                        ) : (
                          <input
                            type="text"
                            id={field}
                            name={field}
                            value={formData[field] || ''}
                            onChange={handleInputChange}
                            disabled={modalMode === 'view' || (modalMode === 'edit' && field === displayFields[0])}
                          />
                        )}
                      </div>
                    ))}
                </div>
              )}
              
              <div className="modal-buttons">
                {modalMode !== 'view' && (
                  <button 
                    className="primary-button" 
                    onClick={() => handleModalAction(modalMode)}
                  >
                    {modalMode === 'create' ? 'Create' : 
                     modalMode === 'edit' ? 'Update' : 
                     modalMode === 'delete' ? 'Delete' : 'Confirm'}
                  </button>
                )}
                <button 
                  className="secondary-button" 
                  onClick={() => setModalOpen(false)}
                >
                  {modalMode === 'view' ? 'Close' : 'Cancel'}
                </button>
              </div>
            </div>
          </div>
        )}
      </div>
    </div>
  );
};

export default BaseEntityManagement;
