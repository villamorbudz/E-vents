import React, { useState, useEffect } from 'react';
import { adminService } from '../services/adminApiService';
import UserEditForm from '../components/UserEditForm';

const AdminPage = () => {
  const [users, setUsers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [editingUser, setEditingUser] = useState(null);
  const [showEditForm, setShowEditForm] = useState(false);

  // Load users on component mount
  useEffect(() => {
    fetchUsers();
  }, []);

  const fetchUsers = async () => {
    try {
      setLoading(true);
      const data = await adminService.getAllUsers();
      setUsers(data);
      setError(null);
    } catch (err) {
      setError('Failed to load users. ' + (typeof err === 'string' ? err : err.message));
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
        alert('Failed to delete user: ' + (typeof err === 'string' ? err : err.message));
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
      alert('Failed to update user: ' + (typeof err === 'string' ? err : err.message));
    }
  };

  const handleCancel = () => {
    setShowEditForm(false);
    setEditingUser(null);
  };

  if (loading) return <div className="admin-page">Loading...</div>;
  if (error) return <div className="admin-page error">{error}</div>;

  return (
    <div className="admin-page">
      <h1>User Administration</h1>
      
      {showEditForm ? (
        <UserEditForm 
          user={editingUser} 
          onUpdate={handleUpdateUser} 
          onCancel={handleCancel} 
        />
      ) : (
        <>
          <table className="user-table">
            <thead>
              <tr>
                <th>ID</th>
                <th>Email</th>
                <th>Name</th>
                <th>Role</th>
                <th>Country</th>
                <th>Region</th>
                <th>City</th>
                <th>Actions</th>
              </tr>
            </thead>
            <tbody>
              {users.length === 0 ? (
                <tr>
                  <td colSpan="8" className="no-data">No users found</td>
                </tr>
              ) : (
                users.map(user => (
                  <tr key={user.userId}>
                    <td>{user.userId}</td>
                    <td>{user.email}</td>
                    <td>
                      {user.firstName} {user.lastName}
                    </td>
                    <td>{user.role?.name || 'USER'}</td>
                    <td>{user.country || '-'}</td>
                    <td>{user.region || '-'}</td>
                    <td>{user.city || '-'}</td>
                    <td className="actions">
                      <button 
                        className="edit-btn" 
                        onClick={() => handleEdit(user)}
                      >
                        Edit
                      </button>
                      <button 
                        className="delete-btn" 
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
          <div className="refresh-container">
            <button className="refresh-btn" onClick={fetchUsers}>
              Refresh Users
            </button>
          </div>
        </>
      )}
    </div>
  );
};

export default AdminPage;