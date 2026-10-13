
package com.clubmanagement.backend;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(
    origins = "http://localhost:5173",
    allowedHeaders = {"Content-Type", "X-Admin-Key"}
)
public class AdminController {

    @Value("${app.admin.username}")
    private String adminUsername;

    @Value("${app.admin.password}")
    private String adminPassword;

    @Value("${app.admin.key}")
    private String adminKey;

    @PostMapping("/login")
    public Map<String, String> login(
            @RequestBody Map<String, String> request) {

        String username = request.get("username");
        String password = request.get("password");

        if (adminUsername.equals(username)
                && adminPassword.equals(password)) {
            return Map.of(
                "message", "Admin login successful",
                "role", "ADMIN",
                "adminKey", adminKey
            );
        }

        throw new ResponseStatusException(
            HttpStatus.UNAUTHORIZED,
            "Invalid admin username or password"
        );
    }
}
