import { Link, useNavigate } from "react-router-dom";
import { useState } from "react";
import { userService } from "../services/apiService";

export default function EventCreation() {
  const navigate = useNavigate();
  const [eventName, setEventName] = useState("");
  const [eventDescription, setEventDescription] = useState("");
  const [selectedTime, setSelectedTime] = useState("");
  const [selectedDate, setSelectedDate] = useState("");
  const [bannerImage, setBannerImage] = useState(null);
  const [previewUrl, setPreviewUrl] = useState(null);
  const [venueName, setVenueName] = useState("");
  const [hostLineup, setHostLineup] = useState("");
  const [errorMessage, setErrorMessage] = useState("");

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

  const handleSubmit = (e) => {
    e.preventDefault();

    // Get today's date and check if the selected date is at least 7 days ahead
    const today = new Date();
    const selectedDateObj = new Date(selectedDate);
    const diffInTime = selectedDateObj - today;
    const diffInDays = diffInTime / (1000 * 3600 * 24);

    // Validate form fields and date selection
    if (!eventName || !eventDescription || !selectedDate || !selectedTime || !venueName || !bannerImage || !hostLineup) {
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
      hostLineup: hostLineup,
      bannerImage: bannerImage // The actual file upload will happen after event creation
    };

    // Save to local storage for the next step (ticketing)
    localStorage.setItem("eventBasicInfo", JSON.stringify(eventData));
    
    // Navigate to the ticketing page
    navigate("/create/ticketing");
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
                  <rect x="2" y="2" width="9" height="9" />
                  <rect x="13" y="2" width="9" height="9" />
                  <rect x="2" y="13" width="9" height="9" />
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
                    <select
                      className="w-full p-2 rounded bg-white text-black"
                      value={selectedTime}
                      onChange={(e) => setSelectedTime(e.target.value)}
                      required
                    >
                      <option value="">Select</option>
                      <option value="09:00">9:00 AM</option>
                      <option value="10:00">10:00 AM</option>
                      <option value="11:00">11:00 AM</option>
                      <option value="12:00">12:00 PM</option>
                      <option value="13:00">1:00 PM</option>
                      <option value="14:00">2:00 PM</option>
                      <option value="15:00">3:00 PM</option>
                      <option value="16:00">4:00 PM</option>
                      <option value="17:00">5:00 PM</option>
                      <option value="18:00">6:00 PM</option>
                      <option value="19:00">7:00 PM</option>
                      <option value="20:00">8:00 PM</option>
                    </select>
                  </div>
                </div>

                <div>
                  <label className="block text-gray-400 mb-2">Host/Lineup:</label>
                  <input
                    type="text"
                    placeholder="Enter Host/Lineup"
                    className="w-full p-2 rounded bg-white text-black"
                    value={hostLineup}
                    onChange={(e) => setHostLineup(e.target.value)}
                    required
                  />
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