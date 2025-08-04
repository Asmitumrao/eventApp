package com.veersa.eventApp.service;

import com.razorpay.RazorpayException;
import com.veersa.eventApp.DTO.PaymentResponse;

public interface PaymentService {

    public PaymentResponse createPaymentLink(int amountInRupees, String email, String contact, Long bookingId) throws RazorpayException;
    boolean verifyPayment(String paymentId, Long bookingId) throws RazorpayException;
}
