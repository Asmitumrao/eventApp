package com.veersa.eventApp.DTO;


import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class PaymentResponse {

    private  String paymentLink;
    private  String message;
    private  String paymentId;
    private  Integer ammount;
}
