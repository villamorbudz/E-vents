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
      throw error.response?.data || error.message || new Error('Connection failed');
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
      throw error.response?.data || error.message || new Error('Registration failed');
    }
  },

  async adminCreateUser(userData) {
    try {
      // Format the data properly before sending to backend
      const formattedData = { ...userData };
      
      // Ensure role is properly formatted as an object with roleId
      if (formattedData.role) {
        if (typeof formattedData.role === 'string' || typeof formattedData.role === 'number') {
          formattedData.role = { roleId: Number(formattedData.role) };
          console.log('Formatted role for API:', formattedData.role);
        }
      }
      
      // Ensure all required fields are present
      if (!formattedData.firstName) formattedData.firstName = '';
      if (!formattedData.lastName) formattedData.lastName = '';
      if (!formattedData.email) formattedData.email = '';
      if (!formattedData.contactNumber) formattedData.contactNumber = '';
      if (!formattedData.country) formattedData.country = '';
      if (!formattedData.password) formattedData.password = '12345678';
      
      console.log('Sending user data to API:', formattedData);
      
      // This uses the same endpoint but doesn't modify local storage
      const response = await api.post('/users/register', formattedData);
      return response.data;
    } catch (error) {
      console.error('User creation error details:', error.response?.data);
      throw error.response?.data || error.message || new Error('Registration failed');
    }
  },

  async updateUser(userId, userData) {
    if (!userId) {
      throw new Error('User ID is required');
    }

    try {
      // First, get the existing user data to use as fallback
      const existingUser = await this.getUserById(userId);
      
      // Create a new object with validated data
      const validatedData = {
        userId: userId,
        firstName: userData.firstName !== null && userData.firstName !== undefined ? userData.firstName : existingUser.firstName,
        lastName: userData.lastName !== null && userData.lastName !== undefined ? userData.lastName : existingUser.lastName,
        email: userData.email !== null && userData.email !== undefined ? userData.email : existingUser.email,
        country: userData.country !== null && userData.country !== undefined ? userData.country : existingUser.country,
        birthdate: userData.birthdate !== null && userData.birthdate !== undefined ? userData.birthdate : existingUser.birthdate,
        contactNumber: userData.contactNumber !== null && userData.contactNumber !== undefined ? userData.contactNumber : existingUser.contactNumber
      };
      
      // Handle role specially since it's an object
      if (userData.role) {
        // Ensure role is properly formatted as an object with roleId
        if (typeof userData.role === 'string') {
          validatedData.role = { roleId: userData.role };
        } else if (typeof userData.role === 'object') {
          // If it's already an object, make sure it has the correct structure
          validatedData.role = { roleId: userData.role.roleId || userData.role.id };
        }
      } else {
        // Use existing role
        validatedData.role = existingUser.role;
      }
      
      // Only include password if it was provided
      if (userData.password) {
        validatedData.password = userData.password;
      }
      
      // Log the data being sent for debugging
      console.log('Sending update data:', validatedData);
      
      const response = await api.put(`/users/${userId}`, validatedData);
      return response.data;
    } catch (error) {
      console.error('Error updating user:', error);
      if (error.response) {
        console.error('Response data:', error.response.data);
      }
      throw error;
    }
  },

  async checkEmailExists(email) {
    const response = await api.get(`/users/exists?email=${email}`);
    return response.data.exists;
  },

  async getCountries() {
    try {
      const response = await api.get('/users/countries');
      return response.data;
    } catch (error) {
      throw error.response?.data || error.message || new Error('Failed to fetch countries');
    }
  },

  async getAllRoles() {
    try {
      const response = await api.get('/roles'); // assumes your backend route is /api/roles
      return response.data;
    } catch (error) {
      throw error.response?.data || error.message || new Error('Failed to fetch roles');
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
    if (!userId) throw new Error('User ID not found');
    const response = await api.get(`/users/${userId}`);
    return response.data;
  },

  async updateUserProfile(profileData) {
    if (!profileData.userId) throw new Error('User ID not found');
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
    const response = await api.put(`/users/restore/${userId}`);
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

// Event Service
export const eventService = {
  // Get all events
  async getAllEvents() {
    try {
      const response = await api.get('/events');
      return response.data;
    } catch (error) {
      console.error('Error fetching events:', error);
      throw error.response?.data || 'Error fetching events';
    }
  },
  
  // Get event by ID
  async getEventById(eventId) {
    try {
      const response = await api.get(`/events/${eventId}`);
      return response.data;
    } catch (error) {
      console.error(`Error fetching event #${eventId}:`, error);
      throw error.response?.data || `Error fetching event #${eventId}`;
    }
  },
  
  // Get all acts
  async getAllActs() {
    try {
      const response = await api.get('/acts');
      return response.data;
    } catch (error) {
      throw error.response?.data || 'Error fetching acts';
    }
  },

  async createEvent(eventData) {
    try {
      // Create a clean copy of the event data and prepare it for submission
      const cleanEventData = { ...eventData };
      
      // Set defaults and format data
      cleanEventData.status = cleanEventData.status || 'SCHEDULED';
      
      // Format date if needed
      if (cleanEventData.date && cleanEventData.date instanceof Date) {
        cleanEventData.date = cleanEventData.date.toISOString().split('T')[0];
      }
      
      // Format time if needed (ensure HH:mm:ss format)
      if (cleanEventData.time && typeof cleanEventData.time === 'string' && cleanEventData.time.split(':').length === 2) {
        cleanEventData.time = `${cleanEventData.time}:00`;
      }
      
      console.log('Creating event with data:', JSON.stringify(cleanEventData, null, 2));
      
      // Send event data to API using axios instance with proper content type headers
      const response = await api.post('/events/create', cleanEventData, {
        headers: {
          'Content-Type': 'application/json',
          'Accept': 'application/json'
        }
      });
      
      return response.data;
    } catch (error) {
      console.error('Error creating event:', error);
      throw error.response?.data || error.message || 'Error creating event';
    }
  },

  async updateEvent(eventId, eventData) {
    try {
      if (!eventId) {
        throw new Error('Event ID is required for update');
      }
      
      console.log(`Updating event ${eventId} with data:`, eventData);
      
      // Make a copy of the data to avoid modifying the original
      const formattedData = { ...eventData };
      
      // Remove time field and ensure date is properly formatted
      if (formattedData.time) {
        delete formattedData.time;
      }
      
      // Create a simplified event object with only the fields the backend expects
      const eventPayload = {
        name: formattedData.name,
        description: formattedData.description,
        date: formattedData.date,
        venue: formattedData.venue,
        status: formattedData.status,
        lineup: formattedData.lineup
      };
      
      console.log('Sending JSON update data:', JSON.stringify(eventPayload));
      
      // Send as JSON data with explicit Content-Type header
      const response = await api.put(`/events/${eventId}`, eventPayload, {
        headers: {
          'Content-Type': 'application/json'
        }
      });
      
      return response.data;
    } catch (error) {
      throw error.response?.data || 'Error updating event';
    }
  },
  
  // Banner image functionality has been removed
  
  
  async createTicketCategory(eventId, categoryData) {
    try {
      const response = await api.post(`/events/${eventId}/ticket-categories`, categoryData, {
        headers: {
          'Content-Type': 'application/json'
        }
      });
      return response.data;
    } catch (error) {
      throw error.response?.data || 'Error creating ticket category';
    }
  },
  
  // Cancel an event
  async cancelEvent(eventId) {
    try {
      const response = await api.delete(`/events/${eventId}/cancel`);
      return response.data;
    } catch (error) {
      console.error(`Error cancelling event #${eventId}:`, error);
      throw error.response?.data || `Error cancelling event #${eventId}`;
    }
  },
  
  // Postpone an event
  async postponeEvent(eventId) {
    try {
      const response = await api.put(`/events/${eventId}/postpone`);
      return response.data;
    } catch (error) {
      console.error(`Error postponing event #${eventId}:`, error);
      throw error.response?.data || `Error postponing event #${eventId}`;
    }
  },
  
  // Delete an event permanently
  async deleteEvent(eventId) {
    try {
      const response = await api.delete(`/events/${eventId}/delete`);
      return response.data;
    } catch (error) {
      console.error(`Error deleting event #${eventId}:`, error);
      throw error.response?.data || `Error deleting event #${eventId}`;
    }
  },
  
  // Get scheduled events
  async getScheduledEvents() {
    try {
      const response = await api.get('/events/scheduled');
      return response.data;
    } catch (error) {
      console.error('Error fetching scheduled events:', error);
      throw error.response?.data || 'Error fetching scheduled events';
    }
  },
  
  // Get postponed events
  async getPostponedEvents() {
    try {
      const response = await api.get('/events/postponed');
      return response.data;
    } catch (error) {
      console.error('Error fetching postponed events:', error);
      throw error.response?.data || 'Error fetching postponed events';
    }
  },
  
  // Get cancelled events
  async getCancelledEvents() {
    try {
      const response = await api.get('/events/cancelled');
      return response.data;
    } catch (error) {
      console.error('Error fetching cancelled events:', error);
      throw error.response?.data || 'Error fetching cancelled events';
    }
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
  },
  
  // Count APIs for dashboard
  async getUserCount() {
    const response = await api.get('/users/all');
    return Array.isArray(response.data) ? response.data.length : 0;
  },
  async getEventCount() {
    const response = await api.get('/events');
    return Array.isArray(response.data) ? response.data.length : 0;
  },
  async getActCount() {
    const response = await api.get('/acts');
    return Array.isArray(response.data) ? response.data.length : 0;
  },
  async getCategoryCount() {
    const response = await api.get('/categories/all');
    return Array.isArray(response.data) ? response.data.length : 0;
  },
  async getTagCount() {
    const response = await api.get('/tags/all');
    return Array.isArray(response.data) ? response.data.length : 0;
  },
  async getTicketCount() {
    const response = await api.get('/tickets/all');
    return Array.isArray(response.data) ? response.data.length : 0;
  },
  async getTicketCategoryCount() {
    const response = await api.get('/ticket-categories/all');
    return Array.isArray(response.data) ? response.data.length : 0;
  },
  async getRatingCount() {
    const response = await api.get('/ratings/all');
    return Array.isArray(response.data) ? response.data.length : 0;
  },
  async getNotificationCount() {
    const response = await api.get('/notifications/all');
    return Array.isArray(response.data) ? response.data.length : 0;
  },
  async getRoleCount() {
    const response = await api.get('/roles');
    return Array.isArray(response.data) ? response.data.length : 0;
  },
  
  // Get all counts at once for dashboard
  async getAllCounts() {
    const results = await Promise.allSettled([
      this.getUserCount(),
      this.getEventCount(),
      this.getActCount(),
      this.getCategoryCount(),
      this.getTagCount(),
      this.getTicketCount(),
      this.getTicketCategoryCount(),
      this.getRatingCount(),
      this.getNotificationCount(),
      this.getRoleCount()
    ]);
    return {
      users: results[0].status === 'fulfilled' ? results[0].value : 0,
      events: results[1].status === 'fulfilled' ? results[1].value : 0,
      acts: results[2].status === 'fulfilled' ? results[2].value : 0,
      categories: results[3].status === 'fulfilled' ? results[3].value : 0,
      tags: results[4].status === 'fulfilled' ? results[4].value : 0,
      tickets: results[5].status === 'fulfilled' ? results[5].value : 0,
      ticketCategories: results[6].status === 'fulfilled' ? results[6].value : 0,
      ratings: results[7].status === 'fulfilled' ? results[7].value : 0,
      notifications: results[8].status === 'fulfilled' ? results[8].value : 0,
      roles: results[9].status === 'fulfilled' ? results[9].value : 0
    };
  }
};

// Category Service
export const categoryService = {
  async getAllCategories() { 
    console.log('API Service: Fetching all active categories...');
    try {
      console.log('API Service: Making GET request to /categories');
      const response = await api.get('/categories'); 
      console.log('API Service: Categories response received:', response);
      
      // Handle case where response.data is a string (JSON with circular references)
      if (typeof response.data === 'string') {
        try {
          console.log('API Service: Parsing string response data');
          // Parse the JSON string and handle circular references
          const parsedData = JSON.parse(response.data, (key, value) => {
            // If we encounter a tag with a category that has the same ID as its parent category,
            // return a simplified version to break the circular reference
            if (key === 'category' && value && value.categoryId) {
              return { categoryId: value.categoryId, name: value.name };
            }
            return value;
          });
          console.log('API Service: Successfully parsed categories data:', parsedData);
          return parsedData;
        } catch (parseError) {
          console.error('API Service: Error parsing categories JSON:', parseError);
          return [];
        }
      }
      
      return response.data; 
    } catch (error) {
      console.error('API Service: Error fetching categories:', error);
      console.error('API Service: Error details:', {
        message: error.message,
        status: error.response?.status,
        statusText: error.response?.statusText,
        data: error.response?.data
      });
      return [];
    }
  },
  async getAllIncludingInactive() { 
    console.log('API Service: Fetching all categories including inactive...');
    try {
      console.log('API Service: Making GET request to /categories/all');
      const response = await api.get('/categories/all'); 
      console.log('API Service: All categories response received:', response);
      
      // Handle case where response.data is a string (JSON with circular references)
      if (typeof response.data === 'string') {
        console.log('API Service: Received string data, bypassing JSON parsing');
        
        // Instead of trying to parse potentially corrupted JSON,
        // make a direct request to get the categories from the database
        try {
          // Make a direct request to get categories from the database
          console.log('API Service: Making direct request to get categories');
          const directResponse = await api.get('/categories/direct');
          if (directResponse.data && Array.isArray(directResponse.data)) {
            console.log('API Service: Successfully retrieved categories via direct endpoint:', directResponse.data);
            return directResponse.data;
          }
        } catch (directError) {
          console.error('API Service: Direct categories request failed:', directError);
          throw new Error('Failed to retrieve categories');
        }
      }
      
      // If response.data is already an object/array, return it directly
      if (response.data && (Array.isArray(response.data) || typeof response.data === 'object')) {
        console.log('API Service: Categories data is already parsed:', response.data);
        return response.data;
      }
      
      // If we get here, something is wrong with the data
      console.error('API Service: Unexpected data format:', response.data);
      return []; 
    } catch (error) {
      console.error('API Service: Error fetching all categories:', error);
      console.error('API Service: Error details:', {
        message: error.message,
        status: error.response?.status,
        statusText: error.response?.statusText,
        data: error.response?.data
      });
      
      console.log('API Service: Returning empty array after error');
      return [];
    }
  },
  async getCategoryById(id) { 
    console.log(`API Service: Fetching category with ID ${id}...`);
    try {
      const response = await api.get(`/categories/${id}`); 
      console.log('API Service: Category by ID response received:', response);
      return response.data; 
    } catch (error) {
      console.error(`API Service: Error fetching category with ID ${id}:`, error);
      throw error;
    }
  },

  
  async createCategory(data) { 
    console.log('API Service: Creating new category:', data);
    try {
      // Create a category object with name and initialize tags as empty array
      const categoryData = { 
        name: data.name,
        tags: [],
        isActive: true
      };
      
      console.log('API Service: Sending category data:', categoryData);
      
      // Use fetch API directly instead of axios
      const response = await fetch(`${API_URL}/categories/create`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${localStorage.getItem('token')}`
        },
        body: JSON.stringify(categoryData)
      });
      
      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }
      
      const result = await response.json();
      console.log('API Service: Category created successfully:', result);
      return result;
    } catch (error) {
      console.error('API Service: Error creating category:', error);
      throw error;
    }
  },  
  
  async updateCategory(id, data) { 
    console.log(`API Service: Updating category with ID ${id}:`, data);
    try {
      const response = await api.put(`/categories/${id}/edit`, data); 
      console.log('API Service: Category updated successfully:', response);
      return response.data; 
    } catch (error) {
      console.error(`API Service: Error updating category with ID ${id}:`, error);
      throw error;
    }
  },
  
  async deactivateCategory(id) { 
    console.log(`API Service: Deactivating category with ID ${id}...`);
    try {
      const response = await api.delete(`/categories/${id}/deactivate`); 
      console.log('API Service: Category deactivated successfully:', response);
      return response.data; 
    } catch (error) {
      console.error(`API Service: Error deactivating category with ID ${id}:`, error);
      throw error;
    }
  },
  
  async deleteCategory(id) { 
    console.log(`API Service: Deleting category with ID ${id}...`);
    try {
      const response = await api.delete(`/categories/${id}/delete`); 
      console.log('API Service: Category deleted successfully:', response);
      return response.data; 
    } catch (error) {
      console.error(`API Service: Error deleting category with ID ${id}:`, error);
      throw error;
    }
  },
  
  async restoreCategory(id) { 
    console.log(`API Service: Restoring category with ID ${id}...`);
    try {
      const response = await api.put(`/categories/restore/${id}`); 
      console.log('API Service: Category restored successfully:', response);
      return response.data; 
    } catch (error) {
      console.error(`API Service: Error restoring category with ID ${id}:`, error);
      throw error;
    }
  },
};

// Tag Service
export const tagService = {
  async getAllTags() { 
    console.log('API Service: Fetching all active tags...');
    try {
      const response = await api.get('/tags'); 
      console.log('API Service: Tags response received:', response);
      return response.data; 
    } catch (error) {
      console.error('API Service: Error fetching tags:', error);
      console.error('API Service: Error details:', {
        message: error.message,
        status: error.response?.status,
        statusText: error.response?.statusText,
        data: error.response?.data
      });
      return [];
    }
  },
  
  async getAllIncludingInactive() { 
    console.log('API Service: Fetching all tags including inactive...');
    try {
      const response = await api.get('/tags/all'); 
      console.log('API Service: All tags response received:', response);
      return response.data; 
    } catch (error) {
      console.error('API Service: Error fetching all tags:', error);
      console.error('API Service: Error details:', {
        message: error.message,
        status: error.response?.status,
        statusText: error.response?.statusText,
        data: error.response?.data
      });
      return [];
    }
  },
  
  async getTagById(id) { 
    console.log(`API Service: Fetching tag with ID ${id}...`);
    try {
      const response = await api.get(`/tags/${id}`); 
      console.log('API Service: Tag by ID response received:', response);
      return response.data; 
    } catch (error) {
      console.error(`API Service: Error fetching tag with ID ${id}:`, error);
      throw error;
    }
  },
  
  async getCategoryNameByTagId(id) { 
    console.log(`API Service: Fetching category name for tag with ID ${id}...`);
    try {
      // Try to use the dedicated endpoint first
      const response = await api.get(`/tags/${id}/category-name`);
      console.log('API Service: Category name for tag response received:', response);
      if (response.data && response.data.categoryName) {
        return response.data.categoryName;
      }
      
      // Fallback to getting the full tag and extracting the category name
      const tag = await this.getTagById(id);
      const categoryName = tag.categoryName || 
        (tag.category ? tag.category.name : 
          (tag.categoryInfo ? tag.categoryInfo.name : null));
      return categoryName; 
    } catch (error) {
      console.error(`API Service: Error fetching category name for tag with ID ${id}:`, error);
      throw error;
    }
  },
  
  async getTagsByCategory(categoryId) { 
    console.log(`API Service: Fetching tags for category ID ${categoryId}...`);
    try {
      const response = await api.get(`/tags/by-category/${categoryId}`); 
      console.log('API Service: Tags by category response received:', response);
      return response.data; 
    } catch (error) {
      console.error(`API Service: Error fetching tags for category ID ${categoryId}:`, error);
      return [];
    }
  },
  
  async createTag(data) { 
    console.log('API Service: Creating new tag:', data);
    try {
      const response = await api.post('/tags', data); 
      console.log('API Service: Tag created successfully:', response);
      return response.data; 
    } catch (error) {
      console.error('API Service: Error creating tag:', error);
      throw error;
    }
  },
  
  async updateTag(id, data) { 
    console.log(`API Service: Updating tag with ID ${id}:`, data);
    try {
      const response = await api.put(`/tags/${id}`, data); 
      console.log('API Service: Tag updated successfully:', response);
      return response.data; 
    } catch (error) {
      console.error(`API Service: Error updating tag with ID ${id}:`, error);
      throw error;
    }
  },
  
  async deactivateTag(id) { 
    console.log(`API Service: Deactivating tag with ID ${id}...`);
    try {
      const response = await api.delete(`/tags/${id}/deactivate`); 
      console.log('API Service: Tag deactivated successfully:', response);
      return response.data; 
    } catch (error) {
      console.error(`API Service: Error deactivating tag with ID ${id}:`, error);
      throw error;
    }
  },
  
  async restoreTag(id) { 
    console.log(`API Service: Restoring tag with ID ${id}...`);
    try {
      const response = await api.put(`/tags/restore/${id}`); 
      console.log('API Service: Tag restored successfully:', response);
      return response.data; 
    } catch (error) {
      console.error(`API Service: Error restoring tag with ID ${id}:`, error);
      throw error;
    }
  },
};

// Act Service
export const actService = {
  async getAllActs() { 
    console.log('API Service: Fetching all active acts...');
    try {
      const response = await api.get('/acts'); 
      console.log('API Service: Acts response received:', response);
      return response.data; 
    } catch (error) {
      console.error('API Service: Error fetching acts:', error);
      console.error('API Service: Error details:', {
        message: error.message,
        status: error.response?.status,
        statusText: error.response?.statusText,
        data: error.response?.data
      });
      return [];
    }
  },
  
  async getAllIncludingInactive() { 
    console.log('API Service: Fetching all acts including inactive...');
    try {
      const response = await api.get('/acts/all'); 
      console.log('API Service: All acts response received:', response);
      return response.data; 
    } catch (error) {
      console.error('API Service: Error fetching all acts:', error);
      console.error('API Service: Error details:', {
        message: error.message,
        status: error.response?.status,
        statusText: error.response?.statusText,
        data: error.response?.data
      });
      return [];
    }
  },
  
  async getActById(id) { 
    console.log(`API Service: Fetching act with ID ${id}...`);
    try {
      const response = await api.get(`/acts/${id}`); 
      console.log('API Service: Act by ID response received:', response);
      return response.data; 
    } catch (error) {
      console.error(`API Service: Error fetching act with ID ${id}:`, error);
      throw error;
    }
  },
  
  async getCategoryNameByActId(id) { 
    console.log(`API Service: Fetching category name for act with ID ${id}...`);
    try {
      // Try to use the dedicated endpoint first
      const response = await api.get(`/acts/${id}/category-name`);
      console.log('API Service: Category name response received:', response);
      if (response.data && response.data.categoryName) {
        return response.data.categoryName;
      }
      
      // Fallback to getting the full act and extracting the category name
      const act = await this.getActById(id);
      const categoryName = act.categoryName || (act.category ? act.category.name : null);
      return categoryName; 
    } catch (error) {
      console.error(`API Service: Error fetching category name for act with ID ${id}:`, error);
      throw error;
    }
  },
  
  async getTagNamesByActId(id) { 
    console.log(`API Service: Fetching tag names for act with ID ${id}...`);
    try {
      // Try to use the dedicated endpoint first
      const response = await api.get(`/acts/${id}/tag-names`);
      console.log('API Service: Tag names response received:', response);
      if (response.data && response.data.tagNames !== undefined) {
        return response.data.tagNames;
      }
      
      // Fallback to getting the full act and extracting the tag names
      const act = await this.getActById(id);
      const tagNames = act.tagNames || 
        (act.tags && act.tags.length > 0 ? 
          act.tags.map(tag => tag.name).join(', ') : '');
      return tagNames; 
    } catch (error) {
      console.error(`API Service: Error fetching tag names for act with ID ${id}:`, error);
      throw error;
    }
  },
  
  async createAct(data) { 
    console.log('API Service: Creating new act:', data);
    try {
      const response = await api.post('/acts/create', data); 
      console.log('API Service: Act created successfully:', response);
      return response.data; 
    } catch (error) {
      console.error('API Service: Error creating act:', error);
      throw error;
    }
  },
  
  async updateAct(id, data) { 
    console.log(`API Service: Updating act with ID ${id}:`, data);
    try {
      const response = await api.put(`/acts/${id}/edit`, data); 
      console.log('API Service: Act updated successfully:', response);
      return response.data; 
    } catch (error) {
      console.error(`API Service: Error updating act with ID ${id}:`, error);
      throw error;
    }
  },
  
  async deactivateAct(id) { 
    console.log(`API Service: Deactivating act with ID ${id}...`);
    try {
      const response = await api.delete(`/acts/${id}`); 
      console.log('API Service: Act deactivated successfully:', response);
      return response.data; 
    } catch (error) {
      console.error(`API Service: Error deactivating act with ID ${id}:`, error);
      throw error;
    }
  },
  
  async restoreAct(id) { 
    console.log(`API Service: Restoring act with ID ${id}...`);
    try {
      const response = await api.put(`/acts/restore/${id}`); 
      console.log('API Service: Act restored successfully:', response);
      return response.data; 
    } catch (error) {
      console.error(`API Service: Error restoring act with ID ${id}:`, error);
      throw error;
    }
  },
};

// Ticket Service
export const ticketService = {
  async getAllTickets() { 
    console.log('API Service: Fetching all active tickets...');
    try {
      const response = await api.get('/tickets'); 
      console.log('API Service: Tickets response received:', response);
      return response.data; 
    } catch (error) {
      console.error('API Service: Error fetching tickets:', error);
      console.error('API Service: Error details:', {
        message: error.message,
        status: error.response?.status,
        statusText: error.response?.statusText,
        data: error.response?.data
      });
      return [];
    }
  },
  
  async getAllIncludingInactive() { 
    console.log('API Service: Fetching all tickets including inactive...');
    try {
      const response = await api.get('/tickets/all'); 
      console.log('API Service: All tickets response received:', response);
      return response.data; 
    } catch (error) {
      console.error('API Service: Error fetching all tickets:', error);
      console.error('API Service: Error details:', {
        message: error.message,
        status: error.response?.status,
        statusText: error.response?.statusText,
        data: error.response?.data
      });
      return [];
    }
  },
  
  async getTicketById(id) { 
    console.log(`API Service: Fetching ticket with ID ${id}...`);
    try {
      const response = await api.get(`/tickets/${id}`); 
      console.log('API Service: Ticket by ID response received:', response);
      return response.data; 
    } catch (error) {
      console.error(`API Service: Error fetching ticket with ID ${id}:`, error);
      throw error;
    }
  },
  
  async getTicketsByUser(userId) { 
    console.log(`API Service: Fetching tickets for user ID ${userId}...`);
    try {
      const response = await api.get(`/tickets/user/${userId}`); 
      console.log('API Service: Tickets by user response received:', response);
      return response.data; 
    } catch (error) {
      console.error(`API Service: Error fetching tickets for user ID ${userId}:`, error);
      return [];
    }
  },
  
  async getTicketsByEvent(eventId) { 
    console.log(`API Service: Fetching tickets for event ID ${eventId}...`);
    try {
      const response = await api.get(`/tickets/event/${eventId}`); 
      console.log('API Service: Tickets by event response received:', response);
      return response.data; 
    } catch (error) {
      console.error(`API Service: Error fetching tickets for event ID ${eventId}:`, error);
      return [];
    }
  },
  
  async getTicketsByCategory(catId) { 
    console.log(`API Service: Fetching tickets for category ID ${catId}...`);
    try {
      const response = await api.get(`/tickets/category/${catId}`); 
      console.log('API Service: Tickets by category response received:', response);
      return response.data; 
    } catch (error) {
      console.error(`API Service: Error fetching tickets for category ID ${catId}:`, error);
      return [];
    }
  },
  
  async getTicketsByStatus(status) { 
    console.log(`API Service: Fetching tickets with status ${status}...`);
    try {
      const response = await api.get(`/tickets/status/${status}`); 
      console.log('API Service: Tickets by status response received:', response);
      return response.data; 
    } catch (error) {
      console.error(`API Service: Error fetching tickets with status ${status}:`, error);
      return [];
    }
  },
  
  async createTicket(data) { 
    console.log('API Service: Creating new ticket:', data);
    try {
      const response = await api.post('/tickets', data); 
      console.log('API Service: Ticket created successfully:', response);
      return response.data; 
    } catch (error) {
      console.error('API Service: Error creating ticket:', error);
      throw error;
    }
  },
  
  async updateTicket(id, data) { 
    console.log(`API Service: Updating ticket with ID ${id}:`, data);
    try {
      const response = await api.put(`/tickets/${id}`, data); 
      console.log('API Service: Ticket updated successfully:', response);
      return response.data; 
    } catch (error) {
      console.error(`API Service: Error updating ticket with ID ${id}:`, error);
      throw error;
    }
  },
  
  async deactivateTicket(id) { 
    console.log(`API Service: Deactivating ticket with ID ${id}...`);
    try {
      const response = await api.delete(`/tickets/${id}`); 
      console.log('API Service: Ticket deactivated successfully:', response);
      return response.data; 
    } catch (error) {
      console.error(`API Service: Error deactivating ticket with ID ${id}:`, error);
      throw error;
    }
  },
  
  async restoreTicket(id) { 
    console.log(`API Service: Restoring ticket with ID ${id}...`);
    try {
      const response = await api.put(`/tickets/restore/${id}`); 
      console.log('API Service: Ticket restored successfully:', response);
      return response.data; 
    } catch (error) {
      console.error(`API Service: Error restoring ticket with ID ${id}:`, error);
      throw error;
    }
  },
};

// TicketCategory Service
export const ticketCategoryService = {
  async getAll() { 
    console.log('API Service: Fetching all active ticket categories...');
    try {
      const response = await api.get('/ticket-categories'); 
      console.log('API Service: Ticket categories response received:', response);
      return response.data; 
    } catch (error) {
      console.error('API Service: Error fetching ticket categories:', error);
      console.error('API Service: Error details:', {
        message: error.message,
        status: error.response?.status,
        statusText: error.response?.statusText,
        data: error.response?.data
      });
      return [];
    }
  },
  
  async getAllIncludingInactive() { 
    console.log('API Service: Fetching all ticket categories including inactive...');
    try {
      const response = await api.get('/ticket-categories/all'); 
      console.log('API Service: All ticket categories response received:', response);
      return response.data; 
    } catch (error) {
      console.error('API Service: Error fetching all ticket categories:', error);
      console.error('API Service: Error details:', {
        message: error.message,
        status: error.response?.status,
        statusText: error.response?.statusText,
        data: error.response?.data
      });
      return [];
    }
  },
  
  async getById(id) { 
    console.log(`API Service: Fetching ticket category with ID ${id}...`);
    try {
      const response = await api.get(`/ticket-categories/${id}`); 
      console.log('API Service: Ticket category by ID response received:', response);
      return response.data; 
    } catch (error) {
      console.error(`API Service: Error fetching ticket category with ID ${id}:`, error);
      throw error;
    }
  },
  
  async searchByName(name) { 
    console.log(`API Service: Searching ticket categories with name ${name}...`);
    try {
      const response = await api.get(`/ticket-categories/search?name=${name}`); 
      console.log('API Service: Ticket categories search response received:', response);
      return response.data; 
    } catch (error) {
      console.error(`API Service: Error searching ticket categories with name ${name}:`, error);
      return [];
    }
  },
  
  async getAvailable() { 
    console.log('API Service: Fetching available ticket categories...');
    try {
      const response = await api.get('/ticket-categories/available'); 
      console.log('API Service: Available ticket categories response received:', response);
      return response.data; 
    } catch (error) {
      console.error('API Service: Error fetching available ticket categories:', error);
      return [];
    }
  },
  
  async getByEvent(eventId) { 
    console.log(`API Service: Fetching ticket categories for event ID ${eventId}...`);
    try {
      const response = await api.get(`/ticket-categories/event/${eventId}`); 
      console.log('API Service: Ticket categories by event response received:', response);
      return response.data; 
    } catch (error) {
      console.error(`API Service: Error fetching ticket categories for event ID ${eventId}:`, error);
      return [];
    }
  },
  
  async getAvailableByEvent(eventId) { 
    console.log(`API Service: Fetching available ticket categories for event ID ${eventId}...`);
    try {
      const response = await api.get(`/ticket-categories/event/${eventId}/available`); 
      console.log('API Service: Available ticket categories by event response received:', response);
      return response.data; 
    } catch (error) {
      console.error(`API Service: Error fetching available ticket categories for event ID ${eventId}:`, error);
      return [];
    }
  },
  
  async create(data) { 
    console.log('API Service: Creating new ticket category:', data);
    try {
      const response = await api.post('/ticket-categories', data); 
      console.log('API Service: Ticket category created successfully:', response);
      return response.data; 
    } catch (error) {
      console.error('API Service: Error creating ticket category:', error);
      throw error;
    }
  },
  
  async update(id, data) { 
    console.log(`API Service: Updating ticket category with ID ${id}:`, data);
    try {
      const response = await api.put(`/ticket-categories/${id}`, data); 
      console.log('API Service: Ticket category updated successfully:', response);
      return response.data; 
    } catch (error) {
      console.error(`API Service: Error updating ticket category with ID ${id}:`, error);
      throw error;
    }
  },
  
  async deactivate(id) { 
    console.log(`API Service: Deactivating ticket category with ID ${id}...`);
    try {
      const response = await api.delete(`/ticket-categories/${id}/deactivate`); 
      console.log('API Service: Ticket category deactivated successfully:', response);
      return response.data; 
    } catch (error) {
      console.error(`API Service: Error deactivating ticket category with ID ${id}:`, error);
      throw error;
    }
  },
  
  async restore(id) { 
    console.log(`API Service: Restoring ticket category with ID ${id}...`);
    try {
      const response = await api.put(`/ticket-categories/restore/${id}`); 
      console.log('API Service: Ticket category restored successfully:', response);
      return response.data; 
    } catch (error) {
      console.error(`API Service: Error restoring ticket category with ID ${id}:`, error);
      throw error;
    }
  },
};

// Rating Service
export const ratingService = {
  async create(ratingValue, entityId, entityType, message) { const params = new URLSearchParams({ ratingValue, entityId, entityType, message }); const response = await api.post('/ratings', null, { params }); return response.data; },
  async getByEntity(entityId, entityType) { const response = await api.get(`/ratings/entity/${entityId}`, { params: { entityType } }); return response.data; },
  async getByUser(userId) { const response = await api.get(`/ratings/user/${userId}`); return response.data; },
  async update(ratingId, message) { const response = await api.put(`/ratings/${ratingId}`, null, { params: { message } }); return response.data; },
  async delete(ratingId) { const response = await api.delete(`/ratings/${ratingId}`); return response.data; },
  async getAverage(entityId, entityType) { const response = await api.get(`/ratings/average/${entityId}`, { params: { entityType } }); return response.data; },
};

// Notification Service
export const notificationService = {
  async getAll() { const response = await api.get('/notifications'); return response.data; },
  async getAllIncludingInactive() { const response = await api.get('/notifications/all'); return response.data; },
  async getById(id) { const response = await api.get(`/notifications/${id}`); return response.data; },
  async getByUser(userId) { const response = await api.get(`/notifications/user/${userId}`); return response.data; },
  async getUnreadByUser(userId) { const response = await api.get(`/notifications/user/${userId}/unread`); return response.data; },
  async create(data) { const response = await api.post('/notifications', data); return response.data; },
  async update(id, data) { const response = await api.put(`/notifications/${id}`, data); return response.data; },
  async markRead(id) { const response = await api.put(`/notifications/${id}/read`); return response.data; },
  async deactivate(id) { const response = await api.delete(`/notifications/${id}/deactivate`); return response.data; },
  async restore(id) { const response = await api.put(`/notifications/restore/${id}`); return response.data; },
};

// Role Service
export const roleService = {
  async getAllRoles() { const response = await api.get('/roles'); return response.data; },
  async getRoleById(id) { const response = await api.get(`/roles/${id}`); return response.data; },
  async createRole(data) { const response = await api.post('/roles', data); return response.data; },
  async updateRole(id, data) { const response = await api.put(`/roles/${id}`, data); return response.data; },
  async deactivateRole(id) { const response = await api.delete(`/roles/${id}`); return response.data; },
  async restoreRole(id) { const response = await api.put(`/roles/restore/${id}`); return response.data; },
};

