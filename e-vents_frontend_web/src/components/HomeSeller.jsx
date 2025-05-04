import { Link, useNavigate } from "react-router-dom";
import { userService } from "../services/apiService";


export default function HomeSeller() {
  const navigate = useNavigate(); // for programmatic navigation

  return (
    <div className="min-h-screen bg-gray-900 text-white">
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
          <Link to="/homeseller" className="hover:underline text-black">My Events</Link>
          <Link to="/create" className="hover:underline">Create Events</Link>
          <Link to="/profile" className="hover:underline">Profile</Link>
        </div>
      </div>

      {/* Main Content */}
      <div className="bg-gray-900 p-8">
        <h1 className="text-3xl font-semibold text-red-600 mb-8">Events</h1>
      </div>
    </div>
  );
}
