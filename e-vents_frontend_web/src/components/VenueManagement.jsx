import { useState } from "react";
import { eventService } from "../services/apiService";

export default function VenueManagement({ onVenueAdded, onClose }) {
  const [venueName, setVenueName] = useState("");
  const [venueAddress, setVenueAddress] = useState("");
  const [venueCapacity, setVenueCapacity] = useState("");
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");

  const handleSubmit = async (e) => {
    e.preventDefault();
    setIsLoading(true);
    setError("");
    setSuccess("");

    if (!venueName || !venueAddress) {
      setError("Venue name and address are required");
      setIsLoading(false);
      return;
    }

    try {
      // Create venue object
      const venueData = {
        name: venueName,
        address: venueAddress,
        capacity: venueCapacity ? parseInt(venueCapacity) : 100
      };

      // Call API to create venue
      const newVenue = await eventService.createVenue(venueData);
      
      setSuccess("Venue added successfully!");
      onVenueAdded(newVenue); // Notify parent component
      
      // Reset form
      setVenueName("");
      setVenueAddress("");
      setVenueCapacity("");
    } catch (error) {
      console.error("Error creating venue:", error);
      setError("Failed to create venue. Please try again.");
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="bg-gray-800 p-6 rounded-lg">
      <div className="flex justify-between items-center mb-4">
        <h2 className="text-xl font-semibold text-white">Add New Venue</h2>
        <button 
          className="text-gray-400 hover:text-white"
          onClick={onClose}
        >
          ✕
        </button>
      </div>

      {error && (
        <div className="bg-red-600 text-white p-2 rounded mb-4">
          {error}
        </div>
      )}

      {success && (
        <div className="bg-green-600 text-white p-2 rounded mb-4">
          {success}
        </div>
      )}

      <form onSubmit={handleSubmit}>
        <div className="mb-4">
          <label className="block text-gray-400 mb-1">Venue Name:</label>
          <input
            type="text"
            placeholder="Enter venue name"
            className="w-full p-2 rounded bg-white text-black"
            value={venueName}
            onChange={(e) => setVenueName(e.target.value)}
            required
          />
        </div>

        <div className="mb-4">
          <label className="block text-gray-400 mb-1">Address:</label>
          <input
            type="text"
            placeholder="Enter venue address"
            className="w-full p-2 rounded bg-white text-black"
            value={venueAddress}
            onChange={(e) => setVenueAddress(e.target.value)}
            required
          />
        </div>

        <div className="mb-4">
          <label className="block text-gray-400 mb-1">Capacity (optional):</label>
          <input
            type="number"
            placeholder="Enter capacity"
            className="w-full p-2 rounded bg-white text-black"
            value={venueCapacity}
            onChange={(e) => setVenueCapacity(e.target.value)}
          />
        </div>

        <div className="flex justify-center">
          <button
            type="submit"
            className="bg-[#5798FF] text-white px-6 py-2 rounded-md"
            disabled={isLoading}
          >
            {isLoading ? "Adding..." : "Add Venue"}
          </button>
        </div>
      </form>
    </div>
  );
}