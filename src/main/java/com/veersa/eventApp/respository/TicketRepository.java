package com.veersa.eventApp.respository;

import com.veersa.eventApp.model.Ticket;
import com.veersa.eventApp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {

    // Define custom query methods if needed
    // For example, find tickets by event ID or user ID
    List<Ticket> findByEventId(Long eventId);
    List<Ticket> findByUser(User user);

    @Modifying
    @Query("DELETE FROM Ticket t WHERE t.booking.id = :bookingId")
    void deleteByBookingId(@Param("bookingId") Long bookingId);


}
