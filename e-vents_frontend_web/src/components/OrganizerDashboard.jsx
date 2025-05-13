import { useState, useEffect } from "react";
import { useParams } from "react-router-dom";

export default function OrganizerDashboard() {
  const { eventId } = useParams();
  
  // All events with ticket data including prices
  const allEvents = [
    {
      id: 1,
      title: "TYLER, THE CREATOR: CHROMAKOPIA THE WORLD TOUR",
      venue: "ARANETA COLISEUM",
      date: "September 20, 2025",
      time: "8PM",
      tickets: [
        { category: "General Admission", price: 1500, total: 250, sold: 0, left: 250 },
        { category: "VIP", price: 10000, total: 100, sold: 0, left: 100 },
        { category: "Lower Box", price: 6000, total: 150, sold: 0, left: 150 },
        { category: "Upper Box", price: 4000, total: 200, sold: 0, left: 200 }
      ]
    },
    {
      id: 2,
      title: "MUSIC FESTIVAL:",
      subtitle: "SM SEASIDE - MOUNTAIN WING",
      venue: "SM SEASIDE",
      date: "September 20, 2025",
      time: "8PM",
      tickets: [
        { category: "General Admission", price: 1200, total: 500, sold: 0, left: 500 },
        { category: "VIP", price: 8000, total: 200, sold: 0, left: 200 },
        { category: "Premium", price: 5500, total: 100, sold: 0, left: 100 },
        { category: "VVIP", price: 12000, total: 50, sold: 0, left: 50 }
      ]
    },
    {
      id: 3,
      title: "TAYLOR SWIFT: THE ERAS TOUR",
      venue: "PHILIPPINE ARENA",
      date: "October 15, 2025",
      time: "7PM",
      tickets: [
        { category: "General Admission", price: 2500, total: 1000, sold: 0, left: 1000 },
        { category: "VIP", price: 15000, total: 500, sold: 1, left: 499},
        { category: "Lower Box", price: 8500, total: 800, sold: 0, left: 800},
        { category: "Upper Box", price: 5000, total: 1200, sold: 1, left: 1199 },
        { category: "Premium Standing", price: 12000, total: 300, sold: 0, left: 300 }
      ]
    },
    {
      id: 4,
      title: "ANIME CONVENTION 2025",
      venue: "SMX CONVENTION CENTER",
      date: "November 5, 2025",
      time: "10AM",
      tickets: [
        { category: "1-Day Pass", price: 1000, total: 2000, sold: 0, left: 2000 },
        { category: "3-Day Pass", price: 2500, total: 1500, sold: 0, left: 1500 },
        { category: "VIP Pass", price: 5000, total: 500, sold: 0, left: 500 },
        { category: "Meet & Greet", price: 8000, total: 100, sold: 0, left: 100 }
      ]
    }
  ];

  // Find the selected event based on the ID from URL params, or default to the first event
  const [selectedEvent, setSelectedEvent] = useState(() => {
    const id = parseInt(eventId) || 1;
    return allEvents.find(event => event.id === id) || allEvents[0];
  });
  
  // Update selected event when URL parameter changes
  useEffect(() => {
    if (eventId) {
      const id = parseInt(eventId);
      const event = allEvents.find(event => event.id === id);
      if (event) {
        setSelectedEvent(event);
      }
    }
  }, [eventId]);

  // Format number to have commas for thousands
  const formatNumber = (number) => {
    return new Intl.NumberFormat('en').format(number);
  };

  return (
    <div className="min-h-screen bg-gray-900 text-white">
      {/* Navbar */}
      <div className="bg-blue-500 flex justify-between items-center px-10 py-4">
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
          <a href="/homeseller" className="hover:underline">My Events</a>
          <a href="/create" className="hover:underline">Create Events</a>
          <a href="/profile" className="hover:underline">Profile</a>
        </div>
      </div>

      {/* Event Detail Header */}
      <div className="bg-indigo-900 bg-opacity-60 p-8 flex items-start gap-6">

        <div>
          <h1 className="text-2xl font-bold">{selectedEvent.title}</h1>
          {selectedEvent.subtitle && <h2 className="text-xl font-semibold">{selectedEvent.subtitle}</h2>}
          <p className="text-lg">{selectedEvent.venue}</p>
          <p>{selectedEvent.location}</p>
          <p>{selectedEvent.date} {selectedEvent.time}</p>
        </div>
      </div>

      {/* Tickets Table */}
      <div className="p-8">
        <div className="overflow-hidden rounded-lg">
          <table className="w-full text-center">
            <thead>
              <tr className="bg-red-700">
                <th className="py-3 px-4 text-left">Ticket Category</th>
                <th className="py-3 px-4">Price</th>
                <th className="py-3 px-4">Total Tickets</th>
                <th className="py-3 px-4">Tickets Sold</th>
                <th className="py-3 px-4">Tickets Left</th>
                <th className="py-3 px-4">Sales</th>
              </tr>
            </thead>
            <tbody className="bg-gray-800">
              {selectedEvent.tickets.map((ticket, index) => (
                <tr key={index} className="border-b border-gray-700">
                  <td className="py-3 px-4 text-left">
                    <div className="bg-white text-black text-center py-2 px-4 rounded">
                      {ticket.category}
                    </div>
                  </td>
                  <td className="py-3 px-4">
                    <div className="bg-white text-black text-center py-2 px-4 rounded">
                      {formatNumber(ticket.price)}
                    </div>
                  </td>
                  <td className="py-3 px-4">
                    <div className="bg-white text-black text-center py-2 px-4 rounded">
                      {ticket.total}
                    </div>
                  </td>
                  <td className="py-3 px-4">
                    <div className="bg-white text-black text-center py-2 px-4 rounded">
                      {ticket.sold}
                    </div>
                  </td>
                  <td className="py-3 px-4">
                    <div className="bg-white text-black text-center py-2 px-4 rounded">
                      {ticket.left}
                    </div>
                  </td>
                  <td className="py-3 px-4">
                    <div className="bg-white text-black text-center py-2 px-4 rounded">
                      {formatNumber(Number(ticket.price) * Number(ticket.sold))}
                    </div>
                  </td>
                </tr>
              ))}
              <tr className="bg-gray-700 font-bold">
                <td className="py-3 px-4 text-left" colSpan="5">Total Sales</td>
                <td className="py-3 px-4">
                  <div className="bg-green-600 text-white text-center py-2 px-4 rounded">
                    {formatNumber(
                      selectedEvent.tickets.reduce((total, ticket) => total + (Number(ticket.price) * Number(ticket.sold)), 0)
                    )}
                  </div>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );
}