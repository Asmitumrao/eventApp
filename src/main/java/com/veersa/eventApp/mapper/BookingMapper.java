package com.veersa.eventApp.mapper;


import com.veersa.eventApp.DTO.BookingRequest;
import com.veersa.eventApp.DTO.BookingResponse;
import com.veersa.eventApp.DTO.TicketDTO;
import com.veersa.eventApp.model.Booking;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.List;

@Lazy
@Component
public class BookingMapper {

    public BookingResponse mapToBookingResponse(Booking booking) {
        BookingResponse dto = new BookingResponse();
        dto.setBookingId(booking.getId());
        dto.setEventId(booking.getEvent().getId());
        dto.setUserId(booking.getUser().getId());
        dto.setBookingTime(booking.getBookingTime());

        List<TicketDTO> ticketDTOs = booking.getTickets().stream().map(ticket -> {
            TicketDTO ticketDTO = new TicketDTO();
            ticketDTO.setId(ticket.getId());
            ticketDTO.setTicketNumber(ticket.getTicketNumber());
            ticketDTO.setBookingId(booking.getId());
            return ticketDTO;
        }).toList();

        dto.setTickets(ticketDTOs);
        return dto;
    }

    public List<BookingResponse> mapToBookingResponse(List<Booking> bookings) {
        return bookings.stream()
                .map(this::mapToBookingResponse)
                .toList();
    }

}
