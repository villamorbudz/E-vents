import React, { useState, useEffect } from 'react';
import { userService } from '../services/apiService';

const UserEditForm = ({ user, onUpdate, onCancel }) => {
  const [formData, setFormData] = useState({
    userId: user.userId || '',
    email: user.email || '',
    firstName: user.firstName || '',
    lastName: user.lastName || '',
    role: user.role?.name || 'USER',
    country: user.country || '',
    region: user.region || '',
    city: user.city || '',
    postalCode: user.postalCode || '',
    birthdate: user.birthdate || '',
    contactNumber: user.contactNumber || ''
  });

  const [countries, setCountries] = useState([]);
  const [regions, setRegions] = useState([]);
  const [cities, setCities] = useState([]);

  useEffect(() => {
    // Load countries on component mount
    const loadCountries = async () => {
      try {
        const countriesList = await userService.getCountries();
        setCountries(countriesList);
        
        // If user has a country, load its regions
        if (formData.country) {
          const regionsList = await userService.getRegions(formData.country);
          setRegions(regionsList);
          
          // If user has a region, load its cities
          if (formData.region) {
            const citiesList = await userService.getCities(formData.country, formData.region);
            setCities(citiesList);
          }
        }
      } catch (error) {
        console.error('Error loading location data:', error);
      }
    };
    
    loadCountries();
  }, [formData.country, formData.region]);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData({ ...formData, [name]: value });
  };

  const handleCountryChange = async (e) => {
    const country = e.target.value;
    setFormData({
      ...formData,
      country,
      region: '',
      city: ''
    });
    
    // Load regions for selected country
    try {
      const regionsList = await userService.getRegions(country);
      setRegions(regionsList);
      setCities([]);
    } catch (error) {
      console.error('Error loading regions:', error);
    }
  };

  const handleRegionChange = async (e) => {
    const region = e.target.value;
    setFormData({
      ...formData,
      region,
      city: ''
    });
    
    // Load cities for selected region
    try {
      const citiesList = await userService.getCities(formData.country, region);
      setCities(citiesList);
    } catch (error) {
      console.error('Error loading cities:', error);
    }
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    onUpdate(formData);
  };

  return (
    <div className="user-edit-form">
      <h2>Edit User</h2>
      <form onSubmit={handleSubmit}>
        <div className="form-group">
          <label htmlFor="email">Email</label>
          <input
            type="email"
            id="email"
            name="email"
            value={formData.email}
            onChange={handleChange}
            required
          />
        </div>
        
        <div className="form-row">
          <div className="form-group">
            <label htmlFor="firstName">First Name</label>
            <input
              type="text"
              id="firstName"
              name="firstName"
              value={formData.firstName}
              onChange={handleChange}
              required
            />
          </div>
          
          <div className="form-group">
            <label htmlFor="lastName">Last Name</label>
            <input
              type="text"
              id="lastName"
              name="lastName"
              value={formData.lastName}
              onChange={handleChange}
              required
            />
          </div>
        </div>
        
        <div className="form-group">
          <label htmlFor="role">Role</label>
          <select
            id="role"
            name="role"
            value={formData.role}
            onChange={handleChange}
          >
            <option value="USER">User</option>
            <option value="ADMIN">Admin</option>
          </select>
        </div>
        
        <div className="form-group">
          <label htmlFor="country">Country</label>
          <select
            id="country"
            name="country"
            value={formData.country}
            onChange={handleCountryChange}
          >
            <option value="">Select Country</option>
            {countries.map((country, index) => (
              <option key={index} value={country}>
                {country}
              </option>
            ))}
          </select>
        </div>
        
        <div className="form-row">
          <div className="form-group">
            <label htmlFor="region">Region</label>
            <select
              id="region"
              name="region"
              value={formData.region}
              onChange={handleRegionChange}
              disabled={!formData.country}
            >
              <option value="">Select Region</option>
              {regions.map((region, index) => (
                <option key={index} value={region}>
                  {region}
                </option>
              ))}
            </select>
          </div>
          
          <div className="form-group">
            <label htmlFor="city">City</label>
            <select
              id="city"
              name="city"
              value={formData.city}
              onChange={handleChange}
              disabled={!formData.region}
            >
              <option value="">Select City</option>
              {cities.map((city, index) => (
                <option key={index} value={city}>
                  {city}
                </option>
              ))}
            </select>
          </div>
        </div>
        
        <div className="form-group">
          <label htmlFor="postalCode">Postal Code</label>
          <input
            type="text"
            id="postalCode"
            name="postalCode"
            value={formData.postalCode}
            onChange={handleChange}
          />
        </div>
        
        <div className="form-row">
          <div className="form-group">
            <label htmlFor="birthdate">Birthdate</label>
            <input
              type="date"
              id="birthdate"
              name="birthdate"
              value={formData.birthdate}
              onChange={handleChange}
            />
          </div>
          
          <div className="form-group">
            <label htmlFor="contactNumber">Contact Number</label>
            <input
              type="tel"
              id="contactNumber"
              name="contactNumber"
              value={formData.contactNumber}
              onChange={handleChange}
            />
          </div>
        </div>
        
        <div className="form-actions">
          <button type="submit" className="save-btn">
            Save Changes
          </button>
          <button type="button" className="cancel-btn" onClick={onCancel}>
            Cancel
          </button>
        </div>
      </form>
    </div>
  );
};

export default UserEditForm;