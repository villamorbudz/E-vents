import { useState } from 'react';
import { Link } from 'react-router-dom';

export default function TicketingDetails() {
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
    // In a real implementation, this would handle file uploads
    console.log("Photo upload initiated", e);
    // Mock adding a photo
    setPhotos([...photos, 'new-photo']);
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
        
        {/* This should be Save button instead of Payment button */}
        <div className="flex justify-center mt-16">
          <Link to="/create/details/payment">
            <button className="bg-red-500 hover:bg-red-600 text-white px-6 py-3 rounded-full">
              Proceed to Payment
            </button>
          </Link>
        </div>
      </div>
    </div>
  );
}