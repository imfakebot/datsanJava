package com.tanh.datsan.controller;

import com.tanh.datsan.entity.Account;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.tanh.datsan.repository.PitchRepository;
import org.springframework.beans.factory.annotation.Autowired;

@Controller
public class AccountController {

    private final PitchRepository pitchRepository;
    private final com.tanh.datsan.repository.AccountRepository accountRepository;
    private final org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    @Autowired
    public AccountController(PitchRepository pitchRepository, 
                             com.tanh.datsan.repository.AccountRepository accountRepository,
                             org.springframework.security.crypto.password.PasswordEncoder passwordEncoder) {
        this.pitchRepository = pitchRepository;
        this.accountRepository = accountRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/")
    public String showHomePage(Model model, java.security.Principal principal) {
        if (principal != null) {
            Account userAccount = accountRepository.findByUsername(principal.getName()).orElse(null);
            model.addAttribute("account", userAccount != null ? userAccount : new Account());
        } else {
            model.addAttribute("account", new Account());
        }
        model.addAttribute("pitches", pitchRepository.findAll());
        return "index";
    }
    @org.springframework.web.bind.annotation.PostMapping("/profile/update")
    public String updateProfile(@org.springframework.web.bind.annotation.RequestParam String fullName,
                                @org.springframework.web.bind.annotation.RequestParam String phone,
                                java.security.Principal principal,
                                org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttributes) {
        if (principal == null) return "redirect:/login";
        Account userAccount = accountRepository.findByUsername(principal.getName()).orElse(null);
        if (userAccount != null) {
            userAccount.setFullName(fullName);
            userAccount.setPhone(phone);
            accountRepository.save(userAccount);
            redirectAttributes.addFlashAttribute("successMessage", "Cập nhật thông tin thành công!");
        }
        return "redirect:/dashboard";
    }

    @org.springframework.web.bind.annotation.PostMapping("/profile/password")
    public String changePassword(@org.springframework.web.bind.annotation.RequestParam String oldPassword,
                                 @org.springframework.web.bind.annotation.RequestParam String newPassword,
                                 @org.springframework.web.bind.annotation.RequestParam String confirmPassword,
                                 java.security.Principal principal,
                                 org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttributes) {
        if (principal == null) return "redirect:/login";
        Account userAccount = accountRepository.findByUsername(principal.getName()).orElse(null);
        if (userAccount != null) {
            if (!passwordEncoder.matches(oldPassword, userAccount.getPassword())) {
                redirectAttributes.addFlashAttribute("errorMessage", "Mật khẩu cũ không chính xác!");
                return "redirect:/dashboard";
            }
            if (!newPassword.equals(confirmPassword)) {
                redirectAttributes.addFlashAttribute("errorMessage", "Mật khẩu mới không khớp!");
                return "redirect:/dashboard";
            }
            userAccount.setPassword(passwordEncoder.encode(newPassword));
            accountRepository.save(userAccount);
            redirectAttributes.addFlashAttribute("successMessage", "Đổi mật khẩu thành công!");
        }
        return "redirect:/dashboard";
    }

}
