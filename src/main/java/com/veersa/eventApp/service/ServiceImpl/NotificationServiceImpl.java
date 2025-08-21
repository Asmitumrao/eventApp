package com.veersa.eventApp.service.ServiceImpl;

import com.veersa.eventApp.DTO.EmailRequest;
import com.veersa.eventApp.exception.BookingNotFoundException;
import com.veersa.eventApp.feign.EmailerClient;
import com.veersa.eventApp.model.Booking;
import com.veersa.eventApp.respository.BookingRepository;
import com.veersa.eventApp.service.BookingService;

import com.veersa.eventApp.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {


    private final EmailerClient emailerClient;
    private final BookingRepository bookingRepository;


    @Override
    public void bookingCreatedNotification(Long bookingId) {


        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException("Booking not found with id: " + bookingId));

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
                .orElseThrow(() -> new BookingNotFoundException("Booking not found with id: " + bookingId));

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
                .orElseThrow(() -> new BookingNotFoundException("Booking not found with id: " + bookingId));
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


//
//    private void mailSender(String to, String subject, String body) {
//        EmailRequest emailRequest = new EmailRequest(to, subject, body);
//
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_JSON);
//        headers.set("X-API-KEY", "9b73f8d8c1e647d7b9ab02d34992f48b58ff7b3c87a9f1a2d3c7b81e9d4f0a6c"); // store in env var
//
//        HttpEntity<EmailRequest> request = new HttpEntity<>(emailRequest, headers);
//
//        ResponseEntity<String> result = restTemplate.postForEntity(
//                "http://localhost:8081/api/email/send",
//                request,
//                String.class
//        );
//
//        System.out.println("Email sent successfully: " + result.getBody());
//    }

    public void mailSender(String to, String subject, String body) {
        EmailRequest emailRequest = new EmailRequest(to, subject, body);
        emailerClient.sendEmail(emailRequest);
    }


}
