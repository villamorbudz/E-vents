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

// Event Service
export const eventService = {
  async getAllActs() {
    try {
      const response = await api.get('/acts');
      return response.data;
    } catch (error) {
      throw error.response?.data || 'Error fetching acts';
    }
  },

  async createEvent(eventData, bannerImage) {
    try {
      // First create the event
      const response = await api.post('/events/create', eventData, {
        headers: {
          'Content-Type': 'application/json'
        }
      });
      const eventId = response.data.id;
      
      // If there's a banner image, upload it
      if (bannerImage && eventId) {
        const formData = new FormData();
        formData.append('file', bannerImage); // Changed from 'image' to 'file' to match backend parameter name
        await api.post(`/events/${eventId}/banner`, formData, {
          headers: {
            'Content-Type': 'multipart/form-data'
          }
        });
      }
      
      return response.data;
    } catch (error) {
      throw error.response?.data || 'Error creating event';
    }
  },

  async createTicketCategory(eventId, categoryData) {
    try {
      const response = await api.post(`/events/${eventId}/ticket-categories`, categoryData);
      return response.data;
    } catch (error) {
      throw error.response?.data || 'Error creating ticket category';
    }
  },

  async getEventById(eventId) {
    try {
      const response = await api.get(`/events/${eventId}`);
      return response.data;
    } catch (error) {
      throw error.response?.data || 'Error fetching event';
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
  }
};

// Category Service
export const categoryService = {
  async getAllCategories() { const response = await api.get('/categories'); return response.data; },
  async getAllIncludingInactive() { const response = await api.get('/categories/all'); return response.data; },
  async getCategoryById(id) { const response = await api.get(`/categories/${id}`); return response.data; },
  async createCategory(data) { const response = await api.post('/categories/create', data); return response.data; },
  async updateCategory(id, data) { const response = await api.put(`/categories/${id}/edit`, data); return response.data; },
  async deactivateCategory(id) { const response = await api.delete(`/categories/${id}/deactivate`); return response.data; },
  async deleteCategory(id) { const response = await api.delete(`/categories/${id}/delete`); return response.data; },
  async restoreCategory(id) { const response = await api.put(`/categories/restore/${id}`); return response.data; },
};

// Tag Service
export const tagService = {
  async getAllTags() { const response = await api.get('/tags'); return response.data; },
  async getAllIncludingInactive() { const response = await api.get('/tags/all'); return response.data; },
  async getTagById(id) { const response = await api.get(`/tags/${id}`); return response.data; },
  async getTagsByCategory(categoryId) { const response = await api.get(`/tags/by-category/${categoryId}`); return response.data; },
  async createTag(data) { const response = await api.post('/tags', data); return response.data; },
  async updateTag(id, data) { const response = await api.put(`/tags/${id}`, data); return response.data; },
  async deactivateTag(id) { const response = await api.delete(`/tags/${id}/deactivate`); return response.data; },
  async restoreTag(id) { const response = await api.put(`/tags/restore/${id}`); return response.data; },
};

// Act Service
export const actService = {
  async getAllActs() { const response = await api.get('/acts'); return response.data; },
  async getAllIncludingInactive() { const response = await api.get('/acts/all'); return response.data; },
  async getActById(id) { const response = await api.get(`/acts/${id}`); return response.data; },
  async createAct(data) { const response = await api.post('/acts/create', data); return response.data; },
  async updateAct(id, data) { const response = await api.put(`/acts/${id}/edit`, data); return response.data; },
  async deactivateAct(id) { const response = await api.delete(`/acts/${id}`); return response.data; },
  async restoreAct(id) { const response = await api.put(`/acts/restore/${id}`); return response.data; },
};

// Ticket Service
export const ticketService = {
  async getAllTickets() { const response = await api.get('/tickets'); return response.data; },
  async getAllIncludingInactive() { const response = await api.get('/tickets/all'); return response.data; },
  async getTicketById(id) { const response = await api.get(`/tickets/${id}`); return response.data; },
  async getTicketsByUser(userId) { const response = await api.get(`/tickets/user/${userId}`); return response.data; },
  async getTicketsByEvent(eventId) { const response = await api.get(`/tickets/event/${eventId}`); return response.data; },
  async getTicketsByCategory(catId) { const response = await api.get(`/tickets/category/${catId}`); return response.data; },
  async getTicketsByStatus(status) { const response = await api.get(`/tickets/status/${status}`); return response.data; },
  async createTicket(data) { const response = await api.post('/tickets', data); return response.data; },
  async updateTicket(id, data) { const response = await api.put(`/tickets/${id}`, data); return response.data; },
  async deactivateTicket(id) { const response = await api.delete(`/tickets/${id}/deactivate`); return response.data; },
  async restoreTicket(id) { const response = await api.put(`/tickets/restore/${id}`); return response.data; },
};

// TicketCategory Service
export const ticketCategoryService = {
  async getAll() { const response = await api.get('/ticket-categories'); return response.data; },
  async getAllIncludingInactive() { const response = await api.get('/ticket-categories/all'); return response.data; },
  async getById(id) { const response = await api.get(`/ticket-categories/${id}`); return response.data; },
  async searchByName(name) { const response = await api.get(`/ticket-categories/search?name=${name}`); return response.data; },
  async getAvailable() { const response = await api.get('/ticket-categories/available'); return response.data; },
  async getByEvent(eventId) { const response = await api.get(`/ticket-categories/event/${eventId}`); return response.data; },
  async getAvailableByEvent(eventId) { const response = await api.get(`/ticket-categories/event/${eventId}/available`); return response.data; },
  async create(data) { const response = await api.post('/ticket-categories', data); return response.data; },
  async update(id, data) { const response = await api.put(`/ticket-categories/${id}`, data); return response.data; },
  async deactivate(id) { const response = await api.delete(`/ticket-categories/${id}/deactivate`); return response.data; },
  async restore(id) { const response = await api.put(`/ticket-categories/restore/${id}`); return response.data; },
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

/* Generic error handler
export const errorHandlerService = {
  handleError(error) {
    if (!error) return 'An unknown error occurred';
    if (typeof error === 'string') return error;
    if (error.response) {
      const { status, data } = error.response;
      switch (status) {
        case 400: return data.message || 'Invalid request';
        case 401: return 'Unauthorized';
        case 403: return 'Forbidden';
        case 404: return 'Not found';
        case 500: return 'Server error';
        default: return 'An error occurred';
      }
    }
    return error.message || 'An unexpected error occurred';
  }
};*/
