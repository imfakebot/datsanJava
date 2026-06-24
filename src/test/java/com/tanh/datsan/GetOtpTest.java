package com.tanh.datsan;

import com.tanh.datsan.repository.AccountRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class GetOtpTest {

    @Autowired
    private AccountRepository accountRepository;

    @Test
    public void getOtp() {
        accountRepository.findAll().forEach(a -> {
            if (a.getLoginOtp() != null || a.getVerificationCode() != null) {
                System.out.println("======================================");
                System.out.println("ACCOUNT: " + a.getEmail());
                System.out.println("IS VERIFIED: " + a.isVerified());
                System.out.println("VERIFICATION CODE: " + a.getVerificationCode());
                System.out.println("LOGIN OTP: " + a.getLoginOtp());
                System.out.println("======================================");
            }
        });
    }
}
