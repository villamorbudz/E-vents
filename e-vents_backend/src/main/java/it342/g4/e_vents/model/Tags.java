package it342.g4.e_vents.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Entity
@Table(name = "tags")
public class Tags {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tag_id")
    private Long tagId;

    @Column(nullable = false)
    private String name;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    @JsonBackReference
    private Category category;
    
    @Column(name = "is_active", nullable = false)
    private boolean isActive = true;
    public Long getTagId() {
        return tagId;
    }

    public void setTagId(Long tagId) {
        this.tagId = tagId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @JsonIgnore
    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }
    
    /**
     * Returns essential category information without causing an infinite reference loop
     * @return Map containing category ID and name
     */
    public Map<String, Object> getCategoryInfo() {
        if (category == null) {
            return null;
        }
        Map<String, Object> categoryInfo = new HashMap<>();
        categoryInfo.put("categoryId", category.getCategoryId());
        categoryInfo.put("name", category.getName());
        categoryInfo.put("isActive", category.isActive());
        return categoryInfo;
    }
    
    /**
     * Helper method to get the category name
     * @return The name of the category this tag belongs to
     */
    public String getCategoryName() {
        return category != null ? category.getName() : null;
    }
    
    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }
    
    @ManyToMany(mappedBy = "tags")
    @JsonBackReference
    private List<Act> acts = new ArrayList<>();
    
    public List<Act> getActs() {
        return acts;
    }
    
    public void setActs(List<Act> acts) {
        this.acts = acts;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        Tags tags = (Tags) o;
        
        if (tagId != null ? !tagId.equals(tags.tagId) : tags.tagId != null) return false;
        return name != null ? name.equals(tags.name) : tags.name == null;
    }
    
    @Override
    public int hashCode() {
        int result = tagId != null ? tagId.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        return result;
    }
    
    @Override
    public String toString() {
        return "Tags{" +
                "tagId=" + tagId +
                ", name='" + name + '\'' +
                ", category=" + (category != null ? category.getName() : "null") +
                ", isActive=" + isActive +
                '}';
    }
}
