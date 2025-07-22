package com.veersa.eventApp.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime bookingTime;

    @ManyToOne
    @JoinColumn(name = "event_id")
    @NotNull
    private Event event;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @NotNull
    private User user;

    private int numberOfSeats;


    @PrePersist
    public void onCreate() {
        this.bookingTime = LocalDateTime.now();
    }

}

