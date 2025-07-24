package com.veersa.eventApp.respository;

import com.veersa.eventApp.model.Ticket;
import com.veersa.eventApp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {

    // Define custom query methods if needed
    // For example, find tickets by event ID or user ID
    List<Ticket> findByEventId(Long eventId);
    List<Ticket> findByUser(User user);
}
