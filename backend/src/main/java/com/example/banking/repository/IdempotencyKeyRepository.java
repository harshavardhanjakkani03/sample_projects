package com.example.banking.repository;

import com.example.banking.model.IdempotencyKey;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.OffsetDateTime;
import java.util.Optional;

public interface IdempotencyKeyRepository extends JpaRepository<IdempotencyKey, Long> {
    Optional<IdempotencyKey> findByKeyValue(String keyValue);
    Page<IdempotencyKey> findByCreatedAtAfter(OffsetDateTime cutoff, Pageable pageable);
    Page<IdempotencyKey> findAll(Pageable pageable);
}
