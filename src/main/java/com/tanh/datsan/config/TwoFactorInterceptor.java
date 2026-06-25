package com.tanh.datsan.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Component
public class TwoFactorInterceptor implements HandlerInterceptor {
    // Paths that are allowed without 2FA
    private static final Set<String> EXCLUDED = new HashSet<>(Arrays.asList(
            "/login", "/login-otp", "/register", "/verify", "/css/", "/js/", "/images/", "/static/","/logout", "/access-denied", "/error"
    ));

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String path = request.getRequestURI();

        // allow excluded prefixes
        for (String p : EXCLUDED) {
            if (path.startsWith(request.getContextPath() + p) || path.startsWith(p)) {
                return true;
            }
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
            HttpSession session = request.getSession(false);
            if (session != null) {
                Object pending = session.getAttribute("2fa_pending");
                Object passed = session.getAttribute("2fa_passed");
                // if pending and not passed, redirect to OTP page
                if (Boolean.TRUE.equals(pending) && !Boolean.TRUE.equals(passed)) {
                    response.sendRedirect(request.getContextPath() + "/login-otp");
                    return false;
                }
            }
        }

        return true;
    }
}