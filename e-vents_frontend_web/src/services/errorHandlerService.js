// src/services/errorHandlerService.js

/**
 * Service for handling API errors consistently across the application
 */
export const errorHandlerService = {
  /**
   * Handles error objects from API calls and returns user-friendly error messages
   * @param {Error|Object} error - The error object from axios or other sources
   * @return {string} A user-friendly error message
   */
  handleError(error) {
    // If we have a response with data, use that first
    if (error.response && error.response.data) {
      // Check if data is a string already
      if (typeof error.response.data === 'string') {
        return error.response.data;
      }
      
      // Check if there's an error message in the data
      if (error.response.data.message) {
        return error.response.data.message;
      }
      
      // Check if there's an error key with a message
      if (error.response.data.error) {
        return typeof error.response.data.error === 'string' 
          ? error.response.data.error 
          : JSON.stringify(error.response.data.error);
      }
      
      // If data is an object but doesn't have specific error fields, stringify it
      return JSON.stringify(error.response.data);
    }
    
    // Handle network errors
    if (error.request && !error.response) {
      return 'Network error: Please check your connection';
    }
    
    // Handle axios timeout
    if (error.code === 'ECONNABORTED') {
      return 'Request timed out: Please try again';
    }
    
    // Handle other error cases
    return error.message || 'An unknown error occurred';
  }
};