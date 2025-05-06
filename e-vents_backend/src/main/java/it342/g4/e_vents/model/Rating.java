package it342.g4.e_vents.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import java.time.LocalDateTime;

@Entity
@Table(name = "ratings")
public class Rating {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "rating_id")
    private Long ratingId;

    @Min(0)
    @Max(5)
    @Column(nullable = false)
    private int ratingValue;

    @Column(nullable = false)
    private Long ratedEntityId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EntityType ratedEntityType;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(length = 500)
    private String message;

    @Column(nullable = false)
    private boolean isActive = true;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public enum EntityType {
        EVENT,
        ACT
    }

    // Default constructor for JPA
    protected Rating() {}

    // Builder class
    public static class Builder {
        private final Rating rating;

        public Builder() {
            rating = new Rating();
        }

        public Builder ratingValue(int value) {
            rating.ratingValue = value;
            return this;
        }

        public Builder ratedEntityId(Long id) {
            rating.ratedEntityId = id;
            return this;
        }

        public Builder ratedEntityType(EntityType type) {
            rating.ratedEntityType = type;
            return this;
        }

        public Builder user(User user) {
            rating.user = user;
            return this;
        }

        public Builder message(String message) {
            rating.message = message;
            return this;
        }

        public Rating build() {
            return rating;
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    // Getters
    public Long getRatingId() { return ratingId; }
    public int getRatingValue() { return ratingValue; }
    public Long getRatedEntityId() { return ratedEntityId; }
    public EntityType getRatedEntityType() { return ratedEntityType; }
    public User getUser() { return user; }
    public String getMessage() { return message; }
    public boolean isActive() { return isActive; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    // Only necessary setters
    public void setMessage(String message) { this.message = message; }
    public void setActive(boolean active) { isActive = active; }
}
