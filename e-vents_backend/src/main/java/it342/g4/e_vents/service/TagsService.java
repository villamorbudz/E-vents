package it342.g4.e_vents.service;

import it342.g4.e_vents.model.Category;
import it342.g4.e_vents.model.Tags;
import it342.g4.e_vents.repository.TagsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TagsService {

    private final TagsRepository tagsRepository;

    @Autowired
    public TagsService(TagsRepository tagsRepository) {
        this.tagsRepository = tagsRepository;
    }

    public List<Tags> getAllTags() {
        return tagsRepository.findAll();
    }

    public Optional<Tags> getTagById(Long id) {
        return tagsRepository.findById(id);
    }

    public List<Tags> getTagsByCategory(Category category) {
        return tagsRepository.findByCategory(category);
    }

    public List<Tags> searchTags(String query) {
        return tagsRepository.findByNameContainingIgnoreCase(query);
    }

    public Tags saveTag(Tags tag) {
        return tagsRepository.save(tag);
    }

    public void deleteTag(Long id) {
        tagsRepository.deleteById(id);
    }
}
