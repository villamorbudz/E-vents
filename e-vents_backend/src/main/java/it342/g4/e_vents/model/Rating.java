package it342.g4.e_vents.model;

import jakarta.persistence.*;

@Entity
@Table(name = "ratings")
public class Rating {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "rating_id")
    private Long ratingId;

    @Column(nullable = false)
    private int ratingValue; // Rating value between 0 and 5

    @Column(nullable = false)
    private Long ratedEntityId; // ID of the entity being rated

    @Column(nullable = false)
    private String ratedEntityType; // Type of the entity (e.g., "Event", "Act")

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // User who gave the rating

    @Column(length = 500)
    private String message; // Optional message with the rating

    @Column(nullable = false)
    private boolean isActive; // Indicates if the rating is active

    // Getters and setters
    public Long getRatingId() {
        return ratingId;
    }

    public void setRatingId(Long ratingId) {
        this.ratingId = ratingId;
    }

    public int getRatingValue() {
        return ratingValue;
    }

    public void setRatingValue(int ratingValue) {
        this.ratingValue = ratingValue;
    }

    public Long getRatedEntityId() {
        return ratedEntityId;
    }

    public void setRatedEntityId(Long ratedEntityId) {
        this.ratedEntityId = ratedEntityId;
    }

    public String getRatedEntityType() {
        return ratedEntityType;
    }

    public void setRatedEntityType(String ratedEntityType) {
        this.ratedEntityType = ratedEntityType;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean isActive) {
        this.isActive = isActive;
    }
}
