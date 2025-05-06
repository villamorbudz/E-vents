package it342.g4.e_vents.repository;

import it342.g4.e_vents.model.Venue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface VenueRepository extends JpaRepository<Venue, Long> {
    Optional<Venue> findByGooglePlaceId(String googlePlaceId);
    
    @Query("SELECT v FROM Venue v WHERE " +
           "LOWER(v.name) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(v.formattedAddress) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<Venue> searchVenues(@Param("query") String query);
    
    @Query("SELECT v FROM Venue v WHERE " +
           "LOWER(v.name) = LOWER(:name) AND " +
           "LOWER(v.formattedAddress) = LOWER(:formattedAddress)")
    Optional<Venue> findByExactNameAndAddress(
        @Param("name") String name,
        @Param("formattedAddress") String formattedAddress
    );
}
