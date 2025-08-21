package com.veersa.eventApp.service.ServiceImpl;

import com.veersa.eventApp.exception.BookingNotFoundException;
import com.veersa.eventApp.model.Booking;
import com.veersa.eventApp.model.BookingStatus;
import com.veersa.eventApp.respository.BookingRepository;
import com.veersa.eventApp.service.TicketService;
import com.veersa.eventApp.util.PDFGenerator;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;


@Service
@RequiredArgsConstructor
public class TicketServiceImpl implements TicketService {


    private final BookingRepository bookingRepository;
    private final PDFGenerator pdfGenerator;

    @Override
    public void downloadTicketsForBooking(Long bookingId, HttpServletResponse response) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException("Booking not found"));
        if(booking.getStatus().equals(BookingStatus.CANCELLED)) {
            throw new BookingNotFoundException("Booking is cancelled and tickets cannot be downloaded");
        }

        try {
            pdfGenerator.generateTicketsPdf(booking, response);
        } catch (IOException e) {
            throw new RuntimeException("Error generating PDF", e);
        }
    }


}
