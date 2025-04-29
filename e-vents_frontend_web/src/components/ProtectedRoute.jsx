// src/components/ProtectedRoute.jsx

import { Navigate, Outlet } from 'react-router-dom';
import { userService } from '../services/apiService';

export default function ProtectedRoute() {
  // Check if user is authenticated (has valid token)
  const isAuthenticated = userService.isAuthenticated();
  
  // If not authenticated, redirect to login page instead of root
  if (!isAuthenticated) {
    console.log('User not authenticated, redirecting to login page');
    return <Navigate to="/login" replace />;
  }
  
  // If authenticated, render the child routes
  return <Outlet />;
}