package com.tanh.datsan.service;

import com.tanh.datsan.entity.Account;
import java.util.Optional;
import java.util.List;

public interface AccountService {
    Optional<Account> findByUsername(String username);
    List<Account> findAll();
    Account save(Account account);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    Optional<Account> findById(Long id);
}
