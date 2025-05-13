import { useState, useEffect } from 'react';
import { userService, api, adminService } from "../services/apiService";
import {
  FaUsers,
  FaCalendarAlt,
  FaList,
  FaTags,
  FaTicketAlt,
  FaStar,
  FaBell,
  FaUserTag,
  FaChartBar,
  FaExclamationTriangle
} from 'react-icons/fa';
import { motion } from 'framer-motion';
import '../styles/AdminPage.css';
import AdminSidebar from './AdminSidebar';
import { Link } from 'react-router-dom';

function AdminPage() {
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [stats, setStats] = useState({
    users: 0,
    events: 0,
    acts: 0,
    categories: 0,
    tags: 0,
    tickets: 0,
    ticketCategories: 0,
    ratings: 0,
    notifications: 0,
    roles: 0
  });
  const [recentUsers, setRecentUsers] = useState([]);
  const [recentEvents, setRecentEvents] = useState([]);

  useEffect(() => {
    if (!userService.isAdmin()) {
      setError('Only administrators can access this page');
      return;
    }
    loadDashboardData();
  }, []);

  const loadDashboardData = async () => {
    setLoading(true);
    setError(null);

    try {
      // Use the adminService.getAllCounts method to get all counts at once
      const counts = await adminService.getAllCounts();
      
      // Set the stats with the returned counts
      setStats(counts);

      await Promise.all([
        loadRecentUsers(),
        loadRecentEvents()
      ]);
    } catch (err) {
      console.error('Error loading dashboard data:', err);
      setError('Failed to load dashboard data');
    } finally {
      setLoading(false);
    }
  };

  const fetchCount = async (endpoint) => {
    try {
      const { data } = await api.get(endpoint);
      return Array.isArray(data) ? data.length : 0;
    } catch (err) {
      console.error(`Error fetching count for ${endpoint}:`, err);
      return 0;
    }
  };

  const loadRecentUsers = async () => {
    try {
      const { data } = await api.get('/users/all');
      const recent = data
        .filter(user => !user.role || user.role.name !== 'ADMIN')
        .sort((a, b) => new Date(b.createdAt || 0) - new Date(a.createdAt || 0))
        .slice(0, 5);
      setRecentUsers(recent);
    } catch (err) {
      console.error('Error loading recent users:', err);
    }
  };

  const loadRecentEvents = async () => {
    try {
      const { data } = await api.get('/events');
      const recent = data
        .sort((a, b) => new Date(b.createdAt || 0) - new Date(a.createdAt || 0))
        .slice(0, 5);
      setRecentEvents(recent);
    } catch (err) {
      console.error('Error loading recent events:', err);
    }
  };

  return (
    <div className="admin-page">
      <AdminSidebar />
      <motion.div 
        className="admin-content"
        initial={{ opacity: 0 }}
        animate={{ opacity: 1 }}
        exit={{ opacity: 0 }}
        transition={{ duration: 0.3 }}
      >
        <div className="admin-header">
          <h1 className="admin-title">Admin Dashboard</h1>
          <div className="admin-date">{new Date().toLocaleDateString('en-US', { weekday: 'long', year: 'numeric', month: 'long', day: 'numeric' })}</div>
        </div>
        
        {error && (
          <div className="admin-alert error">
            <FaExclamationTriangle />
            <span>{error}</span>
          </div>
        )}
        
        {loading ? (
          <div className="admin-loading">
            <div className="spinner"></div>
            <p>Loading dashboard data...</p>
          </div>
        ) : (
          <>
            <div className="dashboard-summary">
              <div className="summary-card primary">
                <div className="summary-icon">
                  <FaUsers />
                </div>
                <div className="summary-content">
                  <h3>{stats.users}</h3>
                  <p>Total Users</p>
                </div>
                <Link to="/admin/users" className="summary-link">View All</Link>
              </div>
              
              <div className="summary-card success">
                <div className="summary-icon">
                  <FaCalendarAlt />
                </div>
                <div className="summary-content">
                  <h3>{stats.events}</h3>
                  <p>Total Events</p>
                </div>
                <Link to="/admin/events" className="summary-link">View All</Link>
              </div>
              
              <div className="summary-card warning">
                <div className="summary-icon">
                  <FaTicketAlt />
                </div>
                <div className="summary-content">
                  <h3>{stats.tickets}</h3>
                  <p>Total Tickets</p>
                </div>
                <Link to="/admin/tickets" className="summary-link">View All</Link>
              </div>
              
              <div className="summary-card info">
                <div className="summary-icon">
                  <FaUserTag />
                </div>
                <div className="summary-content">
                  <h3>{stats.roles}</h3>
                  <p>User Roles</p>
                </div>
                <Link to="/admin/roles" className="summary-link">View All</Link>
              </div>
            </div>

            <div className="dashboard-grid">
              <div className="dashboard-section">
                <div className="section-header">
                  <h2><FaChartBar /> Platform Statistics</h2>
                </div>
                <div className="stats-grid">
                  <motion.div
                    className="stat-card"
                    whileHover={{ scale: 1.03 }}
                    transition={{ type: "spring", stiffness: 300 }}
                  >
                    <FaList className="stat-icon" />
                    <div className="stat-info">
                      <h3>Acts</h3>
                      <p>{stats.acts}</p>
                    </div>
                  </motion.div>

                  <motion.div
                    className="stat-card"
                    whileHover={{ scale: 1.03 }}
                    transition={{ type: "spring", stiffness: 300 }}
                  >
                    <FaList className="stat-icon" />
                    <div className="stat-info">
                      <h3>Categories</h3>
                      <p>{stats.categories}</p>
                    </div>
                  </motion.div>

                  <motion.div
                    className="stat-card"
                    whileHover={{ scale: 1.03 }}
                    transition={{ type: "spring", stiffness: 300 }}
                  >
                    <FaTags className="stat-icon" />
                    <div className="stat-info">
                      <h3>Tags</h3>
                      <p>{stats.tags}</p>
                    </div>
                  </motion.div>

                  <motion.div
                    className="stat-card"
                    whileHover={{ scale: 1.03 }}
                    transition={{ type: "spring", stiffness: 300 }}
                  >
                    <FaTicketAlt className="stat-icon" />
                    <div className="stat-info">
                      <h3>Ticket Categories</h3>
                      <p>{stats.ticketCategories}</p>
                    </div>
                  </motion.div>

                  <motion.div
                    className="stat-card"
                    whileHover={{ scale: 1.03 }}
                    transition={{ type: "spring", stiffness: 300 }}
                  >
                    <FaStar className="stat-icon" />
                    <div className="stat-info">
                      <h3>Ratings</h3>
                      <p>{stats.ratings}</p>
                    </div>
                  </motion.div>

                  <motion.div
                    className="stat-card"
                    whileHover={{ scale: 1.03 }}
                    transition={{ type: "spring", stiffness: 300 }}
                  >
                    <FaBell className="stat-icon" />
                    <div className="stat-info">
                      <h3>Notifications</h3>
                      <p>{stats.notifications}</p>
                    </div>
                  </motion.div>
                </div>
              </div>

              <div className="dashboard-section recent-users">
                <div className="section-header">
                  <h2><FaUsers /> Recent Users</h2>
                  <Link to="/admin/users" className="view-all">View All</Link>
                </div>
                {recentUsers.length > 0 ? (
                  <div className="recent-list">
                    {recentUsers.map(user => (
                      <div className="recent-item" key={user.userId || user.id}>
                        <div className="user-avatar">
                          {(user.firstName || user.username || user.email || '?').charAt(0).toUpperCase()}
                        </div>
                        <div className="user-info">
                          <h4>{user.username || user.email}</h4>
                          <p>{user.firstName} {user.lastName}</p>
                          <span className="date-badge">{new Date(user.createdAt).toLocaleDateString()}</span>
                        </div>
                      </div>
                    ))}
                  </div>
                ) : (
                  <div className="empty-state">
                    <p>No recent users</p>
                  </div>
                )}
              </div>

              <div className="dashboard-section recent-events">
                <div className="section-header">
                  <h2><FaCalendarAlt /> Recent Events</h2>
                  <Link to="/admin/events" className="view-all">View All</Link>
                </div>
                {recentEvents.length > 0 ? (
                  <div className="recent-list">
                    {recentEvents.map(event => (
                      <div className="recent-item" key={event.eventId || event.id}>
                        <div className="event-image">
                          {event.bannerUrl ? (
                            <img src={event.bannerUrl} alt={event.title} />
                          ) : (
                            <div className="event-placeholder">
                              <FaCalendarAlt />
                            </div>
                          )}
                        </div>
                        <div className="event-info">
                          <h4>{event.title}</h4>
                          <p>{event.description && event.description.substring(0, 60)}...</p>
                          <div className="event-meta">
                            <span className="date-badge">{new Date(event.createdAt).toLocaleDateString()}</span>
                            {event.category && <span className="category-badge">{event.category.name}</span>}
                          </div>
                        </div>
                      </div>
                    ))}
                  </div>
                ) : (
                  <div className="empty-state">
                    <p>No recent events</p>
                  </div>
                )}
              </div>
            </div>
          </>
        )}
      </motion.div>
    </div>
  );
}

export default AdminPage;