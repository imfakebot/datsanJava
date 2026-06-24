package com.tanh.datsan.repository;

import com.tanh.datsan.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    Optional<Account> findByUsername(String username);
    Optional<Account> findByEmail(String email);
    Optional<Account> findByVerificationCode(String verificationCode);
    Optional<Account> findByLoginOtp(String loginOtp);

    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}
