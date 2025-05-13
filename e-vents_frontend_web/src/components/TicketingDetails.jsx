import { useState, useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { userService } from '../services/apiService';

export default function TicketingDetails() {
  const navigate = useNavigate();
  const [categories, setCategories] = useState([
    {
      name: 'General Admission',
      price: '',
      capacity: '',
      description: ''
    }
  ]);
  const [newCategoryName, setNewCategoryName] = useState('');
  const [editingIndex, setEditingIndex] = useState(0); // Track which category is being edited
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [errorMessage, setErrorMessage] = useState('');
  const [basicInfo, setBasicInfo] = useState(null);

  // Load the event basic info from localStorage
  useEffect(() => {
    const storedInfo = localStorage.getItem("eventBasicInfo");
    if (storedInfo) {
      setBasicInfo(JSON.parse(storedInfo));
    } else {
      // If no event info, redirect back to event creation
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

  const validateTicketCategories = () => {
    for (const category of categories) {
      if (!category.name || !category.price || !category.capacity) {
        setErrorMessage('All ticket categories must have a name, price, and capacity.');
        return false;
      }
      
      if (isNaN(parseFloat(category.price)) || parseFloat(category.price) <= 0) {
        setErrorMessage('Ticket prices must be valid positive numbers.');
        return false;
      }
      
      if (isNaN(parseInt(category.capacity)) || parseInt(category.capacity) <= 0) {
        setErrorMessage('Ticket capacity must be a valid positive number.');
        return false;
      }
    }
    return true;
  };

  const handleSubmit = async () => {
    if (!validateTicketCategories()) {
      return;
    }

    try {
      setIsSubmitting(true);

      // Get current user ID (normally would come from auth context)
      const { userId } = userService.getCurrentUser();

      // Prepare event data from the basic info
      const eventData = {
        name: basicInfo.name,
        description: basicInfo.description,
        date: basicInfo.date,
        time: basicInfo.time,
        venue: basicInfo.venue,
        hostLineup: basicInfo.hostLineup,
      };

      // Submit event with ticket categories
      const createdEvent = await eventService.createEvent(eventData, categories, userId);

      // Clear localStorage
      localStorage.removeItem("eventBasicInfo");

      // Navigate to the home/seller page
      navigate('/homeseller');
    } catch (error) {
      console.error('Error creating event:', error);
      setErrorMessage('Failed to create event. Please try again.');
    } finally {
      setIsSubmitting(false);
    }
  };
  
  // Validate ticket categories before submission
  const validateCategories = () => {
    // Check if there's at least one category
    if (categories.length === 0) {
      setErrorMessage('Please add at least one ticket category.');
      return false;
    }
    
    // Check if all categories have required fields
    for (const category of categories) {
      if (!category.name || !category.price || !category.capacity) {
        setErrorMessage('All ticket categories must have a name, price, and capacity.');
        return false;
      }
      
      // Validate price is a number
      if (isNaN(parseFloat(category.price)) || parseFloat(category.price) <= 0) {
        setErrorMessage('Ticket prices must be valid positive numbers.');
        return false;
      }
      
      // Validate capacity is a positive integer
      if (isNaN(parseInt(category.capacity)) || parseInt(category.capacity) <= 0) {
        setErrorMessage('Ticket capacity must be a valid positive number.');
        return false;
      }
    }
    
    return true;
  };
  
  // Handle form submission
  const handleFormSubmit = async (e) => {
    e.preventDefault();
    setErrorMessage('');
    
    if (!eventId) {
      setErrorMessage('No event ID found. Please create an event first.');
      return;
    }
    
    if (!validateCategories()) {
      return;
    }
    
    setIsSubmitting(true);
    
    try {
      // Create each ticket category
      for (const category of categories) {
        const categoryData = {
          name: category.name,
          price: parseFloat(category.price),
          capacity: parseInt(category.capacity),
          description: category.description || ''
        };
        
        await eventService.createTicketCategory(eventId, categoryData);
      }
      
      // Navigate to the seller homepage or event details page
      navigate('/homeseller');
    } catch (error) {
      console.error('Error creating ticket categories:', error);
      setErrorMessage(typeof error === 'string' ? error : 'Failed to create ticket categories. Please try again.');
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <div className="flex flex-col min-h-screen bg-gray-900 text-white">
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
      
      {/* Error Message Pop-up */}
      {errorMessage && (
        <div className="fixed top-0 left-0 right-0 bottom-0 flex justify-center items-center z-50 bg-black bg-opacity-50">
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
      
      {/* Main content */}
      <div className="flex-1 p-8 bg-gray-900">
        <h1 className="text-2xl mb-8 text-gray-300">Ticket Details</h1>
        
        {/* Loading indicator */}
        {isLoading && (
          <div className="flex justify-center mb-6">
            <div className="animate-spin rounded-full h-10 w-10 border-t-2 border-b-2 border-blue-500"></div>
          </div>
        )}
        
        {/* Error message */}
        {errorMessage && (
          <div className="bg-red-500 text-white p-3 rounded mb-6">
            {errorMessage}
          </div>
        )}
        
        {/* Event info banner */}
        {event && (
          <div className="bg-blue-900 p-4 rounded mb-6">
            <h2 className="text-xl font-bold">{event.name}</h2>
            <p className="text-sm text-gray-300">Setting up tickets for your event</p>
          </div>
        )}
        
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
                    type="text" 
                    placeholder="Set price" 
                    className="border p-1 w-full"
                    value={categories[editingIndex].price}
                    onChange={(e) => handleUpdateCategory('price', e.target.value)} 
                  />
                </div>
                
                <div className="mb-4">
                  <label className="block mb-1">Capacity:</label>
                  <input 
                    type="text" 
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
        
        {/* Create button */}
        <div className="flex justify-center mt-16">
          <button 
            onClick={handleFormSubmit}
            disabled={isSubmitting}
            className={`bg-red-500 hover:bg-red-600 text-white px-6 py-3 rounded-full ${isSubmitting ? 'opacity-50 cursor-not-allowed' : ''}`}
          >
            {isSubmitting ? 'Creating...' : 'Create Event'}
          </button>
        </div>
      </div>
    </div>
  );
}