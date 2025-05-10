import React from 'react';
import BaseEntityManagement from './BaseEntityManagement';
import { ticketService } from '../../services/apiService';

const TicketManagement = () => {
  const displayFields = ['ticketId', 'userId', 'eventId'];
  
  const fetchTickets = async () => {
    try {
      const data = await ticketService.getAllIncludingInactive();
      return data;
    } catch (error) {
      console.error('Error fetching tickets:', error);
      return [];
    }
  };
  
  const handleCreateTicket = async (ticketData) => {
    return await ticketService.createTicket(ticketData);
  };
  
  const handleEditTicket = async (ticketData) => {
    return await ticketService.updateTicket(ticketData.ticketId, ticketData);
  };
  
  const handleToggleTicket = async (ticketData) => {
    if (ticketData.active) {
      return await ticketService.deactivateTicket(ticketData.ticketId);
    } else {
      return await ticketService.restoreTicket(ticketData.ticketId);
    }
  };
  
  const handleDeleteTicket = async (ticketData) => {
    return await ticketService.deactivateTicket(ticketData.ticketId);
  };
  
  // Editable fields (no ID fields)
  const ticketFormFields = ['userId', 'eventId'];

  return (
    <BaseEntityManagement
      title="Ticket Management"
      entityName="Ticket"
      fetchItems={fetchTickets}
      displayFields={displayFields}
      handleCreate={handleCreateTicket}
      handleEdit={handleEditTicket}
      handleToggle={handleToggleTicket}
      handleDelete={handleDeleteTicket}
    />
  );
};

export default TicketManagement;
