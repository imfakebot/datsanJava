package com.tanh.datsan.config;

import com.tanh.datsan.constant.Role;
import com.tanh.datsan.entity.Account;
import com.tanh.datsan.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        Account admin = accountRepository.findByUsername("admin").orElse(new Account());
        admin.setUsername("admin");
        admin.setPassword(passwordEncoder.encode("123456"));
        admin.setFullName("Super Admin");
        admin.setEmail("admin@datsan.com");
        admin.setPhone("0123456789");
        admin.setRole(Role.ADMIN);
        admin.setVerified(true);
        admin.setLocked(false);
        
        accountRepository.save(admin);
        System.out.println("====== THÔNG BÁO ======");
        System.out.println("Tài khoản admin đã được tạo hoặc cập nhật mật khẩu!");
        System.out.println("Username: admin");
        System.out.println("Password: 123456");
        System.out.println("=======================");
    }
}
