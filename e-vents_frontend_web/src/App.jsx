import { useState } from 'react'
import { useLocation } from 'react-router-dom';
import { Routes, Route } from 'react-router-dom'
import ProtectedRoute from './components/ProtectedRoute';
import Login from './components/Login'
import SignupStep1 from './components/SignupStep1'
import SignupStep2 from './components/SignupStep2'
import HomeSeller from './components/HomeSeller'
import EventCreation from './components/EventCreation'
import TicketingDetails from './components/TicketingDetails';
import Profile from './components/Profile';
import { AnimatePresence } from "framer-motion";
import './App.css'

function App() {
  const location = useLocation(); // get current route

  return (
    <AnimatePresence mode="wait">
      <Routes location={location} key={location.pathname}>
        <Route path="/login" element={<Login />} />
        <Route path="/signup" element={<SignupStep1 />} />
        <Route path="/signup/personal-info" element={<SignupStep2 />} />
        
          <Route path="/homeseller" element={<HomeSeller />} />
          <Route path="/create" element={<EventCreation />} />
          <Route path="/create/ticketing" element={<TicketingDetails />} />
          <Route path="/profile" element={<Profile />} />
        
      </Routes>
    </AnimatePresence>
  );
}

export default App