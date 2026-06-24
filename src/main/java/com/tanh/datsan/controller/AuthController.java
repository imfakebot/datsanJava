package com.tanh.datsan.controller;

import com.tanh.datsan.dto.RegisterRequest;
import com.tanh.datsan.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    @GetMapping("/")
    public String index() {
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String loginPage(@RequestParam(required = false) String error, Model model) {
        if (error != null) {
            model.addAttribute("error", "Tên đăng nhập hoặc mật khẩu không chính xác!");
        }
        return "login";
    }

    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("registerRequest", new RegisterRequest());
        return "register";
    }

    @PostMapping("/register")
    public String register(@ModelAttribute RegisterRequest request, Model model) {
        try {
            if (!request.getPassword().equals(request.getConfirmPassword())) {
                model.addAttribute("error", "Mật khẩu không khớp!");
                return "register";
            }

            authService.register(request);
            // after register, user must verify email via OTP. Show verify page instructing to check email
            return "redirect:/verify?sent";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            return "register";
        }
    }

    @GetMapping("/verify")
    public String verifyPage(@RequestParam(required = false) String code,
                             @RequestParam(required = false) String sent,
                             Model model) {
        if (code != null) {
            try {
                authService.verifyAccount(code);
                return "redirect:/login?verified";
            } catch (RuntimeException e) {
                model.addAttribute("error", e.getMessage());
                model.addAttribute("title", "Xác thực Email");
                model.addAttribute("subtitle", "Nhập mã xác thực đã gửi tới email để hoàn tất đăng ký");
                model.addAttribute("action", "/verify");
                model.addAttribute("labelText", "Mã xác thực");
                return "otp-verify";
            }
        }

        if (sent != null) {
            model.addAttribute("info", "✓ Mã xác thực đã được gửi tới email của bạn. Vui lòng kiểm tra và nhập mã bên dưới.");
        }
        model.addAttribute("title", "Xác thực Email");
        model.addAttribute("subtitle", "Nhập mã xác thực đã gửi tới email để hoàn tất đăng ký");
        model.addAttribute("action", "/verify");
        model.addAttribute("labelText", "Mã xác thực");
        return "otp-verify";
    }

    @PostMapping("/verify")
    public String verifySubmit(@RequestParam String code, Model model) {
        try {
            authService.verifyAccount(code);
            return "redirect:/login?verified";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("title", "Xác thực Email");
            model.addAttribute("subtitle", "Nhập mã xác thực đã gửi tới email để hoàn tất đăng ký");
            model.addAttribute("action", "/verify");
            model.addAttribute("labelText", "Mã xác thực");
            return "otp-verify";
        }
    }

    @GetMapping("/login-otp")
    public String loginOtpPage(Model model, HttpSession session) {
        Object user = session.getAttribute("2fa_user");
        if (user == null) {
            return "redirect:/login";
        }
        model.addAttribute("info", "✓ Mã OTP đã được gửi tới email của bạn.");
        model.addAttribute("title", "Xác thực Đăng nhập");
        model.addAttribute("subtitle", "Nhập mã OTP đã gửi tới email để hoàn tất đăng nhập");
        model.addAttribute("action", "/login-otp");
        model.addAttribute("labelText", "Mã OTP");
        return "otp-verify";
    }

    @PostMapping("/login-otp")
    public String loginOtpSubmit(@RequestParam String code, HttpSession session, Model model) {
        Object user = session.getAttribute("2fa_user");
        if (user == null) {
            return "redirect:/login";
        }
        String username = String.valueOf(user);
        try {
            authService.verifyLoginOtp(username, code);
            session.setAttribute("2fa_passed", Boolean.TRUE);
            session.removeAttribute("2fa_pending");
            session.removeAttribute("2fa_user");
            return "redirect:/dashboard";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("title", "Xác thực Đăng nhập");
            model.addAttribute("subtitle", "Nhập mã OTP đã gửi tới email để hoàn tất đăng nhập");
            model.addAttribute("action", "/login-otp");
            model.addAttribute("labelText", "Mã OTP");
            return "otp-verify";
        }
    }

    @GetMapping("/dashboard")
    public String dashboard() {
        return "dashboard";
    }
}
