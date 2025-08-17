package com.veersa.eventApp.service.ServiceImpl;

import com.razorpay.PaymentLink;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.veersa.eventApp.DTO.PaymentResponse;
import com.veersa.eventApp.service.PaymentService;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class RazorpayPaymentService implements PaymentService {

    private final RazorpayClient razorpayClient;

    public RazorpayPaymentService(@Value("${razorpay.key}") String key,
                                  @Value("${razorpay.secret}") String secret) throws RazorpayException {
        this.razorpayClient = new RazorpayClient(key, secret);
    }

    public PaymentResponse createPaymentLink(int amountInRupees, String email, String contact, Long bookingId) throws RazorpayException {

        JSONObject payload = new JSONObject();
        payload.put("amount", amountInRupees * 100); // in paise
        payload.put("currency", "INR");
        payload.put("description", "Event Booking #" + bookingId);
        payload.put("customer", new JSONObject()
                .put("name", email)
                .put("email", email)
                .put("contact", contact)
        );
//        payload.put("callback_url", "http://localhost:8080/api/payment/verify?bookingId=" + bookingId);
//        payload.put("callback_method", "get");

        PaymentLink payment = razorpayClient.paymentLink.create(payload);

        PaymentResponse paymentResponse =PaymentResponse.builder()
                .paymentId(payment.get("id"))
                .paymentLink(payment.get("short_url"))
                .ammount(amountInRupees)
                .message("Payment link created successfully")
                .BookingId(bookingId.toString())
                .build();

        return paymentResponse;
    }

    @Override
    public boolean verifyPayment(String paymentId,Long bookingId) throws RazorpayException {
        try{
            PaymentLink paymentLink = razorpayClient.paymentLink.fetch(paymentId);
            JSONObject json = new JSONObject(paymentLink.toString());

            if(!json.isNull("status") && json.getString("status").equals("paid")) {
                // Payment is successful, you can update your booking status here
                log.info("Payment successful for booking ID: {}", bookingId);
                return true;
            }
            return false;
        }
        catch (Exception e) {
            log.error("Error verifying payment: {}", e.getMessage());
            throw new RazorpayException("Payment verification failed", e);
        }
    }


}
