import React from 'react';
import { FaTimes, FaSpinner } from 'react-icons/fa';
import './AdminComponents.css';

const EntityModal = ({ 
  isOpen, 
  onClose, 
  title, 
  mode, 
  children, 
  onConfirm, 
  confirmText, 
  error,
  successMessage,
  isLoading,
  item
}) => {
  if (!isOpen) return null;
  
  return (
    <div className="modal-overlay">
      <div className="modal">
        <div className="modal-header">
          <h2>{title}</h2>
          <button className="close-button" onClick={onClose}>
            <FaTimes />
          </button>
        </div>
        
        {(mode === 'delete' || mode === 'toggle') ? (
          <div className="confirmation-message">
            <p>
              {mode === 'delete' 
                ? 'Are you sure you want to delete this item?' 
                : `Are you sure you want to ${item?.active ? 'deactivate' : 'activate'} this item?`}
            </p>
            <pre>{JSON.stringify(item, null, 2)}</pre>
          </div>
        ) : (
          <div className="entity-form">
            {children}
          </div>
        )}
        
        {error && <div className="error-message">{error}</div>}
        {successMessage && <div className="success-message">{successMessage}</div>}
        
        <div className="modal-buttons">
          <button 
            className="primary-button" 
            onClick={onConfirm}
            disabled={isLoading}
          >
            {isLoading ? (
              <>
                <FaSpinner className="spinner" />
                {' Loading...'}
              </>
            ) : (
              confirmText || (mode === 'create' ? 'Create' : mode === 'edit' ? 'Update' : mode === 'delete' ? 'Delete' : 'Confirm')
            )}
          </button>
          <button 
            className="secondary-button" 
            onClick={onClose}
            disabled={isLoading}
          >
            Cancel
          </button>
        </div>
      </div>
    </div>
  );
};

export default EntityModal;
