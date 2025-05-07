import { FcGoogle } from "react-icons/fc";
import { FaFacebook } from "react-icons/fa";
import { useNavigate } from "react-router-dom";
import { motion } from 'framer-motion';
import { useState } from 'react';
import backgroundImage from '../assets/images/loginBG.png';
import { userService } from '../services/apiService';

export default function Login() {
  const navigate = useNavigate();
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const [isLoading, setIsLoading] = useState(false);

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
      const userData = await userService.login(email, password);
      
      // Store user data in localStorage
      localStorage.setItem('isLoggedIn', 'true');
      localStorage.setItem('userData', JSON.stringify(userData));
      localStorage.setItem('userEmail', email);
      
      if (userData.role === 'ADMIN') {
        navigate('/admin');
      } else if(userData.role === 'USER'){
        navigate('/home'); // Or wherever regular users should go
      }else{
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