package com.veersa.eventApp.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String ticketNumber; // You can generate this uniquely (UUID or custom format)

    @ManyToOne
    @JoinColumn(name = "booking_id")
    private Booking booking;

    private String seatNumber; // Optional: if seat-based

    private LocalDateTime issuedAt;

    private Boolean checkedIn = false; // Default to false, can be updated when user checks in

    @ManyToOne
    private Event event; // link the ticket to an event

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user; // link the ticket to a user who booked itcam

    @Override
    public String toString() {
        return "Ticket{" +
                "id=" + id +
                ", ticketNumber='" + ticketNumber + '\'' +
                // do NOT call booking.toString()
                ", bookingId=" + (booking != null ? booking.getId() : null) +
                '}';
    }
}
