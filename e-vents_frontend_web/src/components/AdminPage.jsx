import { useState, useEffect } from 'react';
import { userService, adminService, errorHandlerService } from "../services/apiService";
import { FaEdit, FaTrash, FaUndo, FaUserPlus, FaSearch } from 'react-icons/fa';
import { motion } from 'framer-motion';
import { useNavigate } from "react-router-dom";
import '../styles/AdminPage.css';

function AdminPage() {
  const [users, setUsers] = useState([]);
  const [roles, setRoles] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [currentUser, setCurrentUser] = useState(null);
  const [isEditModalOpen, setIsEditModalOpen] = useState(false);
  const [isCreateModalOpen, setIsCreateModalOpen] = useState(false);
  const [isDeleteModalOpen, setIsDeleteModalOpen] = useState(false);
  const [searchTerm, setSearchTerm] = useState('');
  const [countries, setCountries] = useState([]);
  const [regions, setRegions] = useState([]);
  const [cities, setCities] = useState([]);
  const navigate = useNavigate();

  // Form state for editing and creating users
  const [formData, setFormData] = useState({
    firstName: '',
    lastName: '',
    email: '',
    password: '',
    contactNumber: '',
    birthdate: '',
    country: '',
    region: '',
    city: '',
    postalCode: '',
    roleId: ''
  });

  useEffect(() => {
    // Check if the current user is an admin
    if (!userService.isAdmin()) {
      setError('Only administrators can access this page');
      return;
    }

    fetchUsers();
    fetchRoles();
    fetchCountries();
  }, []);

  const fetchUsers = async () => {
    try {
      setLoading(true);
      const allUsers = await userService.getAllUsers();
      // Filter out users with admin role before setting state
      const filteredUsers = allUsers.filter(user => 
        !user.role || user.role.name !== 'ADMIN'
      );
      setUsers(filteredUsers);
      setLoading(false);
    } catch (err) {
      const errorMessage = errorHandlerService.handleError(err);
      setError(`Failed to fetch users: ${errorMessage}`);
      console.error('Error fetching users:', err);
      setLoading(false);
    }
  };

  const fetchRoles = async () => {
    try {
      const allRoles = await userService.getAllRoles();
      
      // Filter out admin role for user creation/editing
      const filteredRoles = allRoles.filter(role => role.name !== 'ADMIN');
      setRoles(filteredRoles);
    } catch (err) {
      console.error('Error fetching roles:', err);
      const errorMessage = errorHandlerService.handleError(err);
      setError(`Failed to fetch roles: ${errorMessage}`);
    }
  };

  const fetchCountries = async () => {
    try {
      const availableCountries = await userService.getCountries();
      setCountries(availableCountries);
    } catch (err) {
      console.error('Error fetching countries:', err);
      // Using default fallback from userService
    }
  };

  const fetchRegions = async (country) => {
    try {
      const availableRegions = await userService.getRegions(country);
      setRegions(availableRegions);
    } catch (err) {
      console.error('Error fetching regions:', err);
      // Using default fallback from userService
    }
  };

  const fetchCities = async (country, region) => {
    try {
      const availableCities = await userService.getCities(country, region);
      setCities(availableCities);
    } catch (err) {
      console.error('Error fetching cities:', err);
      // Using default fallback from userService
    }
  };

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setFormData({
      ...formData,
      [name]: value
    });

    // Fetch regions when country changes
    if (name === 'country') {
      fetchRegions(value);
      setFormData(prev => ({
        ...prev,
        region: '',
        city: ''
      }));
    }

    // Fetch cities when region changes
    if (name === 'region') {
      fetchCities(formData.country, value);
      setFormData(prev => ({
        ...prev,
        city: ''
      }));
    }
  };

  const handleEditUser = async (user) => {
    try {
      // Get the full user profile with all details
      const userProfile = await userService.getUserById(user.userId);
      setCurrentUser(userProfile);
      
      setFormData({
        userId: userProfile.userId,
        firstName: userProfile.firstName || '',
        lastName: userProfile.lastName || '',
        email: userProfile.email || '',
        contactNumber: userProfile.contactNumber || '',
        birthdate: userProfile.birthdate ? new Date(userProfile.birthdate).toISOString().split('T')[0] : '',
        country: userProfile.country || '',
        region: userProfile.region || '',
        city: userProfile.city || '',
        postalCode: userProfile.postalCode || '',
        roleId: userProfile.role ? userProfile.role.roleId : ''
      });

      // Fetch regions and cities for the selected country and region
      if (userProfile.country) {
        fetchRegions(userProfile.country);
        if (userProfile.region) {
          fetchCities(userProfile.country, userProfile.region);
        }
      }

      setIsEditModalOpen(true);
    } catch (err) {
      const errorMessage = errorHandlerService.handleError(err);
      setError(`Failed to get user details: ${errorMessage}`);
    }
  };

  const handleDeleteUser = (user) => {
    setCurrentUser(user);
    setIsDeleteModalOpen(true);
  };

  const confirmDeleteUser = async () => {
    try {
      await userService.deleteUser(currentUser.userId);
      setUsers(users.filter(user => user.userId !== currentUser.userId));
      setIsDeleteModalOpen(false);
      setCurrentUser(null);
    } catch (err) {
      const errorMessage = errorHandlerService.handleError(err);
      setError(`Failed to delete user: ${errorMessage}`);
    }
  };

  const handleCreateUser = () => {
    setFormData({
      firstName: '',
      lastName: '',
      email: '',
      password: '',
      contactNumber: '',
      birthdate: '',
      country: '',
      region: '',
      city: '',
      postalCode: '',
      // Remove roleId since it will be assigned a default role
    });
    setIsCreateModalOpen(true);
  };

  const submitCreateUser = async (e) => {
    e.preventDefault();
    try {
      // Prepare the user data without role specification
      const userData = {
        ...formData,
        birthdate: formData.birthdate ? new Date(formData.birthdate) : null,
        // Remove role setting - will use default role from backend
      };
  
      // Use a different method for admin creating users that doesn't affect current login state
      const newUser = await userService.adminCreateUser(userData);
      
      // Only add the new user to the list if they don't have an admin role
      if (!newUser.role || newUser.role.name !== 'ADMIN') {
        setUsers([...users, newUser]);
      }
      
      setIsCreateModalOpen(false);
    } catch (err) {
      const errorMessage = errorHandlerService.handleError(err);
      setError(`Failed to create user: ${errorMessage}`);
    }
  };

  const submitEditUser = async (e) => {
    e.preventDefault();
    try {
      // Prepare the user data with proper format for role
      const userData = {
        ...formData,
        birthdate: formData.birthdate ? new Date(formData.birthdate) : null,
        role: formData.roleId ? { roleId: formData.roleId } : null
      };

      // If we're sending roleId separately from the role object for the API
      if (userData.roleId) {
        delete userData.roleId; // Remove roleId as it's now in the role object
      }

      const updatedUser = await userService.updateUserProfile(userData);
      
      // Check if the updated user now has an admin role
      if (updatedUser.role && updatedUser.role.name === 'ADMIN') {
        // Remove from display if now an admin
        setUsers(users.filter(user => user.userId !== currentUser.userId));
      } else {
        // Update the user in the display
        setUsers(users.map(user => 
          user.userId === currentUser.userId ? updatedUser : user
        ));
      }
      
      setIsEditModalOpen(false);
      setCurrentUser(null);
    } catch (err) {
      const errorMessage = errorHandlerService.handleError(err);
      setError(`Failed to update user: ${errorMessage}`);
    }
  };

  const toggleUserActive = async (user) => {
    try {
      if (user.active) {
        await userService.deactivateUser(user.userId);
      } else {
        await userService.activateUser(user.userId);
      }
      
      // Update the user in the local state
      setUsers(users.map(u => 
        u.userId === user.userId ? { ...u, active: !u.active } : u
      ));
    } catch (err) {
      const errorMessage = errorHandlerService.handleError(err);
      setError(`Failed to update user status: ${errorMessage}`);
    }
  };

  // Filter users based on search term
  const filteredUsers = users.filter(user => {
    const searchTermLower = searchTerm.toLowerCase();
    return (
      (user.firstName && user.firstName.toLowerCase().includes(searchTermLower)) ||
      (user.lastName && user.lastName.toLowerCase().includes(searchTermLower)) ||
      (user.email && user.email.toLowerCase().includes(searchTermLower)) ||
      (user.role && user.role.name && user.role.name.toLowerCase().includes(searchTermLower))
    );
  });

  if (error && error === 'Only administrators can access this page') {
    return (
      <div className="admin-error-container">
        <h2>Access Denied</h2>
        <p>{error}</p>
      </div>
    );
  }

  const handleLogout = () => {
    userService.logout(); 
    // The logout function already handles navigation
  };

  return (
    <div className="admin-container">
      <h1 className="admin-title">User Administration</h1>

      {/* Logout Button */}
      <button 
        onClick={handleLogout} 
        className="logout-button text-black hover:underline bg-transparent border-none cursor-pointer"
      >
        Logout
      </button>
      
      {error && <div className="admin-error">{error}</div>}
      
      <div className="admin-controls">
        <div className="admin-search">
          <FaSearch className="search-icon" />
          <input
            type="text"
            placeholder="Search users..."
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
            className="search-input"
          />
        </div>
        
        <button className="admin-button create-button" onClick={handleCreateUser}>
          <FaUserPlus /> New User
        </button>
        
      </div>
      
      {loading ? (
        <div className="admin-loading">Loading users...</div>
      ) : (
        <div className="admin-table-container">
          <table className="admin-table">
            <thead>
              <tr>
                <th>ID</th>
                <th>Name</th>
                <th>Email</th>
                <th>Contact</th>
                <th>Country</th>
                <th>City</th>
                <th>Role</th>
                <th>Status</th>
                <th>Actions</th>
              </tr>
            </thead>
            <tbody>
              {filteredUsers.length > 0 ? (
                filteredUsers.map(user => (
                  <tr key={user.userId} className={!user.active ? 'inactive-user' : ''}>
                    <td>{user.userId}</td>
                    <td>{`${user.firstName} ${user.lastName}`}</td>
                    <td>{user.email}</td>
                    <td>{user.contactNumber}</td>
                    <td>{user.country}</td>
                    <td>{user.city}</td>
                    <td>{user.role ? user.role.name : 'N/A'}</td>
                    <td>
                      <span className={`status-badge ${user.active ? 'active' : 'inactive'}`}>
                        {user.active ? 'Active' : 'Inactive'}
                      </span>
                    </td>
                    <td className="action-buttons">
                      <button className="icon-button edit" onClick={() => handleEditUser(user)}>
                        <FaEdit />
                      </button>
                      <button className="icon-button delete" onClick={() => handleDeleteUser(user)}>
                        <FaTrash />
                      </button>
                      <button className="icon-button status" onClick={() => toggleUserActive(user)}>
                        <FaUndo />
                      </button>
                    </td>
                  </tr>
                ))
              ) : (
                <tr>
                  <td colSpan="9" className="no-users">
                    No users found
                  </td>
                </tr>
              )}
            </tbody>
          </table>
        </div>
      )}

      {/* Edit User Modal */}
      {isEditModalOpen && (
        <div className="modal-overlay">
          <motion.div 
            className="modal"
            initial={{ opacity: 0, y: -50 }}
            animate={{ opacity: 1, y: 0 }}
            exit={{ opacity: 0, y: 50 }}
          >
            <h2>Edit User</h2>
            <form onSubmit={submitEditUser}>
              <div className="form-row">
                <div className="form-group">
                  <label htmlFor="firstName">First Name</label>
                  <input
                    type="text"
                    id="firstName"
                    name="firstName"
                    value={formData.firstName}
                    onChange={handleInputChange}
                    required
                  />
                </div>
                <div className="form-group">
                  <label htmlFor="lastName">Last Name</label>
                  <input
                    type="text"
                    id="lastName"
                    name="lastName"
                    value={formData.lastName}
                    onChange={handleInputChange}
                    required
                  />
                </div>
              </div>

              <div className="form-row">
                <div className="form-group">
                  <label htmlFor="email">Email</label>
                  <input
                    type="email"
                    id="email"
                    name="email"
                    value={formData.email}
                    onChange={handleInputChange}
                    required
                    readOnly
                  />
                </div>
                <div className="form-group">
                  <label htmlFor="contactNumber">Contact Number</label>
                  <input
                    type="text"
                    id="contactNumber"
                    name="contactNumber"
                    value={formData.contactNumber}
                    onChange={handleInputChange}
                    required
                  />
                </div>
              </div>

              <div className="form-row">
                <div className="form-group">
                  <label htmlFor="birthdate">Birthdate</label>
                  <input
                    type="date"
                    id="birthdate"
                    name="birthdate"
                    value={formData.birthdate}
                    onChange={handleInputChange}
                    required
                  />
                </div>
                <div className="form-group">
                  <label htmlFor="roleId">Role</label>
                  <select
                    id="roleId"
                    name="roleId"
                    value={formData.roleId}
                    onChange={handleInputChange}
                    required
                  >
                    <option value="">Select Role</option>
                    {roles.map(role => (
                      <option key={role.roleId} value={role.roleId}>{role.name}</option>
                    ))}
                  </select>
                </div>
              </div>

              <div className="form-row">
                <div className="form-group">
                  <label htmlFor="country">Country</label>
                  <select
                    id="country"
                    name="country"
                    value={formData.country}
                    onChange={handleInputChange}
                    required
                  >
                    <option value="">Select Country</option>
                    {countries.map(country => (
                      <option key={country} value={country}>{country}</option>
                    ))}
                  </select>
                </div>
                <div className="form-group">
                  <label htmlFor="region">Region</label>
                  <select
                    id="region"
                    name="region"
                    value={formData.region}
                    onChange={handleInputChange}
                    required
                    disabled={!formData.country}
                  >
                    <option value="">Select Region</option>
                    {regions.map(region => (
                      <option key={region} value={region}>{region}</option>
                    ))}
                  </select>
                </div>
              </div>

              <div className="form-row">
                <div className="form-group">
                  <label htmlFor="city">City</label>
                  <select
                    id="city"
                    name="city" 
                    value={formData.city}
                    onChange={handleInputChange}
                    required
                    disabled={!formData.region}
                  >
                    <option value="">Select City</option>
                    {cities.map(city => (
                      <option key={city} value={city}>{city}</option>
                    ))}
                  </select>
                </div>
                <div className="form-group">
                  <label htmlFor="postalCode">Postal Code</label>
                  <input
                    type="text"
                    id="postalCode"
                    name="postalCode"
                    value={formData.postalCode}
                    onChange={handleInputChange}
                    required
                  />
                </div>
              </div>

              <div className="form-buttons">
                <button type="button" onClick={() => setIsEditModalOpen(false)}>Cancel</button>
                <button type="submit" className="primary-button">Save Changes</button>
              </div>
            </form>
          </motion.div>
        </div>
      )}

      {/* Create User Modal */}
      {isCreateModalOpen && (
        <div className="modal-overlay">
          <motion.div 
            className="modal"
            initial={{ opacity: 0, y: -50 }}
            animate={{ opacity: 1, y: 0 }}
            exit={{ opacity: 0, y: 50 }}
          >
            <h2>Create New User</h2>
            <form onSubmit={submitCreateUser}>
              <div className="form-row">
                <div className="form-group">
                  <label htmlFor="firstName">First Name</label>
                  <input
                    type="text"
                    id="firstName"
                    name="firstName"
                    value={formData.firstName}
                    onChange={handleInputChange}
                    required
                  />
                </div>
                <div className="form-group">
                  <label htmlFor="lastName">Last Name</label>
                  <input
                    type="text"
                    id="lastName"
                    name="lastName"
                    value={formData.lastName}
                    onChange={handleInputChange}
                    required
                  />
                </div>
              </div>

              <div className="form-row">
                <div className="form-group">
                  <label htmlFor="email">Email</label>
                  <input
                    type="email"
                    id="email"
                    name="email"
                    value={formData.email}
                    onChange={handleInputChange}
                    required
                  />
                </div>
                <div className="form-group">
                  <label htmlFor="password">Password</label>
                  <input
                    type="password"
                    id="password"
                    name="password"
                    value={formData.password}
                    onChange={handleInputChange}
                    required
                  />
                </div>
              </div>

              <div className="form-row">
                <div className="form-group">
                  <label htmlFor="contactNumber">Contact Number</label>
                  <input
                    type="text"
                    id="contactNumber"
                    name="contactNumber"
                    value={formData.contactNumber}
                    onChange={handleInputChange}
                    required
                  />
                </div>
                <div className="form-group">
                  <label htmlFor="birthdate">Birthdate</label>
                  <input
                    type="date"
                    id="birthdate"
                    name="birthdate"
                    value={formData.birthdate}
                    onChange={handleInputChange}
                    required
                  />
                </div>
              </div>

              {/* Removed role selection from create user form */}

              <div className="form-row">
                <div className="form-group">
                  <label htmlFor="country">Country</label>
                  <select
                    id="country"
                    name="country"
                    value={formData.country}
                    onChange={handleInputChange}
                    required
                  >
                    <option value="">Select Country</option>
                    {countries.map(country => (
                      <option key={country} value={country}>{country}</option>
                    ))}
                  </select>
                </div>
                <div className="form-group">
                  <label htmlFor="region">Region</label>
                  <select
                    id="region"
                    name="region"
                    value={formData.region}
                    onChange={handleInputChange}
                    required
                    disabled={!formData.country}
                  >
                    <option value="">Select Region</option>
                    {regions.map(region => (
                      <option key={region} value={region}>{region}</option>
                    ))}
                  </select>
                </div>
              </div>

              <div className="form-row">
                <div className="form-group">
                  <label htmlFor="city">City</label>
                  <select
                    id="city"
                    name="city"
                    value={formData.city}
                    onChange={handleInputChange}
                    required
                    disabled={!formData.region}
                  >
                    <option value="">Select City</option>
                    {cities.map(city => (
                      <option key={city} value={city}>{city}</option>
                    ))}
                  </select>
                </div>
                <div className="form-group">
                  <label htmlFor="postalCode">Postal Code</label>
                  <input
                    type="text"
                    id="postalCode"
                    name="postalCode"
                    value={formData.postalCode}
                    onChange={handleInputChange}
                    required
                  />
                </div>
              </div>

              <div className="form-buttons">
                <button type="button" onClick={() => setIsCreateModalOpen(false)}>Cancel</button>
                <button type="submit" className="primary-button">Create User</button>
              </div>
            </form>
          </motion.div>
        </div>
      )}

      {/* Delete Confirmation Modal */}
      {isDeleteModalOpen && currentUser && (
        <div className="modal-overlay">
          <motion.div 
            className="modal delete-modal"
            initial={{ opacity: 0, scale: 0.8 }}
            animate={{ opacity: 1, scale: 1 }}
            exit={{ opacity: 0, scale: 0.8 }}
          >
            <h2>Confirm Delete</h2>
            <p>Are you sure you want to delete the user: <strong>{currentUser.firstName} {currentUser.lastName}</strong>?</p>
            <p className="warning">This action cannot be undone.</p>
            
            <div className="form-buttons">
              <button type="button" onClick={() => setIsDeleteModalOpen(false)}>Cancel</button>
              <button type="button" className="delete-button" onClick={confirmDeleteUser}>Delete</button>
            </div>
          </motion.div>
        </div>
      )}
    </div>
  );
}

export default AdminPage;