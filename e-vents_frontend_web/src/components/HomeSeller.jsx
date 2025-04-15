import { Link } from "react-router-dom";

export default function HomePage() {
  return (
    <div className="min-h-screen bg-gray-900 text-white">
      {/* Navbar */}
      <div className="bg-[#BD0027] flex justify-between items-center px-10 py-4">
        <h1 className="text-2xl font-bold">E-Vents</h1>
        <div className="flex gap-10 text-lg">
          <Link to="/homeseller" className="hover:underline">Home</Link>
          <Link to="/events" className="hover:underline">Events</Link>
          <Link to="/create" className="hover:underline">Create Events</Link>
          <Link to="/profile" className="hover:underline">Profile</Link>
        </div>
      </div>

      {/* Hero Section */}
      <div className="relative">
        <img
          src="/assets/images/homeBG.png"
          alt="Concert"
          className="w-full h-[500px] object-cover"
        />
        <div className="absolute bottom-4 left-1/2 transform -translate-x-1/2 flex gap-4">
          <button className="bg-[#BD0027] text-white px-6 py-2 rounded-full">Buy Tickets</button>
          <button className="bg-[#BD0027] text-white px-6 py-2 rounded-full">Near You</button>
          <button className="bg-[#BD0027] text-white px-6 py-2 rounded-full">Popular Now</button>
          <button className="bg-[#BD0027] text-white px-6 py-2 rounded-full">Upcoming</button>
        </div>
      </div>

      {/* Featured Events */}
      <div className="px-10 py-10">
        <h2 className="text-2xl font-semibold mb-6">Featured Events</h2>
        <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
          <div className="bg-gray-800 rounded-lg overflow-hidden">
            <img src="/assets/images/event1.png" alt="Event 1" className="w-full h-40 object-cover" />
            <div className="p-4">
              <h3 className="text-lg font-bold">Event 1</h3>
              <p className="text-sm">Details about Event 1</p>
            </div>
          </div>

          <div className="bg-gray-800 rounded-lg overflow-hidden">
            <img src="/assets/images/event2.png" alt="Event 2" className="w-full h-40 object-cover" />
            <div className="p-4">
              <h3 className="text-lg font-bold">Event 2</h3>
              <p className="text-sm">Details about Event 2</p>
            </div>
          </div>

          <div className="bg-gray-800 rounded-lg overflow-hidden">
            <img src="/assets/images/event3.png" alt="Event 3" className="w-full h-40 object-cover" />
            <div className="p-4">
              <h3 className="text-lg font-bold">Event 3</h3>
              <p className="text-sm">Details about Event 3</p>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
