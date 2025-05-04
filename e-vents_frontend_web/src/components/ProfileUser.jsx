import { useState, useEffect } from 'react';
import { Link, useNavigate } from "react-router-dom";
import { userService } from "../services/apiService";

export default function ProfileUser() {
  const navigate = useNavigate();
  const [profile, setProfile] = useState({
    userId: '',
    firstName: '',
    lastName: '',
    email: '',
    contactNumber: '',
    birthdate: '',
    country: '',
    region: '',
    city: '',
    postalCode: ''
  });
  
  const [isEditing, setIsEditing] = useState(false);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState(null);
  
  // Fetch user profile data on component mount
  useEffect(() => {
    async function fetchUserProfile() {
      try {
        if (!userService.isAuthenticated()) {
          navigate('/login');
          return;
        }
        
        setIsLoading(true);
        const userData = await userService.getUserProfile();
        const formattedBirthdate = userData.birthdate ? userData.birthdate.split('T')[0] : '';
        setProfile({
          userId: userData.userId,
          firstName: userData.firstName || '',
          lastName: userData.lastName || '',
          email: userData.email || '',
          contactNumber: userData.contactNumber || '',
          birthdate: formattedBirthdate || '',
          country: userData.country || '',
          region: userData.region || '',
          city: userData.city || '',
          postalCode: userData.postalCode || ''
        });
        setIsLoading(false);
      } catch (err) {
        console.error('Failed to fetch profile:', err);
        setError('Failed to load profile data. Please try again later.');
        setIsLoading(false);
      }
    }
    
    fetchUserProfile();
  }, [navigate]);
  
  const handleChange = (e) => {
    const { name, value } = e.target;
    setProfile(prev => ({
      ...prev,
      [name]: value
    }));
  };
  
  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      setIsLoading(true);
      await userService.updateUserProfile(profile);
      setIsLoading(false);
      setIsEditing(false);
    } catch (err) {
      console.error('Failed to update profile:', err);
      setError('Failed to update profile. Please try again.');
      setIsLoading(false);
    }
  };

  const toggleEdit = () => {
    console.log('Toggling edit mode from', isEditing, 'to', !isEditing);
    setIsEditing(prev => !prev);
  };

  const handleLogout = () => {
    userService.logout(); 
    navigate("/login");        
  };
  
  if (isLoading && profile.firstName === '') {
    return <div className="min-h-screen bg-gray-900 text-white flex items-center justify-center">
      <div className="text-xl">Loading profile...</div>
    </div>;
  }
  
  // The edit button is placed outside the form
  const EditButton = () => (
    <button
      type="button"
      className="bg-white text-blue-700 px-10 py-2 rounded-full hover:bg-gray-100"
      onClick={toggleEdit}
    >
      Edit
    </button>
  );
  
  const SaveButton = () => (
    <button
      type="submit"
      className="bg-white text-blue-700 px-10 py-2 rounded-full hover:bg-gray-100"
    >
      Save
    </button>
  );
  
  return (
    <div className="min-h-screen bg-gray-900 text-white">
      {/* Header */}
      <div className="bg-[#BD0027] flex justify-between items-center px-10 py-4">
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
          <Link to="/homes" className="hover:underline">Home</Link>
          <Link to="/discover" className="hover:underline">Discover Events</Link>
          <Link to="/myevents" className="hover:underline">My Events</Link>
          <Link to="/userprofile" className="hover:underline text-black">Profile</Link>
        </div>
      </div>
      
      {/* Main Content */}
      <main className="container mx-auto p-6">
        <div className="flex justify-between items-center mb-6">
          <h2 className="text-xl">My Account</h2>
          <button onClick={handleLogout} className="text-white hover:underline bg-transparent border-none cursor-pointer">
            Logout
          </button>
        </div>
        
        {error && (
          <div className="bg-red-500 text-white p-3 rounded mb-4">
            {error}
          </div>
        )}
        
        {/* Profile Card */}
        <div className="bg-[#BD0027] rounded-lg p-8 max-w-3xl mx-auto">
          {isEditing ? (
            <form onSubmit={handleSubmit}>
              <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                {/* First Name */}
                <div>
                  <label className="block mb-1">First Name</label>
                  <input
                    type="text"
                    name="firstName"
                    className="w-full p-2 rounded text-black"
                    value={profile.firstName}
                    onChange={handleChange}
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
                    readOnly
                  />
                </div>
                
                {/* Contact Number */}
                <div>
                  <label className="block mb-1">Contact Number</label>
                  <input
                    type="tel"
                    name="contactNumber"
                    className="w-full p-2 rounded text-black"
                    value={profile.contactNumber}
                    onChange={handleChange}
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
                  />
                </div>
                
                <div className="md:col-span-2 flex justify-between mt-4">
                  <button
                    type="button"
                    className="bg-red-500 text-white px-6 py-2 rounded-full hover:bg-red-600"
                    onClick={() => navigate('/change-password')}
                  >
                    Change Password
                  </button>
                  
                  <SaveButton />
                </div>
              </div>
            </form>
          ) : (
            <div>
              <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                {/* First Name */}
                <div>
                  <label className="block mb-1">First Name</label>
                  <div className="bg-white rounded p-2 text-black">{profile.firstName}</div>
                </div>
                
                {/* Last Name */}
                <div>
                  <label className="block mb-1">Last Name</label>
                  <div className="bg-white rounded p-2 text-black">{profile.lastName}</div>
                </div>
                
                {/* Email */}
                <div className="md:col-span-2">
                  <label className="block mb-1">Email</label>
                  <div className="bg-white rounded p-2 text-black">{profile.email}</div>
                </div>
                
                {/* Contact Number */}
                <div>
                  <label className="block mb-1">Contact Number</label>
                  <div className="bg-white rounded p-2 text-black">{profile.contactNumber}</div>
                </div>
                
                {/* Birthdate */}
                <div>
                  <label className="block mb-1">Birthdate</label>
                  <div className="bg-white rounded p-2 text-black">{profile.birthdate}</div>
                </div>
                
                {/* Country */}
                <div>
                  <label className="block mb-1">Country</label>
                  <div className="bg-white rounded p-2 text-black">{profile.country}</div>
                </div>
                
                {/* Region */}
                <div>
                  <label className="block mb-1">Region</label>
                  <div className="bg-white rounded p-2 text-black">{profile.region}</div>
                </div>
                
                {/* City */}
                <div>
                  <label className="block mb-1">City</label>
                  <div className="bg-white rounded p-2 text-black">{profile.city}</div>
                </div>
                
                {/* Postal Code */}
                <div>
                  <label className="block mb-1">Postal Code</label>
                  <div className="bg-white rounded p-2 text-black">{profile.postalCode}</div>
                </div>
                
                <div className="md:col-span-2 flex justify-between mt-4">
                  <button
                    type="button"
                    className="bg-red-500 text-white px-6 py-2 rounded-full hover:bg-red-600"
                    onClick={() => navigate('/change-password')}
                  >
                    Change Password
                  </button>
                  
                  <EditButton />
                </div>
              </div>
            </div>
          )}
        </div>
      </main>
    </div>
  );
}