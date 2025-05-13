import React, { useState, useEffect } from 'react';
import { CardElement, useStripe, useElements } from '@stripe/react-stripe-js';
import axios from 'axios';

const CARD_ELEMENT_OPTIONS = {
  style: {
    base: {
      color: '#fff',
      fontFamily: '"Helvetica Neue", Helvetica, sans-serif',
      fontSmoothing: 'antialiased',
      fontSize: '16px',
      '::placeholder': {
        color: '#aab7c4'
      }
    },
    invalid: {
      color: '#fa755a',
      iconColor: '#fa755a'
    }
  }
};

// Payment Modal Component
const PaymentModal = ({ isOpen, onClose, children }) => {
  if (!isOpen) return null;

  return (
    <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
      <div className="bg-gray-800 rounded-lg p-6 w-full max-w-md relative">
        <button 
          onClick={onClose} 
          className="absolute top-4 right-4 text-gray-400 hover:text-white"
        >
          <svg xmlns="http://www.w3.org/2000/svg" className="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M6 18L18 6M6 6l12 12" />
          </svg>
        </button>
        {children}
      </div>
    </div>
  );
};

export default function StripePayment({ amount, onPaymentSuccess, onClose }) {
  const [succeeded, setSucceeded] = useState(false);
  const [error, setError] = useState(null);
  const [processing, setProcessing] = useState(false);
  const [disabled, setDisabled] = useState(true);
  const [clientSecret, setClientSecret] = useState('');
  const [paymentMethod, setPaymentMethod] = useState({
    cardName: '',
    email: ''
  });
  
  const stripe = useStripe();
  const elements = useElements();
  
  useEffect(() => {
    // Create PaymentIntent as soon as the page loads
    if (amount <= 0) return;
    
    // Show loading state while creating payment intent
    setProcessing(true);
    
    axios.post('https://e-vents-4bld.onrender.com/api/payment/create-payment-intent', {
      amount: amount * 100, // convert to cents
      currency: 'php'
    })
    .then(res => {
      setClientSecret(res.data.clientSecret);
      setProcessing(false);
    })
    .catch(err => {
      setError('Error creating payment intent: ' + (err.response?.data?.message || err.message));
      setProcessing(false);
    });
  }, [amount]);
  
  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setPaymentMethod(prev => ({
      ...prev,
      [name]: value
    }));
  };
  
  const handleChange = async (event) => {
    // Listen for changes in the CardElement
    // and display any errors as the customer types their card details
    setDisabled(event.empty);
    setError(event.error ? event.error.message : '');
  };
  
  const handleSubmit = async ev => {
    ev.preventDefault();
    setProcessing(true);
    
    if (!stripe || !elements) {
      // Stripe.js has not loaded yet. Make sure to disable
      // form submission until Stripe.js has loaded.
      setProcessing(false);
      return;
    }
    
    const payload = await stripe.confirmCardPayment(clientSecret, {
      payment_method: {
        card: elements.getElement(CardElement),
        billing_details: {
          name: paymentMethod.cardName,
          email: paymentMethod.email,
        }
      }
    });
    
    if (payload.error) {
      setError(`Payment failed: ${payload.error.message}`);
      setProcessing(false);
    } else {
      setError(null);
      setProcessing(false);
      setSucceeded(true);
      
      // Call the success callback after a short delay
      setTimeout(() => {
        if (onPaymentSuccess) {
          onPaymentSuccess(payload);
        }
      }, 1500);
    }
  };
  
  return (
    <PaymentModal isOpen={true} onClose={onClose}>
      <h3 className="text-xl font-bold mb-4">Pay with Card</h3>
      
      {succeeded ? (
        <div className="mb-4">
          <div className="bg-green-600 bg-opacity-20 border border-green-500 text-green-500 p-3 rounded-md mb-4 flex items-center">
            <svg xmlns="http://www.w3.org/2000/svg" className="h-5 w-5 mr-2" viewBox="0 0 20 20" fill="currentColor">
              <path fillRule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zm3.707-9.293a1 1 0 00-1.414-1.414L9 10.586 7.707 9.293a1 1 0 00-1.414 1.414l2 2a1 1 0 001.414 0l4-4z" clipRule="evenodd" />
            </svg>
            Payment successful!
          </div>
          <p className="text-gray-300 text-center">Redirecting to your events...</p>
        </div>
      ) : (
        <form onSubmit={handleSubmit}>
          <div className="mb-4">
            <label className="block text-gray-300 mb-2">Full Name</label>
            <input
              type="text"
              name="cardName"
              value={paymentMethod.cardName}
              onChange={handleInputChange}
              className="w-full bg-gray-700 text-white p-3 rounded focus:outline-none focus:ring-2 focus:ring-red-500"
              placeholder="Name on card"
              required
            />
          </div>
          
          <div className="mb-4">
            <label className="block text-gray-300 mb-2">Email</label>
            <input
              type="email"
              name="email"
              value={paymentMethod.email}
              onChange={handleInputChange}
              className="w-full bg-gray-700 text-white p-3 rounded focus:outline-none focus:ring-2 focus:ring-red-500"
              placeholder="Receipt email"
              required
            />
          </div>
          
          <div className="mb-4">
            <label className="block text-gray-300 mb-2">Card Details</label>
            <div className="bg-gray-700 p-3 rounded focus:outline-none focus:ring-2 focus:ring-red-500">
              <CardElement options={CARD_ELEMENT_OPTIONS} onChange={handleChange} />
            </div>
            <p className="text-gray-400 text-xs mt-2">
              Test card: 4242 4242 4242 4242 | Date: Any future date | CVC: Any 3 digits
            </p>
          </div>
          
          {error && (
            <div className="bg-red-600 bg-opacity-20 border border-red-500 text-red-500 p-3 rounded-md mb-4">
              {error}
            </div>
          )}
          
          <div className="flex justify-between items-center">
            <p className="text-lg font-semibold">Total: ₱{amount ? amount.toLocaleString() : '0'}</p>
            <button
              disabled={processing || disabled || succeeded}
              type="submit"
              className={`bg-red-600 text-white py-2 px-6 rounded font-medium transition
                ${(processing || disabled || succeeded) ? 'opacity-50 cursor-not-allowed' : 'hover:bg-red-700'}`}
            >
              {processing ? 'Processing...' : 'Pay Now'}
            </button>
          </div>
        </form>
      )}
    </PaymentModal>
  );
}