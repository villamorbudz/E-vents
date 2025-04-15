package it342.g4.e_vents.model;

import jakarta.persistence.*;

@Entity
@Table(name = "ticket_categories")
public class TicketCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ticket_category_id")
    private Long ticketCategoryId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Double basePrice;

    @Lob
    private String description;

    // Add validation when updating totalTickets (totalTickets >= ticketsSold)

    @Column(nullable = false)
    private int totalTickets;

    @Column(nullable = false)
    private int ticketsSold;

    @Column(nullable = false)
    private String status;

    // Getters and setters

    public Long getTicketCategoryId() {
        return ticketCategoryId;
    }

    public void setTicketCategoryId(Long ticketCategoryId) {
        this.ticketCategoryId = ticketCategoryId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getBasePrice() {
        return basePrice;
    }

    public void setBasePrice(Double basePrice) {
        this.basePrice = basePrice;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getTotalTickets() {
        return totalTickets;
    }

    public void setTotalTickets(int totalTickets) {
        this.totalTickets = totalTickets;
    }

    public int getTicketsSold() {
        return ticketsSold;
    }

    public void setTicketsSold(int ticketsSold) {
        this.ticketsSold = ticketsSold;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getAvailableTickets() {
        return totalTickets - ticketsSold;
    }
}
