import { Link, useNavigate } from "react-router-dom";
import { useState } from "react";

export default function EventDetailsPage() {
  const navigate = useNavigate();
  const [selectedTime, setSelectedTime] = useState("");
  const [selectedDate, setSelectedDate] = useState("");
  const [venueName, setVenueName] = useState("");
  const [bannerImage, setBannerImage] = useState(null);
  const [previewUrl, setPreviewUrl] = useState(null);
  const [errors, setErrors] = useState({});

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

  const validateForm = () => {
    const newErrors = {};
    
    if (!venueName.trim()) {
      newErrors.venue = "Venue is required";
    }
    
    if (!selectedDate) {
      newErrors.date = "Date is required";
    }
    
    if (!selectedTime) {
      newErrors.time = "Time is required";
    }
    
    if (!bannerImage) {
      newErrors.banner = "Banner image is required";
    }
    
    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    
    // Validate the form
    if (validateForm()) {
      // Process the form data and continue to next step
      console.log({
        venue: venueName,
        date: selectedDate,
        time: selectedTime,
        bannerImage
      });
      // Navigate to TicketingDetails page
      navigate("/create/details/ticketing");
    } else {
      // Scroll to top to show errors
      window.scrollTo(0, 0);
    }
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
        <div className="max-w-6xl mx-auto">
          <h2 className="text-xl text-gray-300 mb-8">More Details:</h2>
          
          {/* Error Summary */}
          {Object.keys(errors).length > 0 && (
            <div className="bg-red-800 text-white p-4 rounded mb-6">
              <h3 className="font-bold">Please fill in all required fields:</h3>
              <ul className="list-disc pl-5 mt-2">
                {Object.values(errors).map((error, index) => (
                  <li key={index}>{error}</li>
                ))}
              </ul>
            </div>
          )}
          
          <form onSubmit={handleSubmit} className="flex flex-wrap">
            {/* Left Side - Form Fields */}
            <div className="w-full md:w-1/2 pr-0 md:pr-6 space-y-6">
              {/* Venue */}
              <div>
                <label className="block text-gray-400 mb-2">
                  Venue: <span className="text-red-500">*</span>
                </label>
                <input 
                  type="text" 
                  placeholder="Enter Address" 
                  className={`w-full p-2 rounded bg-white text-black ${errors.venue ? 'border-2 border-red-500' : ''}`}
                  value={venueName}
                  onChange={(e) => {
                    setVenueName(e.target.value);
                    if (errors.venue) {
                      setErrors({...errors, venue: null});
                    }
                  }}
                />
                {errors.venue && <p className="text-red-500 text-sm mt-1">{errors.venue}</p>}
              </div>
              
              {/* Date */}
              <div>
                <label className="block text-gray-400 mb-2">
                  Date: <span className="text-red-500">*</span>
                </label>
                <div className="relative">
                  <input 
                    type="date" 
                    className={`w-full p-2 rounded bg-white text-black appearance-none ${errors.date ? 'border-2 border-red-500' : ''}`}
                    value={selectedDate}
                    onChange={(e) => {
                      setSelectedDate(e.target.value);
                      if (errors.date) {
                        setErrors({...errors, date: null});
                      }
                    }}
                  />
                  <div className="absolute inset-y-0 right-0 flex items-center pr-3 pointer-events-none">
                    <svg className="w-5 h-5 text-gray-500" fill="currentColor" viewBox="0 0 20 20">
                      <path fillRule="evenodd" d="M6 2a1 1 0 00-1 1v1H4a2 2 0 00-2 2v10a2 2 0 002 2h12a2 2 0 002-2V6a2 2 0 00-2-2h-1V3a1 1 0 10-2 0v1H7V3a1 1 0 00-1-1zm0 5a1 1 0 000 2h8a1 1 0 100-2H6z" clipRule="evenodd"></path>
                    </svg>
                  </div>
                </div>
                {errors.date && <p className="text-red-500 text-sm mt-1">{errors.date}</p>}
              </div>
              
              {/* Time */}
              <div>
                <label className="block text-gray-400 mb-2">
                  Time: <span className="text-red-500">*</span>
                </label>
                <div className="relative">
                  <select 
                    className={`w-full p-2 rounded bg-white text-black appearance-none ${errors.time ? 'border-2 border-red-500' : ''}`}
                    value={selectedTime}
                    onChange={(e) => {
                      setSelectedTime(e.target.value);
                      if (errors.time) {
                        setErrors({...errors, time: null});
                      }
                    }}
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
                  <div className="absolute inset-y-0 right-0 flex items-center pr-3 pointer-events-none">
                    <svg className="w-4 h-4 text-gray-800" fill="none" stroke="currentColor" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg">
                      <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M19 9l-7 7-7-7"></path>
                    </svg>
                  </div>
                </div>
                {errors.time && <p className="text-red-500 text-sm mt-1">{errors.time}</p>}
              </div>
            </div>
            
            {/* Right Side - Banner Upload */}
            <div className="w-full md:w-1/2 mt-6 md:mt-0">
              <div>
                <label className="block text-gray-400 mb-2">
                  Banner: <span className="text-red-500">*</span>
                </label>
                <div 
                  className={`border-2 ${errors.banner ? 'border-red-500' : 'border-dashed border-gray-400'} rounded-lg h-64 flex flex-col items-center justify-center bg-white text-gray-500 cursor-pointer overflow-hidden`}
                  onClick={() => document.getElementById('banner-upload').click()}
                >
                  {previewUrl ? (
                    <img 
                      src={previewUrl} 
                      alt="Banner Preview" 
                      className="w-full h-full object-cover"
                    />
                  ) : (
                    <div className="text-center p-4">
                      <svg className="mx-auto h-12 w-12 text-gray-400" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M4 16l4.586-4.586a2 2 0 012.828 0L16 16m-2-2l1.586-1.586a2 2 0 012.828 0L20 14m-6-6h.01M6 20h12a2 2 0 002-2V6a2 2 0 00-2-2H6a2 2 0 00-2 2v12a2 2 0 002 2z" />
                      </svg>
                      <p className="mt-1">Click to upload banner image</p>
                      {errors.banner && <p className="text-red-500 text-sm mt-1">Required</p>}
                    </div>
                  )}
                  <input 
                    id="banner-upload" 
                    type="file" 
                    accept="image/*" 
                    className="hidden"
                    onChange={(e) => {
                      handleImageUpload(e);
                      if (errors.banner) {
                        setErrors({...errors, banner: null});
                      }
                    }}
                  />
                </div>
              </div>
            </div>
            
            {/* Submit Button */}
            <div className="w-full flex justify-center mt-12">
              <button 
                type="submit" 
                className="bg-[#FF0033] text-white px-12 py-3 rounded-full font-medium hover:bg-[#DD0033] transition-colors"
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