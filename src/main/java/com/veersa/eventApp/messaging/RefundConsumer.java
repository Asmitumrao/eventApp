package com.veersa.eventApp.messaging;

import com.veersa.eventApp.DTO.RefundMessage;
import com.veersa.eventApp.config.RabbitMQConfig;
import com.veersa.eventApp.model.Booking;
import com.veersa.eventApp.service.BookingService;
import com.veersa.eventApp.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RefundConsumer {

    private final BookingService bookingService;
    private final PaymentService paymentService;

    @RabbitListener(queues = RabbitMQConfig.REFUND_QUEUE)
    public void processRefund(RefundMessage message) {

        paymentService.refundPayment(message.getBookingId());
        bookingService.deleteBooking(message.getBookingId());
    }
}
