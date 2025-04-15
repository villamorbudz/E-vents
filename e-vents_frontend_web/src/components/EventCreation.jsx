import { Link, useNavigate } from "react-router-dom";
import { useState } from "react";

export default function EventCreation() {
  const navigate = useNavigate();
  const [eventName, setEventName] = useState("");
  const [eventDescription, setEventDescription] = useState("");
  const [eventType, setEventType] = useState("");
  const [tags, setTags] = useState("");
  const [country, setCountry] = useState("");

  const handleSubmit = (e) => {
    e.preventDefault();
    
    // You can store form data in state or context if needed
    const eventData = {
      name: eventName,
      description: eventDescription,
      type: eventType,
      tags: tags,
      country: country
    };
    
    // Optional: Store data in localStorage or context
    localStorage.setItem("eventBasicInfo", JSON.stringify(eventData));
    
    // Navigate to the event details page
    navigate("/create/details");
  };

  return (
    <div className="min-h-screen bg-gray-900 text-white">
      {/* Navbar */}
      <div className="bg-[#BD0027] flex justify-between items-center px-10 py-4">
        <h1 className="text-2xl font-bold">E-Vents</h1>
        <div className="flex gap-10 text-lg">
          <Link to="/homeseller" className="hover:underline">Home</Link>
          <Link to="/events" className="hover:underline">Events</Link>
          <Link to="/create" className="hover:underline">Create Events</Link>
          <Link to="/profile" className="hover:underline">Profile</Link>
        </div>
      </div>
      
      {/* Main Content */}
      <div className="bg-gray-900 p-8">
        <div className="flex gap-8 max-w-6xl mx-auto">
          {/* Left Side - Logo Upload */}
          <div className="w-1/3">
            <div className="bg-[#BD0027] p-6 rounded-lg flex flex-col items-center justify-center h-full">
              <div className="text-center mb-4">
                <p className="text-xl">Logo here</p>
              </div>
              
              <div className="text-center mt-auto">
                <h2 className="text-2xl font-bold mb-2">E-Vents</h2>
                <p className="text-lg">Create and<br />host your event.</p>
              </div>
            </div>
          </div>
          
          {/* Right Side - Form */}
          <div className="w-2/3">
            <form className="space-y-6" onSubmit={handleSubmit}>
              {/* Event Name */}
              <div>
                <label className="block text-gray-400 mb-1">Event Name:</label>
                <input 
                  type="text" 
                  placeholder="Enter Name" 
                  className="w-full p-2 rounded bg-white text-black"
                  value={eventName}
                  onChange={(e) => setEventName(e.target.value)}
                  required
                />
              </div>
              
              {/* Event Description */}
              <div>
                <label className="block text-gray-400 mb-1">Event Description:</label>
                <input 
                  type="text" 
                  placeholder="Enter Description" 
                  className="w-full p-2 rounded bg-white text-black"
                  value={eventDescription}
                  onChange={(e) => setEventDescription(e.target.value)}
                  required
                />
              </div>
              
              {/* Event Type */}
              <div>
                <label className="block text-gray-400 mb-1">Event type:</label>
                <div className="relative">
                  <select 
                    className="w-full p-2 rounded bg-white text-black appearance-none"
                    value={eventType}
                    onChange={(e) => setEventType(e.target.value)}
                    required
                  >
                    <option value="">Select</option>
                    <option value="conference">Conference</option>
                    <option value="concert">Concert</option>
                    <option value="workshop">Workshop</option>
                    <option value="sport">Sport</option>
                  </select>
                  <div className="absolute inset-y-0 right-0 flex items-center pr-3 pointer-events-none">
                    <svg className="w-4 h-4 text-gray-800" fill="none" stroke="currentColor" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg">
                      <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M19 9l-7 7-7-7"></path>
                    </svg>
                  </div>
                </div>
              </div>
              
              {/* Tags */}
              <div>
                <label className="block text-gray-400 mb-1">Tags:</label>
                <div className="relative">
                  <select 
                    className="w-full p-2 rounded bg-white text-black appearance-none"
                    value={tags}
                    onChange={(e) => setTags(e.target.value)}
                    required
                  >
                    <option value="">Select</option>
                    <option value="music">Music</option>
                    <option value="business">Business</option>
                    <option value="tech">Technology</option>
                    <option value="art">Art</option>
                  </select>
                  <div className="absolute inset-y-0 right-0 flex items-center pr-3 pointer-events-none">
                    <svg className="w-4 h-4 text-gray-800" fill="none" stroke="currentColor" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg">
                      <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M19 9l-7 7-7-7"></path>
                    </svg>
                  </div>
                </div>
              </div>
              
              {/* Country */}
              <div>
                <label className="block text-gray-400 mb-1">Country:</label>
                <div className="relative">
                  <select 
                    className="w-full p-2 rounded bg-white text-black appearance-none"
                    value={country}
                    onChange={(e) => setCountry(e.target.value)}
                    required
                  >
                    <option value="">Select</option>
                    <option value="us">United States</option>
                    <option value="ca">Canada</option>
                    <option value="uk">United Kingdom</option>
                    <option value="au">Australia</option>
                  </select>
                  <div className="absolute inset-y-0 right-0 flex items-center pr-3 pointer-events-none">
                    <svg className="w-4 h-4 text-gray-800" fill="none" stroke="currentColor" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg">
                      <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M19 9l-7 7-7-7"></path>
                    </svg>
                  </div>
                </div>
              </div>
              
              {/* Submit Button */}
              <div className="flex justify-center mt-8">
                <button 
                  type="submit" 
                  className="bg-[#BD0027] text-white px-8 py-2 rounded-full"
                >
                  Proceed
                </button>
              </div>
            </form>
          </div>
        </div>
      </div>
    </div>
  );
}