package com.tanh.datsan.service;

import com.tanh.datsan.dto.RegisterRequest;
import com.tanh.datsan.entity.Account;
import com.tanh.datsan.constant.Role;
import com.tanh.datsan.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    public Account register(RegisterRequest request) {
        if (accountRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Tên đăng nhập đã tồn tại!");
        }
        if (accountRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email đã được đăng ký!");
        }

        Account account = Account.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .email(request.getEmail())
                .fullName(request.getFullName())
                .phone(request.getPhone())
                .role(Role.CUSTOMER)
                .isVerified(false)
                .build();

        // generate OTP code and expiry
        String code = generateOtpCode();
        account.setVerificationCode(code);
        account.setVerificationCodeExpiry(LocalDateTime.now().plusMinutes(15));

        Account saved = accountRepository.save(account);

        // send (fake) email with OTP - logs to console by default
        emailService.sendVerificationEmail(saved);

        return saved;
    }

    private String generateOtpCode() {
        Random rnd = new Random();
        int number = 100000 + rnd.nextInt(900000);
        return String.valueOf(number);
    }

    public boolean verifyAccount(String code) {
        Account account = accountRepository.findByVerificationCode(code)
                .orElseThrow(() -> new RuntimeException("Mã xác thực không hợp lệ."));

        if (account.getVerificationCodeExpiry() == null || account.getVerificationCodeExpiry().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Mã xác thực đã hết hạn.");
        }

        account.setVerified(true);
        account.setVerificationCode(null);
        account.setVerificationCodeExpiry(null);
        accountRepository.save(account);
        return true;
    }

    // Generate OTP for login (2FA) and send via email
    public void generateLoginOtpForUser(String username) {
        Account account = accountRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Tài khoản không tồn tại!"));

        String code = generateOtpCode();
        account.setLoginOtp(code);
        account.setLoginOtpExpiry(LocalDateTime.now().plusMinutes(10));
        accountRepository.save(account);

        // send login OTP email (real SMTP configured)
        emailService.sendLoginOtpEmail(account);
    }

    public boolean verifyLoginOtp(String username, String code) {
        Account account = accountRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Tài khoản không tồn tại!"));

        if (account.getLoginOtp() == null || !account.getLoginOtp().equals(code)) {
            throw new RuntimeException("Mã OTP không hợp lệ.");
        }

        if (account.getLoginOtpExpiry() == null || account.getLoginOtpExpiry().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Mã OTP đã hết hạn.");
        }

        account.setLoginOtp(null);
        account.setLoginOtpExpiry(null);
        accountRepository.save(account);
        return true;
    }

    public Account findByUsername(String username) {
        return accountRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Tài khoản không tồn tại!"));
    }

    public Account findById(Long id) {
        return accountRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tài khoản không tồn tại!"));
    }
}
