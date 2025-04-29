// src/services/apiService.js - FIXED VERSION

import axios from 'axios';

const API_URL = 'http://localhost:8080/api';

// Create axios instance
const api = axios.create({
  baseURL: API_URL,
  headers: {
    'Content-Type': 'application/json'
  }
});

// Better isAuthenticated method with improved token validation
const isAuthenticated = () => {
  const token = localStorage.getItem('token');
  const isLoggedInFlag = localStorage.getItem('isLoggedIn') === 'true';
  
  // Enhanced debug logging
  console.log('Authentication check:', {
    tokenExists: !!token,
    isLoggedIn: isLoggedInFlag,
    tokenLength: token ? token.length : 0
  });
  
  if (!token) return false;
  
  // Check if token has proper structure (simple check)
  const parts = token.split('.');
  if (parts.length !== 3) {
    console.warn('Token does not have valid JWT structure');
    return false;
  }
  
  // Check if token is expired
  try {
    const payload = JSON.parse(atob(parts[1])); // Decode payload
    const expiry = payload.exp * 1000; // Convert to milliseconds
    const now = Date.now();
    
    if (expiry < now) {
      console.warn('Token has expired');
      return false;
    }
    
    // Additional debug for token validity
    console.log('Token validity check:', {
      valid: true,
      expiresIn: Math.round((expiry - now) / 1000 / 60) + ' minutes'
    });
  } catch (e) {
    console.warn('Error checking token expiry:', e);
    return false;
  }
  
  return true;
};

// Store token with consistent format (without 'Bearer ' prefix)
const storeToken = (token) => {
  if (!token) {
    console.error('Attempted to store null/empty token');
    return false;
  }
  
  // Remove Bearer prefix if present
  const tokenValue = token.startsWith('Bearer ') ? token.substring(7) : token;
  
  // Store token
  localStorage.setItem('token', tokenValue);
  console.log('Token stored successfully:', tokenValue.substring(0, 10) + '...');
  
  return true;
};

// Clear all auth data
const clearAuthData = () => {
  localStorage.removeItem('token');
  localStorage.removeItem('userData');
  localStorage.removeItem('isLoggedIn');
  localStorage.removeItem('userEmail');
  console.log('Auth data cleared');
};

// Improved request interceptor with consistent token handling
api.interceptors.request.use(
  (config) => {
    // Define public routes that don't need authentication warnings
    const publicRoutes = [
      '/users/register', 
      '/users/login', 
      '/users/exists',
      '/users/countries', 
      '/users/regions',
      '/users/cities'
    ];
    
    // Check if we're on a public route
    const isPublicRoute = publicRoutes.some(route => config.url.includes(route));
    
    // Get token from localStorage
    const token = localStorage.getItem('token');
    
    // Add token if it exists (with consistent Bearer prefix handling)
    if (token) {
      // Always add Bearer prefix when sending to API
      config.headers['Authorization'] = `Bearer ${token}`;
      
      // Debug logging
      if (config.url.includes('/events/create-new')) {
        console.log('Event creation - adding auth header:', `Bearer ${token.substring(0, 10)}...`);
      }
    } else if (!isPublicRoute) {
      // Only log warning for protected routes, not login/register
      console.warn('No token found for protected route:', config.url);
    }
    
    return config;
  },
  (error) => {
    console.error('Request interceptor error:', error);
    return Promise.reject(error);
  }
);

// Response interceptor to handle token expiration consistently
api.interceptors.response.use(
  (response) => {
    return response;
  },
  (error) => {
    // Handle 401 or 403 errors (unauthorized/forbidden)
    if (error.response && (error.response.status === 401 || error.response.status === 403)) {
      console.error('Authentication error:', error.response.status);
      
      // Clear authentication data
      clearAuthData();
      
      // Redirect to login page automatically
      window.location.href = '/login';
      
      // Return a clearer error for UI handling
      return Promise.reject({
        message: 'Your session has expired - please log in again',
        originalError: error
      });
    }
    
    return Promise.reject(error);
  }
);

