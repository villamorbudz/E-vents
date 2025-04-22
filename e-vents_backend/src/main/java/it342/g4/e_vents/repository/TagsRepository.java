package it342.g4.e_vents.repository;

import it342.g4.e_vents.model.Category;
import it342.g4.e_vents.model.Tags;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TagsRepository extends JpaRepository<Tags, Long> {
    List<Tags> findByCategory(Category category);
    List<Tags> findByNameContainingIgnoreCase(String name);
}
