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
import org.springframework.web.bind.annotation.GetMapping;
import java.util.List;
import java.util.Map;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;

@RestController
@RequestMapping("/api/idempotency")
public class IdempotencyController {

    private final IdempotencyKeyRepository repo;
    private final ObjectMapper mapper = new ObjectMapper();

    public IdempotencyController(IdempotencyKeyRepository repo) {
        this.repo = repo;
    }

    @GetMapping("/{key}")
    public ResponseEntity<?> getByKey(@PathVariable("key") String key) {
        Optional<IdempotencyKey> maybe = repo.findByKeyValue(key);
        return maybe.map(k -> {
            Map<String, Object> resp = Map.of(
                    "id", k.getId(),
                    "keyValue", k.getKeyValue(),
                    "userId", k.getUserId(),
                    "createdAt", k.getCreatedAt(),
                    "transactionId", k.getTransactionId(),
                    "result", parseResult(k.getResultJson())
            );
            return ResponseEntity.ok(resp);
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("")
    public ResponseEntity<?> list(@RequestParam(name = "days", required = false) Integer days,
                  @RequestParam(name = "page", required = false, defaultValue = "0") int page,
                  @RequestParam(name = "size", required = false, defaultValue = "20") int size) {
    OffsetDateTime cutoff = days == null ? null : OffsetDateTime.now().minusDays(days);
    Pageable pageable = PageRequest.of(page, size);
    Page<IdempotencyKey> keyPage;
    if (cutoff != null) {
        keyPage = repo.findByCreatedAtAfter(cutoff, pageable);
    } else {
        keyPage = repo.findAll(pageable);
    }

    List<Map<String, Object>> content = keyPage.stream().map(k -> Map.of(
        "id", k.getId(),
        "keyValue", k.getKeyValue(),
        "userId", k.getUserId(),
        "createdAt", k.getCreatedAt(),
        "transactionId", k.getTransactionId()
    )).toList();

    return ResponseEntity.ok(Map.of(
        "content", content,
        "page", keyPage.getNumber(),
        "size", keyPage.getSize(),
        "totalElements", keyPage.getTotalElements(),
        "totalPages", keyPage.getTotalPages()
    ));
    }

    @DeleteMapping("/cleanup")
    public ResponseEntity<?> cleanup(@RequestParam(name = "days", required = false, defaultValue = "30") int days) {
        OffsetDateTime cutoff = OffsetDateTime.now().minusDays(days);
        int[] deleted = {0};
        repo.findAll().stream().filter(k -> k.getCreatedAt().isBefore(cutoff)).forEach(k -> { repo.delete(k); deleted[0]++; });
        return ResponseEntity.ok(Map.of("deleted", deleted[0]));
    }

    @DeleteMapping("/{key}")
    public ResponseEntity<?> deleteKey(@PathVariable("key") String key) {
        Optional<IdempotencyKey> maybe = repo.findByKeyValue(key);
        if (maybe.isPresent()) {
            repo.delete(maybe.get());
            return ResponseEntity.ok(Map.of("deleted", 1));
        }
        return ResponseEntity.status(404).body(Map.of("deleted", 0));
    }

    private Object parseResult(String json) {
        if (json == null) return null;
        try {
            return mapper.readValue(json, Object.class);
        } catch (JsonProcessingException e) {
            return json;
        }
    }
}
