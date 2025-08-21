package com.veersa.eventApp.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class BookingRequest {

    @NotNull(message = "Event ID cannot be blank")
    private Long eventId;

    @NotBlank(message = "User Email is required and cannot be blank")
    private String userEmail;

    @NotBlank(message = "User Name is required and cannot be blank")
    private String userPhone;


    @NotNull(message = "Number of seats cannot be null")
    private Integer  numberOfSeats;

    private List<AttendeeRequest> attendees;
}
