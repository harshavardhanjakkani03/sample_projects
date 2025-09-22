package com.example.banking.controller;

import com.example.banking.model.IdempotencyKey;
import com.example.banking.repository.IdempotencyKeyRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;
import java.time.OffsetDateTime;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/api/idempotency")
public class IdempotencyController {

    private final IdempotencyKeyRepository repo;

    public IdempotencyController(IdempotencyKeyRepository repo) {
        this.repo = repo;
    }

    @GetMapping("/{key}")
    public ResponseEntity<?> getByKey(@PathVariable("key") String key) {
        Optional<IdempotencyKey> maybe = repo.findByKeyValue(key);
        return maybe.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/cleanup")
    public ResponseEntity<?> cleanup(@RequestParam(name = "days", required = false, defaultValue = "30") int days) {
        OffsetDateTime cutoff = OffsetDateTime.now().minusDays(days);
        int[] deleted = {0};
        repo.findAll().stream().filter(k -> k.getCreatedAt().isBefore(cutoff)).forEach(k -> { repo.delete(k); deleted[0]++; });
        return ResponseEntity.ok(Map.of("deleted", deleted[0]));
    }
}
