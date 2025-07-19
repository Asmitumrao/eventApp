package com.veersa.eventApp.respository;

import com.veersa.eventApp.model.Booking;
import com.veersa.eventApp.model.Event;
import com.veersa.eventApp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    // Get all bookings by a specific user
    List<Booking> findByUser(User user);

    // Get all bookings for a specific event
    List<Booking> findByEvent(Event event);
}
