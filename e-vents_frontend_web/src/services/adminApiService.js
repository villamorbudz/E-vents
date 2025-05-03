// src/services/adminApiService.js
import { api } from './apiService';

export const adminService = {
  // Get all users
  async getAllUsers() {
    try {
      const response = await api.get('/users/all');
      return response.data;
    } catch (error) {
      console.error('Error fetching users:', error);
      if (error.response && error.response.data) {
        throw error.response.data;
      }
      throw 'Error connecting to server';
    }
  },

  // Get user by ID
  async getUserById(userId) {
    try {
      const response = await api.get(`/users/${userId}`);
      return response.data;
    } catch (error) {
      console.error('Error fetching user:', error);
      if (error.response && error.response.data) {
        throw error.response.data;
      }
      throw 'Error fetching user details';
    }
  },

  // Update user
  async updateUser(userId, userData) {
    try {
      const response = await api.put(`/users/${userId}`, userData);
      return response.data;
    } catch (error) {
      console.error('Error updating user:', error);
      if (error.response && error.response.data) {
        throw error.response.data;
      }
      throw 'Error updating user';
    }
  },

  // Delete user (you'll need to add this endpoint to your backend)
  async deleteUser(userId) {
    try {
      const response = await api.delete(`/users/${userId}`);
      return response.data;
    } catch (error) {
      console.error('Error deleting user:', error);
      if (error.response && error.response.data) {
        throw error.response.data;
      }
      throw 'Error deleting user';
    }
  }
};