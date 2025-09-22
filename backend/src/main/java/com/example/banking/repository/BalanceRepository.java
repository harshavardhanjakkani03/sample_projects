package com.example.banking.repository;

import com.example.banking.model.Balance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import jakarta.persistence.LockModeType;
import java.util.Optional;

public interface BalanceRepository extends JpaRepository<Balance, Long> {

	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("select b from Balance b where b.userId = :userId")
	Optional<Balance> findByUserIdForUpdate(Long userId);

}
