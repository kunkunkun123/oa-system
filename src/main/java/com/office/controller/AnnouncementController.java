package com.office.controller;

import com.office.entity.Announcement;
import com.office.service.AnnouncementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/announcements")
public class AnnouncementController {
    @Autowired
    private AnnouncementService announcementService;
    
    @GetMapping
    public ResponseEntity<?> getAnnouncements(@RequestParam(required = false) Boolean all) {
        if (Boolean.TRUE.equals(all)) {
            return ResponseEntity.ok(announcementService.getAllAnnouncements());
        }
        return ResponseEntity.ok(announcementService.getActiveAnnouncements());
    }
    
    @PostMapping
    public ResponseEntity<?> createAnnouncement(@RequestBody Announcement announcement, 
                                              @RequestAttribute Integer currentUserId) {
        announcement.setCreatorId(currentUserId);
        announcementService.createAnnouncement(announcement);
        return ResponseEntity.ok().build();
    }
    
    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateStatus(@PathVariable("id") Integer id, 
                                        @RequestBody Integer status) {
        announcementService.updateStatus(id, status);
        return ResponseEntity.ok().build();
    }
} 