import { NavLink } from 'react-router-dom';
import { userService } from '../services/apiService';
import {
  FaUsers,
  FaCalendarAlt,
  FaTicketAlt,
  FaTags,
  FaList,
  FaBell,
  FaStar,
  FaThLarge,
  FaSignOutAlt,
} from 'react-icons/fa';
import './AdminSidebar.css';

const AdminSidebar = () => {
  const user = JSON.parse(localStorage.getItem('userData') || '{}');

  const logout = () => {
    userService.logout();
  };

  const menu = [
    { label: 'Dashboard', to: '/admin', icon: <FaThLarge /> },
    { label: 'Users', to: '/admin/users', icon: <FaUsers /> },
    { label: 'Events', to: '/admin/events', icon: <FaCalendarAlt /> },
    { label: 'Acts', to: '/admin/acts', icon: <FaList /> },
    { label: 'Categories', to: '/admin/categories', icon: <FaList /> },
    { label: 'Tags', to: '/admin/tags', icon: <FaTags /> },
    { label: 'Tickets', to: '/admin/tickets', icon: <FaTicketAlt /> },
    { label: 'Ticket Categories', to: '/admin/ticketcategories', icon: <FaTicketAlt /> },
    { label: 'Ratings', to: '/admin/ratings', icon: <FaStar /> },
    { label: 'Notifications', to: '/admin/notifications', icon: <FaBell /> },
    { label: 'Roles', to: '/admin/roles', icon: <FaList /> },
  ];

  return (
    <aside className="admin-sidebar">
      <div className="sidebar-menu">
        {menu.map((item) => (
          <NavLink key={item.to} to={item.to} className="sidebar-link">
            {item.icon}
            <span>{item.label}</span>
          </NavLink>
        ))}
      </div>

      <div className="sidebar-footer">
        <div className="sidebar-user">
          <strong>
            {user.firstName} {user.lastName}
          </strong>
          <small>{user.role?.name}</small>
        </div>
        <button className="logout-btn" onClick={logout}>
          <FaSignOutAlt style={{ marginRight: '4px' }} /> Logout
        </button>
      </div>
    </aside>
  );
};

export default AdminSidebar;
