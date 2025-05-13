package it342.g4.e_vents;

import it342.g4.e_vents.model.Act;
import it342.g4.e_vents.model.Category;
import it342.g4.e_vents.model.Event;
import it342.g4.e_vents.model.Role;
import it342.g4.e_vents.model.Tags;
import it342.g4.e_vents.model.User;
import it342.g4.e_vents.model.enums.Tag;
import it342.g4.e_vents.repository.ActRepository;
import it342.g4.e_vents.repository.CategoryRepository;
import it342.g4.e_vents.repository.EventRepository;
import it342.g4.e_vents.repository.RoleRepository;
import it342.g4.e_vents.repository.TagsRepository;
import it342.g4.e_vents.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
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
            ActRepository actRepository,
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            EventRepository eventRepository) {
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
            
            // Initialize Users if they don't exist
            initializeUsers(userRepository, roleRepository, passwordEncoder);
            
            // Initialize Acts if they don't exist
            initializeActs(actRepository, categoryRepository, tagsRepository);
            
            // Initialize Events if they don't exist
            initializeEvents(eventRepository, userRepository, actRepository);
        };
    }
    
    /**
     * Helper method to create a Date object for January 1, 2000
     * @return Date object set to January 1, 2000
     */
    private Date createJan2000Date() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(2000, Calendar.JANUARY, 1);
        return calendar.getTime();
    }
    
    private void initializeUsers(
            UserRepository userRepository,
            RoleRepository roleRepository,
            PasswordEncoder passwordEncoder) {
            
        // Initialize admin user
        if (!userRepository.existsByEmail("admin@events.com")) {
            User adminUser = new User();
            adminUser.setFirstName("Admin");
            adminUser.setLastName("User");
            adminUser.setEmail("admin@events.com");
            adminUser.setPassword(passwordEncoder.encode("12345678"));
            adminUser.setContactNumber("1234567890");
            adminUser.setCountry("Philippines");
            
            // Set birthdate to January 1, 2000
            adminUser.setBirthdate(createJan2000Date());
            
            adminUser.setActive(true);
            
            // Set ADMIN role
            Role adminRole = roleRepository.findByName("ADMIN")
                    .orElseThrow(() -> new RuntimeException("Role ADMIN not found"));
            adminUser.setRole(adminRole);
            
            userRepository.save(adminUser);
            System.out.println("Initialized admin user: admin@events.com");
        }
        
        // Initialize organizer user
        if (!userRepository.existsByEmail("limalima@events.com")) {
            User organizerUser = new User();
            organizerUser.setFirstName("Renato");
            organizerUser.setLastName("Limalima");
            organizerUser.setEmail("limalima@events.com");
            organizerUser.setPassword(passwordEncoder.encode("12345678"));
            organizerUser.setContactNumber("0123456789");
            organizerUser.setCountry("Philippines");
            
            // Set birthdate to January 1, 2000
            organizerUser.setBirthdate(createJan2000Date());
            
            organizerUser.setActive(true);
            
            // Set ORGANIZER role
            Role organizerRole = roleRepository.findByName("ORGANIZER")
                    .orElseThrow(() -> new RuntimeException("Role ORGANIZER not found"));
            organizerUser.setRole(organizerRole);
            
            userRepository.save(organizerUser);
            System.out.println("Initialized organizer user: limalima@events.com");
        }
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
    
    /**
     * Initialize default events in the system
     * @param eventRepository Repository for Event entities
     * @param userRepository Repository for User entities
     * @param actRepository Repository for Act entities
     */
    private void initializeEvents(
            EventRepository eventRepository,
            UserRepository userRepository,
            ActRepository actRepository) {
        
        // Check if we already have events
        if (eventRepository.count() > 0) {
            return;
        }
        
        // Get user with ID 1 to be the host
        User host = userRepository.findById(1L)
                .orElseThrow(() -> new RuntimeException("User with ID 1 not found"));
        
        // Get acts for event lineups
        Act taylorSwift = actRepository.findByName("Taylor Swift")
                .orElseThrow(() -> new RuntimeException("Act 'Taylor Swift' not found"));
        
        Act laLakers = actRepository.findByName("Los Angeles Lakers")
                .orElseThrow(() -> new RuntimeException("Act 'Los Angeles Lakers' not found"));
        
        Act hamilton = actRepository.findByName("Hamilton: An American Musical")
                .orElseThrow(() -> new RuntimeException("Act 'Hamilton: An American Musical' not found"));
        
        // Create Event 1: Music Concert
        Event musicConcert = new Event();
        musicConcert.setName("Summer Music Festival 2025");
        musicConcert.setDescription("A spectacular summer music festival featuring Taylor Swift and other top artists.");
        musicConcert.setDate(LocalDate.of(2025, 7, 15));
        musicConcert.setTime(LocalTime.of(18, 30));
        musicConcert.setVenue("National Stadium, Manila");
        musicConcert.setUser(host);
        musicConcert.setStatus(Event.STATUS_SCHEDULED);
        musicConcert.setActive(true);
        musicConcert.setLineup(List.of(taylorSwift));
        
        eventRepository.save(musicConcert);
        System.out.println("Initialized event: Summer Music Festival 2025");
        
        // Create Event 2: Sports Exhibition
        Event sportsEvent = new Event();
        sportsEvent.setName("Basketball Exhibition Match 2025");
        sportsEvent.setDescription("Watch the legendary LA Lakers in an exhibition match, followed by a special Hamilton performance.");
        sportsEvent.setDate(LocalDate.of(2025, 8, 20));
        sportsEvent.setTime(LocalTime.of(19, 0));
        sportsEvent.setVenue("Araneta Coliseum, Quezon City");
        sportsEvent.setUser(host);
        sportsEvent.setStatus(Event.STATUS_SCHEDULED);
        sportsEvent.setActive(true);
        sportsEvent.setLineup(Arrays.asList(laLakers, hamilton));
        
        eventRepository.save(sportsEvent);
        System.out.println("Initialized event: Basketball Exhibition Match 2025");
    }
}
