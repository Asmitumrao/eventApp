package com.veersa.eventApp.sheduler;

import com.veersa.eventApp.model.Booking;
import com.veersa.eventApp.model.BookingStatus;
import com.veersa.eventApp.respository.BookingRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class BookingCleanupScheduler {
    private final BookingRepository bookingRepository;

    // Runs every 10 minutes
    @Scheduled(fixedRate = 10 * 60 * 1000)
    @Transactional
    public void removeExpiredPendingBookings() {
        bookingRepository.deleteAllByStatusAndExpiresAtBefore(
                BookingStatus.PENDING, LocalDateTime.now());
        log.info("Expired booking cleanup completed at {}", LocalDateTime.now());
    }

}
