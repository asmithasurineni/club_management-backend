
package com.clubmanagement.backend.event;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/events")
@CrossOrigin(
    origins = "http://localhost:5173",
    allowedHeaders = {"Content-Type", "X-Admin-Key"}
)
public class EventController {

    private final EventRepository repository;

    @Value("${app.admin.key}")
    private String adminKey;

    public EventController(EventRepository repository) {
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
    public List<Event> getAllEvents() {
        return repository.findAllByOrderByEventDateAsc();
    }

    @PostMapping
    public Event addEvent(
            @RequestHeader(value = "X-Admin-Key", required = false)
            String suppliedKey,
            @RequestBody Event event) {

        requireAdmin(suppliedKey);

        if (event.getClubId() == null ||
                event.getName() == null ||
                event.getName().isBlank()) {
            throw new IllegalArgumentException(
                "Club ID and event name are required");
        }

        event.setId(null);
        return repository.save(event);
    }
}
