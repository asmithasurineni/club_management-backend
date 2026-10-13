
package com.clubmanagement.backend.membership;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/memberships")
@CrossOrigin(
    origins = "http://localhost:5173",
    allowedHeaders = {"Content-Type", "X-Admin-Key"}
)
public class MembershipController {

    private final MembershipRepository repository;

    @Value("${app.admin.key}")
    private String adminKey;

    public MembershipController(MembershipRepository repository) {
        this.repository = repository;
    }

    private void requireAdmin(String suppliedKey) {
        if (suppliedKey == null || !adminKey.equals(suppliedKey)) {
            throw new ResponseStatusException(
                HttpStatus.FORBIDDEN, "Admin authorization required"
            );
        }
    }

    @PostMapping("/join")
    public Map<String, String> joinClub(
            @RequestBody Map<String, Long> request) {

        Long studentId = request.get("studentId");
        Long clubId = request.get("clubId");

        if (studentId == null || clubId == null) {
            throw new IllegalArgumentException(
                "Student ID and Club ID are required");
        }

        if (repository.existsByStudentIdAndClubId(studentId, clubId)) {
            throw new IllegalArgumentException(
                "You have already requested this club");
        }

        Membership membership = new Membership();
        membership.setStudentId(studentId);
        membership.setClubId(clubId);
        membership.setStatus("PENDING");
        repository.save(membership);

        return Map.of(
            "message", "Membership request submitted successfully"
        );
    }

    @GetMapping("/student/{studentId}")
    public List<Membership> getStudentMemberships(
            @PathVariable Long studentId) {
        return repository.findByStudentId(studentId);
    }

    @GetMapping
    public List<Membership> getAllMemberships(
            @RequestHeader(value = "X-Admin-Key", required = false)
            String suppliedKey) {

        requireAdmin(suppliedKey);
        return repository.findAll();
    }

    @PutMapping("/{id}/status")
    public Map<String, String> updateMembershipStatus(
            @PathVariable Long id,
            @RequestHeader(value = "X-Admin-Key", required = false)
            String suppliedKey,
            @RequestBody Map<String, String> request) {

        requireAdmin(suppliedKey);

        String status = request.get("status");

        if (status == null ||
                !(status.equals("APPROVED") ||
                  status.equals("REJECTED"))) {
            throw new IllegalArgumentException(
                "Status must be APPROVED or REJECTED");
        }

        Membership membership = repository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException(
                "Membership request not found"));

        membership.setStatus(status);
        repository.save(membership);

        return Map.of(
            "message", "Membership status updated successfully",
            "status", status
        );
    }
}
