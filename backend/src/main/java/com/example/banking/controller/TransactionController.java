package com.example.banking.controller;

import com.example.banking.model.Transaction;
import com.example.banking.service.TransactionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping("/transactions")
    public ResponseEntity<?> transact(@RequestHeader(value = "Idempotency-Key", required = true) String idempotencyKey,
                                      @RequestParam Long userId,
                                      @RequestParam BigDecimal amount,
                                      @RequestParam String type,
                                      @RequestParam String channel) {
        Transaction t = transactionService.createTransaction(userId, amount, type, channel, idempotencyKey);
        return ResponseEntity.ok(t);
    }

    @GetMapping("/users/{id}/transactions")
    public ResponseEntity<List<Transaction>> history(@PathVariable("id") Long userId) {
        return ResponseEntity.ok(transactionService.history(userId));
    }
}
