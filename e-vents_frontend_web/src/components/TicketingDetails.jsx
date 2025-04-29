import { useState, useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { eventService, userService } from '../services/apiService';

export default function TicketingDetails() {
  const navigate = useNavigate();
  const [eventBasicInfo, setEventBasicInfo] = useState(null);
  const [categories, setCategories] = useState([
    {
      name: 'General Admission',
      price: '',
      capacity: '',
      description: ''
    }
  ]);
  const [newCategoryName, setNewCategoryName] = useState('');
  const [photos, setPhotos] = useState([]);
  const [editingIndex, setEditingIndex] = useState(0); // Track which category is being edited
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [errorMessage, setErrorMessage] = useState('');
  const [successMessage, setSuccessMessage] = useState('');

  // Check authentication on component mount
  useEffect(() => {
    // First verify the user is authenticated
    if (!userService.isAuthenticated()) {
      console.log('User not authenticated, redirecting to login');
      navigate('/login');
      return;
    }

    // Load the event basic info from localStorage
    const storedEventInfo = localStorage.getItem('eventBasicInfo');
    if (storedEventInfo) {
      setEventBasicInfo(JSON.parse(storedEventInfo));
    } else {
      // Redirect back to event creation if no data is found
      navigate('/create');
    }
  }, [navigate]);

  const handleAddCategory = () => {
    if (newCategoryName.trim() !== '') {
      setCategories([...categories, {
        name: newCategoryName,
        price: '',
        capacity: '',
        description: ''
      }]);
      setNewCategoryName('');
      // Set the newly added category as the one being edited
      setEditingIndex(categories.length);
    }
  };

  const handleRemoveCategory = (indexToRemove) => {
    // Remove the category and its details
    const updatedCategories = categories.filter((_, index) => index !== indexToRemove);
    setCategories(updatedCategories);
    
    // Adjust editingIndex if necessary
    if (editingIndex === indexToRemove) {
      // If we're removing the currently edited category, select the first category
      setEditingIndex(0);
    } else if (editingIndex > indexToRemove) {
      // If we're removing a category before the currently edited one, adjust the index
      setEditingIndex(editingIndex - 1);
    }
  };

  const handleUpdateCategory = (field, value) => {
    const updatedCategories = [...categories];
    updatedCategories[editingIndex] = {
      ...updatedCategories[editingIndex],
      [field]: value
    };
    setCategories(updatedCategories);
  };

  const handlePhotoUpload = (e) => {
    const files = e.target.files;
    if (files && files.length > 0) {
      // In a real implementation, you would handle multiple file uploads
      // For now, we'll just keep track of the file objects
      const newPhotos = [...photos];
      for (let i = 0; i < files.length; i++) {
        newPhotos.push(files[i]);
      }
      setPhotos(newPhotos);
    }
  };

  // Modified handleSubmit with better error handling
  const handleSubmit = async () => {
    setIsSubmitting(true);
    setErrorMessage('');

    // Validate ticket categories
    const invalidCategories = categories.filter(
      cat => !cat.name || !cat.price || !cat.capacity
    );
    
    if (invalidCategories.length > 0) {
      setErrorMessage('All ticket categories must have a name, price, and capacity.');
      setIsSubmitting(false);
      return;
    }

    try {
      if (!eventBasicInfo) {
        throw new Error('Event basic information not found');
      }

      // Verify authentication token exists
      if (!userService.isAuthenticated()) {
        console.log('User authentication check failed, redirecting to login');
        navigate('/login');
        return;
      }

      // Create the complete event data
      const completeEventData = {
        ...eventBasicInfo,
        ticketCategories: categories
      };

      // Create event request object
      const eventRequest = eventService.createEventRequest(eventBasicInfo);
      
      try {
        console.log('Submitting event creation request');
        const createdEvent = await eventService.createEventFromRequest(eventRequest);
        
        // If event was created successfully, we can now handle the banner image upload
        if (createdEvent && createdEvent.id) {
          // Get the banner image from localStorage or the component state
          const bannerImageFile = localStorage.getItem('eventBannerImage');
          
          if (bannerImageFile) {
            try {
              // Upload the banner image
              await eventService.uploadBannerImage(createdEvent.id, bannerImageFile);
              console.log('Banner image uploaded successfully');
            } catch (imageError) {
              console.error('Error uploading banner image:', imageError);
              // Continue with the process even if image upload fails
            }
          }

          // Save ticket categories - this would require an additional API endpoint
          try {
            // This would be where you save ticket categories if the API supports it
            console.log('Would save ticket categories for event ID:', createdEvent.id);
            // Example API call (placeholder):
            // await eventService.addTicketCategories(createdEvent.id, categories);
          } catch (ticketError) {
            console.error('Error saving ticket categories:', ticketError);
          }

          // Show success message
          setSuccessMessage('Event created successfully!');
          
          // Clean up localStorage
          localStorage.removeItem('eventBasicInfo');
          localStorage.removeItem('eventBannerImage');
          
          // Redirect to the my events page after a short delay
          setTimeout(() => {
            navigate('/homeseller'); // Navigate to the seller's events page
          }, 2000);
        } else {
          throw new Error('Failed to create event - response incomplete');
        }
      } catch (error) {
        console.error('Event creation error:', error);
        
        // Check for session expiration errors specifically
        if (error.message && error.message.includes('session has expired')) {
          // Handle session expiration
          setErrorMessage('Your session has expired. Please log in again to continue.');
          
          // Delay navigation to allow the error message to be seen
          setTimeout(() => {
            navigate('/');
          }, 2000);
        } else {
          setErrorMessage(`Failed to create event: ${error.message || 'Unknown error'}`);
        }
      }
    } catch (error) {
      console.error('Submit handler error:', error);
      setErrorMessage(`Failed to create event: ${error.message || 'Unknown error'}`);
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <div className="flex flex-col min-h-screen bg-gray-900 text-white">
      {/* Navbar */}
      <div className="bg-[#5798FF] flex justify-between items-center px-10 py-4">
        <h1 className="text-2xl font-bold">E-Vents</h1>
        <div className="flex gap-10 text-lg">
          <Link to="/homeseller" className="hover:underline">My Events</Link>
          <Link to="/create" className="hover:underline text-black">Create Events</Link>
          <Link to="/profile" className="hover:underline">Profile</Link>
        </div>
      </div>
      
      {/* Error Message */}
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
      
      {/* Success Message */}
      {successMessage && (
        <div className="fixed top-0 left-0 right-0 bottom-0 flex justify-center items-center z-50">
          <div className="bg-white text-black p-8 rounded-md w-1/3 text-center">
            <p className="text-green-600 font-bold text-xl">{successMessage}</p>
            <p className="mt-2">Redirecting to your events page...</p>
          </div>
        </div>
      )}
      
      {/* Main content */}
      <div className="flex-1 p-8 bg-gray-900">
        <h1 className="text-2xl mb-8 text-gray-300">Ticket Details</h1>
        
        <div className="flex justify-center">
          {/* Left column */}
          <div className="w-full md:w-1/2 space-y-6">
            <div className="mb-6">
              <h2 className="mb-2">Ticket Categories:</h2>
              <div className="flex items-center mb-4">
                <input
                  type="text"
                  placeholder="Add Category"
                  className="p-2 rounded bg-gray-200 text-gray-800 flex-1"
                  value={newCategoryName}
                  onChange={(e) => setNewCategoryName(e.target.value)}
                />
                <button 
                  onClick={handleAddCategory}
                  className="ml-2 bg-gray-200 hover:bg-gray-300 text-gray-500 p-2 rounded-full"
                >
                  <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                    <line x1="12" y1="5" x2="12" y2="19" />
                    <line x1="5" y1="12" x2="19" y2="12" />
                  </svg>
                </button>
              </div>
            
              <h3 className="mb-2">Existing Categories</h3>
              {categories.map((category, index) => (
                <div 
                  key={index} 
                  className={`flex items-center justify-between p-2 rounded mb-2 ${
                    editingIndex === index ? 'bg-blue-600 text-white' : 'bg-gray-200 text-gray-800'
                  }`}
                  onClick={() => setEditingIndex(index)}
                >
                  <span>{category.name}</span>
                  <button 
                    onClick={(e) => {
                      e.stopPropagation();
                      handleRemoveCategory(index);
                    }}
                    className="text-red-500 hover:text-red-700"
                  >
                    <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                      <circle cx="12" cy="12" r="10" />
                      <line x1="8" y1="12" x2="16" y2="12" />
                    </svg>
                  </button>
                </div>
              ))}
            </div>
            
            {categories.length > 0 && (
              <div className="bg-gray-200 text-gray-800 p-4 rounded">
                <h3 className="mb-3 font-semibold">Editing: {categories[editingIndex].name}</h3>
                
                <div className="mb-4">
                  <label className="block mb-1">Ticket Price:</label>
                  <input 
                    type="number" 
                    placeholder="Set price" 
                    className="border p-1 w-full"
                    value={categories[editingIndex].price}
                    onChange={(e) => handleUpdateCategory('price', e.target.value)} 
                  />
                </div>
                
                <div className="mb-4">
                  <label className="block mb-1">Capacity:</label>
                  <input 
                    type="number" 
                    placeholder="Set capacity" 
                    className="border p-1 w-full"
                    value={categories[editingIndex].capacity}
                    onChange={(e) => handleUpdateCategory('capacity', e.target.value)} 
                  />
                </div>
                
                <div className="mb-4">
                  <label className="block mb-1">Description:</label>
                  <textarea 
                    placeholder="Set description" 
                    className="border p-1 w-full"
                    rows="3"
                    value={categories[editingIndex].description}
                    onChange={(e) => handleUpdateCategory('description', e.target.value)} 
                  />
                </div>
              </div>
            )}
          </div>
        </div>
        
        {/* Submit Button */}
        <div className="flex justify-center mt-16">
          <button 
            onClick={handleSubmit}
            disabled={isSubmitting}
            className="bg-red-500 hover:bg-red-600 text-white px-6 py-3 rounded-full"
          >
            {isSubmitting ? "Processing..." : "Create Event"}
          </button>
        </div>
      </div>
    </div>
  );
}