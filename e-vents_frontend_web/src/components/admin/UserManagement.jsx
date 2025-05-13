import React, { useState, useEffect } from 'react';
import BaseEntityManagement from './BaseEntityManagement';
import EntityModal from './EntityModal';
import { userService, roleService } from '../../services/apiService';
import './UserManagementTable.css'; // Import the custom CSS for user management table

const UserManagement = () => {
  // Display fields for the table headers
  const displayFields = ['ID', 'First Name', 'Last Name', 'Email', 'Birthdate', 'Contact Number', 'Country', 'Role', 'Created Date', 'Active'];
  
  // Getter parameters that match the attribute names in the data
  const getterParams = ['userId', 'firstName', 'lastName', 'email', 'birthdate', 'contactNumber', 'country', 'role.name', 'dateCreated', 'active'];
  
  const [modalOpen, setModalOpen] = useState(false);
  const [modalMode, setModalMode] = useState('create');
  const [selectedItem, setSelectedItem] = useState({});
  const [formData, setFormData] = useState({});
  const [error, setError] = useState(null);
  const [successMessage, setSuccessMessage] = useState(null);
  const [refreshTrigger, setRefreshTrigger] = useState(0);
  const [validationErrors, setValidationErrors] = useState({});
  
  // State for select boxes
  const [countries, setCountries] = useState([]);
  const [roles, setRoles] = useState([]);
  const [isLoading, setIsLoading] = useState(false);

  // State to store the user role ID
  const [userRoleId, setUserRoleId] = useState('');

  // Fetch initial data for select boxes
  useEffect(() => {
    const fetchInitialData = async () => {
      try {
        const countriesData = await userService.getCountries();
        setCountries(countriesData);
        
        const rolesData = await userService.getAllRoles();
        console.log('Available roles:', rolesData);
        setRoles(rolesData);
        
        // Find the user role and store its ID
        const userRole = rolesData.find(role => 
          role.name.toLowerCase() === 'user' || 
          role.name.toLowerCase() === 'attendee'
        );
        
        if (userRole) {
          console.log('Found user role:', userRole);
          setUserRoleId(userRole.roleId);
        } else {
          console.log('User role not found in:', rolesData);
        }
      } catch (error) {
        console.error('Error fetching initial data:', error);
      }
    };
    
    fetchInitialData();
  }, []);


  
  const fetchUsers = async () => {
    try {
      const data = await userService.getAllUsers();
      return data;
    } catch (error) {
      console.error('Error fetching users:', error);
      return [];
    }
  };
  
  const handleInputChange = (e) => {
    const { name, value, type, checked } = e.target;
    
    // Special handling for role selection
    if (name === 'role') {
      console.log('Role selected:', value);
      // Store the role value directly as a string
      setFormData(prev => ({
        ...prev,
        [name]: value
      }));
    } else {
      setFormData(prev => ({
        ...prev,
        [name]: type === 'checkbox' ? checked : value
      }));
    }
  };

  const handleCreateClick = () => {
    setModalMode('create');
    setSelectedItem({});
    setFormData({
      firstName: '',
      lastName: '',
      email: '',
      birthdate: '',
      contactNumber: '',
      country: '',
      region: '',
      city: '',
      postalCode: '',
      role: userRoleId, // Set default role to user/attendee
      password: ''
    });
    console.log('Setting default role for new user:', userRoleId);
    setError(null);
    setSuccessMessage(null);
    setValidationErrors({});
    setModalOpen(true);
  };

  const handleEditClick = (item) => {
    setModalMode('edit');
    setSelectedItem(item);
    
    // Initialize form data with selected item data
    const formattedItem = {
      ...item,
      role: item.role?.roleId || '',  // Store just the roleId as a string for the select box
      // Format birthdate if needed
      birthdate: item.birthdate ? new Date(item.birthdate).toISOString().split('T')[0] : '',
      // Ensure password is not fetched
      password: ''
    };
    
    console.log('Editing user with role:', item.role, 'formatted as:', formattedItem.role);
    
    setFormData(formattedItem);
    setError(null);
    setSuccessMessage(null);
    setValidationErrors({});
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
    
    // Validate all required fields except password
    if (!formData.firstName || formData.firstName.trim() === '') {
      errors.firstName = 'First name is required';
    }
    
    if (!formData.lastName || formData.lastName.trim() === '') {
      errors.lastName = 'Last name is required';
    }
    
    if (!formData.email || formData.email.trim() === '') {
      errors.email = 'Email is required';
    } else if (!/^\S+@\S+\.\S+$/.test(formData.email)) {
      errors.email = 'Email format is invalid';
    }
    
    if (!formData.birthdate) {
      errors.birthdate = 'Birthdate is required';
    }
    
    if (!formData.contactNumber || formData.contactNumber.trim() === '') {
      errors.contactNumber = 'Contact number is required';
    }
    
    if (!formData.country || formData.country.trim() === '') {
      errors.country = 'Country is required';
    }
    
    if (!formData.role) {
      errors.role = 'Role is required';
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
      
      // Prepare data
      const userData = {
        ...formData
      };
      
      // Always ensure role is properly formatted as an object with roleId
      if (userData.role) {
        if (typeof userData.role === 'string') {
          // If no role is selected and it's a create operation, use the default user role
          if (userData.role === '' && modalMode === 'create') {
            userData.role = userRoleId;
            console.log('Using default role ID:', userRoleId);
          } else {
            // Just keep the role ID as a string/number - the service will format it
            console.log('Using selected role ID:', userData.role);
          }
        } else if (typeof userData.role === 'object' && userData.role.roleId) {
          // If role is already an object, just use the roleId
          userData.role = userData.role.roleId;
          console.log('Extracted role ID from object:', userData.role);
        }
      } else if (modalMode === 'create') {
        // If no role is provided for a new user, use the default role
        userData.role = userRoleId;
        console.log('No role provided, using default role ID:', userRoleId);
      }
      
      switch (modalMode) {
        case 'create':
          // Set default password if none is provided
          if (!userData.password || userData.password.trim() === '') {
            userData.password = '12345678';
          }
          result = await userService.adminCreateUser(userData);
          successMsg = `User '${userData.firstName} ${userData.lastName}' created successfully!`;
          // Exit form on successful creation
          setModalOpen(false);
          setRefreshTrigger(prev => prev + 1);
          break;
        case 'edit':
          // Don't send empty password
          if (!userData.password || userData.password.trim() === '') {
            delete userData.password;
          }
          result = await userService.updateUser(selectedItem.userId, userData);
          
          // Simple success message for the edit operation
          successMsg = `User '${userData.firstName} ${userData.lastName}' updated successfully!`;
          break;
        case 'toggle':
          if (selectedItem.active) {
            result = await userService.deactivateUser(selectedItem.userId);
            successMsg = `User '${selectedItem.firstName} ${selectedItem.lastName}' deactivated successfully!`;
          } else {
            result = await userService.activateUser(selectedItem.userId);
            successMsg = `User '${selectedItem.firstName} ${selectedItem.lastName}' activated successfully!`;
          }
          break;
        case 'delete':
          await userService.deleteUser(selectedItem.userId);
          successMsg = `User '${selectedItem.firstName} ${selectedItem.lastName}' deleted successfully!`;
          break;
        default:
          break;
      }
      
      // Set success message
      setSuccessMessage(successMsg);
      
      // For toggle and delete operations, close modal and refresh data immediately
      if (modalMode === 'toggle' || modalMode === 'delete') {
        setModalOpen(false);
        setRefreshTrigger(prev => prev + 1);
      }
      
      // For edit mode, show success message for 1.5 seconds then close modal
      if (modalMode === 'edit') {
        // Update the selectedItem to reflect the changes
        setSelectedItem({
          ...selectedItem,
          ...userData,
          role: typeof userData.role === 'object' ? userData.role : { roleId: userData.role }
        });
        
        // Auto-close the modal after 1.5 seconds
        setTimeout(() => {
          setModalOpen(false);
          setRefreshTrigger(prev => prev + 1);
        }, 1500);
      }
    } catch (error) {
      console.error(`Error ${modalMode} user:`, error);
      
      // Simplified error message
      let errorMessage;
      
      if (error.response) {
        // For HTTP errors, use a simple message
        errorMessage = 'An error occurred while processing your request. Please try again.';
      } else if (error.message) {
        // For network errors
        errorMessage = 'Connection error. Please check your network and try again.';
      } else {
        // Fallback for unknown errors
        errorMessage = 'Unknown error occurred. Please try again later.';
      }
      
      setError(errorMessage);
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <>
      <BaseEntityManagement
        title="User Management"
        entityName="User"
        fetchItems={fetchUsers}
        displayFields={displayFields}
        getterParams={getterParams}
        onCreateClick={handleCreateClick}
        onEditClick={handleEditClick}
        onToggleClick={handleToggleClick}
        onDeleteClick={handleDeleteClick}
        refreshTrigger={refreshTrigger}
        className="user-management-table"
      />
      
      <EntityModal
        isOpen={modalOpen}
        onClose={() => {
          setModalOpen(false);
          setRefreshTrigger(prev => prev + 1); // Refresh table when modal is closed
          setValidationErrors({}); // Clear validation errors when closing
        }}
        title={`${modalMode.charAt(0).toUpperCase() + modalMode.slice(1)} User`}
        mode={modalMode}
        onConfirm={handleConfirm}
        error={error}
        successMessage={successMessage}
        isLoading={isLoading}
        item={selectedItem}
      >
        {(modalMode === 'create' || modalMode === 'edit') && (
          <>
            <div className="form-row">
              <div className="form-group">
                <label htmlFor="firstName">First Name *</label>
                <input
                  type="text"
                  id="firstName"
                  name="firstName"
                  value={formData.firstName || ''}
                  onChange={handleInputChange}
                  className={validationErrors.firstName ? 'input-error' : ''}
                  required
                />
                {validationErrors.firstName && (
                  <div className="validation-error">{validationErrors.firstName}</div>
                )}
              </div>
              
              <div className="form-group">
                <label htmlFor="lastName">Last Name *</label>
                <input
                  type="text"
                  id="lastName"
                  name="lastName"
                  value={formData.lastName || ''}
                  onChange={handleInputChange}
                  className={validationErrors.lastName ? 'input-error' : ''}
                  required
                />
                {validationErrors.lastName && (
                  <div className="validation-error">{validationErrors.lastName}</div>
                )}
              </div>
            </div>
            
            <div className="form-group">
              <label htmlFor="email">Email *</label>
              <input
                type="email"
                id="email"
                name="email"
                value={formData.email || ''}
                onChange={handleInputChange}
                className={validationErrors.email ? 'input-error' : ''}
                required
              />
              {validationErrors.email && (
                <div className="validation-error">{validationErrors.email}</div>
              )}
            </div>
            
            <div className="form-row">
              <div className="form-group">
                <label htmlFor="birthdate">Birthdate *</label>
                <input
                  type="date"
                  id="birthdate"
                  name="birthdate"
                  value={formData.birthdate || ''}
                  onChange={handleInputChange}
                  className={validationErrors.birthdate ? 'input-error' : ''}
                  required
                />
                {validationErrors.birthdate && (
                  <div className="validation-error">{validationErrors.birthdate}</div>
                )}
              </div>
              
              <div className="form-group">
                <label htmlFor="contactNumber">Contact Number *</label>
                <input
                  type="tel"
                  id="contactNumber"
                  name="contactNumber"
                  value={formData.contactNumber || ''}
                  onChange={handleInputChange}
                  className={validationErrors.contactNumber ? 'input-error' : ''}
                  required
                />
                {validationErrors.contactNumber && (
                  <div className="validation-error">{validationErrors.contactNumber}</div>
                )}
              </div>
            </div>
            
            <div className="form-row">
              <div className="form-group">
                <label htmlFor="country">Country *</label>
                <select
                  id="country"
                  name="country"
                  value={formData.country || ''}
                  onChange={handleInputChange}
                  className={validationErrors.country ? 'input-error' : ''}
                  required
                >
                  <option value="">Select Country</option>
                  {countries.map(country => (
                    <option key={country} value={country}>{country}</option>
                  ))}
                </select>
                {validationErrors.country && (
                  <div className="validation-error">{validationErrors.country}</div>
                )}
              </div>
            </div>
            
            <div className="form-group">
              <label htmlFor="role">Role *</label>
              <select
                id="role"
                name="role"
                value={formData.role || ''}
                onChange={handleInputChange}
                className={validationErrors.role ? 'input-error' : ''}
                required
              >
                <option value="">Select Role</option>
                {roles.map(role => (
                  <option key={role.roleId} value={role.roleId}>{role.name}</option>
                ))}
              </select>
              {validationErrors.role && (
                <div className="validation-error">{validationErrors.role}</div>
              )}
            </div>
            
            <div className="form-group">
              <label htmlFor="password">
                {modalMode === 'create' ? 'Password (leave blank for default: 12345678)' : 'Password (leave blank to keep current)'}
              </label>
              <input
                type="password"
                id="password"
                name="password"
                value={formData.password || ''}
                onChange={handleInputChange}
                required={false}
              />
            </div>
          </>
        )}
      </EntityModal>
    </>
  );
};

export default UserManagement;
