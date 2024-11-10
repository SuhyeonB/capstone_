package com.engdiarytoon.server.happiness;

import com.engdiarytoon.server.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/happiness")
public class HappinessController {

    private final HappinessService happinessService;

    @Autowired
    public HappinessController(HappinessService happinessService) {
        this.happinessService = happinessService;
    }

    // Record today's happiness score
    @PostMapping
    public ResponseEntity<Happiness> recordHappiness(@AuthenticationPrincipal User user, @RequestBody Map<String, Float> request) {
        Float value = request.get("value");
        if (value == null) {
            return ResponseEntity.badRequest().body(null);
        }

        try {
            Happiness happiness = happinessService.recordHappiness(user, value);
            return ResponseEntity.status(HttpStatus.CREATED).body(happiness);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(null);
        }
    }

    // Update today's happiness score
    @PutMapping
    public ResponseEntity<Happiness> updateHappiness(@AuthenticationPrincipal User user, @RequestBody Map<String, Float> request) {
        Float value = request.get("value");
        if (value == null) {
            return ResponseEntity.badRequest().body(null);
        }

        try {
            Happiness updatedHappiness = happinessService.updateHappiness(user, value);
            return ResponseEntity.ok(updatedHappiness);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    // Retrieve today's happiness score
    @GetMapping
    public ResponseEntity<Happiness> getTodayHappiness(@AuthenticationPrincipal User user) {
        Optional<Happiness> happiness = happinessService.getTodayHappiness(user);
        return happiness.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(null));
    }

    // Delete today's happiness score
    @DeleteMapping
    public ResponseEntity<Void> deleteTodayHappiness(@AuthenticationPrincipal User user) {
        try {
            happinessService.deleteTodayHappiness(user);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
