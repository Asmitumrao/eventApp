package com.veersa.eventApp.mapper;

import com.veersa.eventApp.DTO.CategoryRequest;
import com.veersa.eventApp.model.EventCategory;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Component
public class EventCategoryMapper {

    public EventCategory toEntity(CategoryRequest request) {
        EventCategory eventCategory = new EventCategory();
        eventCategory.setName(request.getName());
        eventCategory.setDescription(request.getDescription());
        return eventCategory;
    }
}

