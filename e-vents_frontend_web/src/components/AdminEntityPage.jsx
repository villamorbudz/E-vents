import React, { useState, useEffect } from 'react';
import { useParams } from 'react-router-dom';
import {
  userService,
  eventService,
  categoryService,
  tagService,
  actService,
  ticketService,
  ticketCategoryService,
  ratingService,
  notificationService,
  roleService
} from '../services/apiService';
import AdminSidebar from './AdminSidebar';
import { FaEdit, FaTrash, FaUndo, FaPlus } from 'react-icons/fa';
import '../styles/AdminEntityPage.css';

const displayFields = {
  users: {
    table: ['userId', 'firstName', 'lastName', 'email', 'active'],
    form: [
      'firstName',
      'lastName',
      'email',
      'birthdate',
      'contactNumber',
      'country',
      'region',
      'city',
      'postalCode',
      'role',
      'password'
    ]
  },
  events: { table: ['eventId', 'name', 'date', 'status'], form: ['name', 'date', 'status'] },
  acts: { table: ['actId', 'name'], form: ['name'] },
  categories: { table: ['categoryId', 'name'], form: ['name'] },
  tags: { table: ['tagId', 'name'], form: ['name'] },
  tickets: { table: ['ticketId', 'userId', 'eventId'], form: ['userId', 'eventId'] },
  ticketcategories: { table: ['ticketCategoryId', 'name'], form: ['name'] },
  ratings: { table: ['ratingId', 'ratingValue', 'entityType'], form: ['ratingValue', 'entityType'] },
  notifications: { table: ['notificationId', 'userId', 'message'], form: ['userId', 'message'] },
  roles: { table: ['roleId', 'name'], form: ['name'] },
};

// Map each entity to its service-fetch function
const serviceMap = {
  users: userService.getAllUsers,
  events: eventService.getAllEvents,
  acts: actService.getAllActs,
  categories: categoryService.getAllIncludingInactive,
  tags: tagService.getAllIncludingInactive,
  tickets: ticketService.getAllIncludingInactive,
  ticketcategories: ticketCategoryService.getAll,
  notifications: notificationService.getAll,
  roles: roleService.getAllRoles,
};

const AdminEntityPage = () => {
  const { entity } = useParams();
  const [items, setItems] = useState([]);
  const [loading, setLoading] = useState(true);
  const [modalOpen, setModalOpen] = useState(false);
  const [modalItem, setModalItem] = useState(null);
  const [modalMode, setModalMode] = useState('view');
  const fields = displayFields[entity] || [];

  useEffect(() => {
    const fetchItems = async () => {
      setLoading(true);
      try {
        const fetchFn = serviceMap[entity];
        if (fetchFn) {
          const data = await fetchFn();
          setItems(data);
        }
      } catch (e) {
        console.error(e);
      }
      setLoading(false);
    };
    if (serviceMap[entity]) fetchItems();
  }, [entity]);

  return (
    <div className="admin-layout">
      <AdminSidebar />
      <div className="admin-container" style={{ marginLeft: '260px' }}>
        <h1 className="admin-title">
          {entity.charAt(0).toUpperCase() + entity.slice(1)} Management
        </h1>
        <button className="admin-button create-button" onClick={() => { setModalMode('create'); setModalOpen(true); }}>
          <FaPlus /> New {entity.slice(0, -1)}
        </button>
        {loading ? (
          <div>Loading...</div>
        ) : (
          <table className="admin-table">
            <thead>
              <tr>
                {fields.map(f => <th key={f}>{f}</th>)}
                <th>Actions</th>
              </tr>
            </thead>
            <tbody>
              {items.map(item => (
                <tr key={item[fields[0]]}>
                  {fields.map(f => <td key={f}>{String(item[f] ?? '')}</td>)}
                  <td className="action-buttons">
                    <button onClick={() => { setModalItem(item); setModalMode('edit'); setModalOpen(true); }}><FaEdit/></button>
                    <button onClick={() => { setModalItem(item); setModalMode('toggle'); setModalOpen(true); }}><FaUndo/></button>
                    <button onClick={() => { setModalItem(item); setModalMode('delete'); setModalOpen(true); }}><FaTrash/></button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
        {modalOpen && (
          <div className="modal-overlay">
            <div className="modal">
              <h2>{modalMode.charAt(0).toUpperCase() + modalMode.slice(1)} {entity.slice(0, -1)}</h2>
              <pre>{JSON.stringify(modalItem, null, 2)}</pre>
              <button onClick={() => setModalOpen(false)}>Close</button>
            </div>
          </div>
        )}
      </div>
    </div>
  );
};

export default AdminEntityPage;
