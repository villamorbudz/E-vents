import { useState } from 'react'
import { useLocation } from 'react-router-dom';
import { Routes, Route, Navigate } from 'react-router-dom'
import ProtectedRoute from './components/ProtectedRoute';
import Login from './components/Login'
import SignupStep1 from './components/SignupStep1'
import SignupStep2 from './components/SignupStep2'
import HomeSeller from './components/HomeSeller'
import EventCreation from './components/EventCreation'
import TicketingDetails from './components/TicketingDetails';
import Profile from './components/Profile';
import AdminPage from './components/AdminPage';
import { userService } from './services/apiService';
import EventsLandingPage from './components/LandingPage';
import './styles/AdminPage.css';
import { AnimatePresence } from "framer-motion";
import './App.css'

function App() {
  const location = useLocation(); // get current route

  const AdminRoute = ({ children }) => {
    // Check if user is logged in and has admin role
    const isAuthenticated = userService.isAuthenticated();
    const userData = localStorage.getItem('userData') ? JSON.parse(localStorage.getItem('userData')) : {};
    const isAdmin = userData.role === 'ADMIN';

    console.log("Admin route check:", { isAuthenticated, userData, isAdmin });
    
    if (!isAdmin) {
      return <Navigate to="/login" />;
    }
    
    return children;
  };

  return (
    <AnimatePresence mode="wait">
      <Routes location={location} key={location.pathname}>
        <Route path="/" element={<EventsLandingPage />} />
        <Route path="/login" element={<Login />} />
        <Route path="/signup" element={<SignupStep1 />} />
        <Route path="/signup/personal-info" element={<SignupStep2 />} />
        <Route path="/admin" element={<AdminRoute><AdminPage /></AdminRoute>}/>
        <Route element={<ProtectedRoute />}>
          <Route path="/homeseller" element={<HomeSeller />} />
          <Route path="/create" element={<EventCreation />} />
          <Route path="/create/ticketing" element={<TicketingDetails />} />
          <Route path="/profile" element={<Profile />} />
        </Route>
      </Routes>
    </AnimatePresence>
  );
}

export default App