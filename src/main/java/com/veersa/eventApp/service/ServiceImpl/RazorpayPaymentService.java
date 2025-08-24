package com.veersa.eventApp.service.ServiceImpl;

import com.razorpay.PaymentLink;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.razorpay.Refund;
import com.veersa.eventApp.model.Booking;
import com.veersa.eventApp.model.BookingStatus;
import com.veersa.eventApp.respository.BookingRepository;
import com.veersa.eventApp.service.BookingService;
import com.veersa.eventApp.service.PaymentService;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class RazorpayPaymentService implements PaymentService {

    private final RazorpayClient razorpayClient;
    private final BookingRepository bookingRepository;

    public RazorpayPaymentService(@Value("${razorpay.key}") String key,
                                  @Value("${razorpay.secret}") String secret, BookingRepository bookingRepository) throws RazorpayException {


        System.out.println("Razorpay Key: " + key);
        System.out.println("Razorpay Secret: " + secret);

        this.bookingRepository = bookingRepository;
        this.razorpayClient = new RazorpayClient(key, secret);

        System.out.println(razorpayClient);
        System.out.println("Razorpay Client initialized successfully");
    }

    @Override
    public PaymentLink createPaymentLink(Double amountInRupees, String email, String contact, Long bookingId) throws RazorpayException {

        JSONObject payload = new JSONObject();
        payload.put("amount", amountInRupees * 100); // in paise
        payload.put("currency", "INR");
        payload.put("description", "Event Booking #" + bookingId);
        payload.put("customer", new JSONObject()
                .put("name", email)
                .put("email", email)
                .put("contact", contact)
        );

        System.out.println("Creating payment link with payload: " + payload.toString());

        PaymentLink paymentLink = razorpayClient.paymentLink.create(payload);

        System.out.println("Payment link created: " + paymentLink.toString());

        return paymentLink;

    }

    @Override
    public boolean verifyPayment(Booking booking) throws RazorpayException {

        String paymentLinkId=booking.getPaymentId();

        try{

            PaymentLink paymentLink = razorpayClient.paymentLink.fetch(paymentLinkId);
            JSONObject json = new JSONObject(paymentLink.toString());

            if(!json.isNull("status") && json.getString("status").equals("paid")) {
                // Payment is successful, you can update your booking status here
//                loger.info("Payment successful for booking ID: {}", bookingId);
                return true;
            }
            return false;
        }
        catch (Exception e) {
//            log.error("Error verifying payment: {}", e.getMessage());
            throw new RazorpayException("Payment verification failed", e);
        }
    }

//    @Override
    @Transactional
    public void refundPayment(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        if (booking.getStatus().equals(BookingStatus.PENDING) || booking.getStatus().equals(BookingStatus.CANCELLED)) {
            throw new RuntimeException("Booking is not eligible for refund");
        }

        if (booking.getRefundId() != null) {
            throw new RuntimeException("Refund already processed for this booking");
        }

        try {
            JSONObject refundRequest = new JSONObject();
            refundRequest.put("amount", booking.getAmount() * 100); // amount in paise

            // Call Razorpay refund API
            String paymentId = getPaymentId(booking.getPaymentId());
            Refund refund = razorpayClient.payments.refund(paymentId, refundRequest);

            booking.setStatus(BookingStatus.REFUND_INITIATED);
            booking.setRefundId(refund.get("id")); // Assuming refund ID is retrieved with "id" key
            bookingRepository.save(booking);
//            log.info("Refund initiated for booking ID: {}", bookingId);

        } catch (Exception e) {
            System.out.println("Refund failed for booking ID: " + booking.getId() + ", Error: " + e.getMessage());
//            log.error("Refund failed for booking ID: " + booking.getId(), e);
        }
    }

    public String getPaymentId(String paymentLinkId) throws RazorpayException {
        try {
            // Fetch payment link details
            PaymentLink paymentLink = razorpayClient.paymentLink.fetch(paymentLinkId);
            JSONObject paymentLinkJson = new JSONObject(paymentLink.toString());

            // Check if payments exist
            if (paymentLinkJson.has("payments")) {
                JSONArray payments = paymentLinkJson.getJSONArray("payments");

                // Look for the first successful payment
                for (int i = 0; i < payments.length(); i++) {
                    JSONObject payment = payments.getJSONObject(i);
                    String status = payment.getString("status");

                    // Return payment ID if status is captured (successful)
                    if ("captured".equals(status)) {
                        return payment.getString("payment_id");
                    }
                }
            }
            return null; // No successful payment found

        } catch (Exception e) {
            throw new RazorpayException("Failed to get payment ID: " + e.getMessage());
        }
    }



}
