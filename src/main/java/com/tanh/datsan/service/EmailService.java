package com.tanh.datsan.service;

import com.tanh.datsan.entity.Account;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    @Autowired
    private JavaMailSender mailSender;

    /**
     * Send verification email using configured SMTP. Falls back to logging on error.
     */
    public void sendVerificationEmail(Account account) {
        if (account == null || account.getEmail() == null) return;

        String to = account.getEmail();
        String subject = "[Datsan] Mã xác thực tài khoản";
        String body = String.format("Xin chào %s,\n\nMã xác thực của bạn là: %s\nHoặc nhấn link: http://localhost:8080/verify?code=%s\n\nMã hợp lệ trong 15 phút.",
                account.getFullName() == null ? account.getUsername() : account.getFullName(),
                account.getVerificationCode(),
                account.getVerificationCode());

        try {
            SimpleMailMessage msg = new SimpleMailMessage();
            msg.setTo(to);
            msg.setFrom(System.getProperty("app.mail.from", "no-reply@datsan.local"));
            msg.setSubject(subject);
            msg.setText(body);
            mailSender.send(msg);
            logger.info("[EmailService] Verification email sent to {}", to);
        } catch (Exception e) {
            logger.error("[EmailService] Failed to send verification email, falling back to log", e);
            logger.info("[FallbackEmail] To: {}", to);
            logger.info("[FallbackEmail] Subject: {}", subject);
            logger.info("[FallbackEmail] Body:\n{}", body);
        }
    }

    public void sendLoginOtpEmail(Account account) {
        if (account == null || account.getEmail() == null) return;

        String to = account.getEmail();
        String subject = "[Datsan] Mã OTP đăng nhập";
        String body = String.format("Xin chào %s,\n\nMã OTP đăng nhập của bạn là: %s\nMã hợp lệ trong 10 phút.",
                account.getFullName() == null ? account.getUsername() : account.getFullName(),
                account.getLoginOtp());

        try {
            SimpleMailMessage msg = new SimpleMailMessage();
            msg.setTo(to);
            msg.setFrom(System.getProperty("app.mail.from", "no-reply@datsan.local"));
            msg.setSubject(subject);
            msg.setText(body);
            mailSender.send(msg);
            logger.info("[EmailService] Login OTP email sent to {}", to);
        } catch (Exception e) {
            logger.error("[EmailService] Failed to send login OTP email, falling back to log", e);
            logger.info("[FallbackEmail] To: {}", to);
            logger.info("[FallbackEmail] Subject: {}", subject);
            logger.info("[FallbackEmail] Body:\n{}", body);
        }
    }
}