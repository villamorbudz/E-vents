import { useState } from "react";
import { useNavigate, Link } from 'react-router-dom';

export default function HomeSeller() {
  const navigate = useNavigate();
  const [searchTerm, setSearchTerm] = useState("");
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [currentEvent, setCurrentEvent] = useState(null);
  
  // Form state for editing
  const [editForm, setEditForm] = useState({
    title: "",
    venue: "",
    date: "",
    time: ""
  });

  // Mock event data
  const [events, setEvents] = useState([
    {
      id: 1,
      title: "TYLER, THE CREATOR: CHROMAKOPIA THE WORLD TOUR",
      venue: "ARANETA COLISEUM",
      date: "September 20, 2025",
      time: "8PM"
    },
    {
      id: 2,
      title: "MUSIC FESTIVAL:",
      subtitle: "SM SEASIDE - MOUNTAIN WING",
      venue: "SM SEASIDE",
      date: "September 20, 2025",
      time: "8PM"
    },
    {
      id: 3,
      title: "TAYLOR SWIFT: THE ERAS TOUR",
      venue: "PHILIPPINE ARENA",
      date: "October 15, 2025",
      time: "7PM"
    },
    {
      id: 4,
      title: "ANIME CONVENTION 2025",
      venue: "SMX CONVENTION CENTER",
      date: "November 5, 2025",
      time: "10AM"
    }
  ]);

  const handleEventClick = (eventId) => {
    navigate(`/organizer-dashboard/${eventId}`);
  };

  const handleEditClick = (e, event) => {
    // Stop event propagation to prevent triggering the parent onClick
    e.stopPropagation();
    
    // Set the current event and populate the form
    setCurrentEvent(event);
    setEditForm({
      title: event.title,
      venue: event.venue,
      date: event.date,
      time: event.time
    });
    
    // Open the modal
    setIsModalOpen(true);
  };

  const handleFormChange = (e) => {
    const { name, value } = e.target;
    setEditForm(prev => ({
      ...prev,
      [name]: value
    }));
  };

  const handleSave = () => {
    // Update the events array with the edited event
    setEvents(events.map(event => 
      event.id === currentEvent.id 
        ? { ...event, ...editForm } 
        : event
    ));
    
    // Close the modal
    setIsModalOpen(false);
  };

  // Filter events based on search term
  const filteredEvents = events.filter(event => {
    const searchValue = searchTerm.toLowerCase();
    return (
      event.title.toLowerCase().includes(searchValue) ||
      (event.subtitle && event.subtitle.toLowerCase().includes(searchValue)) ||
      event.venue.toLowerCase().includes(searchValue) ||
      event.date.toLowerCase().includes(searchValue)
    );
  });

  return (
    <div className="min-h-screen bg-gray-900 text-white">
      {/* Navbar */}
      <div className="bg-[#5798FF] flex justify-between items-center px-10 py-4">
        <div className="flex items-center">
          <span className="text-3xl font-bold">
            <span className="flex items-center">
              <svg
                className="w-8 h-8 mr-1"
                viewBox="0 0 24 24"
                fill="white"
                xmlns="http://www.w3.org/2000/svg"
              >
                <rect x="2" y="2" width="9" height="9" />
                <rect x="13" y="2" width="9" height="9" />
                <rect x="2" y="13" width="9" height="9" />
              </svg>
              vents
            </span>
          </span>
        </div>
        <div className="flex gap-10 text-lg">
          <Link to="/homeseller" className="hover:underline text-black">My Events</Link>
          <Link to="/create" className="hover:underline">Create Events</Link>
          <Link to="/profile" className="hover:underline">Profile</Link>
        </div>
      </div>

      {/* Main Content */}
      <div className="bg-gray-900 p-8">
        <div className="flex justify-between items-center mb-8">
          <h1 className="text-3xl font-semibold text-red-600">Events</h1>
          
          {/* Search Bar */}
          <div className="relative">
            <input
              type="text"
              placeholder="Search events..."
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
              className="w-64 px-4 py-2 rounded-full bg-gray-800 text-white border border-gray-700 focus:outline-none focus:border-blue-500"
            />
            <svg
              className="w-5 h-5 absolute right-3 top-2.5 text-gray-400"
              fill="none"
              stroke="currentColor"
              viewBox="0 0 24 24"
              xmlns="http://www.w3.org/2000/svg"
            >
              <path
                strokeLinecap="round"
                strokeLinejoin="round"
                strokeWidth={2}
                d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z"
              />
            </svg>
          </div>
        </div>
        
        {/* Events List */}
        {filteredEvents.length > 0 ? (
          <div className="space-y-6">
            {filteredEvents.map(event => (
              <div 
                key={event.id} 
                className="flex items-start gap-6 bg-opacity-30 p-2 rounded hover:bg-gray-800 transition cursor-pointer"
                onClick={() => handleEventClick(event.id)}
              >
                <div className="flex-1">
                  <h2 className="text-xl font-bold">{event.title}</h2>
                  {event.subtitle && (
                    <h3 className="text-xl font-bold mb-1">{event.subtitle}</h3>
                  )}
                  <p className="text-gray-300">{event.venue}</p>
                  <p className="text-gray-300">{event.date} {event.time}</p>
                </div>
                <div>
                  <button 
                    onClick={(e) => handleEditClick(e, event)}
                    className="bg-blue-600 hover:bg-blue-700 text-white px-4 py-2 rounded flex items-center"
                  >
                    <svg 
                      className="w-4 h-4 mr-1" 
                      fill="none" 
                      stroke="currentColor" 
                      viewBox="0 0 24 24" 
                      xmlns="http://www.w3.org/2000/svg"
                    >
                      <path 
                        strokeLinecap="round" 
                        strokeLinejoin="round" 
                        strokeWidth={2} 
                        d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z" 
                      />
                    </svg>
                    Edit
                  </button>
                </div>
              </div>
            ))}
          </div>
        ) : (
          <div className="text-center py-10">
            <p className="text-xl text-gray-400">No events found matching "{searchTerm}"</p>
          </div>
        )}
      </div>

      {/* Edit Modal - Modified to keep the background visible */}
      {isModalOpen && currentEvent && (
        <div className="fixed inset-0 backdrop-filter backdrop-blur-sm z-50 flex items-center justify-center">
          <div className="bg-gray-800 bg-opacity-90 p-6 rounded-lg shadow-lg w-full max-w-2xl border border-gray-700">
            <div className="flex justify-between items-center mb-4">
              <h2 className="text-xl font-bold">Edit Event</h2>
              <button 
                onClick={() => setIsModalOpen(false)}
                className="text-gray-400 hover:text-white"
              >
                <svg 
                  className="w-6 h-6" 
                  fill="none" 
                  stroke="currentColor" 
                  viewBox="0 0 24 24" 
                  xmlns="http://www.w3.org/2000/svg"
                >
                  <path 
                    strokeLinecap="round" 
                    strokeLinejoin="round" 
                    strokeWidth={2} 
                    d="M6 18L18 6M6 6l12 12" 
                  />
                </svg>
              </button>
            </div>
            
            <div className="space-y-4">
              <div>
                <label className="block text-sm font-medium text-gray-300 mb-1">Event Title</label>
                <input
                  type="text"
                  name="title"
                  value={editForm.title}
                  onChange={handleFormChange}
                  className="w-full px-3 py-2 rounded bg-gray-700 text-white border border-gray-600 focus:outline-none focus:border-blue-500"
                />
              </div>
              
              <div>
                <label className="block text-sm font-medium text-gray-300 mb-1">Venue</label>
                <input
                  type="text"
                  name="venue"
                  value={editForm.venue}
                  onChange={handleFormChange}
                  className="w-full px-3 py-2 rounded bg-gray-700 text-white border border-gray-600 focus:outline-none focus:border-blue-500"
                />
              </div>
              
              
              
              <div className="grid grid-cols-2 gap-4">
                <div>
                  <label className="block text-sm font-medium text-gray-300 mb-1">Date</label>
                  <input
                    type="text"
                    name="date"
                    value={editForm.date}
                    onChange={handleFormChange}
                    className="w-full px-3 py-2 rounded bg-gray-700 text-white border border-gray-600 focus:outline-none focus:border-blue-500"
                  />
                </div>
                
                <div>
                  <label className="block text-sm font-medium text-gray-300 mb-1">Time</label>
                  <input
                    type="text"
                    name="time"
                    value={editForm.time}
                    onChange={handleFormChange}
                    className="w-full px-3 py-2 rounded bg-gray-700 text-white border border-gray-600 focus:outline-none focus:border-blue-500"
                  />
                </div>
              </div>
              
              <div className="flex justify-end gap-3 mt-6">
                <button
                  onClick={() => setIsModalOpen(false)}
                  className="px-4 py-2 bg-gray-600 hover:bg-gray-700 text-white rounded"
                >
                  Cancel
                </button>
                <button
                  onClick={handleSave}
                  className="px-4 py-2 bg-blue-600 hover:bg-blue-700 text-white rounded"
                >
                  Save Changes
                </button>
              </div>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}