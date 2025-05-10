package it342.g4.e_vents.dto;


public class PaymentRequest {
    private Long amount;
    private String currency;
    
    // Default constructor
    public PaymentRequest() {}
    
    public PaymentRequest(Long amount, String currency) {
        this.amount = amount;
        this.currency = currency;
    }
    
    // Getters and setters
    public Long getAmount() {
        return amount;
    }
    
    public void setAmount(Long amount) {
        this.amount = amount;
    }
    
    public String getCurrency() {
        return currency;
    }
    
    public void setCurrency(String currency) {
        this.currency = currency;
    }
}
