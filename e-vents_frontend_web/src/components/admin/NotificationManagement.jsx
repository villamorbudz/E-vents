import React from 'react';
import BaseEntityManagement from './BaseEntityManagement';
import { notificationService } from '../../services/apiService';

const NotificationManagement = () => {
  const displayFields = ['notificationId', 'userId', 'message'];
  
  const fetchNotifications = async () => {
    try {
      const data = await notificationService.getAllIncludingInactive();
      return data;
    } catch (error) {
      console.error('Error fetching notifications:', error);
      return [];
    }
  };
  
  const handleCreateNotification = async (notificationData) => {
    return await notificationService.create(notificationData);
  };
  
  const handleEditNotification = async (notificationData) => {
    return await notificationService.update(notificationData.notificationId, notificationData);
  };
  
  const handleToggleNotification = async (notificationData) => {
    if (notificationData.active) {
      return await notificationService.deactivate(notificationData.notificationId);
    } else {
      return await notificationService.restore(notificationData.notificationId);
    }
  };
  
  const handleDeleteNotification = async (notificationData) => {
    return await notificationService.deactivate(notificationData.notificationId);
  };
  
  return (
    <BaseEntityManagement
      title="Notification Management"
      entityName="Notification"
      fetchItems={fetchNotifications}
      displayFields={displayFields}
      handleCreate={handleCreateNotification}
      handleEdit={handleEditNotification}
      handleToggle={handleToggleNotification}
      handleDelete={handleDeleteNotification}
    />
  );
};

export default NotificationManagement;
