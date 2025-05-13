import { Link, useNavigate } from "react-router-dom";
import { FcGoogle } from "react-icons/fc";
import { FaFacebook } from "react-icons/fa";
import { useState } from "react";
import { motion } from "framer-motion";
import backgroundImage from '../assets/images/loginBG.png';
import { userService } from '../services/apiService';

export default function SignupStep1() {
  const navigate = useNavigate();
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [confirmPassword, setConfirmPassword] = useState("");
  const [errors, setErrors] = useState({});
  const [isLoading, setIsLoading] = useState(false);

  const validateForm = () => {
    const newErrors = {};
    
    // Email validation
    if (!email) {
      newErrors.email = "Email is required";
    } else if (!/\S+@\S+\.\S+/.test(email)) {
      newErrors.email = "Email is invalid";
    }
    
    // Password validation
    if (!password) {
      newErrors.password = "Password is required";
    } else if (password.length < 8) {
      newErrors.password = "Password must be at least 8 characters";
    }
    
    // Confirm password validation
    if (!confirmPassword) {
      newErrors.confirmPassword = "Please confirm your password";
    } else if (password !== confirmPassword) {
      newErrors.confirmPassword = "Passwords do not match";
    }
    
    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleNext = async (e) => {
    e.preventDefault();
    
    if (!validateForm()) {
      return;
    }
    
    setIsLoading(true);
    
    try {
      // Check if email already exists
      const emailExists = await userService.checkEmailExists(email);
      
      if (emailExists) {
        setErrors(prev => ({ ...prev, email: "Email is already registered" }));
        setIsLoading(false);
        return;
      }
      
      // Save data to localStorage
      const signupData = {
        email,
        password,
        step: 1,
        timestamp: new Date().toISOString()
      };
      
      localStorage.setItem("signupData", JSON.stringify(signupData));
      navigate("/signup/personal-info");
    } catch (error) {
      setErrors(prev => ({ ...prev, form: typeof error === 'string' ? error : "An error occurred" }));
    } finally {
      setIsLoading(false);
    }
  };

  const handleSocialSignup = (provider) => {
    alert(`${provider} signup is not implemented yet`);
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
        animate={{ x: 0 }}
        exit={{ x: "200%" }}
        transition={{ duration: 0.5, ease: "easeOut" }}
        className="w-1/2 bg-[#BD0027]/80 rounded-l-[80px] flex flex-col justify-center items-center text-white p-10"
      >
        <h2 className="text-2xl font-bold mb-6">Create Account</h2>
        <p className="text-sm mb-4 text-center w-1/2">Please enter your email and create a password to get started</p>
        
        <form onSubmit={handleNext} className="w-full flex flex-col items-center" autoComplete="off" autoSave="off">
          {errors.form && (
            <div className="w-1/2 bg-red-100 border border-red-400 text-red-700 px-4 py-2 rounded mb-4 text-center">
              {errors.form}
            </div>
          )}
          
          <div className="w-1/2 mb-4">
            <input 
              type="email" 
              placeholder="Email" 
              className={`w-full p-3 rounded-md bg-white text-black border-2 ${errors.email ? 'border-red-500' : 'border-black'}`}
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              autoComplete="off"
              name="email-no-autofill"
            />
            {errors.email && <p className="text-sm text-red-200 mt-1">{errors.email}</p>}
          </div>
          
          <div className="w-1/2 mb-4">
            <input 
              type="password" 
              placeholder="Password" 
              className={`w-full p-3 rounded-md bg-white text-black border-2 ${errors.password ? 'border-red-500' : 'border-black'}`}
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              autoComplete="new-password"
              name="password-no-autofill"
            />
            {errors.password && <p className="text-sm text-red-200 mt-1">{errors.password}</p>}
          </div>
          
          <div className="w-1/2 mb-6">
            <input 
              type="password" 
              placeholder="Confirm Password" 
              className={`w-full p-3 rounded-md bg-white text-black border-2 ${errors.confirmPassword ? 'border-red-500' : 'border-black'}`}
              value={confirmPassword}
              onChange={(e) => setConfirmPassword(e.target.value)}
              autoComplete="new-password"
              name="confirm-password-no-autofill"
            />
            {errors.confirmPassword && <p className="text-sm text-red-200 mt-1">{errors.confirmPassword}</p>}
          </div>
          
          <p className="mb-3">or sign up with</p>
          <div className="flex gap-4 mb-6">
            <button 
              type="button"
              className="bg-white p-2 rounded-md hover:bg-gray-100 transition-colors"
              onClick={() => handleSocialSignup('Google')}
            >
              <FcGoogle size={24} />
            </button>
          </div>
          
          <button 
            type="submit" 
            className="bg-white text-black px-10 py-2 rounded-md font-semibold mb-4 hover:bg-gray-100 transition-colors flex items-center"
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
              <>Next</>
            )}
          </button>
        </form>
        
        <p className="text-sm">
          Already have an account?{" "}
          <Link to="/" className="underline hover:text-gray-200 transition-colors">SIGN IN</Link>
        </p>
      </motion.div>
    </div>
  );
}