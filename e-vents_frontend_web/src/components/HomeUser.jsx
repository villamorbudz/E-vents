import { Link, useNavigate } from "react-router-dom";
import { useState } from "react";
import { userService } from "../services/apiService";
import NotificationsComponent from "../components/NotificationsComponent";

// Mock events data with complete details including tickets
export const mockEventsData = [
  {
    id: 1,
    title: "TYLER, THE CREATOR: CHROMAKOPIA THE WORLD TOUR",
    venue: "ARANETA COLISEUM",
    date: "September 20, 2025",
    time: "8PM",
    rating: 4.5,
    description: "Experience Tyler, The Creator's CHROMAKOPIA Tour live in Manila! Following the success of his album, Tyler brings his unique aesthetic and energy to the stage for an unforgettable performance.",
    tickets: [
      {
        id: 101,
        type: "General Admission",
        price: 1500,
        description: "Standing",
      },
      {
        id: 102,
        type: "VIP",
        price: 10000,
        description: "Access to exclusive area near the stage",
      },
      {
        id: 103,
        type: "Lower Box",
        price: 6000,
        description: "Sitting near the stage",
      },
      {
        id: 104,
        type: "Upper Box",
        price: 4000,
        description: "Sitting far from stage",
      },
    ]
  },
  {
    id: 2,
    title: "MUSIC FESTIVAL:",
    subtitle: "SM SEASIDE - MOUNTAIN WING",
    venue: "SM SEASIDE",
    date: "September 20, 2025",
    time: "8PM",
    rating: 4.0,
    description: "A multi-stage music festival featuring the best local and international artists. Enjoy a full day of music across multiple genres with food stalls and interactive activities.",
    tickets: [
      {
        id: 201,
        type: "Day Pass",
        price: 2500,
        description: "Full day access to all stages",
      },
      {
        id: 202,
        type: "VIP Pass",
        price: 4500,
        description: "Full day access with VIP lounge and backstage tours",
      },
      {
        id: 203,
        type: "Weekend Pass",
        price: 4000,
        description: "Access for both festival days",
      },
      {
        id: 204,
        type: "Weekend VIP",
        price: 7500,
        description: "VIP access for both festival days",
      },
    ]
  },
  {
    id: 3,
    title: "TAYLOR SWIFT: THE ERAS TOUR",
    venue: "PHILIPPINE ARENA",
    date: "October 15, 2025",
    time: "7PM",
    rating: 5.0,
    description: "Taylor Swift brings her record-breaking Eras Tour to the Philippines! Experience a journey through all of Taylor's musical eras in this spectacular 3+ hour show.",
    tickets: [
      {
        id: 301,
        type: "Premium Standing",
        price: 12000,
        description: "Prime viewing on the floor level",
      },
      {
        id: 302,
        type: "Lower Box",
        price: 8500,
        description: "Lower box seats with excellent views",
      },
      {
        id: 303,
        type: "Upper Box",
        price: 5000,
        description: "Upper box seating",
      },
      {
        id: 304,
        type: "General Admission",
        price: 2500,
        description: "General admission seating",
      },
      {
        id: 305,
        type: "VIP",
        price: 15000,
        description: "Premium seats, special merchandise, and early entry",
      },
    ]
  },
  {
    id: 4,
    title: "ANIME CONVENTION 2025",
    venue: "SMX CONVENTION CENTER",
    date: "November 5, 2025",
    time: "10AM",
    rating: 4.2,
    description: "The biggest anime and gaming convention in the Philippines! Meet voice actors, attend panels, shop exclusive merchandise, and participate in cosplay competitions.",
    tickets: [
      {
        id: 401,
        type: "1-Day Pass",
        price: 800,
        description: "Single day admission",
      },
      {
        id: 402,
        type: "3-Day Pass",
        price: 2000,
        description: "Full weekend access to all events",
      },
      {
        id: 403,
        type: "VIP Pass",
        price: 3500,
        description: "Priority entry, exclusive merch, and meet & greet passes",
      },
      {
        id: 404,
        type: "Cosplay Contest Entry",
        price: 500,
        description: "Standard admission plus cosplay contest registration",
      },
    ]
  }
];

export default function HomeUser() {
  const navigate = useNavigate(); // for programmatic navigation

  const [searchTerm, setSearchTerm] = useState("");
  
  // Use the mock events data
  const [events, setEvents] = useState(mockEventsData);
  
  // Updated to navigate to the event detail page instead of organizer dashboard
  const handleEventClick = (eventId) => {
    navigate(`/event/${eventId}`);
  };

  // Filter events based on search term
  const filteredEvents = events.filter(event => {
    const searchValue = searchTerm.toLowerCase();
    return (
      event.title.toLowerCase().includes(searchValue) ||
      (event.subtitle && event.subtitle.toLowerCase().includes(searchValue)) ||
      event.venue.toLowerCase().includes(searchValue) ||
      event.location.toLowerCase().includes(searchValue) ||
      event.date.toLowerCase().includes(searchValue)
    );
  });

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
          <Link to="/home" className="hover:underline text-black">Home</Link>
          <Link to="/myevents" className="hover:underline">My Events</Link>
          <Link to="/userprofile" className="hover:underline">Profile</Link>
          <NotificationsComponent />
        </div>
      </div>

      {/* Main Content */}
      <div className="bg-gray-900 p-8">
        <div className="flex justify-between items-center mb-8">
          <div>
            <h1 className="text-3xl font-semibold text-red-600">Events</h1>
            <h1 className="text-xl font-semibold text-white">Find Your Next Experience</h1>
            <h3 className="text-lg text-gray-400 mt-1">Discover and attend the most exciting events around you.</h3>
          </div>
          
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
        
        {/* Events List - Added from HomeSeller */}
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
                  <p className="text-gray-300">{event.location}</p>
                  <p className="text-gray-300">{event.date} {event.time}</p>
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
    </div>
  );
}