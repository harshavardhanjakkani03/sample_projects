package com.example.banking.schedule;

import com.example.banking.repository.IdempotencyKeyRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.time.OffsetDateTime;

@Component
public class IdempotencyCleanup {

    private final IdempotencyKeyRepository repo;

    public IdempotencyCleanup(IdempotencyKeyRepository repo) {
        this.repo = repo;
    }

    // run daily
    @Scheduled(cron = "0 0 2 * * ?")
    public void cleanupOld() {
        OffsetDateTime cutoff = OffsetDateTime.now().minusDays(30);
        // repository doesn't have explicit method; delete by scanning
        repo.findAll().stream()
                .filter(k -> k.getCreatedAt().isBefore(cutoff))
                .forEach(k -> repo.delete(k));
    }
}
