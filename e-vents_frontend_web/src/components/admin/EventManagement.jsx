import React from 'react';
import BaseEntityManagement from './BaseEntityManagement';
import { api, eventService } from '../../services/apiService';

const EventManagement = () => {
  const displayFields = ['eventId', 'name', 'date', 'status'];
  
  const fetchEvents = async () => {
    try {
      // The API doesn't have getAllEvents, so we'll use the endpoint directly
      const { data } = await api.get('/events');
      return data;
    } catch (error) {
      console.error('Error fetching events:', error);
      return [];
    }
  };
  
  const handleCreateEvent = async (eventData) => {
    return await eventService.createEvent(eventData);
  };
  
  const handleEditEvent = async (eventData) => {
    return await eventService.updateEvent(eventData.eventId, eventData);
  };
  
  const handleToggleEvent = async (eventData) => {
    try {
      // Using direct API calls since these methods don't exist in eventService
      if (eventData.status === 'ACTIVE') {
        const { data } = await api.put(`/events/${eventData.eventId}/deactivate`);
        return data;
      } else {
        const { data } = await api.put(`/events/${eventData.eventId}/activate`);
        return data;
      }
    } catch (error) {
      console.error('Error toggling event status:', error);
      throw error;
    }
  };
  
  const handleDeleteEvent = async (eventData) => {
    try {
      const { data } = await api.delete(`/events/${eventData.eventId}`);
      return data;
    } catch (error) {
      console.error('Error deleting event:', error);
      throw error;
    }
  };
  
  return (
    <BaseEntityManagement
      title="Event Management"
      entityName="Event"
      fetchItems={fetchEvents}
      displayFields={displayFields}
      handleCreate={handleCreateEvent}
      handleEdit={handleEditEvent}
      handleToggle={handleToggleEvent}
      handleDelete={handleDeleteEvent}
    />
  );
};

export default EventManagement;
