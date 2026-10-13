
package com.clubmanagement.backend.announcement;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/announcements")
@CrossOrigin(
    origins = "http://localhost:5173",
    allowedHeaders = {"Content-Type", "X-Admin-Key"}
)
public class AnnouncementController {

    private final AnnouncementRepository repository;

    @Value("${app.admin.key}")
    private String adminKey;

    public AnnouncementController(AnnouncementRepository repository) {
        this.repository = repository;
    }

    private void requireAdmin(String suppliedKey) {
        if (suppliedKey == null || !adminKey.equals(suppliedKey)) {
            throw new ResponseStatusException(
                HttpStatus.FORBIDDEN, "Admin authorization required"
            );
        }
    }

    @GetMapping
    public List<Announcement> getAllAnnouncements() {
        return repository.findAllByOrderByCreatedAtDesc();
    }

    @PostMapping
    public Announcement addAnnouncement(
            @RequestHeader(value = "X-Admin-Key", required = false)
            String suppliedKey,
            @RequestBody Announcement announcement) {

        requireAdmin(suppliedKey);

        if (announcement.getTitle() == null ||
                announcement.getTitle().isBlank()) {
            throw new IllegalArgumentException(
                "Announcement title is required");
        }

        announcement.setId(null);
        return repository.save(announcement);
    }
}
