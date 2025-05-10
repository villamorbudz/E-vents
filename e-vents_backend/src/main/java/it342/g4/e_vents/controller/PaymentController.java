package it342.g4.e_vents.controller;


import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import it342.g4.e_vents.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import java.util.HashMap;
import it342.g4.e_vents.dto.PaymentRequest;

@RestController
@RequestMapping("/api/payment")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;
    
    @PostMapping("/create-payment-intent")
    public ResponseEntity<Map<String, String>> createPaymentIntent(@RequestBody PaymentRequest paymentRequest) {
        try {
            PaymentIntent paymentIntent = paymentService.createPaymentIntent(
                paymentRequest.getAmount(), 
                paymentRequest.getCurrency()
            );
            
            Map<String, String> response = new HashMap<>();
            response.put("clientSecret", paymentIntent.getClientSecret());
            
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (StripeException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
