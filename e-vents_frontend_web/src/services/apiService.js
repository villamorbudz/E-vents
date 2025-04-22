// src/services/apiService.js
import axios from 'axios';

const API_BASE_URL = 'http://localhost:8080/api';

// Create axios instance with base configuration
const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json'
  }
});

// User related API calls
export const userService = {
  // Register a new user (Step 2 of signup)
  register: async (userData) => {
    try {
      const response = await api.post('/users/register', userData);
      return response.data;
    } catch (error) {
      throw error.response ? error.response.data : error.message;
    }
  },

  // Check if email already exists
  checkEmailExists: async (email) => {
    try {
      const response = await api.get(`/users/exists?email=${email}`);
      return response.data.exists;
    } catch (error) {
      throw error.response ? error.response.data : error.message;
    }
  },

  // Login user
  login: async (email, password) => {
    try {
      const response = await api.post(`/users/login?email=${encodeURIComponent(email)}&password=${encodeURIComponent(password)}`);
      return response.data;
    } catch (error) {
      throw error.response ? error.response.data : error.message;
    }
  },

  // Get list of countries
  getCountries: async () => {
    try {
      const response = await api.get('/users/countries');
      return response.data;
    } catch (error) {
      throw error.response ? error.response.data : error.message;
    }
  },

  // Get regions for a country
  getRegions: async (country) => {
    try {
      const response = await api.get(`/users/regions/${encodeURIComponent(country)}`);
      return response.data;
    } catch (error) {
      throw error.response ? error.response.data : error.message;
    }
  },

  // Get cities for a region
  getCities: async (country, region) => {
    try {
      const response = await api.get(`/users/cities/${encodeURIComponent(country)}/${encodeURIComponent(region)}`);
      return response.data;
    } catch (error) {
      throw error.response ? error.response.data : error.message;
    }
  },

  // Change password
  changePassword: async (email, password) => {
    try {
      const response = await api.post('/users/change-password', { email, password });
      return response.data;
    } catch (error) {
      throw error.response ? error.response.data : error.message;
    }
  }
};