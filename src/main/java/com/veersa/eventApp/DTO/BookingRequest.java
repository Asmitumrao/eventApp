package com.veersa.eventApp.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class BookingRequest {

    @NotBlank(message = "Event ID cannot be blank")
    private Long eventId;

    @NotBlank(message = "User Email is required and cannot be blank")
    private String userEmail;

    @NotBlank(message = "User Name is required and cannot be blank")
    private String userPhone;

    @NotBlank(message = "User Name is required and cannot be blank")
    @Size(min = 1, message = "Number of seats must be at least 1")
    private int numberOfSeats;

    private List<AttendeeRequest> attendees;
}
