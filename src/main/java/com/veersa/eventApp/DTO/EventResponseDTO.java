package com.veersa.eventApp.DTO;


import lombok.Data;

import java.time.LocalDateTime;

@Data
public class EventResponseDTO {


    private Long id;
    private String name;
    private String description;
    private LocalDateTime dateTime;
    private int availableSeats;
    private String organizerName;

}
