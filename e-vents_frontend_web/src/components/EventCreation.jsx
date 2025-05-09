import { Link, useNavigate } from "react-router-dom";
import { useState, useEffect } from "react";
import { userService, eventService } from "../services/apiService";
import { FaTimes } from "react-icons/fa";

export default function EventCreation() {
  const navigate = useNavigate();
  const [eventName, setEventName] = useState("");
  const [eventDescription, setEventDescription] = useState("");
  const [selectedTime, setSelectedTime] = useState("");
  const [selectedDate, setSelectedDate] = useState("");
  const [bannerImage, setBannerImage] = useState(null);
  const [previewUrl, setPreviewUrl] = useState(null);
  const [venueName, setVenueName] = useState("");
  const [acts, setActs] = useState([]);
  const [selectedActs, setSelectedActs] = useState([]);
  const [searchTerm, setSearchTerm] = useState("");
  const [isLoading, setIsLoading] = useState(false);
  const [errorMessage, setErrorMessage] = useState("");

  // Fetch acts when component mounts
  useEffect(() => {
    const fetchActs = async () => {
      setIsLoading(true);
      try {
        const actsData = await eventService.getAllActs();
        setActs(actsData);
      } catch (error) {
        console.error('Error fetching acts:', error);
      } finally {
        setIsLoading(false);
      }
    };

    fetchActs();
  }, []);

  // Handle banner image upload
  const handleImageUpload = (e) => {
    const file = e.target.files[0];
    if (file) {
      setBannerImage(file);
      const fileReader = new FileReader();
      fileReader.onload = () => {
        setPreviewUrl(fileReader.result);
      };
      fileReader.readAsDataURL(file);
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setIsLoading(true);
    setErrorMessage("");

    try {
      // Check if user is authenticated
      if (!userService.isAuthenticated()) {
        setErrorMessage("You must be logged in to create an event. Please log in and try again.");
        navigate('/login');
        return;
      }
      // Get today's date and check if the selected date is at least 7 days ahead
      const today = new Date();
      const selectedDateObj = new Date(selectedDate);
      const diffInTime = selectedDateObj - today;
      const diffInDays = diffInTime / (1000 * 3600 * 24);

      // Validate form fields and date selection
      if (!eventName || !eventDescription || !selectedDate || !selectedTime || !venueName || selectedActs.length === 0) {
        setErrorMessage("All fields must be filled.");
        return;
      }

      if (diffInDays < 7) {
        setErrorMessage("Please select a date at least 7 days from now.");
        return;
      }

      const eventData = {
        name: eventName,
        description: eventDescription,
        date: selectedDate,
        time: selectedTime,
        venue: venueName,
        actIds: selectedActs.map(act => act.id),
      };

      // Create the event using the API
      const createdEvent = await eventService.createEvent(eventData, bannerImage);
      
      // Navigate to ticketing details with the event ID
      navigate(`/create/ticketing?eventId=${createdEvent.id}`);
    } catch (error) {
      console.error('Error creating event:', error);
      setErrorMessage(typeof error === 'string' ? error : 'Failed to create event. Please try again.');
    } finally {
      setIsLoading(false);
    }
  };

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
                <rect key="rect-1" x="2" y="2" width="9" height="9" />
                <rect key="rect-2" x="13" y="2" width="9" height="9" />
                <rect key="rect-3" x="2" y="13" width="9" height="9" />
              </svg>
              vents
            </span>
          </span>
        </div>
        <div className="flex gap-10 text-lg">
          <Link to="/homeseller" className="hover:underline">My Events</Link>
          <Link to="/create" className="hover:underline text-black">Create Events</Link>
          <Link to="/profile" className="hover:underline">Profile</Link>
        </div>
      </div>

      {/* Main Content */}
      <div className="bg-gray-900 p-8">
        <div className="max-w-6xl mx-auto">
          {/* Error Message Pop-up */}
          {errorMessage && (
            <div className="fixed top-0 left-0 right-0 bottom-0 flex justify-center items-center z-50">
              <div className="bg-white text-black p-8 rounded-md w-1/3 text-center">
                <p>{errorMessage}</p>
                <button 
                  onClick={() => setErrorMessage("")}
                  className="mt-4 bg-red-600 text-white px-6 py-2 rounded-md"
                >
                  Close
                </button>
              </div>
            </div>
          )}
          
          <form onSubmit={handleSubmit}>
            <div className="flex flex-col md:flex-row gap-8">
              {/* Left Side - Event Details */}
              <div className="w-full md:w-1/2 space-y-6">
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

                <div className="flex gap-4">
                  <div className="w-1/2">
                    <label className="block text-gray-400 mb-2">Event Date:</label>
                    <input
                      type="date"
                      className="w-full p-2 rounded bg-white text-black"
                      value={selectedDate}
                      onChange={(e) => setSelectedDate(e.target.value)}
                      required
                    />
                  </div>
                  <div className="w-1/2">
                    <label className="block text-gray-400 mb-2">Time:</label>
                    <input
                      type="time"
                      className="w-full p-2 rounded bg-white text-black"
                      value={selectedTime}
                      onChange={(e) => setSelectedTime(e.target.value)}
                      required
                    />
                  </div>
                </div>

                <div>
                  <label className="block text-gray-400 mb-2">Host/Lineup:</label>
                  <div className="relative">
                    <input
                      type="text"
                      placeholder="Search for acts"
                      className="w-full p-2 rounded bg-white text-black mb-2"
                      value={searchTerm}
                      onChange={(e) => setSearchTerm(e.target.value)}
                    />
                    {isLoading ? (
                      <div className="w-full p-2 bg-gray-100 text-gray-500 text-center rounded">
                        Loading acts...
                      </div>
                    ) : (
                      <div className="max-h-40 overflow-y-auto bg-white rounded mb-2">
                        {acts
                          .filter(act => 
                            act.name.toLowerCase().includes(searchTerm.toLowerCase()) &&
                            !selectedActs.some(selected => selected.id === act.id)
                          )
                          .map(act => (
                            <div 
                              key={act.id} 
                              className="p-2 hover:bg-gray-100 cursor-pointer text-black"
                              onClick={() => setSelectedActs([...selectedActs, act])}
                            >
                              {act.name}
                            </div>
                          ))
                        }
                        {searchTerm && 
                          acts.filter(act => 
                            act.name.toLowerCase().includes(searchTerm.toLowerCase()) &&
                            !selectedActs.some(selected => selected.id === act.id)
                          ).length === 0 && (
                            <div className="p-2 text-gray-500 text-center">
                              No matching acts found
                            </div>
                          )
                        }
                      </div>
                    )}
                    <div className="flex flex-wrap gap-2 mb-2">
                      {selectedActs.map(act => (
                        <div 
                          key={act.id} 
                          className="bg-blue-500 text-white px-2 py-1 rounded-full flex items-center"
                        >
                          <span className="mr-1">{act.name}</span>
                          <button 
                            type="button"
                            onClick={() => setSelectedActs(selectedActs.filter(a => a.id !== act.id))}
                            className="hover:text-red-200"
                          >
                            <FaTimes size={12} />
                          </button>
                        </div>
                      ))}
                    </div>
                    {selectedActs.length === 0 && (
                      <div className="text-red-400 text-sm">Please select at least one act</div>
                    )}
                  </div>
                </div>

                <div>
                  <label className="block text-gray-400 mb-2">Venue:</label>
                  <input
                    type="text"
                    placeholder="Enter Address"
                    className="w-full p-2 rounded bg-white text-black"
                    value={venueName}
                    onChange={(e) => setVenueName(e.target.value)}
                    required
                  />
                </div>
              </div>

              {/* Right Side - Banner Upload */}
              <div className="w-full md:w-1/2">
                <label className="block text-gray-400 mb-2">Banner:</label>
                <div
                  className="border-2 border-dashed border-gray-400 rounded-lg h-64 flex flex-col items-center justify-center bg-gray-100 text-gray-500 cursor-pointer overflow-hidden"
                  onClick={() => document.getElementById('banner-upload').click()}
                >
                  {previewUrl ? (
                    <img
                      src={previewUrl}
                      alt="Banner Preview"
                      className="w-full h-full object-cover object-center"
                    />
                  ) : (
                    <div className="text-center p-4">
                      <svg
                        className="mx-auto h-12 w-12 text-gray-400"
                        xmlns="http://www.w3.org/2000/svg"
                        fill="none"
                        viewBox="0 0 24 24"
                        stroke="currentColor"
                      >
                        <path
                          strokeLinecap="round"
                          strokeLinejoin="round"
                          strokeWidth="2"
                          d="M4 16l4.586-4.586a2 2 0 012.828 0L16 16m-2-2l1.586-1.586a2 2 0 012.828 0L20 14m-6-6h.01M6 20h12a2 2 0 002-2V6a2 2 0 00-2-2H6a2 2 0 00-2 2v12a2 2 0 002 2z"
                        />
                      </svg>
                      <p className="mt-1">Click to upload banner image</p>
                    </div>
                  )}
                  <input
                    id="banner-upload"
                    type="file"
                    accept="image/*"
                    className="hidden"
                    onChange={handleImageUpload}
                  />
                </div>
              </div>
            </div>

            {/* Submit Button */}
            <div className="flex justify-center mt-8">
              <button
                type="submit"
                className="bg-red-500 text-white px-8 py-2 rounded-full hover:bg-red-600"
              >
                Proceed
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>
  );
}