// src/components/ProtectedRoute.jsx

import { Navigate, Outlet } from 'react-router-dom';
import { userService } from '../services/apiService';

export default function ProtectedRoute() {
  if (!userService.isAuthenticated()) {
    // Redirect to login if not authenticated
    return <Navigate to="/" replace />;
  }

  return <Outlet />;
}