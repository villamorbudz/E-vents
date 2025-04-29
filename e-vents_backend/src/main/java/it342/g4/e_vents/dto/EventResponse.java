package it342.g4.e_vents.dto;

import java.sql.Time;
import java.util.Date;

/**
 * DTO for event response with simplified data
 */
public class EventResponse {
    private Long eventId;
    private String name;
    private String description;
    private Date date;
    private Time time;
    private String venueName;
    private String status;
    private String bannerImagePath;
    
    // Constructor for converting from Event entity
    public EventResponse(Long eventId, String name, String description, 
                        Date date, Time time, String venueName, 
                        String status, String bannerImagePath) {
        this.eventId = eventId;
        this.name = name;
        this.description = description;
        this.date = date;
        this.time = time;
        this.venueName = venueName;
        this.status = status;
        this.bannerImagePath = bannerImagePath;
    }
    
    // Getters
    public Long getEventId() {
        return eventId;
    }
    
    public String getName() {
        return name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public Date getDate() {
        return date;
    }
    
    public Time getTime() {
        return time;
    }
    
    public String getVenueName() {
        return venueName;
    }
    
    public String getStatus() {
        return status;
    }
    
    public String getBannerImagePath() {
        return bannerImagePath;
    }
}