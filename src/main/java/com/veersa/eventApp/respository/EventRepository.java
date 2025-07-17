package com.veersa.eventApp.respository;

import com.veersa.eventApp.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventRepository extends JpaRepository<Event,Long> {
}
