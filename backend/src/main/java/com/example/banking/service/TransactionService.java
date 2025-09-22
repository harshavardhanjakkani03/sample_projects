package com.example.banking.service;

import com.example.banking.model.IdempotencyKey;
import com.example.banking.model.Transaction;
import com.example.banking.model.Balance;
import com.example.banking.repository.IdempotencyKeyRepository;
import com.example.banking.repository.TransactionRepository;
import com.example.banking.repository.UserRepository;
import com.example.banking.repository.BalanceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final IdempotencyKeyRepository idempotencyKeyRepository;
    private final UserRepository userRepository;
    private final BalanceRepository balanceRepository;

    public TransactionService(TransactionRepository transactionRepository, IdempotencyKeyRepository idempotencyKeyRepository, UserRepository userRepository, BalanceRepository balanceRepository) {
        this.transactionRepository = transactionRepository;
        this.idempotencyKeyRepository = idempotencyKeyRepository;
        this.userRepository = userRepository;
        this.balanceRepository = balanceRepository;
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public Transaction createTransaction(Long userId, BigDecimal amount, String type, String channel, String idempotencyKey) {
        // Check idempotency
        Optional<IdempotencyKey> existing = idempotencyKeyRepository.findByKeyValue(idempotencyKey);
        if (existing.isPresent()) {
            IdempotencyKey k = existing.get();
            if (k.getTransactionId() != null) {
                return transactionRepository.findById(k.getTransactionId()).orElseThrow(() -> new RuntimeException("Original transaction not found"));
            }
            throw new com.example.banking.exception.IdempotencyInProgressException("Idempotent request in progress");
        }

        // Reserve idempotency key first
        IdempotencyKey key = new IdempotencyKey();
        key.setKeyValue(idempotencyKey);
        key.setUserId(userId);
        idempotencyKeyRepository.save(key);

        // Ensure user exists and is approved
        userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

        // Lock or create balance row (pessimistic)
        Optional<Balance> maybeBal = balanceRepository.findByUserIdForUpdate(userId);
        Balance bal;
        if (maybeBal.isPresent()) {
            bal = maybeBal.get();
        } else {
            // create a balance row and save it, then re-lock
            Balance b = new Balance();
            b.setUserId(userId);
            b.setBalance(java.math.BigDecimal.ZERO);
            try {
                balanceRepository.save(b);
            } catch (Exception ex) {
                // possible race: another tx created it first; ignore and re-fetch
            }
            bal = balanceRepository.findByUserIdForUpdate(userId).orElseThrow(() -> new RuntimeException("Failed to acquire balance row"));
        }

        // Apply balance change (simple: deposit increases, withdraw decreases)
        if ("withdraw".equalsIgnoreCase(type) && bal.getBalance().compareTo(amount) < 0) {
            throw new RuntimeException("Insufficient funds");
        }
        if ("withdraw".equalsIgnoreCase(type)) {
            bal.setBalance(bal.getBalance().subtract(amount));
        } else {
            bal.setBalance(bal.getBalance().add(amount));
        }
        balanceRepository.save(bal);

        // Create transaction record
        Transaction t = new Transaction();
        t.setUserId(userId);
        t.setAmount(amount);
        t.setType(type);
        t.setChannel(channel);
        Transaction saved = transactionRepository.save(t);

        // update idempotency with transaction id and optional result
        key.setTransactionId(saved.getId());
        key.setResultJson("{\"transactionId\": " + saved.getId() + "}");
        idempotencyKeyRepository.save(key);

        return saved;
    }

    public List<Transaction> history(Long userId) {
        return transactionRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }
}
