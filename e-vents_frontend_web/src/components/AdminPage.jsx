import React, { useState, useEffect } from 'react';
import { adminService } from '../services/adminApiService';
import UserEditForm from '../components/UserEditForm';
import { useNavigate } from 'react-router-dom';

const AdminPage = () => {
  const [users, setUsers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [editingUser, setEditingUser] = useState(null);
  const [showEditForm, setShowEditForm] = useState(false);
  const navigate = useNavigate();

  // Check admin access and load users on component mount
  useEffect(() => {
    checkAdminAccess();
    fetchUsers();
  }, []);

  // Verify admin access before proceeding
  const checkAdminAccess = () => {
    const userData = JSON.parse(localStorage.getItem('userData') || '{}');
    const isAdmin = userData.role === 'ADMIN';
    const adminToken = localStorage.getItem('adminToken');
    
    if (!isAdmin || !adminToken) {
      // Redirect non-admin users to login
      alert('Admin access required. Please login with admin credentials.');
      navigate('/login');
    }
  };

  const fetchUsers = async () => {
    try {
      setLoading(true);
      const data = await adminService.getAllUsers();
      setUsers(data);
      setError(null);
    } catch (err) {
      console.error("Failed to fetch users:", err);
      if (err === 'Access denied: Admin privileges required' || 
          (err.response && err.response.status === 403)) {
        // Handle permission error specifically
        setError('Access denied: Admin privileges required. Please login with admin credentials.');
        setTimeout(() => navigate('/login'), 3000); // Redirect after showing error
      } else {
        setError('Failed to load users. ' + (typeof err === 'string' ? err : err.message));
      }
    } finally {
      setLoading(false);
    }
  };

  const handleEdit = (user) => {
    setEditingUser(user);
    setShowEditForm(true);
  };

  const handleDelete = async (userId) => {
    if (window.confirm('Are you sure you want to delete this user?')) {
      try {
        await adminService.deleteUser(userId);
        setUsers(users.filter(user => user.userId !== userId));
        alert('User deleted successfully');
      } catch (err) {
        if (err === 'Access denied: Admin privileges required') {
          alert('Admin privileges required for this action. Please login again.');
          navigate('/login');
        } else {
          alert('Failed to delete user: ' + (typeof err === 'string' ? err : err.message));
        }
      }
    }
  };

  const handleUpdateUser = async (updatedUser) => {
    try {
      const result = await adminService.updateUser(updatedUser.userId, updatedUser);
      setUsers(users.map(user => 
        user.userId === updatedUser.userId ? result : user
      ));
      setShowEditForm(false);
      alert('User updated successfully');
    } catch (err) {
      if (err === 'Access denied: Admin privileges required') {
        alert('Admin privileges required for this action. Please login again.');
        navigate('/login');
      } else {
        alert('Failed to update user: ' + (typeof err === 'string' ? err : err.message));
      }
    }
  };

  const handleCancel = () => {
    setShowEditForm(false);
    setEditingUser(null);
  };

  const handleLogout = () => {
    // Clear all auth tokens and user data
    localStorage.removeItem('token');
    localStorage.removeItem('adminToken');
    localStorage.removeItem('userData');
    localStorage.removeItem('isLoggedIn');
    navigate('/login');
  };

  if (loading) {
    return (
      <div className="admin-page flex justify-center items-center min-h-screen">
        <div className="animate-spin rounded-full h-12 w-12 border-t-2 border-b-2 border-red-600"></div>
      </div>
    );
  }
  
  if (error) {
    return (
      <div className="admin-page min-h-screen flex flex-col items-center justify-center p-4">
        <div className="bg-red-100 border-l-4 border-red-500 text-red-700 p-4 mb-4 w-full max-w-lg">
          <h3 className="font-bold">Error</h3>
          <p>{error}</p>
        </div>
        <div className="flex gap-4">
          <button 
            onClick={fetchUsers} 
            className="bg-blue-500 hover:bg-blue-600 text-white py-2 px-4 rounded"
          >
            Try Again
          </button>
          <button 
            onClick={handleLogout} 
            className="bg-gray-500 hover:bg-gray-600 text-white py-2 px-4 rounded"
          >
            Return to Login
          </button>
        </div>
      </div>
    );
  }

  return (
    <div className="admin-page p-6 max-w-full">
      <div className="flex justify-between items-center mb-6">
        <h1 className="text-2xl font-bold">User Administration</h1>
        <button 
          onClick={handleLogout}
          className="bg-red-500 hover:bg-red-600 text-white px-4 py-2 rounded"
        >
          Logout
        </button>
      </div>
      
      {showEditForm ? (
        <UserEditForm 
          user={editingUser} 
          onUpdate={handleUpdateUser} 
          onCancel={handleCancel} 
        />
      ) : (
        <>
          <div className="overflow-x-auto">
            <table className="user-table min-w-full bg-white border border-gray-200">
              <thead className="bg-gray-100">
                <tr>
                  <th className="py-2 px-3 border-b">ID</th>
                  <th className="py-2 px-3 border-b">Email</th>
                  <th className="py-2 px-3 border-b">Name</th>
                  <th className="py-2 px-3 border-b">Role</th>
                  <th className="py-2 px-3 border-b">Country</th>
                  <th className="py-2 px-3 border-b">Region</th>
                  <th className="py-2 px-3 border-b">City</th>
                  <th className="py-2 px-3 border-b">Actions</th>
                </tr>
              </thead>
              <tbody>
                {users.length === 0 ? (
                  <tr>
                    <td colSpan="8" className="no-data text-center py-4">No users found</td>
                  </tr>
                ) : (
                  users.map(user => (
                    <tr key={user.userId} className="hover:bg-gray-50">
                      <td className="py-2 px-3 border-b">{user.userId}</td>
                      <td className="py-2 px-3 border-b">{user.email}</td>
                      <td className="py-2 px-3 border-b">
                        {user.firstName} {user.lastName}
                      </td>
                      <td className="py-2 px-3 border-b">{user.role?.name || 'USER'}</td>
                      <td className="py-2 px-3 border-b">{user.country || '-'}</td>
                      <td className="py-2 px-3 border-b">{user.region || '-'}</td>
                      <td className="py-2 px-3 border-b">{user.city || '-'}</td>
                      <td className="py-2 px-3 border-b actions flex gap-2">
                        <button 
                          className="edit-btn bg-blue-500 hover:bg-blue-600 text-white py-1 px-3 rounded"
                          onClick={() => handleEdit(user)}
                        >
                          Edit
                        </button>
                        <button 
                          className="delete-btn bg-red-500 hover:bg-red-600 text-white py-1 px-3 rounded"
                          onClick={() => handleDelete(user.userId)}
                        >
                          Delete
                        </button>
                      </td>
                    </tr>
                  ))
                )}
              </tbody>
            </table>
          </div>
          <div className="refresh-container mt-4">
            <button 
              className="refresh-btn bg-green-500 hover:bg-green-600 text-white px-4 py-2 rounded"
              onClick={fetchUsers}
            >
              Refresh Users
            </button>
          </div>
        </>
      )}
    </div>
  );
};

export default AdminPage;