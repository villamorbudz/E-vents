package it342.g4.e_vents.model;

import jakarta.persistence.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import javax.imageio.ImageIO;
import com.fasterxml.jackson.annotation.JsonBackReference;

@Entity
@Table(name = "acts")
public class Act {
    public Long getActId() {
        return actId;
    }

    public void setActId(Long actId) {
        this.actId = actId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    @ManyToMany(mappedBy = "lineup")
    @JsonBackReference
    private List<Event> events;

    public List<Event> getEvents() {
        return events;
    }

    public void setEvents(List<Event> events) {
        this.events = events;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "act_id")
    private Long actId;

    @Column(nullable = false)
    private String name;

    @Lob
    private byte[] image;

    @Column(nullable = false)
    private String category;

    @Lob
    private String description;

    @ElementCollection
    private List<String> tags;


    public BufferedImage getImageAsBufferedImage() {
        if (image != null) {
            try (ByteArrayInputStream bais = new ByteArrayInputStream(image)) {
                return ImageIO.read(bais);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public void setImageFromBufferedImage(BufferedImage bufferedImage) {
        if (bufferedImage != null) {
            try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
                ImageIO.write(bufferedImage, "png", baos);
                this.image = baos.toByteArray();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
