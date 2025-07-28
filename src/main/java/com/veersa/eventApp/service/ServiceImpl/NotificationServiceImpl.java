package com.veersa.eventApp.service.ServiceImpl;

import com.veersa.eventApp.DTO.EmailRequest;
import com.veersa.eventApp.model.Booking;
import com.veersa.eventApp.respository.BookingRepository;
import com.veersa.eventApp.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements BookingService.NotificationService {


    private final RestTemplate restTemplate;
    private final BookingRepository bookingRepository;

    @Value("${notification.service.url}")
    private static String NOTIFICATION_SERVICE_URL;



    @Override
    public void bookingCreatedNotification(Long bookingId) {


        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found with id: " + bookingId));

        String to = booking.getUser().getEmail().toLowerCase();
        String subject = "Booking Confirmation";
        String body = "Dear " + booking.getUser().getUsername() + ",\n\n" +
                "Your booking for the event '" + booking.getEvent().getName() + "' has been confirmed.\n" +
                "Booking ID: " + booking.getId() + "\n" +
                "Number of Seats: " + booking.getNumberOfSeats() + "\n\n" +
                "Thank you for your booking!\n\n" +
                "Best regards,\n" +
                "EventApp Team";
        mailSender(to, subject, body);
    }


    @Override
    public void bookingCancelledNotification(Long bookingId) {

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found with id: " + bookingId));

        String to = booking.getUser().getEmail().toLowerCase();
        String subject = "Booking Cancellation";
        String body = "Dear " + booking.getUser().getUsername() + ",\n\n" +
                "Your booking for the event '" + booking.getEvent().getName() + "' has been cancelled.\n" +
                "Booking ID: " + booking.getId() + "\n" +
                "Number of Seats: " + booking.getNumberOfSeats() + "\n\n" +
                "We apologize for any inconvenience caused.\n\n" +
                "Best regards,\n" +
                "EventApp Team";
        mailSender(to, subject, body);


    }

    @Override
    public void bookingUpdatedNotification(Long bookingId) {

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found with id: " + bookingId));
        String to = booking.getUser().getEmail().toLowerCase();
        String subject = "Booking Update";
        String body = "Dear " + booking.getUser().getUsername() + ",\n\n" +
                "Your booking for the event '" + booking.getEvent().getName() + "' has been updated.\n" +
                "Booking ID: " + booking.getId() + "\n" +
                "Number of Seats: " + booking.getNumberOfSeats() + "\n\n" +
                "Thank you for your continued support!\n\n" +
                "Best regards,\n" +
                "EventApp Team";
        mailSender(to, subject, body);
    }
    private void mailSender(String to, String subject, String body) {
        EmailRequest emailRequest = new EmailRequest(to, subject, body);

        ResponseEntity<String> result = restTemplate.postForEntity(
                "http://localhost:8081/api/email/send",
                emailRequest,
                String.class
        );
        System.out.println("Email sent successfully: " + result.getBody());
    }
}
