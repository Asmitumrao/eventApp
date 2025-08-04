package com.veersa.eventApp.service;

public interface NotificationService {

    void bookingCreatedNotification(Long bookingId);

    void bookingCancelledNotification(Long bookingId);

    void bookingUpdatedNotification(Long bookingId);
    
}
