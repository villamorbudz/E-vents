import { useState } from 'react';
import { Link, useNavigate } from "react-router-dom";

export default function EventsLandingPage() {
  const [isMenuOpen, setIsMenuOpen] = useState(false);
  const [showLoginModal, setShowLoginModal] = useState(false);
  const [modalMessage, setModalMessage] = useState('');
  
  // This simulates React Router's navigate function
  const navigate = useNavigate();
  
  const handleActionClick = (action) => {
    setModalMessage(`You need to login first to ${action}.`);
    setShowLoginModal(true);
  };
  
  const handleLoginClick = () => {
    setShowLoginModal(false);
    navigate('/login');
  };
  
  // Link component to simulate React Router Link
  const Link = ({ to, children, onClick, className }) => {
    const handleClick = (e) => {
      e.preventDefault();
      if (onClick) {
        onClick(e);
      } else if (to && to !== '#') {
        navigate(to);
      }
    };
    
    return (
      <a href={to || '#'} onClick={handleClick} className={className}>
        {children}
      </a>
    );
  };
  
  return (
    <div className="flex flex-col min-h-screen">
      {/* Navigation bar */}
      <header className="bg-[#BD0027] text-white">
        <div className="container mx-auto flex justify-between items-center px-4 py-3">
          {/* Logo */}
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
          
          {/* Desktop Navigation */}
          <nav className="hidden md:flex space-x-8">
            <Link to="/" className="hover:text-gray-200 text-black">Home</Link>
            <Link to="/discover" className="hover:text-gray-200">Discover Events</Link>
            <Link 
              to="#" 
              className="hover:text-gray-200" 
              onClick={() => handleActionClick('create events')}
            >
              Create Events
            </Link>
            <Link to="/login" className="hover:text-gray-200">Log in</Link>
          </nav>
          
          {/* Mobile menu button */}
          <div className="md:hidden">
            <button 
              className="text-white"
              onClick={() => setIsMenuOpen(!isMenuOpen)}
            >
              <svg 
                className="w-6 h-6" 
                fill="none" 
                stroke="currentColor" 
                viewBox="0 0 24 24" 
                xmlns="http://www.w3.org/2000/svg"
              >
                <path 
                  strokeLinecap="round" 
                  strokeLinejoin="round" 
                  strokeWidth="2" 
                  d="M4 6h16M4 12h16m-7 6h7" 
                />
              </svg>
            </button>
          </div>
        </div>
        
        {/* Mobile Navigation */}
        {isMenuOpen && (
          <nav className="bg-red-500 px-4 py-2 md:hidden">
            <div className="flex flex-col space-y-2">
              <Link to="/" className="hover:text-gray-200">Home</Link>
              <Link to="/discover" className="hover:text-gray-200">Discover Events</Link>
              <Link 
                to="#" 
                className="hover:text-gray-200"
                onClick={() => handleActionClick('create events')}
              >
                Create Events
              </Link>
              <Link to="/login" className="hover:text-gray-200">Log in</Link>
            </div>
          </nav>
        )}
      </header>
      
      {/* Hero Section */}
      <main className="flex-grow bg-gray-900 text-white">
        <div 
          className="relative h-screen bg-cover bg-center"
          style={{
            backgroundImage: `linear-gradient(rgba(0, 0, 0, 0.7), rgba(0, 0, 0, 0.7)), url('/api/placeholder/1200/800')`
          }}
        >
          <div className="absolute inset-0 flex flex-col items-center justify-center px-4">
            <h1 className="text-5xl md:text-6xl font-bold mb-6 text-center">
              Find Your Next Experience
            </h1>
            <p className="text-xl md:text-2xl mb-8 text-center max-w-3xl">
              Discover and attend the most exciting events happening around you.
            </p>
            
            <div className="flex flex-col md:flex-row space-y-4 md:space-y-0 md:space-x-4">
              <Link 
                to="#" 
                className="bg-red-500 hover:bg-red-600 text-white font-bold py-3 px-8 rounded-full text-center"
                onClick={() => handleActionClick('buy tickets')}
              >
                Buy Tickets
              </Link>
              <Link 
                to="/popular" 
                className="bg-red-500 hover:bg-red-600 text-white font-bold py-3 px-8 rounded-full text-center"
              >
                Popular Now
              </Link>
            </div>
          </div>
        </div>
      </main>
      
      {/* Login Modal */}
      {showLoginModal && (
        <div className="fixed inset-0 flex items-center justify-center z-50 pointer-events-none">
          <div className="bg-white rounded-lg shadow-xl p-6 m-4 max-w-sm w-full text-center pointer-events-auto">
            <div className="mb-4">
              <h3 className="text-lg font-medium text-gray-900">{modalMessage}</h3>
            </div>
            <div className="flex justify-between space-x-3">
              <button
                className="bg-gray-300 hover:bg-gray-400 text-gray-800 font-bold py-2 px-4 rounded-lg w-1/2"
                onClick={() => setShowLoginModal(false)}
              >
                Cancel
              </button>
              <button
                className="bg-red-500 hover:bg-red-600 text-white font-bold py-2 px-4 rounded-lg w-1/2"
                onClick={handleLoginClick}
              >
                Log In
              </button>
            </div>
          </div>
        </div>
      )}
      
      {/* Footer */}
      <footer className="bg-gray-900 text-white py-6">
        <div className="container mx-auto px-4">
          <div className="flex flex-col md:flex-row justify-between">
            <div className="mb-4 md:mb-0">
              <span className="text-2xl font-bold flex items-center">
                <svg 
                  className="w-6 h-6 mr-1" 
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
              <p className="mt-2">Your gateway to amazing experiences</p>
            </div>
            
            <div className="flex flex-col md:flex-row space-y-4 md:space-y-0 md:space-x-8">
              <div>
                <h3 className="text-lg font-bold mb-2">Company</h3>
                <ul className="space-y-1">
                  <li><Link to="/about" className="hover:text-gray-300">About Us</Link></li>
                  <li><Link to="/careers" className="hover:text-gray-300">Careers</Link></li>
                  <li><Link to="/contact" className="hover:text-gray-300">Contact</Link></li>
                </ul>
              </div>
              
              <div>
                <h3 className="text-lg font-bold mb-2">Support</h3>
                <ul className="space-y-1">
                  <li><Link to="/help" className="hover:text-gray-300">Help Center</Link></li>
                  <li><Link to="/terms" className="hover:text-gray-300">Terms of Service</Link></li>
                  <li><Link to="/privacy" className="hover:text-gray-300">Privacy Policy</Link></li>
                </ul>
              </div>
            </div>
          </div>
          
          <div className="mt-8 border-t border-gray-800 pt-6 text-sm text-gray-400">
            <p>&copy; 2025 Events. All rights reserved.</p>
          </div>
        </div>
      </footer>
    </div>
  );
}