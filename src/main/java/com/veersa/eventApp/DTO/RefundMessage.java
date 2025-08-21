package com.veersa.eventApp.DTO;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RefundMessage implements Serializable {

    private Long bookingId;
    private Long userId;
    private Double amount;
}
