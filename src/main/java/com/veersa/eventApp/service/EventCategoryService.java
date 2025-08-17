package com.veersa.eventApp.service;

import com.veersa.eventApp.DTO.CategoryRequest;
import com.veersa.eventApp.model.EventCategory;

import java.util.List;

public interface EventCategoryService {

    // Define methods related to event categories here
    // For example:
     List<EventCategory> getAllCategories();
     EventCategory getCategoryById(Long id);
     EventCategory createCategory(CategoryRequest request);
     EventCategory updateCategory(Long id, CategoryRequest request);
     void deleteCategory(Long id);
}
