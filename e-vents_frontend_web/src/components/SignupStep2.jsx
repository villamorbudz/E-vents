import { Link, useNavigate } from "react-router-dom";
import { useState, useEffect } from "react";
import { motion } from "framer-motion";
import backgroundImage from '../assets/images/loginBG.png';
import { userService } from '../services/apiService';

export default function SignupStep2() {
  const [countryOptions, setCountryOptions] = useState([]);
  const [countryLoading, setCountryLoading] = useState(false);
  const navigate = useNavigate();
  const [formData, setFormData] = useState({
    firstName: "",
    lastName: "",
    contactNumber: "", // changed from mobile to match backend
    birthdate: "",
    country: "",
  });
  const [errors, setErrors] = useState({});
  const [isLoading, setIsLoading] = useState(false);

  // Fetch country options on mount
  useEffect(() => {
    setCountryLoading(true);
    userService.getCountries().then((countries) => {
      setCountryOptions(countries);
    }).finally(() => setCountryLoading(false));
  }, []);

  // Check for existing signup data in localStorage
  useEffect(() => {
    const savedData = localStorage.getItem("signupData");
    if (!savedData) {
      // Redirect to step 1 if no data is found
      navigate("/signup");
      return;
    }
    
    const parsedData = JSON.parse(savedData);
    
    // Fill form with saved data if available
    if (parsedData.personalInfo) {
      setFormData(prev => ({
        ...prev,
        ...parsedData.personalInfo
      }));
    }
  }, [navigate]);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: value
    }));
  };

  const validateForm = () => {
    const newErrors = {};
    
    // First name validation
    if (!formData.firstName.trim()) {
      newErrors.firstName = "First name is required";
    }
    
    // Last name validation
    if (!formData.lastName.trim()) {
      newErrors.lastName = "Last name is required";
    }
    
    // Mobile validation
    if (!formData.contactNumber) {
      newErrors.contactNumber = "Contact number is required";
    } else if (!/^\d{10,15}$/.test(formData.contactNumber.replace(/[^0-9]/g, ''))) {
      newErrors.contactNumber = "Enter a valid contact number";
    }
    
    // Birthdate validation
    if (!formData.birthdate) {
      newErrors.birthdate = "Birthdate is required";
    } else {
      const today = new Date();
      const birthDate = new Date(formData.birthdate);
      
      const age = today.getFullYear() - birthDate.getFullYear();
      const monthDiff = today.getMonth() - birthDate.getMonth();
      
      if (age < 18 || (age === 18 && monthDiff < 0)) {
        newErrors.birthdate = "You must be at least 18 years old";
      }
    }
    
    // Country validation
    if (!formData.country.trim()) {
      newErrors.country = "Country is required";
    }
    
    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    
    if (!validateForm()) {
      return;
    }
    
    setIsLoading(true);
    
    try {
      // Get existing signup data
      const existingData = JSON.parse(localStorage.getItem("signupData")) || {};
      
      // Prepare user data for registration
      const userData = {
        firstName: formData.firstName,
        lastName: formData.lastName,
        email: existingData.email,
        password: existingData.password,
        contactNumber: formData.contactNumber,
        birthdate: new Date(formData.birthdate),
        country: formData.country
      };
      
      // Register user
      const registeredUser = await userService.register(userData);
      
      // Update with personal info
      const updatedData = {
        ...existingData,
        personalInfo: formData,
        userId: registeredUser.userId,
        step: 2,
        completed: true,
        timestamp: new Date().toISOString()
      };
      
      // Save updated data
      localStorage.setItem("signupData", JSON.stringify(updatedData));
      localStorage.setItem("isLoggedIn", "true");
      localStorage.setItem("userData", JSON.stringify(registeredUser));
      localStorage.setItem("userEmail", registeredUser.email);
      
      // Redirect to home page or dashboard
      navigate("/login");
    } catch (error) {
      setErrors(prev => ({ 
        ...prev, 
        form: typeof error === 'string' ? error : "Failed to register account. Please try again."
      }));
    } finally {
      setIsLoading(false);
    }
  };

  const handleBack = () => {
    // Save current form data before navigating back
    const existingData = JSON.parse(localStorage.getItem("signupData")) || {};
    localStorage.setItem("signupData", JSON.stringify({
      ...existingData,
      personalInfo: formData
    }));
    
    navigate("/signup");
  };

  return (
    <div
      className="flex h-screen w-full bg-cover bg-center relative"
      style={{
        backgroundImage: `url(${backgroundImage})`,
        minHeight: '100vh',
        backgroundPosition: 'center',
        backgroundSize: 'cover',
      }}
    >
      {/* Left side just shows background */}
      <motion.div
        initial={{ x: 0 }}
        transition={{ duration: 0.5, ease: "easeInOut" }}
        className="w-1/2 flex flex-col justify-center items-center bg-transparent text-center px-8"
      >
        <h1 className="text-5xl font-extrabold text-red-600 mb-4">
          Welcome to<br />E-Vents
        </h1>
        <p className="text-lg text-white max-w-md">
          Your Gateway to Effortless Event Management and Seamless Ticketing.
          List your events and grab your tickets now!
        </p>
      </motion.div>
      
      {/* Right red panel */}
      <motion.div 
        initial={{ x: "100%" }}
        exit={{ x: "200%" }}
        animate={{ x: 0 }}
        transition={{ duration: 0.5, ease: "easeOut" }}
        className="w-1/2 bg-[#BD0027]/80 rounded-l-[80px] flex flex-col justify-center items-center text-white p-10 overflow-y-auto max-h-screen"
      >
        <h2 className="text-2xl font-bold mb-4">Complete Your Profile</h2>
        <p className="text-sm mb-6 text-center w-3/4">Please provide your personal information to complete your account setup</p>
        
        <form onSubmit={handleSubmit} className="w-full flex flex-col items-center">
          {errors.form && (
            <div className="w-3/4 bg-red-100 border border-red-400 text-red-700 px-4 py-2 rounded mb-4 text-center">
              {errors.form}
            </div>
          )}
          
          <div className="w-3/4 grid grid-cols-2 gap-3 mb-4">
            <div>
              <input 
                name="firstName"
                placeholder="First Name" 
                className={`w-full p-3 rounded-md bg-white text-black border-2 ${errors.firstName ? 'border-red-500' : 'border-black'}`}
                value={formData.firstName}
                onChange={handleChange}
              />
              {errors.firstName && <p className="text-sm text-red-200 mt-1">{errors.firstName}</p>}
            </div>
            
            <div>
              <input 
                name="lastName"
                placeholder="Last Name" 
                className={`w-full p-3 rounded-md bg-white text-black border-2 ${errors.lastName ? 'border-red-500' : 'border-black'}`}
                value={formData.lastName}
                onChange={handleChange}
              />
              {errors.lastName && <p className="text-sm text-red-200 mt-1">{errors.lastName}</p>}
            </div>
          </div>
          
          <div className="w-3/4 mb-4">
            <input 
              name="contactNumber"
              placeholder="Contact Number" 
              className={`w-full p-3 rounded-md bg-white text-black border-2 ${errors.contactNumber ? 'border-red-500' : 'border-black'}`}
              value={formData.contactNumber}
              onChange={handleChange}
            />
            {errors.contactNumber && <p className="text-sm text-red-200 mt-1">{errors.contactNumber}</p>}
          </div>
          
          <div className="w-3/4 mb-4">
            <label className="block text-sm mb-1">Date of Birth</label>
            <input 
              name="birthdate"
              type="date" 
              placeholder="Birthdate" 
              className={`w-full p-3 rounded-md bg-white text-black border-2 ${errors.birthdate ? 'border-red-500' : 'border-black'}`}
              value={formData.birthdate}
              onChange={handleChange}
            />
            {errors.birthdate && <p className="text-sm text-red-200 mt-1">{errors.birthdate}</p>}
          </div>

          <div className="w-3/4 mb-4">
            <select
              name="country"
              className={`w-full p-3 rounded-md bg-white text-black border-2 ${errors.country ? 'border-red-500' : 'border-black'}`}
              value={formData.country}
              onChange={handleChange}
              disabled={countryLoading}
            >
              <option value="">Select Country</option>
              {countryOptions.map((country) => (
                <option key={country} value={country}>{country}</option>
              ))}
            </select>
            {countryLoading && <p className="text-xs text-gray-200 mt-1">Loading countries...</p>}
            {errors.country && <p className="text-sm text-red-200 mt-1">{errors.country}</p>}
          </div>
          
          <div className="w-3/4 flex justify-between mb-4">
            <button 
              type="button" 
              onClick={handleBack}
              className="bg-transparent border-2 border-white text-white px-8 py-2 rounded-md font-semibold hover:bg-white/10 transition-colors"
            >
              Back
            </button>
            
            <button 
              type="submit" 
              className="bg-white text-black px-8 py-2 rounded-md font-semibold hover:bg-gray-100 transition-colors flex items-center"
              disabled={isLoading}
            >
              {isLoading ? (
                <>
                  <svg className="animate-spin h-5 w-5 mr-2" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
                    <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4"></circle>
                    <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
                  </svg>
                  Processing...
                </>
              ) : (
                <>Complete Signup</>
              )}
            </button>
          </div>
        </form>
        
        <p className="text-sm mt-4">
          Already have an account?{" "}
          <Link to="/login" className="underline hover:text-gray-200 transition-colors">SIGN IN</Link>
        </p>
      </motion.div>
    </div>
  );
}