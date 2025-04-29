package it342.g4.e_vents.dto;

import java.sql.Time;
import java.util.Date;
import java.util.List;

/**
 * DTO for creating a new event
 */
public class EventCreationRequest {
    private String name;
    private String description;
    private Date date;
    private Time time;
    private Long venueId;
    private List<Long> lineupIds;
    private Long creatorId;
    
    // Default constructor
    public EventCreationRequest() {
    }
    
    // Getters and setters
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public Date getDate() {
        return date;
    }
    
    public void setDate(Date date) {
        this.date = date;
    }
    
    public Time getTime() {
        return time;
    }
    
    public void setTime(Time time) {
        this.time = time;
    }
    
    public Long getVenueId() {
        return venueId;
    }
    
    public void setVenueId(Long venueId) {
        this.venueId = venueId;
    }
    
    public List<Long> getLineupIds() {
        return lineupIds;
    }
    
    public void setLineupIds(List<Long> lineupIds) {
        this.lineupIds = lineupIds;
    }
    
    public Long getCreatorId() {
        return creatorId;
    }
    
    public void setCreatorId(Long creatorId) {
        this.creatorId = creatorId;
    }
}