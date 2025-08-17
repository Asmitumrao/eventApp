package com.veersa.eventApp.controller;


import com.veersa.eventApp.DTO.CategoryRequest;
import com.veersa.eventApp.model.EventCategory;
import com.veersa.eventApp.service.EventCategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/events/categories")
@RequiredArgsConstructor
public class EventCategoryController {

    private final EventCategoryService eventCategoryService;


     @GetMapping
     public ResponseEntity<List<EventCategory>> getAllCategories() {
         return ResponseEntity.ok(eventCategoryService.getAllCategories());
     }


     @GetMapping("/{id}")
     public ResponseEntity<EventCategory> getCategoryById(@PathVariable Long id) {
         return ResponseEntity.ok(eventCategoryService.getCategoryById(id));
     }


     @PostMapping
     @PreAuthorize("hasRole('ADMIN')")
     public ResponseEntity<EventCategory> createCategory(@Valid @RequestBody CategoryRequest request) {

         return ResponseEntity.ok(eventCategoryService.createCategory(request));
     }

     @PutMapping("/update/{id}")
     @PreAuthorize("hasRole('ADMIN')")
     public ResponseEntity<EventCategory> updateCategory(@PathVariable Long id, @Valid @RequestBody CategoryRequest request) {
         return ResponseEntity.ok(eventCategoryService.updateCategory(id, request));
     }

     @DeleteMapping("/{id}")
     @PreAuthorize("hasRole('ADMIN')")
     public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
         System.out.println("Deleting category with id: " + id);
         eventCategoryService.deleteCategory(id);
         return ResponseEntity.noContent().build();
     }
}
