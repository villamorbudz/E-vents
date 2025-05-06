package it342.g4.e_vents;

import it342.g4.e_vents.model.Act;
import it342.g4.e_vents.model.Category;
import it342.g4.e_vents.model.Role;
import it342.g4.e_vents.model.Tags;
import it342.g4.e_vents.model.enums.Tag;
import it342.g4.e_vents.repository.ActRepository;
import it342.g4.e_vents.repository.CategoryRepository;
import it342.g4.e_vents.repository.RoleRepository;
import it342.g4.e_vents.repository.TagsRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

@SpringBootApplication
public class EVentsApplication {

    public static void main(String[] args) {
        SpringApplication.run(EVentsApplication.class, args);
    }

    @Bean
    @Transactional
    public CommandLineRunner initializeData(
            CategoryRepository categoryRepository,
            RoleRepository roleRepository,
            TagsRepository tagsRepository,
            ActRepository actRepository) {
        return args -> {
            // Initialize Roles if they don't exist
            for (it342.g4.e_vents.model.enums.Role roleEnum : it342.g4.e_vents.model.enums.Role.values()) {
                if (!roleRepository.existsByName(roleEnum.name())) {
                    Role role = new Role();
                    role.setName(roleEnum.name());
                    roleRepository.save(role);
                }
            }

            // Initialize Categories if they don't exist
            for (it342.g4.e_vents.model.enums.Category categoryEnum : it342.g4.e_vents.model.enums.Category.values()) {
                if (!categoryRepository.existsByName(categoryEnum.name())) {
                    Category category = new Category();
                    category.setName(categoryEnum.name());
                    categoryRepository.save(category);
                }
            }

            // Initialize Tags if they don't exist
            for (Tag tagEnum : Tag.values()) {
                if (!tagsRepository.existsByName(tagEnum.name())) {
                    Category category = categoryRepository.findByName(tagEnum.getCategory().name())
                            .orElseThrow(() -> new RuntimeException("Category not found: " + tagEnum.getCategory().name()));
                    
                    Tags tag = new Tags();
                    tag.setName(tagEnum.name());
                    tag.setCategory(category);
                    tagsRepository.save(tag);
                }
            }
            
            // Initialize Acts if they don't exist
            initializeActs(actRepository, categoryRepository, tagsRepository);
        };
    }
    
    private void initializeActs(
            ActRepository actRepository, 
            CategoryRepository categoryRepository, 
            TagsRepository tagsRepository) {
        
        // 1. Taylor Swift (Music act)
        if (!actRepository.existsByName("Taylor Swift")) {
            Act taylorSwift = new Act();
            taylorSwift.setName("Taylor Swift");
            taylorSwift.setDescription("American singer-songwriter known for narrative songs about her personal life.");
            taylorSwift.setActive(true);
            
            Category musicCategory = categoryRepository.findByName("MUSIC")
                    .orElseThrow(() -> new RuntimeException("Category MUSIC not found"));
            taylorSwift.setCategory(musicCategory);
            
            List<Tags> taylorTags = Arrays.asList(
                    tagsRepository.findByName("POP").orElseThrow(() -> new RuntimeException("Tag POP not found")),
                    tagsRepository.findByName("COUNTRY").orElseThrow(() -> new RuntimeException("Tag COUNTRY not found"))
            );
            taylorSwift.setTags(taylorTags);
            actRepository.save(taylorSwift);
        }
        
        // 2. Los Angeles Lakers (Sports act)
        if (!actRepository.existsByName("Los Angeles Lakers")) {
            Act laLakers = new Act();
            laLakers.setName("Los Angeles Lakers");
            laLakers.setDescription("Professional basketball team based in Los Angeles, one of the most successful teams in the NBA.");
            laLakers.setActive(true);
            
            Category sportsCategory = categoryRepository.findByName("SPORTS")
                    .orElseThrow(() -> new RuntimeException("Category SPORTS not found"));
            laLakers.setCategory(sportsCategory);
            
            List<Tags> lakersTags = Arrays.asList(
                    tagsRepository.findByName("BASKETBALL").orElseThrow(() -> new RuntimeException("Tag BASKETBALL not found"))
            );
            laLakers.setTags(lakersTags);
            actRepository.save(laLakers);
        }
        
        // 3. Hamilton: An American Musical (Theatre act)
        if (!actRepository.existsByName("Hamilton: An American Musical")) {
            Act hamilton = new Act();
            hamilton.setName("Hamilton: An American Musical");
            hamilton.setDescription("A sung-and-rapped-through musical about the life of American Founding Father Alexander Hamilton.");
            hamilton.setActive(true);
            
            Category theatreCategory = categoryRepository.findByName("THEATRE")
                    .orElseThrow(() -> new RuntimeException("Category THEATRE not found"));
            hamilton.setCategory(theatreCategory);
            
            List<Tags> hamiltonTags = Arrays.asList(
                    tagsRepository.findByName("MUSICAL").orElseThrow(() -> new RuntimeException("Tag MUSICAL not found"))
            );
            hamilton.setTags(hamiltonTags);
            actRepository.save(hamilton);
        }
    }
}
