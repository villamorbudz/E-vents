// Create a StripeWrapper component to load the Stripe provider
import React from 'react';
import { Elements } from '@stripe/react-stripe-js';
import { loadStripe } from '@stripe/stripe-js';

// Load your Stripe publishable key
// Replace with your actual publishable key
const stripePromise = loadStripe('pk_test_51RNCksFRWeTEEMtWp3j4Cv4UR9ZDUzNzDzfynRhAaqRTXYHY32c8GHk01n899rC4aRbqCL9PHFaTCaQdtjhuJipb005HFwJOEe');

export default function StripeWrapper({ children }) {
  return (
    <Elements stripe={stripePromise}>
      {children}
    </Elements>
  );
}