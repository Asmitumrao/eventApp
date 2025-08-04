package com.veersa.eventApp.service.ServiceImpl;

import com.veersa.eventApp.exception.CategoryAlreadyExistsException;
import com.veersa.eventApp.exception.CategoryNotFoundException;
import com.veersa.eventApp.respository.EventCategoryRepository;
import com.veersa.eventApp.model.EventCategory;
import com.veersa.eventApp.service.EventCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EventCategoryServiceImpl implements EventCategoryService {

    private  final EventCategoryRepository eventCategoryRepository;


    @Override
    public List<EventCategory> getAllCategories() {
        return eventCategoryRepository.findAll();
    }

    @Override
    public EventCategory getCategoryById(Long id) {
        return eventCategoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found with id: " + id));

    }

    @Override
    public EventCategory createCategory(EventCategory request) {
        // Check if the category already exists
        EventCategory existingCategory = eventCategoryRepository.findByName(request.getName());
        if (existingCategory != null) {
            throw new CategoryAlreadyExistsException("Category already exists with name: " + request.getName());
        }

        // Save the new category
        return eventCategoryRepository.save(request);
    }

    @Override
    public EventCategory updateCategory(Long id, EventCategory request) {
        EventCategory existingCategory = eventCategoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException("Category not found with id: " + id));

        // Update the category details
        existingCategory.setName(request.getName());
        existingCategory.setDescription(request.getDescription());

        // Save the updated category
        return eventCategoryRepository.save(existingCategory);
    }

    @Override
    public void deleteCategory(Long id) {
        EventCategory existingCategory = eventCategoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException("Category not found with id: " + id));

        // Delete the category
        eventCategoryRepository.delete(existingCategory);

    }
}
