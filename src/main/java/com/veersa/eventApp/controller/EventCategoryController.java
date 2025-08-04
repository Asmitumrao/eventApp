package com.veersa.eventApp.controller;


import com.veersa.eventApp.model.EventCategory;
import com.veersa.eventApp.service.EventCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/event/categories")
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

     @PostMapping("/admin")
     @PreAuthorize("hasRole('ADMIN')")
     public ResponseEntity<EventCategory> createCategory(@RequestBody EventCategory request) {
         return ResponseEntity.ok(eventCategoryService.createCategory(request));
     }

     @PutMapping("/admin/{id}")
     @PreAuthorize("hasRole('ADMIN')")
     public ResponseEntity<EventCategory> updateCategory(@PathVariable Long id, @RequestBody EventCategory request) {
         return ResponseEntity.ok(eventCategoryService.updateCategory(id, request));
     }

     @DeleteMapping("/admin/{id}")
     @PreAuthorize("hasRole('ADMIN')")
     public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
         eventCategoryService.deleteCategory(id);
         return ResponseEntity.noContent().build();
     }
}
