package com.mcmaster.scheduler.controller;

import com.mcmaster.scheduler.model.ScheduleItem;
import com.mcmaster.scheduler.repository.ScheduleItemRepository;
import com.mcmaster.scheduler.service.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/schedule")
public class ScheduleController {

    @Autowired
    private ScheduleItemRepository scheduleRepo;

    @Autowired
    private JwtService jwtService;

    // ✅ Add a schedule item
    @PostMapping("/add")
    public ResponseEntity<?> addItem(@RequestHeader("Authorization") String authHeader, @RequestBody ScheduleItem item) {
        try {
            String token = authHeader.substring(7);
            String email = jwtService.extractEmail(token);
            item.setOwnerEmail(email);
            ScheduleItem saved = scheduleRepo.save(item);
            return ResponseEntity.ok(saved);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error adding item.");
        }
    }

    // ✅ Get all schedule items
    @GetMapping
    public ResponseEntity<?> getAll(@RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.substring(7);
            String email = jwtService.extractEmail(token);
            List<ScheduleItem> items = scheduleRepo.findByOwnerEmail(email);
            return ResponseEntity.ok(items);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error fetching items.");
        }
    }

    // ✅ Update a schedule item
    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateItem(@RequestHeader("Authorization") String authHeader,
                                        @PathVariable Long id,
                                        @RequestBody ScheduleItem updatedItem) {
        try {
            String token = authHeader.substring(7);
            String email = jwtService.extractEmail(token);

            ScheduleItem existing = scheduleRepo.findById(id).orElse(null);
            if (existing == null || !existing.getOwnerEmail().equals(email)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized update.");
            }

            existing.setTitle(updatedItem.getTitle());
            existing.setType(updatedItem.getType());
            existing.setCourseCode(updatedItem.getCourseCode());
            existing.setDueDate(updatedItem.getDueDate());
            existing.setNotes(updatedItem.getNotes());

            ScheduleItem saved = scheduleRepo.save(existing);
            return ResponseEntity.ok(saved);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error updating item.");
        }
    }

    // ✅ Delete a schedule item
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteItem(@RequestHeader("Authorization") String authHeader,
                                        @PathVariable Long id) {
        try {
            String token = authHeader.substring(7);
            String email = jwtService.extractEmail(token);

            ScheduleItem existing = scheduleRepo.findById(id).orElse(null);
            if (existing != null && existing.getOwnerEmail().equals(email)) {
                scheduleRepo.deleteById(id);
                return ResponseEntity.ok("Item deleted.");
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Item not found or unauthorized.");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error deleting item.");
        }
    }
}
