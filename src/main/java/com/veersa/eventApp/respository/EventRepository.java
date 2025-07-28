package com.veersa.eventApp.respository;

import com.veersa.eventApp.model.Event;
import com.veersa.eventApp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event,Long> {

    @Query("""
    SELECT e FROM Event e WHERE 
    (:keyword IS NULL OR LOWER(e.name) LIKE LOWER(CONCAT('%', :keyword, '%')) 
     OR CAST(e.description AS string) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND
    (:dateAfter IS NULL OR e.startTime >= :dateAfter) AND
    (:minSeats IS NULL OR e.availableSeats >= :minSeats) AND
    (:organizerId IS NULL OR e.organizer.id = :organizerId)
""")
    List<Event> searchEvents(
            @Param("keyword") String keyword,
            @Param("dateAfter") LocalDateTime dateAfter,
            @Param("minSeats") Integer minSeats,
            @Param("organizerId") Long organizerId
    );

    List<Event> findByOrganizer(User organizer);


}
