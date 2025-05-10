// src/services/apiService.js
// Add the missing getCurrentUser method

import axios from 'axios';

const API_URL = 'http://localhost:8080/api';

// Axios instance
export const api = axios.create({
  baseURL: API_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Request interceptor to attach JWT
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token');
    if (token) {
      config.headers['Authorization'] = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

// Response interceptor for handling errors
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      localStorage.removeItem('token');
      localStorage.removeItem('userData');
      localStorage.removeItem('isLoggedIn');
      if (!window.location.pathname.includes('/login')) {
        window.location.href = '/login';
      }
    }
    return Promise.reject(error);
  }
);

// User Service
export const userService = {
  // Add the missing getCurrentUser method
  getCurrentUser() {
    const userData = localStorage.getItem('userData');
    if (userData) {
      return JSON.parse(userData);
    }
    return null;
  },

  async login(email, password) {
    try {
      const response = await api.post('/users/login', null, {
        params: { email, password }
      });

      const { token, ...userData } = response.data;
      if (token) {
        localStorage.setItem('token', token);
        localStorage.setItem('userData', JSON.stringify(userData));
        localStorage.setItem('isLoggedIn', 'true');
        localStorage.setItem('userEmail', email);
      }

      return response.data;
    } catch (error) {
      throw error.response?.data || 'Error connecting to server';
    }
  },

  async register(userData) {
    try {
      const response = await api.post('/users/register', userData);
      const { token, ...user } = response.data;

      if (token) {
        localStorage.setItem('token', token);
        localStorage.setItem('userData', JSON.stringify(user));
        localStorage.setItem('isLoggedIn', 'true');
      }

      return response.data;
    } catch (error) {
      throw error.response?.data || 'Error registering user';
    }
  },

  async adminCreateUser(userData) {
    try {
      // This uses the same endpoint but doesn't modify local storage
      const response = await api.post('/users/register', userData);
      return response.data;
    } catch (error) {
      throw error.response?.data || 'Error registering user';
    }
  },

  async updateUser(userId, userData) {
    // userData must include { role: { roleId: ... } }
    const response = await api.put(`/users/${userId}`, userData);
    return response.data;
  },

  async checkEmailExists(email) {
    const response = await api.get(`/users/exists?email=${email}`);
    return response.data.exists;
  },

  async getCountries() {
    try {
      const response = await api.get('/users/countries');
      return response.data;
    } catch {
      return ['USA', 'Canada', 'UK']; // fallback
    }
  },

  async getRegions(country) {
    try {
      const response = await api.get(`/users/regions/${country}`);
      return response.data;
    } catch {
      return ['Region 1', 'Region 2'];
    }
  },

  async getCities(country, region) {
    try {
      const response = await api.get(`/users/cities/${country}/${region}`);
      return response.data;
    } catch {
      return ['City 1', 'City 2'];
    }
  },

  async getAllRoles() {
    try {
      const response = await api.get('/roles'); // assumes your backend route is /api/roles
      return response.data;
    } catch (error) {
      throw error.response?.data || 'Error fetching roles';
    }
  },

  getUserRole() {
    try {
      const data = JSON.parse(localStorage.getItem('userData') || '{}');
      return data.role || null;
    } catch {
      return null;
    }
  },

  hasRole(role) {
    return this.getUserRole() === role;
  },

  hasAnyRole(roles) {
    return roles.includes(this.getUserRole());
  },

  logout() {
    localStorage.clear();
    window.location.href = '/login';
  },

  isAuthenticated() {
    return !!localStorage.getItem('token');
  },

  isAdmin() {
    return this.getUserRole() === 'ADMIN';
  },

  async getUserProfile() {
    const userId = JSON.parse(localStorage.getItem('userData') || '{}')?.userId;
    if (!userId) throw 'User ID not found';
    const response = await api.get(`/users/${userId}`);
    return response.data;
  },

  async updateUserProfile(profileData) {
    if (!profileData.userId) throw 'User ID not found';
    const response = await api.put(`/users/${profileData.userId}`, profileData);
    return response.data;
  },

  async getAllUsers() {
    const response = await api.get('/users/all');
    return response.data;
  },

  async getUserById(userId) {
    const response = await api.get(`/users/${userId}`);
    return response.data;
  },

  async deleteUser(userId) {
    const response = await api.delete(`/users/${userId}/delete`);
    return response.data;
  },

  async activateUser(userId) {
    const response = await api.put(`/users/${userId}/activate`);
    return response.data;
  },

  async deactivateUser(userId) {
    const response = await api.put(`/users/${userId}/deactivate`);
    return response.data;
  },

  async changePassword(userId, currentPassword, newPassword) {
    const response = await api.put(`/users/${userId}/password`, { currentPassword, newPassword });
    return response.data;
  },

  async requestPasswordReset(email) {
    const response = await api.post('/users/password-reset-request', { email });
    return response.data;
  },

  async resetPassword(token, newPassword) {
    const response = await api.post('/users/password-reset', { token, newPassword });
    return response.data;
  }
};

// Admin-only service
export const adminService = {
  async getDashboardStats() {
    const response = await api.get('/admin/dashboard');
    return response.data;
  },

  async getUsersActivityLogs(page = 0, size = 10) {
    const response = await api.get(`/admin/activity-logs?page=${page}&size=${size}`);
    return response.data;
  },

  async getUserActivityLogs(userId, page = 0, size = 10) {
    const response = await api.get(`/admin/users/${userId}/activity-logs?page=${page}&size=${size}`);
    return response.data;
  }
};