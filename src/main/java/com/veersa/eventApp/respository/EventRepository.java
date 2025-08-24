package com.veersa.eventApp.respository;

import com.veersa.eventApp.model.Event;
import com.veersa.eventApp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.swing.text.html.Option;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event,Long> {


    @Query("SELECT e FROM Event e WHERE e.status != 'CANCELLED'")
    List<Event> findAll();

    @Query("""
    SELECT e FROM Event e WHERE 
    (:keyword IS NULL OR LOWER(e.name) LIKE LOWER(CONCAT('%', :keyword, '%')) 
     OR CAST(e.description AS string) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND
    (:dateAfter IS NULL OR e.startTime >= :dateAfter) AND
    (:minSeats IS NULL OR e.availableSeats >= :minSeats) AND
    (:organizerId IS NULL OR e.organizer.id = :organizerId) AND
    (e.status != 'CANCELLED')
""")
    List<Event> searchEvents(
            @Param("keyword") String keyword,
            @Param("dateAfter") LocalDateTime dateAfter,
            @Param("minSeats") Integer minSeats,
            @Param("organizerId") Long organizerId
    );

    @Query("SELECT e FROM Event e WHERE e.organizer = :organizer AND e.status != 'CANCELLED'")
    List<Event> findByOrganizer(User organizer);

    @Query("SELECT e FROM Event e WHERE e.organizer.id = :organizerId AND e.status != 'CANCELLED'")
    List<Event> findByCategoryId(Long categoryId);

    @Query("SELECT COUNT(e) > 0 FROM Event e WHERE e.category.id = :categoryId")
    boolean existsByCategoryId(Long categoryId);


    @Query("SELECT e FROM Event e WHERE e.startTime >= CURRENT_TIMESTAMP  AND e.status != 'CANCELLED' ORDER BY e.startTime ASC")
    List<Event> findUpcomingEvents();

    @Query("SELECT e FROM Event e WHERE e.startTime < CURRENT_TIMESTAMP AND e.status != 'CANCELLED' ORDER BY e.startTime DESC")
    List<Event> findPastEvents();

    @Query("SELECT e FROM Event e WHERE e.status = 'CANCELLED' AND e.organizer = :user")
    List<Event> findCancelledEventsByOrganizer(User user);

    @Query("SELECT e FROM Event e WHERE e.status = 'SCHEDULED'  ")
    List<Event> findScheduledEvents();


}
