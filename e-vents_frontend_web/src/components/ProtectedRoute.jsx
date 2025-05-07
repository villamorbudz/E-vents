// src/components/ProtectedRoute.jsx
import { Navigate, Outlet } from 'react-router-dom';
import { userService } from '../services/apiService';

// Enhanced ProtectedRoute component that checks both authentication and role
export default function ProtectedRoute({ allowedRoles }) {
  const isAuthenticated = userService.isAuthenticated();
  const userData = JSON.parse(localStorage.getItem('userData') || '{}');
  const userRole = userData.role;

  if (!isAuthenticated) {
    // Redirect to login if not authenticated
    return <Navigate to="/login" replace />;
  }

  // If allowedRoles is provided, check if user has permission
  if (allowedRoles && !allowedRoles.includes(userRole)) {
    // Redirect based on user role
    if (userRole === 'ADMIN') {
      return <Navigate to="/admin" replace />;
    } else if (userRole === 'USER') {
      return <Navigate to="/home" replace />;
    } else if (userRole === 'ORGANIZER') {
      return <Navigate to="/homeseller" replace />;
    } else {
      // Fallback redirect if role is unknown
      return <Navigate to="/login" replace />;
    }
  }

  // User is authenticated and authorized
  return <Outlet />;
}