// src/services/eventService.js
import { api } from './apiService';

export const eventService = {
  // Get all events
  async getAllEvents() {
    try {
      const response = await api.get('/events');
      return response.data;
    } catch (error) {
      console.error('Error fetching events:', error);
      throw error;
    }
  },
  
  // Get event by ID
  async getEventById(eventId) {
    try {
      const response = await api.get(`/events/${eventId}`);
      return response.data;
    } catch (error) {
      console.error(`Error fetching event #${eventId}:`, error);
      throw error;
    }
  },
  
  // Create new event
  async createEvent(eventData, bannerImage) {
    try {
      // First create the event
      const response = await api.post('/events/create', eventData);
      const createdEvent = response.data;
      
      // If we have a banner image, upload it
      if (bannerImage) {
        await this.uploadEventBanner(createdEvent.eventId, bannerImage);
      }
      
      return createdEvent;
    } catch (error) {
      console.error('Error creating event:', error);
      throw error;
    }
  },
  
  // Upload banner image for an event
  async uploadEventBanner(eventId, imageFile) {
    try {
      const formData = new FormData();
      formData.append('file', imageFile);
      
      const response = await api.post(`/events/${eventId}/banner`, formData, {
        headers: {
          'Content-Type': 'multipart/form-data'
        }
      });
      
      return response.data;
    } catch (error) {
      console.error(`Error uploading banner for event #${eventId}:`, error);
      throw error;
    }
  },
  
  // Update existing event
  async updateEvent(eventId, eventData) {
    try {
      const response = await api.put(`/events/${eventId}`, eventData);
      return response.data;
    } catch (error) {
      console.error(`Error updating event #${eventId}:`, error);
      throw error;
    }
  },
  
  // Cancel an event
  async cancelEvent(eventId) {
    try {
      const response = await api.delete(`/events/${eventId}/cancel`);
      return response.data;
    } catch (error) {
      console.error(`Error cancelling event #${eventId}:`, error);
      throw error;
    }
  },
  
  // Postpone an event
  async postponeEvent(eventId) {
    try {
      const response = await api.put(`/events/${eventId}/postpone`);
      return response.data;
    } catch (error) {
      console.error(`Error postponing event #${eventId}:`, error);
      throw error;
    }
  },
  
  // Delete an event permanently
  async deleteEvent(eventId) {
    try {
      const response = await api.delete(`/events/${eventId}/delete`);
      return response.data;
    } catch (error) {
      console.error(`Error deleting event #${eventId}:`, error);
      throw error;
    }
  },
  
  // Get scheduled events
  async getScheduledEvents() {
    try {
      const response = await api.get('/events/scheduled');
      return response.data;
    } catch (error) {
      console.error('Error fetching scheduled events:', error);
      throw error;
    }
  },
  
  // Get postponed events
  async getPostponedEvents() {
    try {
      const response = await api.get('/events/postponed');
      return response.data;
    } catch (error) {
      console.error('Error fetching postponed events:', error);
      throw error;
    }
  },
  
  // Get cancelled events
  async getCancelledEvents() {
    try {
      const response = await api.get('/events/cancelled');
      return response.data;
    } catch (error) {
      console.error('Error fetching cancelled events:', error);
      throw error;
    }
  }
};

export default eventService;