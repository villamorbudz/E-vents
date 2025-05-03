// src/services/adminApiService.js
import { api } from './apiService';

const createAdminApi = () => {
    // Get admin token specifically
    const adminToken = localStorage.getItem('adminToken');
    if (!adminToken) {
      throw new Error('Admin authorization token not found');
    }
    
    // Create a new instance with admin token
    const adminApi = api;
    adminApi.interceptors.request.use(
      (config) => {
        config.headers['Authorization'] = `Bearer ${adminToken}`;
        return config;
      },
      (error) => {
        return Promise.reject(error);
      }
    );
    
    return adminApi;
  };

  export const adminService = {
    // Get all users
    async getAllUsers() {
      try {
        // Use admin token specifically for admin operations
        const adminApi = createAdminApi();
        const response = await adminApi.get('/users/all');
        return response.data;
      } catch (error) {
        console.error('Error fetching users:', error);
        if (error.response && error.response.status === 403) {
          throw 'Access denied: Admin privileges required';
        }
        if (error.response && error.response.data) {
          throw error.response.data;
        }
        throw 'Error connecting to server';
      }
    },
    
    // Get user by ID
    async getUserById(userId) {
      try {
        const adminApi = createAdminApi();
        const response = await adminApi.get(`/users/${userId}`);
        return response.data;
      } catch (error) {
        console.error('Error fetching user:', error);
        if (error.response && error.response.status === 403) {
          throw 'Access denied: Admin privileges required';
        }
        if (error.response && error.response.data) {
          throw error.response.data;
        }
        throw 'Error fetching user details';
      }
    },
    
    // Update user
    async updateUser(userId, userData) {
      try {
        const adminApi = createAdminApi();
        const response = await adminApi.put(`/users/${userId}`, userData);
        return response.data;
      } catch (error) {
        console.error('Error updating user:', error);
        if (error.response && error.response.status === 403) {
          throw 'Access denied: Admin privileges required';
        }
        if (error.response && error.response.data) {
          throw error.response.data;
        }
        throw 'Error updating user';
      }
    },
    
    // Delete user
    async deleteUser(userId) {
      try {
        const adminApi = createAdminApi();
        const response = await adminApi.delete(`/users/${userId}`);
        return response.data;
      } catch (error) {
        console.error('Error deleting user:', error);
        if (error.response && error.response.status === 403) {
          throw 'Access denied: Admin privileges required';
        }
        if (error.response && error.response.data) {
          throw error.response.data;
        }
        throw 'Error deleting user';
      }
    },
    
    // Check admin status
    isAdmin() {
      const userData = JSON.parse(localStorage.getItem('userData') || '{}');
      return userData.role === 'ADMIN';
    }
  };