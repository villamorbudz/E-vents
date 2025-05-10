import { useState, useEffect } from "react";
import { useParams, Link, useNavigate } from "react-router-dom";
import StripeWrapper from "./StripeWrapper";
import StripePayment from "./StripePayment";

// Import the mock events data 
import { mockEventsData } from "./HomeUser";

export default function EventDetail() {
  const { eventId } = useParams();
  const [event, setEvent] = useState(null);
  const [loading, setLoading] = useState(true);
  const [selectedTickets, setSelectedTickets] = useState([]);
  const [totalPurchase, setTotalPurchase] = useState(0);
  const [showStripePayment, setShowStripePayment] = useState(false);
  const navigate = useNavigate();

  // Use the mock events data to find the event
  useEffect(() => {
    // Simulating API call latency
    setTimeout(() => {
      const foundEvent = mockEventsData.find(e => e.id === parseInt(eventId));
      setEvent(foundEvent);
      setLoading(false);
    }, 500);
  }, [eventId]);

  // Handle adding tickets to cart
  const handleAddTicket = (ticket) => {
    const existingTicket = selectedTickets.find(t => t.id === ticket.id);
    
    if (existingTicket) {
      // Increment quantity if ticket already exists
      const updatedTickets = selectedTickets.map(t => 
        t.id === ticket.id ? { ...t, quantity: t.quantity + 1 } : t
      );
      setSelectedTickets(updatedTickets);
    } else {
      // Add new ticket with quantity 1
      setSelectedTickets([...selectedTickets, { ...ticket, quantity: 1 }]);
    }
    
    // Update total purchase
    setTotalPurchase(prevTotal => prevTotal + ticket.price);
  };

  // Render stars for rating
  const renderStars = (rating) => {
    const stars = [];
    const fullStars = Math.floor(rating);
    const hasHalfStar = rating % 1 !== 0;
    
    // Add full stars
    for (let i = 0; i < fullStars; i++) {
      stars.push(
        <span key={`star-${i}`} className="text-yellow-500">★</span>
      );
    }
    
    // Add half star if needed
    if (hasHalfStar) {
      stars.push(
        <span key="half-star" className="text-yellow-500">★</span>
      );
    }
    
    // Add empty stars
    const emptyStars = 5 - stars.length;
    for (let i = 0; i < emptyStars; i++) {
      stars.push(
        <span key={`empty-star-${i}`} className="text-gray-400">★</span>
      );
    }
    
    return stars;
  };

  // Handle proceed to checkout
  const handleProceedToCheckout = () => {
    if (selectedTickets.length === 0) {
      alert("Please select at least one ticket.");
      return;
    }
    
    setShowStripePayment(true);
  };

  // Handle successful payment
  const handlePaymentSuccess = (paymentResponse) => {
    // Process purchased events
    const purchasedEvents = selectedTickets.map(ticket => {
      // Parse date from the event date string
      const eventDate = event.date;
      
      return {
        id: event.id,
        title: event.title,
        venue: event.venue,
        location: event.location,
        date: eventDate,
        time: event.time,
        image: event.image,
        ticketType: ticket.type,
        ticketPrice: ticket.price,
        quantity: ticket.quantity,
        paymentId: paymentResponse.paymentIntent.id
      };
    });
    
    // Store in localStorage
    const storedEvents = localStorage.getItem('purchasedEvents');
    let updatedEvents = storedEvents ? JSON.parse(storedEvents) : [];
    
    // Add new purchased events
    updatedEvents = [...updatedEvents, ...purchasedEvents];
    localStorage.setItem('purchasedEvents', JSON.stringify(updatedEvents));
    
    // Navigate to myevents with the latest purchased event
    setTimeout(() => {
      navigate('/myevents', { 
        state: { 
          newPurchasedEvent: purchasedEvents[0], // Pass first ticket as the latest purchase
          paymentSuccess: true
        }
      });
    }, 2000);
  };

  if (loading) {
    return (
      <div className="min-h-screen bg-gray-900 text-white flex items-center justify-center">
        <p className="text-xl">Loading event details...</p>
      </div>
    );
  }

  if (!event) {
    return (
      <div className="min-h-screen bg-gray-900 text-white flex items-center justify-center">
        <p className="text-xl">Event not found</p>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gray-900 text-white">
      {/* Navbar */}
      <div className="bg-red-600 flex justify-between items-center px-10 py-4">
        <div className="flex items-center">
          <Link to="/home" className="text-3xl font-bold">
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
          </Link>
        </div>
        <div className="flex gap-10 text-lg">
          <Link to="/home" className="hover:underline">Home</Link>
          <Link to="/myevents" className="hover:underline">My Events</Link>
          <Link to="/userprofile" className="hover:underline">Profile</Link>
        </div>
      </div>

      {/* Event Detail Section */}
      <div className="p-8">
        <h1 className="text-2xl font-bold mb-6">Buy Tickets</h1>

        <div className="bg-gray-800 rounded-lg overflow-hidden">
          {/* Event Image and Basic Info */}
          <div className="p-6 flex items-start gap-6">
            <div className="w-48">
              <img 
                src={event.image} 
                alt={event.title} 
                className="w-full h-auto object-cover rounded"
              />
            </div>
            <div>
              <h2 className="text-2xl font-bold text-white">{event.title}</h2>
              <p className="text-lg text-white mt-2">{event.venue}</p>
              <p className="text-gray-300">{event.location}</p>
              <p className="text-gray-300 mt-2">{event.date} {event.time}</p>
              <div className="flex mt-2">
                {renderStars(event.rating)}
              </div>
            </div>
          </div>
        </div>

        {/* Buy Tickets Section */}
        <div className="mt-8">
          <h2 className="text-xl font-bold mb-4">Buy Tickets</h2>
          
          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
            {event.tickets.map((ticket) => (
              <div key={ticket.id} className="bg-gray-800 rounded-lg p-4 flex flex-col">
                <h3 className="text-lg font-semibold">{ticket.type}</h3>
                <p className="text-gray-300 mb-2">₱{ticket.price.toLocaleString()}</p>
                <p className="text-gray-400 text-sm flex-grow">{ticket.description}</p>
                <button 
                  onClick={() => handleAddTicket(ticket)}
                  className="mt-4 bg-gray-700 text-white py-1 px-4 rounded hover:bg-gray-600 transition"
                >
                  Get
                </button>
              </div>
            ))}
          </div>
        </div>

        {/* Total Purchase Section */}
        {selectedTickets.length > 0 && (
          <div className="mt-8 bg-gray-800 rounded-lg p-6">
            <h2 className="text-xl font-bold mb-4">Purchase</h2>
            
            <div className="overflow-x-auto">
              <table className="w-full mb-4">
                <thead>
                  <tr className="text-left border-b border-gray-700">
                    <th className="pb-2 w-1/3">Category</th>
                    <th className="pb-2 w-1/4">Price</th>
                    <th className="pb-2 w-1/4">Quantity</th>
                    <th className="pb-2 w-1/4">Subtotal</th>
                  </tr>
                </thead>
                <tbody>
                  {selectedTickets.map((ticket) => (
                    <tr key={ticket.id} className="border-b border-gray-700">
                      <td className="py-2">{ticket.type}</td>
                      <td className="py-2">₱{ticket.price.toLocaleString()}</td>
                      <td className="py-2">{ticket.quantity}</td>
                      <td className="py-2">₱{(ticket.price * ticket.quantity).toLocaleString()}</td>
                    </tr>
                  ))}
                  <tr className="font-bold">
                    <td className="pt-4">Total Purchase</td>
                    <td className="pt-4"></td>
                    <td className="pt-4"></td>
                    <td className="pt-4">₱{totalPurchase.toLocaleString()}</td>
                  </tr>
                </tbody>
              </table>
            </div>
            
            <div className="flex justify-end mt-4">
              {!showStripePayment ? (
                <button 
                  onClick={handleProceedToCheckout}
                  className="bg-gray-100 text-gray-900 py-2 px-6 rounded font-medium hover:bg-white transition"
                >
                  Proceed to Payment
                </button>
              ) : (
                <StripeWrapper>
                  <StripePayment 
                    amount={totalPurchase} 
                    onPaymentSuccess={handlePaymentSuccess}
                  />
                </StripeWrapper>
              )}
            </div>
          </div>
        )}
      </div>
    </div>
  );
}