package com.veersa.eventApp.respository;

import com.veersa.eventApp.model.Booking;
import com.veersa.eventApp.model.BookingStatus;
import com.veersa.eventApp.model.Event;
import com.veersa.eventApp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    // Get all bookings by a specific user
    @Query("SELECT b FROM Booking b WHERE b.user = :user AND b.status != 'CANCELLED'")
    List<Booking> findByUser(User user);

    @Query("SELECT b FROM Booking b WHERE b.event = :event AND b.status != 'CANCELLED'")
    List<Booking> findByEvent(Event event);

    @Query("SELECT b FROM Booking b WHERE b.user= :user AND b.status = 'CANCELLED'")
    List<Booking> findCancelledBookingsByUser(User user);

    boolean existsByEventId(Long eventId);

    void deleteAllByStatusAndExpiresAtBefore(BookingStatus status, LocalDateTime time);

}
