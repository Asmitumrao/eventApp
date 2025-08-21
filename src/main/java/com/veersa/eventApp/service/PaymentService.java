package com.veersa.eventApp.service;

import com.razorpay.Payment;
import com.razorpay.PaymentLink;
import com.razorpay.RazorpayException;
import com.veersa.eventApp.DTO.PaymentResponse;

public interface PaymentService {

    public PaymentLink createPaymentLink(Double amountInRupees, String email, String contact, Long bookingId) throws RazorpayException;

    boolean verifyPayment(String paymentId, Long bookingId) throws RazorpayException;

    public void refundPayment(Long bookingId);
}