export const userService = {
  async login(email, password) {
    try {
      console.log('Attempting login for:', email);
      
      // Clear any existing auth data first to prevent conflicts
      clearAuthData();
      
      // FIXED: Send credentials in request body instead of params
      const response = await api.post('/users/login', {
        email,
        password
      });
      
      // Store token in localStorage
      if (response.data.token) {
        console.log('Login successful, saving token');
        
        // Store token with improved method
        const tokenStored = storeToken(response.data.token);
        if (!tokenStored) {
          throw new Error('Failed to store authentication token');
        }
        
        // Now set the other values
        localStorage.setItem('userData', JSON.stringify({
          userId: response.data.userId,
          email: response.data.email,
          firstName: response.data.firstName,
          lastName: response.data.lastName
        }));
        localStorage.setItem('isLoggedIn', 'true');
        localStorage.setItem('userEmail', response.data.email);
        
        // Verify token was saved
        const savedToken = localStorage.getItem('token');
        console.log('Token saved successfully:', !!savedToken);
        
        // Log token format for debugging (first few characters only)
        if (savedToken) {
          console.log('Token format check:', savedToken.substring(0, 10) + '...');
        }
      } else {
        console.error('No token received from server');
        throw new Error('No authentication token received');
      }
      
      return response.data;
    } catch (error) {
      console.error('Login request error:', error);
      
      // IMPROVED: Better error handling to show more specific errors
      if (error.response) {
        // The request was made and the server responded with a status code
        // that falls out of the range of 2xx
        if (error.response.status === 401) {
          throw 'Invalid email or password';
        } else if (error.response.data && error.response.data.message) {
          throw error.response.data.message;
        } else {
          throw `Server error: ${error.response.status}`;
        }
      } else if (error.request) {
        // The request was made but no response was received
        throw 'Server is not responding. Please try again later.';
      } else {
        // Something happened in setting up the request that triggered an Error
        throw 'Error connecting to server';
      }
    }
  },
  
  async register(userData) {
    try {
      console.log("Sending to API:", userData);
      
      // Clear any existing auth data first to prevent conflicts
      clearAuthData();
      
      const response = await api.post('/users/register', userData);
      console.log("API response:", response.data);
      
      // Store token in localStorage
      if (response.data.token) {
        // Store token with improved method
        const tokenStored = storeToken(response.data.token);
        if (!tokenStored) {
          throw new Error('Failed to store authentication token');
        }
        
        // Also store some basic user info
        localStorage.setItem('userData', JSON.stringify({
          userId: response.data.userId,
          email: response.data.email,
          firstName: response.data.firstName,
          lastName: response.data.lastName
        }));
        localStorage.setItem('isLoggedIn', 'true');
        localStorage.setItem('userEmail', response.data.email);
      }
      
      return response.data;
    } catch (error) {
      console.error("Registration error:", error);
      if (error.response && error.response.data) {
        throw error.response.data;
      }
      throw 'Error registering user';
    }
  },
  
  // Other methods remain the same...
  async checkEmailExists(email) {
    try {
      const response = await api.get(`/users/exists?email=${email}`);
      return response.data.exists;
    } catch (error) {
      console.error('Error checking email:', error);
      throw error;
    }
  },
  
  async getCountries() {
    try {
      const response = await api.get('/users/countries');
      return response.data;
    } catch (error) {
      console.error('Error fetching countries:', error);
      return ['USA', 'Canada', 'UK']; // Fallback
    }
  },
  
  async getRegions(country) {
    try {
      const response = await api.get(`/users/regions/${country}`);
      return response.data;
    } catch (error) {
      console.error('Error fetching regions:', error);
      return ['Region 1', 'Region 2']; // Fallback
    }
  },
  
  async getCities(country, region) {
    try {
      const response = await api.get(`/users/cities/${country}/${region}`);
      return response.data;
    } catch (error) {
      console.error('Error fetching cities:', error);
      return ['City 1', 'City 2']; // Fallback
    }
  },
  
  // Improved logout method
  logout() {
    // Clear all auth data
    clearAuthData();
    
    // Redirect to login page
    window.location.href = '/login';
  },
  
  // Updated isAuthenticated method that returns detailed auth status
  isAuthenticated() {
    const isAuth = isAuthenticated();
    const token = localStorage.getItem('token');
    const isLoggedInFlag = localStorage.getItem('isLoggedIn') === 'true';
    
    // Return more detailed auth status for debugging
    if (typeof window !== 'undefined' && window.debugAuth) {
      return {
        authenticated: isAuth,
        tokenExists: !!token,
        isLoggedIn: isLoggedInFlag
      };
    }
    
    return isAuth;
  },

  // Remaining methods are unchanged...
};

// Event service remains unchanged
export const eventService = {
  // All methods remain the same
};