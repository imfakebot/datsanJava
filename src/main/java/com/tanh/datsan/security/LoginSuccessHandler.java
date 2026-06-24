package com.tanh.datsan.security;

import com.tanh.datsan.service.AuthService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class LoginSuccessHandler implements AuthenticationSuccessHandler {
    private final AuthService authService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        String username = authentication.getName();

        // Generate login OTP and send email
        authService.generateLoginOtpForUser(username);

        // Mark session as awaiting 2FA and store username
        HttpSession session = request.getSession(true);
        session.setAttribute("2fa_pending", Boolean.TRUE);
        session.setAttribute("2fa_user", username);
        
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        session.setAttribute("is_admin", isAdmin);

        // Redirect to OTP entry page
        response.sendRedirect(request.getContextPath() + "/login-otp");
    }
}