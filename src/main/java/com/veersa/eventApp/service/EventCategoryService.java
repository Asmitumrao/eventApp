package com.veersa.eventApp.service;

import com.veersa.eventApp.model.EventCategory;

import java.util.List;

public interface EventCategoryService {

    // Define methods related to event categories here
    // For example:
     List<EventCategory> getAllCategories();
     EventCategory getCategoryById(Long id);
     EventCategory createCategory(EventCategory request);
     EventCategory updateCategory(Long id, EventCategory request);
     void deleteCategory(Long id);
}
