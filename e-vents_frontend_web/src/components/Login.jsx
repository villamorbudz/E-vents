import { FcGoogle } from "react-icons/fc";
import { FaFacebook } from "react-icons/fa";
import { useNavigate } from "react-router-dom";
import { motion } from 'framer-motion';
import { useState, useEffect } from 'react';
import backgroundImage from '../assets/images/loginBG.png';
import { userService } from '../services/apiService';

export default function Login() {
  const navigate = useNavigate();
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const [isLoading, setIsLoading] = useState(false);
  const [showAdminPrompt, setShowAdminPrompt] = useState(false);

  const ADMIN_EMAIL = "admin@events.com";
  const ADMIN_PASSWORD = "admin123";
  const ADMIN_TOKEN = "admin-mock-token-123456789";

  useEffect(() => {
    // Check if this is the first time the app is running
    const adminConfigured = localStorage.getItem('adminConfigured');
    
    if (!adminConfigured) {
      // Set up the admin user in localStorage for demo purposes
      const adminUser = {
        id: "admin-001",
        name: "Admin User",
        email: ADMIN_EMAIL,
        role: "ADMIN"
      };
      
      // Store the admin credential information
      localStorage.setItem('adminConfigured', 'true');
      localStorage.setItem('adminUser', JSON.stringify(adminUser));
      
      // Show admin login prompt
      setShowAdminPrompt(true);
      
      // Auto-fill admin email after 1 second
      setTimeout(() => {
        setEmail(ADMIN_EMAIL);
      }, 1000);
    }
  }, []);

  const handleNavigateSignup = () => {
    navigate("/signup");
  };

  const validateLogin = () => {
    if (!email.trim()) {
      setError('Email is required');
      return false;
    }

    if (!password) {
      setError('Password is required');
      return false;
    }

    return true;
  };

  const handleLogin = async (e) => {
    e.preventDefault();
    setError('');

    if (!validateLogin()) return;

    setIsLoading(true);

    try {
      // Check for admin login
      if (email === ADMIN_EMAIL && password === ADMIN_PASSWORD) {
        // Get the admin user data
        const adminUser = JSON.parse(localStorage.getItem('adminUser'));
        
        // Store user data in localStorage with admin token
        localStorage.setItem('isLoggedIn', 'true');
        localStorage.setItem('userData', JSON.stringify(adminUser));
        localStorage.setItem('userEmail', email);
        localStorage.setItem('adminToken', ADMIN_TOKEN); // Store admin token specifically
        localStorage.setItem('token', ADMIN_TOKEN); // Also store as regular token
        
        // Navigate to admin dashboard
        setTimeout(() => {
          navigate('/admin');
        }, 1000);
        
        return;
      }
      
      // Regular user login through API
      const userData = await userService.login(email, password);
      
      // Store user data in localStorage
      localStorage.setItem('isLoggedIn', 'true');
      localStorage.setItem('userData', JSON.stringify(userData));
      localStorage.setItem('userEmail', email);
      
      if (userData.role === 'ADMIN') {
        // For admins coming from the API, also set the admin token
        localStorage.setItem('adminToken', userData.token);
        navigate('/adminPage');
      } else {
        navigate('/homeseller'); // Or wherever regular users should go
      }
    } catch (err) {
      setError(typeof err === 'string' ? err : 'Invalid email or password');
      console.error('Login error:', err);
    } finally {
      setIsLoading(false);
    }
  };

  const handleForgotPassword = () => {
    navigate("/forgot-password");
  };

  const handleSocialLogin = (provider) => {
    setError('');
    alert(`${provider} login is not implemented yet.`);
  };

  const handleAdminPromptClose = () => {
    setShowAdminPrompt(false);
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
      {/* Admin credentials prompt */}
      {showAdminPrompt && (
        <div className="absolute top-4 right-4 z-50 bg-white p-4 rounded-lg shadow-lg text-black">
          <div className="flex justify-between items-center mb-2">
            <h3 className="font-bold">Admin Login Available</h3>
            <button 
              onClick={handleAdminPromptClose}
              className="text-gray-500 hover:text-gray-700"
            >
              ✕
            </button>
          </div>
          <p className="mb-2">You can login as admin with:</p>
          <div className="bg-gray-100 p-2 rounded mb-2">
            <p><b>Email:</b> {ADMIN_EMAIL}</p>
            <p><b>Password:</b> {ADMIN_PASSWORD}</p>
          </div>
          <p className="text-sm text-gray-600">This will give you access to the admin dashboard.</p>
        </div>
      )}

      {/* Left red login panel */}
      <motion.div
        initial={{ x: 0 }}
        exit={{ x: "-100%" }}
        transition={{ duration: 0.5, ease: "easeInOut" }}
        className="relative z-10 w-1/2 text-white p-12 flex flex-col justify-center items-center rounded-tr-[80px] rounded-br-[80px] text-center bg-[#BD0027]/80"
      >
        <h1 className="text-4xl font-bold mb-10">LOGIN</h1>

        <form onSubmit={handleLogin} className="w-full flex flex-col items-center">
          {error && (
            <div className="bg-red-100 border border-red-400 text-red-700 px-4 py-2 rounded mb-4 w-1/2 text-center">
              {error}
            </div>
          )}

          <input
            type="text"
            placeholder="Email or Name"
            className="w-1/2 p-3 mb-4 rounded-md bg-white text-black border-2 border-black"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
          />
          <input
            type="password"
            placeholder="Password"
            className="w-1/2 p-3 mb-4 rounded-md bg-white text-black border-2 border-black"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
          />
          <p className="text-sm underline mb-6 cursor-pointer" onClick={handleForgotPassword}>Forgot Password?</p>

          <p className="mb-3">or login using</p>
          <div className="flex gap-4 mb-6">
            <button
              type="button"
              className="bg-white p-2 rounded-md hover:bg-gray-100 transition-colors"
              onClick={() => handleSocialLogin('Google')}
            >
              <FcGoogle size={24} />
            </button>
            <button
              type="button"
              className="bg-white p-2 rounded-md hover:bg-gray-100 transition-colors"
              onClick={() => handleSocialLogin('Facebook')}
            >
              <FaFacebook size={24} color="#4267B2" />
            </button>
          </div>

          <button
            type="submit"
            className="bg-white text-black font-semibold px-10 py-2 rounded-md mb-6 flex items-center justify-center hover:bg-gray-100 transition-colors"
            disabled={isLoading}
          >
            {isLoading ? (
              <>
                <svg className="animate-spin h-5 w-5 mr-2" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
                  <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4"></circle>
                  <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
                </svg>
                LOGGING IN...
              </>
            ) : (
              <>LOGIN</>
            )}
          </button>
        </form>

        <p className="text-sm">
          Don't have an account?{" "}
          <span onClick={handleNavigateSignup} className="underline cursor-pointer hover:text-gray-200 transition-colors">
            SIGN UP
          </span>
        </p>
      </motion.div>

      {/* Right side with animated branding */}
      <motion.div
        initial={{ x: 0 }}
        exit={{ x: "-100%" }}
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
    </div>
  );
}