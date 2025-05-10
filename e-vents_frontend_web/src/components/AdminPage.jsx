import { useState, useEffect } from 'react';
import { userService, api } from "../services/apiService";
import {
  FaUsers,
  FaCalendarAlt,
  FaList,
  FaTags,
  FaTicketAlt,
  FaStar,
  FaBell,
  FaUserTag
} from 'react-icons/fa';
import { motion } from 'framer-motion';
import '../styles/AdminPage.css';
import AdminSidebar from './AdminSidebar';

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
      const [users, events, acts, categories, tags, tickets, ticketCategories, ratings, notifications, roles] = 
        await Promise.allSettled([
          fetchCount('/users/all'),
          fetchCount('/events'),
          fetchCount('/acts'),
          fetchCount('/categories/all'),
          fetchCount('/tags/all'),
          fetchCount('/tickets/all'),
          fetchCount('/ticket-categories/all'),
          fetchCount('/ratings/all'),
          fetchCount('/notifications/all'),
          fetchCount('/roles')
        ]);

      setStats({
        users: users.status === 'fulfilled' ? users.value : 0,
        events: events.status === 'fulfilled' ? events.value : 0,
        acts: acts.status === 'fulfilled' ? acts.value : 0,
        categories: categories.status === 'fulfilled' ? categories.value : 0,
        tags: tags.status === 'fulfilled' ? tags.value : 0,
        tickets: tickets.status === 'fulfilled' ? tickets.value : 0,
        ticketCategories: ticketCategories.status === 'fulfilled' ? ticketCategories.value : 0,
        ratings: ratings.status === 'fulfilled' ? ratings.value : 0,
        notifications: notifications.status === 'fulfilled' ? notifications.value : 0,
        roles: roles.status === 'fulfilled' ? roles.value : 0
      });

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
        <h1 className="admin-title">Admin Dashboard</h1>
        
        {error && <div className="admin-error">{error}</div>}
        
        {loading ? (
          <div className="admin-loading">Loading dashboard data...</div>
        ) : (
          <div className="dashboard-grid">
            <motion.div
              className="stat-card"
              whileHover={{ scale: 1.05 }}
              transition={{ type: "spring", stiffness: 300 }}
            >
              <FaUsers className="stat-icon" />
              <div className="stat-info">
                <h3>Users</h3>
                <p>{stats.users}</p>
              </div>
            </motion.div>

            <motion.div
              className="stat-card"
              whileHover={{ scale: 1.05 }}
              transition={{ type: "spring", stiffness: 300 }}
            >
              <FaCalendarAlt className="stat-icon" />
              <div className="stat-info">
                <h3>Events</h3>
                <p>{stats.events}</p>
              </div>
            </motion.div>

            <motion.div
              className="stat-card"
              whileHover={{ scale: 1.05 }}
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
              whileHover={{ scale: 1.05 }}
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
              whileHover={{ scale: 1.05 }}
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
              whileHover={{ scale: 1.05 }}
              transition={{ type: "spring", stiffness: 300 }}
            >
              <FaTicketAlt className="stat-icon" />
              <div className="stat-info">
                <h3>Tickets</h3>
                <p>{stats.tickets}</p>
              </div>
            </motion.div>

            <motion.div
              className="stat-card"
              whileHover={{ scale: 1.05 }}
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
              whileHover={{ scale: 1.05 }}
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
              whileHover={{ scale: 1.05 }}
              transition={{ type: "spring", stiffness: 300 }}
            >
              <FaBell className="stat-icon" />
              <div className="stat-info">
                <h3>Notifications</h3>
                <p>{stats.notifications}</p>
              </div>
            </motion.div>

            <motion.div
              className="stat-card"
              whileHover={{ scale: 1.05 }}
              transition={{ type: "spring", stiffness: 300 }}
            >
              <FaUserTag className="stat-icon" />
              <div className="stat-info">
                <h3>Roles</h3>
                <p>{stats.roles}</p>
              </div>
            </motion.div>

            <div className="recent-section">
              <h2>Recent Users</h2>
              {recentUsers.length > 0 ? (
                <ul>
                  {recentUsers.map(user => (
                    <li key={user.userId || user.id}>
                      <span>{user.username || user.email}</span>
                      <span>{new Date(user.createdAt).toLocaleDateString()}</span>
                    </li>
                  ))}
                </ul>
              ) : (
                <p>No recent users</p>
              )}
            </div>

            <div className="recent-section">
              <h2>Recent Events</h2>
              {recentEvents.length > 0 ? (
                <ul>
                  {recentEvents.map(event => (
                    <li key={event.eventId || event.id}>
                      <span>{event.title}</span>
                      <span>{new Date(event.createdAt).toLocaleDateString()}</span>
                    </li>
                  ))}
                </ul>
              ) : (
                <p>No recent events</p>
              )}
            </div>
          </div>
        )}
      </motion.div>
    </div>
  );
}

export default AdminPage;