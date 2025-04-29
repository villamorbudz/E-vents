import { Link, useNavigate } from "react-router-dom";
import { useState, useEffect } from "react";
import { eventService, userService } from "../services/apiService";
import VenueManagement from "./VenueManagement"; // Import the new component

export default function EventCreation() {
  const navigate = useNavigate();
  const [eventName, setEventName] = useState("");
  const [eventDescription, setEventDescription] = useState("");
  const [selectedTime, setSelectedTime] = useState("");
  const [selectedDate, setSelectedDate] = useState("");
  const [bannerImage, setBannerImage] = useState(null);
  const [previewUrl, setPreviewUrl] = useState(null);
  const [venues, setVenues] = useState([]);
  const [selectedVenue, setSelectedVenue] = useState("");
  const [errorMessage, setErrorMessage] = useState("");
  const [isLoading, setIsLoading] = useState(false);
  const [showVenueForm, setShowVenueForm] = useState(false); // New state for showing venue form

  // Fetch venues from API on component mount
  useEffect(() => {
    fetchVenues();
  }, []);

  // Function to fetch venues
  const fetchVenues = async () => {
    try {
      const venuesData = await eventService.getVenues();
      setVenues(venuesData);
    } catch (error) {
      console.error("Error fetching venues:", error);
      setErrorMessage("Failed to load venues. Please try again later.");
    }
  };

  // Handle venue added
  const handleVenueAdded = (newVenue) => {
    setVenues(prevVenues => [...prevVenues, newVenue]);
    setSelectedVenue(newVenue.id);
  };

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

    // Get today's date and check if the selected date is at least 7 days ahead
    const today = new Date();
    const selectedDateObj = new Date(selectedDate);
    const diffInTime = selectedDateObj - today;
    const diffInDays = diffInTime / (1000 * 3600 * 24);

    // Validate form fields and date selection
    if (!eventName || !eventDescription || !selectedDate || !selectedTime || !selectedVenue || !bannerImage) {
      setErrorMessage("All fields must be filled.");
      setIsLoading(false);
      return;
    }

    if (diffInDays < 7) {
      setErrorMessage("Please select a date at least 7 days from now.");
      setIsLoading(false);
      return;
    }

    try {
      // Get current user data from localStorage
      const userData = JSON.parse(localStorage.getItem("userData") || "{}");
      const userId = userData.userId;

      if (!userId) {
        setErrorMessage("User authentication required. Please log in again.");
        setIsLoading(false);
        return;
      }

      // Create event request object
      const eventRequest = {
        name: eventName,
        description: eventDescription,
        date: selectedDate,
        time: selectedTime,
        venueId: selectedVenue,
        creatorId: userId,
        lineupIds: [] // Can be populated later if needed
      };

      // Store event data in localStorage for multi-step form
      localStorage.setItem("eventBasicInfo", JSON.stringify(eventRequest));
      
      // Navigate to next step
      navigate("/create/ticketing");
    } catch (error) {
      console.error("Error preparing event data:", error);
      setErrorMessage("An error occurred. Please try again.");
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="min-h-screen bg-gray-900 text-white">
      {/* Navbar */}
      <div className="bg-[#5798FF] flex justify-between items-center px-10 py-4">
        <h1 className="text-2xl font-bold">E-Vents</h1>
        <div className="flex gap-10 text-lg">
          <Link to="/homeseller" className="hover:underline">My Events</Link>
          <Link to="/create" className="hover:underline text-black">Create Events</Link>
          <Link to="/profile" className="hover:underline">Profile</Link>
        </div>
      </div>

      {/* Main Content */}
      <div className="bg-gray-900 p-8">
        <h1 className="text-2xl font-semibold text-red-600 mb-8">Create your Event</h1>
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
          
          {/* Venue Management Modal */}
          {showVenueForm && (
            <div className="fixed top-0 left-0 right-0 bottom-0 flex justify-center items-center z-50 bg-black bg-opacity-70">
              <div className="w-full max-w-md">
                <VenueManagement 
                  onVenueAdded={handleVenueAdded} 
                  onClose={() => setShowVenueForm(false)} 
                />
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
                  <textarea
                    placeholder="Enter Description"
                    className="w-full p-2 rounded bg-white text-black"
                    value={eventDescription}
                    onChange={(e) => setEventDescription(e.target.value)}
                    rows="3"
                    required
                  />
                </div>

                <div>
                  <label className="block text-gray-400 mb-2">Date:</label>
                  <input
                    type="date"
                    className="w-full p-2 rounded bg-white text-black"
                    value={selectedDate}
                    onChange={(e) => setSelectedDate(e.target.value)}
                    required
                  />
                </div>

                <div>
                  <label className="block text-gray-400 mb-2">Time:</label>
                  <select
                    className="w-full p-2 rounded bg-white text-black"
                    value={selectedTime}
                    onChange={(e) => setSelectedTime(e.target.value)}
                    required
                  >
                    <option value="">Select</option>
                    <option value="9:00 AM">9:00 AM</option>
                    <option value="10:00 AM">10:00 AM</option>
                    <option value="11:00 AM">11:00 AM</option>
                    <option value="12:00 PM">12:00 PM</option>
                    <option value="1:00 PM">1:00 PM</option>
                    <option value="2:00 PM">2:00 PM</option>
                    <option value="3:00 PM">3:00 PM</option>
                    <option value="4:00 PM">4:00 PM</option>
                    <option value="5:00 PM">5:00 PM</option>
                    <option value="6:00 PM">6:00 PM</option>
                    <option value="7:00 PM">7:00 PM</option>
                    <option value="8:00 PM">8:00 PM</option>
                  </select>
                </div>

                <div>
                  <div className="flex justify-between items-center mb-2">
                    <label className="block text-gray-400">Venue:</label>
                    <button
                      type="button"
                      className="text-[#5798FF] hover:text-blue-400 text-sm"
                      onClick={() => setShowVenueForm(true)}
                    >
                      + Add New Venue
                    </button>
                  </div>
                  <select
                    className="w-full p-2 rounded bg-white text-black"
                    value={selectedVenue}
                    onChange={(e) => setSelectedVenue(e.target.value)}
                    required
                  >
                    <option value="">Select a venue</option>
                    {venues.map((venue) => (
                      <option key={venue.id} value={venue.id}>
                        {venue.name} - {venue.address}
                      </option>
                    ))}
                  </select>
                </div>
              </div>

              {/* Right Side - Banner Upload */}
              <div className="w-full md:w-1/2">
                <label className="block text-gray-400 mb-2">Banner:</label>
                <div
                  className="border-2 border-dashed border-gray-400 rounded-lg h-64 flex flex-col items-center justify-center bg-white text-gray-500 cursor-pointer overflow-hidden"
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
                className="bg-[#BD0027] text-white px-8 py-2 rounded-full"
                disabled={isLoading}
              >
                {isLoading ? "Processing..." : "Proceed"}
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>
  );
}