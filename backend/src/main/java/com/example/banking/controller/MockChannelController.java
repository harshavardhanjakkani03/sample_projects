package com.example.banking.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/mock")
public class MockChannelController {

    @PostMapping("/atm")
    public ResponseEntity<?> atm(@RequestBody Map<String, Object> payload) {
        // deterministic-ish reference for tests: prefer idempotencyKey or userId when present
        Object idem = payload.getOrDefault("idempotencyKey", payload.get("Idempotency-Key"));
        String ref;
        if (idem instanceof String) {
            ref = "ATM-" + ((String) idem);
        } else if (payload.get("userId") != null) {
            ref = "ATM-user-" + String.valueOf(payload.get("userId"));
        } else {
            ref = "ATM-static-1";
        }
        return ResponseEntity.ok(Map.of("status", "ok", "ref", ref));
    }

    @PostMapping("/upi")
    public ResponseEntity<?> upi(@RequestBody Map<String, Object> payload) {
        Object idem = payload.getOrDefault("idempotencyKey", payload.get("Idempotency-Key"));
        String ref;
        if (idem instanceof String) {
            ref = "UPI-" + ((String) idem);
        } else if (payload.get("userId") != null) {
            ref = "UPI-user-" + String.valueOf(payload.get("userId"));
        } else {
            ref = "UPI-static-1";
        }
        return ResponseEntity.ok(Map.of("status", "ok", "ref", ref));
    }

    @PostMapping("/bank")
    public ResponseEntity<?> bank(@RequestBody Map<String, Object> payload) {
        Object idem = payload.getOrDefault("idempotencyKey", payload.get("Idempotency-Key"));
        String ref;
        if (idem instanceof String) {
            ref = "BANK-" + ((String) idem);
        } else if (payload.get("userId") != null) {
            ref = "BANK-user-" + String.valueOf(payload.get("userId"));
        } else {
            ref = "BANK-static-1";
        }
        return ResponseEntity.ok(Map.of("status", "ok", "ref", ref));
    }
}
