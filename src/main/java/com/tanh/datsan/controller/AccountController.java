package com.tanh.datsan.controller;

import com.tanh.datsan.entity.Account;
import com.tanh.datsan.entity.Pitch;
import com.tanh.datsan.repository.AccountRepository;
import com.tanh.datsan.service.AccountService;
import com.tanh.datsan.service.PitchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.List;

@Controller
public class AccountController {

    private final PitchService pitchService;
    private final AccountService accountService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AccountController(PitchService pitchService, 
                             AccountService accountService,
                             PasswordEncoder passwordEncoder) {
        this.pitchService = pitchService;
        this.accountService = accountService;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/")
    public String showHomePage(@RequestParam(required = false) String search,
                               @RequestParam(required = false) String filterLocation,
                               @RequestParam(required = false) Double userLat,
                               @RequestParam(required = false) Double userLng,
                               Model model, Principal principal) {
        if (principal != null) {
            Account userAccount = accountService.findByUsername(principal.getName()).orElse(null);
            model.addAttribute("account", userAccount != null ? userAccount : new Account());
        } else {
            model.addAttribute("account", new Account());
        }
        
        List<Pitch> pitches = pitchService.findHomePitches(search, filterLocation, userLat, userLng);
        model.addAttribute("locations", pitchService.findDistinctLocations());
        
        if (filterLocation != null && !filterLocation.trim().isEmpty()) {
            model.addAttribute("selectedLocation", filterLocation.trim());
        }
        if (search != null && !search.trim().isEmpty()) {
            model.addAttribute("searchKeyword", search.trim());
        }

        model.addAttribute("pitches", pitches);
        return "index";
    }

    @PostMapping("/profile/update")
    public String updateProfile(@RequestParam String fullName,
                                @RequestParam String phone,
                                Principal principal,
                                RedirectAttributes redirectAttributes) {
        if (principal == null) return "redirect:/login";
        Account userAccount = accountService.findByUsername(principal.getName()).orElse(null);
        if (userAccount != null) {
            userAccount.setFullName(fullName);
            userAccount.setPhone(phone);
            accountService.save(userAccount);
            redirectAttributes.addFlashAttribute("successMessage", "Cập nhật thông tin thành công!");
        }
        return "redirect:/dashboard";
    }

    @PostMapping("/profile/password")
    public String changePassword(@RequestParam String oldPassword,
                                 @RequestParam String newPassword,
                                 @RequestParam String confirmPassword,
                                 Principal principal,
                                 RedirectAttributes redirectAttributes) {
        if (principal == null) return "redirect:/login";
        Account userAccount = accountService.findByUsername(principal.getName()).orElse(null);
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
            accountService.save(userAccount);
            redirectAttributes.addFlashAttribute("successMessage", "Đổi mật khẩu thành công!");
        }
        return "redirect:/dashboard";
    }

}
