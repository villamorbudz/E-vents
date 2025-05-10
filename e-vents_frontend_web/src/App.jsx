import { useLocation } from 'react-router-dom';
import { Routes, Route, Navigate } from 'react-router-dom';
import ProtectedRoute from './components/ProtectedRoute';
import Login from './components/Login';
import SignupStep1 from './components/SignupStep1';
import SignupStep2 from './components/SignupStep2';
import HomeSeller from './components/HomeSeller';
import EventCreation from './components/EventCreation';
import TicketingDetails from './components/TicketingDetails';
import Profile from './components/Profile';
import AdminPage from './components/AdminPage';
import EventsLandingPage from './components/LandingPage';
import HomeUser from './components/HomeUser';
import MyEventsUser from './components/MyEventsUser';
import ProfileUser from './components/ProfileUser';
import EventDetail from './components/EventDetail';
import OrganizerDashboard from './components/OrganizerDashboard';

// Admin components
import UserManagement from './components/admin/UserManagement';
import EventManagement from './components/admin/EventManagement';
import ActManagement from './components/admin/ActManagement';
import CategoryManagement from './components/admin/CategoryManagement';
import TagManagement from './components/admin/TagManagement';
import TicketManagement from './components/admin/TicketManagement';
import TicketCategoryManagement from './components/admin/TicketCategoryManagement';
import RatingManagement from './components/admin/RatingManagement';
import NotificationManagement from './components/admin/NotificationManagement';
import RoleManagement from './components/admin/RoleManagement';
import './styles/AdminPage.css';
import { AnimatePresence } from "framer-motion";
import './App.css';

function App() {
  const location = useLocation(); // get current route
  
  return (
    <AnimatePresence mode="wait">
      <Routes location={location} key={location.pathname}>
        {/* Public routes - anyone can access */}
        <Route path="/" element={<EventsLandingPage />} />
        <Route path="/login" element={<Login />} />
        <Route path="/signup" element={<SignupStep1 />} />
        <Route path="/signup/personal-info" element={<SignupStep2 />} />
        
        {/* Admin-only routes */}
        <Route element={<ProtectedRoute allowedRoles={['ADMIN']} />}>
          <Route path="/admin" element={<AdminPage />} />
          <Route path="/admin/users" element={<UserManagement />} />
          <Route path="/admin/events" element={<EventManagement />} />
          <Route path="/admin/acts" element={<ActManagement />} />
          <Route path="/admin/categories" element={<CategoryManagement />} />
          <Route path="/admin/tags" element={<TagManagement />} />
          <Route path="/admin/tickets" element={<TicketManagement />} />
          <Route path="/admin/ticketcategories" element={<TicketCategoryManagement />} />
          <Route path="/admin/ratings" element={<RatingManagement />} />
          <Route path="/admin/notifications" element={<NotificationManagement />} />
          <Route path="/admin/roles" element={<RoleManagement />} />
        </Route>
        
        {/* User-only routes */}
        <Route element={<ProtectedRoute allowedRoles={['USER']} />}>
          <Route path="/home" element={<HomeUser />} />
          <Route path="/event/:eventId" element={<EventDetail />} />
          <Route path="/myevents" element={<MyEventsUser />} />
          <Route path="/userprofile" element={<ProfileUser />} />
        </Route>
        
        {/* Organizer-only routes */}
        <Route element={<ProtectedRoute allowedRoles={['ORGANIZER']} />}>
          <Route path="/homeseller" element={<HomeSeller />} />
          <Route path="/organizer-dashboard/:eventId" element={<OrganizerDashboard />} />
          <Route path="/create" element={<EventCreation />} />
          <Route path="/create/ticketing" element={<TicketingDetails />} />
          <Route path="/profile" element={<Profile />} />
        </Route>
        
        {/* Catch-all route for undefined paths */}
        <Route path="*" element={<Navigate to="/" replace />} />
      </Routes>
    </AnimatePresence>
  );
}

export default App;