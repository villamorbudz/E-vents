import { useState } from 'react';
import { Link } from "react-router-dom";

export default function Profile() {
  const [profile, setProfile] = useState({
    firstName: '',
    lastName: '',
    email: '',
    mobileNumber: '',
    birthdate: '',
    country: '',
    region: '',
    city: '',
    postalCode: ''
  });
  
  const [isEditing, setIsEditing] = useState(false);
  
  const handleChange = (e) => {
    const { name, value } = e.target;
    setProfile(prev => ({
      ...prev,
      [name]: value
    }));
  };
  
  const handleSubmit = (e) => {
    e.preventDefault();
    // Here you would typically send the data to your backend
    console.log('Profile data submitted:', profile);
    setIsEditing(false);
  };
  
  return (
    <div className="min-h-screen bg-gray-900 text-white">
      {/* Header */}
      {/* Navbar */}
      <div className="bg-[#5798FF] flex justify-between items-center px-10 py-4">
        <h1 className="text-2xl font-bold">E-Vents</h1>
        <div className="flex gap-10 text-lg">
          <Link to="/homeseller" className="hover:underline">My Events</Link>
          <Link to="/create" className="hover:underline">Create Events</Link>
          <Link to="/profile" className="hover:underline text-black">Profile</Link>
        </div>
      </div>
      
      {/* Main Content */}
      <main className="container mx-auto p-6">
        <div className="flex justify-between items-center mb-6">
          <h2 className="text-xl">My Account</h2>
          <button 
            className="text-sm bg-transparent hover:underline"
            onClick={() => console.log('Logging out')}
          >
            Log Out
          </button>
        </div>
        
        {/* Profile Card */}
        <div className="bg-blue-700 rounded-lg p-8 max-w-3xl mx-auto">
          <form onSubmit={handleSubmit}>
            <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
              {/* Profile Picture */}
              <div className="md:col-span-2 flex justify-center mb-4">
                <div className="w-32 h-32 bg-white rounded-full"></div>
              </div>
              
              {/* First Name */}
              <div>
                <label className="block mb-1">First Name</label>
                <input
                  type="text"
                  name="firstName"
                  className="w-full p-2 rounded text-black"
                  value={profile.firstName}
                  onChange={handleChange}
                  disabled={!isEditing}
                />
              </div>
              
              {/* Last Name */}
              <div>
                <label className="block mb-1">Last Name</label>
                <input
                  type="text"
                  name="lastName"
                  className="w-full p-2 rounded text-black"
                  value={profile.lastName}
                  onChange={handleChange}
                  disabled={!isEditing}
                />
              </div>
              
              {/* Email */}
              <div className="md:col-span-2">
                <label className="block mb-1">Email</label>
                <input
                  type="email"
                  name="email"
                  className="w-full p-2 rounded text-black"
                  value={profile.email}
                  onChange={handleChange}
                  disabled={!isEditing}
                />
              </div>
              
              {/* Mobile Number */}
              <div>
                <label className="block mb-1">Mobile Number</label>
                <input
                  type="tel"
                  name="mobileNumber"
                  className="w-full p-2 rounded text-black"
                  value={profile.mobileNumber}
                  onChange={handleChange}
                  disabled={!isEditing}
                />
              </div>
              
              {/* Birthdate */}
              <div>
                <label className="block mb-1">Birthdate</label>
                <input
                  type="date"
                  name="birthdate"
                  className="w-full p-2 rounded text-black"
                  value={profile.birthdate}
                  onChange={handleChange}
                  disabled={!isEditing}
                />
              </div>
              
              {/* Country */}
              <div>
                <label className="block mb-1">Country</label>
                <input
                  type="text"
                  name="country"
                  className="w-full p-2 rounded text-black"
                  value={profile.country}
                  onChange={handleChange}
                  disabled={!isEditing}
                />
              </div>
              
              {/* Region */}
              <div>
                <label className="block mb-1">Region</label>
                <input
                  type="text"
                  name="region"
                  className="w-full p-2 rounded text-black"
                  value={profile.region}
                  onChange={handleChange}
                  disabled={!isEditing}
                />
              </div>
              
              {/* City */}
              <div>
                <label className="block mb-1">City</label>
                <input
                  type="text"
                  name="city"
                  className="w-full p-2 rounded text-black"
                  value={profile.city}
                  onChange={handleChange}
                  disabled={!isEditing}
                />
              </div>
              
              {/* Postal Code */}
              <div>
                <label className="block mb-1">Postal Code</label>
                <input
                  type="text"
                  name="postalCode"
                  className="w-full p-2 rounded text-black"
                  value={profile.postalCode}
                  onChange={handleChange}
                  disabled={!isEditing}
                />
              </div>
              
              {/* Buttons */}
              <div className="md:col-span-2 flex justify-between mt-4">
                <button
                  type="button"
                  className="bg-red-500 text-white px-6 py-2 rounded-full hover:bg-red-600"
                  onClick={() => console.log('Change password clicked')}
                >
                  Change Password
                </button>
                
                {isEditing ? (
                  <button
                    type="submit"
                    className="bg-white text-blue-700 px-10 py-2 rounded-full hover:bg-gray-100"
                  >
                    Save
                  </button>
                ) : (
                  <button
                    type="button"
                    className="bg-white text-blue-700 px-10 py-2 rounded-full hover:bg-gray-100"
                    onClick={() => setIsEditing(true)}
                  >
                    Edit
                  </button>
                )}
              </div>
            </div>
          </form>
        </div>
      </main>
    </div>
  );
}