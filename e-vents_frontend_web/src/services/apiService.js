// src/services/apiService.js

// Create an axios instance
import axios from 'axios';

const API_URL = 'http://localhost:8080/api';

// Create axios instance
export const api = axios.create({
  baseURL: API_URL,
  headers: {
    'Content-Type': 'application/json'
  }
});

// Add interceptor to add JWT token to requests
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token');
    if (token) {
      config.headers['Authorization'] = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// Add response interceptor to handle common errors
api.interceptors.response.use(
  (response) => {
    return response;
  },
  (error) => {
    // Handle 401 Unauthorized errors
    if (error.response && error.response.status === 401) {
      // Clear localStorage and redirect to login
      localStorage.removeItem('token');
      localStorage.removeItem('userData');
      localStorage.removeItem('isLoggedIn');
      
      // Only redirect if we're not already on the login page
      if (!window.location.pathname.includes('/login')) {
        window.location.href = '/login';
      }
    }
    
    return Promise.reject(error);
  }
);

export const userService = {
  // Login - returns JWT token
  async login(email, password) {
    try {
      const response = await api.post('/users/login', null, {
        params: { email, password }
      });
      
      // Store token in localStorage
      if (response.data.token) {
        localStorage.setItem('token', response.data.token);
      }
      
      return response.data;
    } catch (error) {
      if (error.response && error.response.data) {
        throw error.response.data;
      }
      throw 'Error connecting to server';
    }
  },
  
  async register(userData) {
    try {
      console.log("Sending to API:", userData);  // Log before sending
      const response = await api.post('/users/register', userData);
      console.log("API response:", response.data);  // Log after response
      
      // Store token in localStorage
      if (response.data.token) {
        localStorage.setItem('token', response.data.token);
      }
      
      return response.data;
    } catch (error) {
      console.error("Registration error:", error);  // Enhanced error logging
      if (error.response && error.response.data) {
        throw error.response.data;
      }
      throw 'Error registering user';
    }
  },
  
  // Check if email exists
  async checkEmailExists(email) {
    try {
      const response = await api.get(`/users/exists?email=${email}`);
      return response.data.exists;
    } catch (error) {
      console.error('Error checking email:', error);
      throw error;
    }
  },
  
  // Get countries
  async getCountries() {
    try {
      const response = await api.get('/users/countries');
      return response.data;
    } catch (error) {
      console.error('Error fetching countries:', error);
      return ['USA', 'Canada', 'UK']; // Fallback
    }
  },
  
  // Get regions for country
  async getRegions(country) {
    try {
      const response = await api.get(`/users/regions/${country}`);
      return response.data;
    } catch (error) {
      console.error('Error fetching regions:', error);
      return ['Region 1', 'Region 2']; // Fallback
    }
  },
  
  // Get cities for region
  async getCities(country, region) {
    try {
      const response = await api.get(`/users/cities/${country}/${region}`);
      return response.data;
    } catch (error) {
      console.error('Error fetching cities:', error);
      return ['City 1', 'City 2']; // Fallback
    }
  },
  
  // Logout - remove token
  logout() {
    localStorage.removeItem('token');
    localStorage.removeItem('userData');
    localStorage.removeItem('isLoggedIn');
    localStorage.removeItem('userEmail');
    
    // Redirect to login page
    window.location.href = '/login';
  },
  
  // Check if user is authenticated
  isAuthenticated() {
    return localStorage.getItem('token') !== null;
  },

  // fetch user
  async getUserProfile() {
    try {
      // Get user ID from stored data or token payload
      const userData = JSON.parse(localStorage.getItem('userData')) || {};
      const userId = userData.userId;
      
      if (!userId) {
        throw 'User ID not found';
      }
      
      const response = await api.get(`/users/${userId}`);
      return response.data;
    } catch (error) {
      console.error('Error fetching user profile:', error);
      throw error;
    }
  },
  
  // update user
  async updateUserProfile(profileData) {
    try {
      const userId = profileData.userId;
      if (!userId) {
        throw 'User ID not found';
      }
      
      const response = await api.put(`/users/${userId}`, profileData);
      return response.data;
    } catch (error) {
      console.error('Error updating user profile:', error);
      throw error;
    }
  },
  
  // Get all users (admin only)
  async getAllUsers() {
    try {
      const response = await api.get('/users/all');
      return response.data;
    } catch (error) {
      console.error('Error fetching all users:', error);
      throw error;
    }
  },
  
  // Get user by ID (admin only)
  async getUserById(userId) {
    try {
      const response = await api.get(`/users/${userId}`);
      return response.data;
    } catch (error) {
      console.error(`Error fetching user #${userId}:`, error);
      throw error;
    }
  },
  
  // Delete user (admin only)
  async deleteUser(userId) {
    try {
      const response = await api.delete(`/users/${userId}`);
      return response.data;
    } catch (error) {
      console.error(`Error deleting user #${userId}:`, error);
      throw error;
    }
  },
  
  // Activate user (admin only)
  async activateUser(userId) {
    try {
      const response = await api.put(`/users/${userId}/activate`);
      return response.data;
    } catch (error) {
      console.error(`Error activating user #${userId}:`, error);
      throw error;
    }
  },
  
  // Deactivate user (admin only)
  async deactivateUser(userId) {
    try {
      const response = await api.put(`/users/${userId}/deactivate`);
      return response.data;
    } catch (error) {
      console.error(`Error deactivating user #${userId}:`, error);
      throw error;
    }
  },
  
  // Change user password
  async changePassword(userId, currentPassword, newPassword) {
    try {
      const response = await api.put(`/users/${userId}/password`, {
        currentPassword,
        newPassword
      });
      return response.data;
    } catch (error) {
      console.error('Error changing password:', error);
      throw error;
    }
  },
  
  // Request password reset
  async requestPasswordReset(email) {
    try {
      const response = await api.post('/users/password-reset-request', { email });
      return response.data;
    } catch (error) {
      console.error('Error requesting password reset:', error);
      throw error;
    }
  },
  
  // Reset password with token
  async resetPassword(token, newPassword) {
    try {
      const response = await api.post('/users/password-reset', {
        token,
        newPassword
      });
      return response.data;
    } catch (error) {
      console.error('Error resetting password:', error);
      throw error;
    }
  },
  
  // Get user role
  getUserRole() {
    try {
      const userData = JSON.parse(localStorage.getItem('userData')) || {};
      return userData.role || null;
    } catch (error) {
      console.error('Error getting user role:', error);
      return null;
    }
  },
  
  // Check if user is admin
  isAdmin() {
    return this.getUserRole() === 'ADMIN';
  }
};

// Admin Dashboard Service
export const adminService = {
  // Get dashboard stats
  async getDashboardStats() {
    try {
      const response = await api.get('/admin/dashboard');
      return response.data;
    } catch (error) {
      console.error('Error fetching dashboard stats:', error);
      throw error;
    }
  },
  
  // Get users activity logs
  async getUsersActivityLogs(page = 0, size = 10) {
    try {
      const response = await api.get(`/admin/activity-logs?page=${page}&size=${size}`);
      return response.data;
    } catch (error) {
      console.error('Error fetching activity logs:', error);
      throw error;
    }
  },
  
  // Get user activity logs
  async getUserActivityLogs(userId, page = 0, size = 10) {
    try {
      const response = await api.get(`/admin/users/${userId}/activity-logs?page=${page}&size=${size}`);
      return response.data;
    } catch (error) {
      console.error(`Error fetching activity logs for user #${userId}:`, error);
      throw error;
    }
  }
};

// Error Handling Service
export const errorHandlerService = {
  // Handle common errors
  handleError(error) {
    if (!error) {
      return 'An unknown error occurred';
    }
    
    if (typeof error === 'string') {
      return error;
    }
    
    if (error.response) {
      // Server responded with an error
      const { status, data } = error.response;
      
      if (status === 400) {
        return data.message || 'Invalid request';
      }
      
      if (status === 401) {
        return 'Authentication required. Please log in again.';
      }
      
      if (status === 403) {
        return 'You do not have permission to perform this action';
      }
      
      if (status === 404) {
        return 'The requested resource was not found';
      }
      
      if (status === 500) {
        return 'Server error. Please try again later';
      }
      
      return data.message || `Error: ${status}`;
    }
    
    if (error.request) {
      // Request was made but no response received
      return 'No response from server. Please check your internet connection';
    }
    
    // Something else happened in making the request
    return error.message || 'An error occurred while processing your request';
  }
};

// Export everything
export default {
  api,
  userService,
  adminService,
  errorHandlerService
};