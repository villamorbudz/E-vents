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

export default function StripePayment({ amount, onPaymentSuccess }) {
  const [succeeded, setSucceeded] = useState(false);
  const [error, setError] = useState(null);
  const [processing, setProcessing] = useState(false);
  const [disabled, setDisabled] = useState(true);
  const [clientSecret, setClientSecret] = useState('');
  
  const stripe = useStripe();
  const elements = useElements();

  useEffect(() => {
    // Create PaymentIntent as soon as the page loads
    if (amount <= 0) return;
    
    axios.post('http://localhost:8080/api/payment/create-payment-intent', {
      amount: amount * 100, // convert to cents
      currency: 'php'
    })
    .then(res => {
      setClientSecret(res.data.clientSecret);
    })
    .catch(err => {
      setError('Error creating payment intent: ' + err.message);
    });
  }, [amount]);

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
      return;
    }

    const payload = await stripe.confirmCardPayment(clientSecret, {
      payment_method: {
        card: elements.getElement(CardElement)
      }
    });

    if (payload.error) {
      setError(`Payment failed: ${payload.error.message}`);
      setProcessing(false);
    } else {
      setError(null);
      setProcessing(false);
      setSucceeded(true);
      
      // Call the success callback
      if (onPaymentSuccess) {
        onPaymentSuccess(payload);
      }
    }
  };

  return (
    <div className="bg-gray-800 rounded-lg p-6 mt-4">
      <h3 className="text-xl font-bold mb-4">Pay with Card</h3>
      
      {succeeded ? (
        <div className="bg-green-600 bg-opacity-20 border border-green-500 text-green-500 p-3 rounded-md mb-4">
          Payment successful!
        </div>
      ) : (
        <form onSubmit={handleSubmit}>
          <div className="mb-4">
            <label className="block text-gray-300 mb-2">Card Details</label>
            <div className="bg-gray-700 p-3 rounded">
              <CardElement options={CARD_ELEMENT_OPTIONS} onChange={handleChange} />
            </div>
          </div>
          
          {error && (
            <div className="bg-red-600 bg-opacity-20 border border-red-500 text-red-500 p-3 rounded-md mb-4">
              {error}
            </div>
          )}
          
          <button
            disabled={processing || disabled || succeeded}
            type="submit"
            className={`bg-gray-100 text-gray-900 py-2 px-6 rounded font-medium transition w-full 
              ${(processing || disabled || succeeded) ? 'opacity-50 cursor-not-allowed' : 'hover:bg-white'}`}
          >
            {processing ? 'Processing...' : 'Pay ₱' + (amount ? amount.toLocaleString() : '0')}
          </button>
        </form>
      )}
    </div>
  );
}