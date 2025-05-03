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
    // For admin endpoints, try to use admin token first
    if (config.url.includes('/users/all') || 
        config.url.includes('/admin')) {
      const adminToken = localStorage.getItem('adminToken');
      if (adminToken) {
        config.headers['Authorization'] = `Bearer ${adminToken}`;
        return config;
      }
    }
    
    // For regular endpoints, use the normal token
    const token = localStorage.getItem('jwtToken') || localStorage.getItem('token');
    if (token) {
      config.headers['Authorization'] = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// Add response interceptor to handle auth errors
api.interceptors.response.use(
  (response) => {
    return response;
  },
  (error) => {
    if (error.response && error.response.status === 403) {
      console.warn('Access forbidden - you may not have the right permissions');
      // You could redirect to login here or show an error message
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
        
        // If user is admin, also save as admin token
        if (response.data.role === 'ADMIN') {
          localStorage.setItem('adminToken', response.data.token);
        }
      }
      
      return response.data;
    } catch (error) {
      if (error.response && error.response.data) {
        throw error.response.data;
      }
      throw 'Error connecting to server';
    }
  },
  
  // Register user
  async register(userData) {
    try {
      console.log("Sending to API:", userData);  // Log before sending
      const response = await api.post('/users/register', userData);
      console.log("API response:", response.data);  // Log after response
      
      // Store token in localStorage
      if (response.data.token) {
        localStorage.setItem('token', response.data.token);
        
        // If registered as admin, also save admin token
        if (response.data.role === 'ADMIN') {
          localStorage.setItem('adminToken', response.data.token);
        }
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
    localStorage.removeItem('adminToken'); // Also remove admin token
    localStorage.removeItem('userData');
    localStorage.removeItem('isLoggedIn');
    localStorage.removeItem('userEmail');
  },
  
  // Check if user is authenticated
  isAuthenticated() {
    return localStorage.getItem('token') !== null;
  },
  
  // Check if user is admin
  isAdmin() {
    const userData = JSON.parse(localStorage.getItem('userData') || '{}');
    return userData.role === 'ADMIN';
  },

  //fetch user
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
  }
};