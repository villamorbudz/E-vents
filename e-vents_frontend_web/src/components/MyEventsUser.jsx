import { useState, useEffect } from "react";
import { Link, useNavigate, useLocation } from "react-router-dom";
import NotificationsComponent from "../components/NotificationsComponent";
import { mockEventsData } from "./HomeUser"; // Import mock data

export default function MyEventsUser() {
  const [currentDate, setCurrentDate] = useState(new Date());
  const [selectedDate, setSelectedDate] = useState(null);
  const [userEvents, setUserEvents] = useState([]);
  const [eventsByDate, setEventsByDate] = useState({});
  const location = useLocation();
  const navigate = useNavigate();

  // On component mount, check localStorage for purchased events
  useEffect(() => {
    // Load purchased events from localStorage
    const loadPurchasedEvents = () => {
      const storedEvents = localStorage.getItem('purchasedEvents');
      if (storedEvents) {
        const parsedEvents = JSON.parse(storedEvents);
        setUserEvents(parsedEvents);
        
        // Organize events by date for easy lookup
        const eventMap = {};
        parsedEvents.forEach(event => {
          const dateKey = new Date(event.date).toDateString();
          if (!eventMap[dateKey]) {
            eventMap[dateKey] = [];
          }
          eventMap[dateKey].push(event);
        });
        setEventsByDate(eventMap);
      }
    };

    loadPurchasedEvents();

    // Check if we have new purchased event data from state
    if (location.state && location.state.newPurchasedEvent) {
      const newEvent = location.state.newPurchasedEvent;
      
      // Add to localStorage
      const storedEvents = localStorage.getItem('purchasedEvents');
      let updatedEvents = storedEvents ? JSON.parse(storedEvents) : [];
      
      // Check if event is already in the list to avoid duplicates
      const eventExists = updatedEvents.some(event => 
        event.id === newEvent.id && event.ticketType === newEvent.ticketType
      );
      
      if (!eventExists) {
        updatedEvents.push(newEvent);
        localStorage.setItem('purchasedEvents', JSON.stringify(updatedEvents));
        
        // Update state
        setUserEvents(updatedEvents);
        
        // Update events by date
        const dateKey = new Date(newEvent.date).toDateString();
        setEventsByDate(prev => {
          const updated = {...prev};
          if (!updated[dateKey]) {
            updated[dateKey] = [];
          }
          updated[dateKey].push(newEvent);
          return updated;
        });
        
        // Select the date of the new event
        setSelectedDate(new Date(newEvent.date));
        setCurrentDate(new Date(newEvent.date));
      }
    }
  }, [location.state]);

  // Function to generate calendar days
  const generateCalendarDays = () => {
    const year = currentDate.getFullYear();
    const month = currentDate.getMonth();
    
    // First day of the month
    const firstDay = new Date(year, month, 1);
    // Last day of the month
    const lastDay = new Date(year, month + 1, 0);
    
    const daysInMonth = lastDay.getDate();
    const startingDayIndex = firstDay.getDay(); // 0 = Sunday, 1 = Monday, etc.
    
    const calendarDays = [];
    
    // Add empty cells for days before the first day of month
    for (let i = 0; i < startingDayIndex; i++) {
      calendarDays.push(null);
    }
    
    // Add the days of the month
    for (let day = 1; day <= daysInMonth; day++) {
      calendarDays.push(day);
    }
    
    return calendarDays;
  };
  
  // Get month name
  const getMonthName = () => {
    return currentDate.toLocaleString('default', { month: 'long' });
  };
  
  // Navigate to previous month
  const goToPreviousMonth = () => {
    setCurrentDate(new Date(currentDate.getFullYear(), currentDate.getMonth() - 1, 1));
  };
  
  // Navigate to next month
  const goToNextMonth = () => {
    setCurrentDate(new Date(currentDate.getFullYear(), currentDate.getMonth() + 1, 1));
  };
  
  // Check if a day has events (placeholder for now)
  const hasEvent = (day) => {
    if (!day) return false;
    
    const checkDate = new Date(currentDate.getFullYear(), currentDate.getMonth(), day).toDateString();
    return eventsByDate[checkDate] && eventsByDate[checkDate].length > 0;
  };
  
  // Check if a day is today
  const isToday = (day) => {
    if (!day) return false;
    
    const today = new Date();
    return day === today.getDate() && 
           currentDate.getMonth() === today.getMonth() && 
           currentDate.getFullYear() === today.getFullYear();
  };
  
  // Handle day selection
  const handleDayClick = (day) => {
    if (!day) return;
    
    const newSelectedDate = new Date(currentDate.getFullYear(), currentDate.getMonth(), day);
    setSelectedDate(newSelectedDate);
  };

  // Get events for selected date
  const getEventsForSelectedDate = () => {
    if (!selectedDate) return [];
    
    const dateKey = selectedDate.toDateString();
    return eventsByDate[dateKey] || [];
  };

  // Navigate to event details
  const handleEventClick = (eventId) => {
    navigate(`/event/${eventId}`);
  };

  // Format date for display
  const formatDate = (dateString) => {
    const date = new Date(dateString);
    return date.toLocaleDateString('en-US', { 
      weekday: 'long', 
      year: 'numeric', 
      month: 'long', 
      day: 'numeric' 
    });
  };

  return (
    <div className="min-h-screen bg-gray-900 text-white">
      {/* Navbar */}
      <div className="bg-[#BD0027] flex justify-between items-center px-10 py-4">
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
        <div className="flex items-center gap-10 text-lg">
            <Link to="/home" className="hover:underline">Home</Link>
            <Link to="/myevents" className="hover:underline text-black">My Events</Link>
            <Link to="/userprofile" className="hover:underline">Profile</Link>
            <NotificationsComponent />
        </div>
      </div>
      
      {/* Main Content */}
      <div className="bg-gray-900 p-8">
        <h1 className="text-3xl font-semibold text-red-600 mb-8">My Events</h1>
        
        {/* Calendar Section */}
        <div className="max-w-3xl mx-auto bg-gray-800 rounded-lg shadow-lg overflow-hidden">
          {/* Calendar Header */}
          <div className="flex justify-between items-center bg-[#BD0027] p-4">
            <button 
              onClick={goToPreviousMonth}
              className="text-white hover:bg-red-700 p-2 rounded-full"
            >
              <svg className="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M15 19l-7-7 7-7"></path>
              </svg>
            </button>
            
            <h2 className="text-xl font-bold">
              {getMonthName()} {currentDate.getFullYear()}
            </h2>
            
            <button 
              onClick={goToNextMonth}
              className="text-white hover:bg-red-700 p-2 rounded-full"
            >
              <svg className="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M9 5l7 7-7 7"></path>
              </svg>
            </button>
          </div>
          
          {/* Days of Week */}
          <div className="grid grid-cols-7 bg-gray-700">
            {['Sun', 'Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat'].map((day, index) => (
              <div key={index} className="text-center p-2 font-medium">
                {day}
              </div>
            ))}
          </div>
          
          {/* Calendar Days */}
          <div className="grid grid-cols-7 gap-1 p-2">
            {generateCalendarDays().map((day, index) => (
              <div 
                key={index} 
                className={`aspect-square p-2 relative ${
                  !day ? 'opacity-0' : 
                  selectedDate && day === selectedDate.getDate() && currentDate.getMonth() === selectedDate.getMonth() ? 
                  'bg-red-600 text-white rounded-lg' : 
                  isToday(day) ? 
                  'border-2 border-red-500 text-white rounded-lg hover:bg-gray-700 cursor-pointer' : 
                  hasEvent(day) ?
                  'border-2 border-blue-500 text-white rounded-lg hover:bg-gray-700 cursor-pointer' :
                  'hover:bg-gray-700 cursor-pointer rounded-lg'
                }`}
                onClick={() => handleDayClick(day)}
              >
                {day && (
                  <>
                    <span className={isToday(day) ? "font-bold" : ""}>{day}</span>
                    {hasEvent(day) && !isToday(day) && (
                      <div className="absolute bottom-1 right-1 w-2 h-2 bg-blue-400 rounded-full"></div>
                    )}
                  </>
                )}
              </div>
            ))}
          </div>
        </div>
        
        {/* Event List Section - Placeholder for now */}
        <div className="max-w-3xl mx-auto mt-8">
          {selectedDate ? (
            <div>
              <h3 className="text-xl font-semibold mb-4">
                Events for {formatDate(selectedDate)}
              </h3>
              {getEventsForSelectedDate().length > 0 ? (
                <div className="space-y-4">
                  {getEventsForSelectedDate().map((event, index) => (
                    <div 
                      key={index} 
                      className="bg-gray-800 rounded-lg p-4 flex items-center gap-4 cursor-pointer hover:bg-gray-700"
                      onClick={() => handleEventClick(event.id)}
                    >
                      <div>
                        <h4 className="text-lg font-bold">{event.title}</h4>
                        <p className="text-gray-300">{event.venue} • {event.time}</p>
                        <p className="text-gray-400 text-sm">Ticket: {event.ticketType}</p>
                      </div>
                    </div>
                  ))}
                </div>
              ) : (
                <div className="bg-gray-800 rounded-lg p-6 text-center">
                  <p className="text-gray-400">No events found for this date.</p>
                  <Link 
                    to="/home" 
                    className="mt-4 inline-block bg-red-600 text-white px-4 py-2 rounded-lg hover:bg-red-700"
                  >
                    Discover Events
                  </Link>
                </div>
              )}
            </div>
          ) : (
            <div className="bg-gray-800 rounded-lg p-6 text-center">
              <p className="text-gray-400">Select a date to view your events.</p>
            </div>
          )}
        </div>
      </div>
    </div>
  );
}