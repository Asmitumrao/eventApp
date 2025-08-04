package com.veersa.eventApp.respository;

import com.veersa.eventApp.model.EventCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface EventCategoryRepository extends JpaRepository<EventCategory,Long> {

    EventCategory findByName(String name);
}
