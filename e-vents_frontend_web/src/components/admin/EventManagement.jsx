import React, { useState, useEffect } from 'react';
import BaseEntityManagement from './BaseEntityManagement';
import EntityModal from './EntityModal';
import { api, eventService, userService } from '../../services/apiService';
import { FaTimes } from 'react-icons/fa';

const EventManagement = () => {
  // Display fields for the table headers
  const displayFields = ['ID', 'Name', 'Date', 'Status'];
  
  // Getter parameters that match the attribute names in the data
  const getterParams = ['eventId', 'name', 'date', 'status'];
  
  const [modalOpen, setModalOpen] = useState(false);
  const [modalMode, setModalMode] = useState('create');
  const [selectedItem, setSelectedItem] = useState({});
  const [formData, setFormData] = useState({});
  const [error, setError] = useState(null);
  const [refreshTrigger, setRefreshTrigger] = useState(0);
  
  // State for select boxes and form data
  const [acts, setActs] = useState([]);
  const [selectedActs, setSelectedActs] = useState([]);
  const [isLoading, setIsLoading] = useState(false);
  // Banner image functionality has been removed
  const [searchTerm, setSearchTerm] = useState('');

  // Fetch acts data for multiselect
  useEffect(() => {
    const fetchActs = async () => {
      try {
        const actsData = await eventService.getAllActs();
        setActs(actsData);
      } catch (error) {
        console.error('Error fetching acts:', error);
      }
    };
    
    fetchActs();
  }, []);
  
  const fetchEvents = async () => {
    try {
      // Use the eventService.getAllEvents method
      const data = await eventService.getAllEvents();
      return data;
    } catch (error) {
      console.error('Error fetching events:', error);
      return [];
    }
  };
  
  const handleInputChange = (e) => {
    const { name, value, type, checked } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: type === 'checkbox' ? checked : value
    }));
  };
  
  // Banner image functionality has been removed

  const handleCreateClick = () => {
    setModalMode('create');
    setSelectedItem({});
    setFormData({
      name: '',
      description: '',
      date: '',
      time: '',
      venue: '',
      status: 'SCHEDULED'
    });
    setSelectedActs([]);
    // Banner image functionality has been removed
    setSearchTerm('');
    setError(null);
    setModalOpen(true);
  };

  const handleEditClick = (item) => {
    setModalMode('edit');
    setSelectedItem(item);
    
    // Format date and time if needed
    const dateTime = item.date ? new Date(item.date) : new Date();
    const formattedDate = dateTime.toISOString().split('T')[0];
    const formattedTime = dateTime.toTimeString().split(' ')[0].substring(0, 5);
    
    // Get acts from the event
    const eventActs = item.acts || [];
    setSelectedActs(eventActs);
    
    // Banner image functionality has been removed
    
    const formattedItem = {
      ...item,
      name: item.name || '',
      date: formattedDate,
      time: formattedTime,
      venue: item.venue || ''
    };
    
    setFormData(formattedItem);
    setError(null);
    setModalOpen(true);
  };

  const handleToggleClick = (item) => {
    setModalMode('toggle');
    setSelectedItem(item);
    setError(null);
    setModalOpen(true);
  };

  const handleDeleteClick = (item) => {
    setModalMode('delete');
    setSelectedItem(item);
    setError(null);
    setModalOpen(true);
  };

  const handleConfirm = async () => {
    setIsLoading(true);
    try {
      let result;
      
      // Check if user is authenticated
      if (!userService.isAuthenticated()) {
        setError('You must be logged in to manage events');
        setIsLoading(false);
        return;
      }
      
      // Prepare data for API according to the required JSON structure
      const eventData = {
        name: formData.name,
        description: formData.description,
        venue: formData.venue,
        status: formData.status || 'SCHEDULED',
        isActive: true,
        user: {
          userId: 1 // Always set userId to 1 for admin event creation
        },
        lineup: selectedActs.map(act => ({
          actId: act.id || act.actId
        }))
      };
      
      // Format date and time properly for JSON
      if (formData.date && formData.time) {
        // Ensure date is in the correct format (YYYY-MM-DD)
        eventData.date = formData.date;
        
        // Ensure time has seconds (HH:MM:SS)
        if (formData.time.split(':').length === 2) {
          eventData.time = `${formData.time}:00`;
        } else {
          eventData.time = formData.time;
        }
        
        console.log(`Formatted date: ${eventData.date}, time: ${eventData.time}`);
      }
      
      // Banner image functionality has been removed
      
      // Validate form fields
      if (!eventData.name || !eventData.description || !eventData.date || !eventData.venue || selectedActs.length === 0) {
        setError('All fields must be filled');
        setIsLoading(false);
        return;
      }
      
      // Log the processed event data
      console.log('Processed event data before submission:', eventData);
      
      switch (modalMode) {
        case 'create':
          console.log('Creating event with data:', JSON.stringify(eventData, null, 2));
          result = await eventService.createEvent(eventData);
          break;
        case 'edit':
          result = await eventService.updateEvent(selectedItem.eventId, eventData);
          // Banner image functionality has been removed
          break;
        case 'toggle':
          if (selectedItem.status === 'ACTIVE') {
            const { data } = await api.put(`/events/${selectedItem.eventId}/deactivate`);
            result = data;
          } else {
            const { data } = await api.put(`/events/${selectedItem.eventId}/activate`);
            result = data;
          }
          break;
        case 'delete':
          await api.delete(`/events/${selectedItem.eventId}`);
          break;
        default:
          break;
      }
      
      // Close modal and refresh data
      setModalOpen(false);
      setRefreshTrigger(prev => prev + 1);
    } catch (error) {
      console.error(`Error ${modalMode} event:`, error);
      setError(error.response?.data || `Error performing ${modalMode} operation`);
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <>
      <BaseEntityManagement
        title="Event Management"
        entityName="Event"
        fetchItems={fetchEvents}
        displayFields={displayFields}
        getterParams={getterParams}
        onCreateClick={handleCreateClick}
        onEditClick={handleEditClick}
        onToggleClick={handleToggleClick}
        onDeleteClick={handleDeleteClick}
        refreshTrigger={refreshTrigger}
      />
      
      <EntityModal
        isOpen={modalOpen}
        onClose={() => setModalOpen(false)}
        title={`${modalMode.charAt(0).toUpperCase() + modalMode.slice(1)} Event`}
        mode={modalMode}
        onConfirm={handleConfirm}
        error={error}
        item={selectedItem}
      >
        {(modalMode === 'create' || modalMode === 'edit') && (
          <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
            {/* Left Side - Event Details */}
            <div className="space-y-4">
              <div className="form-group">
                <label htmlFor="name">Event Name</label>
                <input
                  type="text"
                  id="name"
                  name="name"
                  placeholder="Enter Name"
                  value={formData.name || ''}
                  onChange={handleInputChange}
                  required
                  className="w-full p-2 rounded bg-white text-black"
                />
              </div>
              
              <div className="form-group">
                <label htmlFor="description">Event Description</label>
                <textarea
                  id="description"
                  name="description"
                  placeholder="Enter Description"
                  value={formData.description || ''}
                  onChange={handleInputChange}
                  rows="4"
                  className="w-full p-2 rounded bg-white text-black"
                  required
                />
              </div>
              
              <div className="space-y-4">
                <div className="form-group">
                  <label htmlFor="date">Date</label>
                  <input
                    type="date"
                    id="date"
                    name="date"
                    value={formData.date || ''}
                    onChange={handleInputChange}
                    required
                    className="w-full p-2 rounded bg-white text-black"
                  />
                </div>
                
                <div className="form-group">
                  <label htmlFor="time">Time</label>
                  <input
                    type="time"
                    id="time"
                    name="time"
                    value={formData.time || ''}
                    onChange={handleInputChange}
                    required
                    className="w-full p-2 rounded bg-white text-black"
                  />
                </div>
              </div>
              
              <div className="form-group">
                <label htmlFor="acts">Host/Lineup</label>
                <div className="relative">
                  <input
                    type="text"
                    placeholder="Search for acts"
                    className="w-full p-2 rounded bg-white text-black mb-2"
                    value={searchTerm}
                    onChange={(e) => setSearchTerm(e.target.value)}
                  />
                  {isLoading ? (
                    <div className="w-full p-2 bg-gray-100 text-gray-500 text-center rounded">
                      Loading acts...
                    </div>
                  ) : (
                    <div className="max-h-40 overflow-y-auto bg-white rounded mb-2">
                      {acts
                        .filter(act => 
                          act.name.toLowerCase().includes(searchTerm.toLowerCase()) &&
                          !selectedActs.some(selected => (selected.id === act.id || selected.actId === act.actId))
                        )
                        .map(act => (
                          <div 
                            key={`act-${act.id || act.actId}`} 
                            className="p-2 hover:bg-gray-100 cursor-pointer text-black"
                            onClick={() => setSelectedActs([...selectedActs, act])}
                          >
                            {act.name}
                          </div>
                        ))
                      }
                      {searchTerm && 
                        acts.filter(act => 
                          act.name.toLowerCase().includes(searchTerm.toLowerCase()) &&
                          !selectedActs.some(selected => selected.id === act.id)
                        ).length === 0 && (
                          <div className="p-2 text-gray-500 text-center">
                            No matching acts found
                          </div>
                        )
                      }
                    </div>
                  )}
                  <div className="flex flex-wrap gap-2 mb-2">
                    {selectedActs.map((act, index) => (
                      <div 
                        key={`selected-act-${act.id || act.actId || index}`} 
                        className="bg-blue-500 text-white px-2 py-1 rounded-full flex items-center"
                      >
                        <span className="mr-1">{act.name}</span>
                        <button 
                          type="button"
                          onClick={() => setSelectedActs(selectedActs.filter((a, i) => 
                            i !== index && (a.id !== act.id || a.actId !== act.actId)
                          ))}
                          className="hover:text-red-200"
                        >
                          <FaTimes size={12} />
                        </button>
                      </div>
                    ))}
                  </div>
                  {selectedActs.length === 0 && (
                    <div className="text-red-400 text-sm">Please select at least one act</div>
                  )}
                </div>
              </div>
              
              <div className="form-group">
                <label htmlFor="venue">Venue</label>
                <input
                  type="text"
                  id="venue"
                  name="venue"
                  placeholder="Enter Address"
                  value={formData.venue || ''}
                  onChange={handleInputChange}
                  required
                  className="w-full p-2 rounded bg-white text-black"
                />
              </div>
              
              {/* Status is automatically set to SCHEDULED for new events */}
            </div>
            
            {/* Banner image functionality has been removed */}
            <div>
              {/* This space is intentionally left empty after banner image removal */}
            </div>
          </div>
        )}
      </EntityModal>
    </>
  );
};

export default EventManagement;
