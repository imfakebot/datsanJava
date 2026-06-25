package com.tanh.datsan.controller;

import com.tanh.datsan.entity.Account;
import com.tanh.datsan.entity.Pitch;
import com.tanh.datsan.service.AccountService;
import com.tanh.datsan.service.PitchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;

@Controller
@RequestMapping("/pitch")
public class PitchController {

    @Autowired
    private PitchService pitchService;

    @Autowired
    private AccountService accountService;

    @GetMapping("/{id}")
    public String showPitchDetail(@PathVariable Long id, Model model, Principal principal) {
        if (principal != null) {
            Account userAccount = accountService.findByUsername(principal.getName()).orElse(null);
            model.addAttribute("account", userAccount != null ? userAccount : new Account());
        } else {
            model.addAttribute("account", new Account());
        }

        Pitch pitch = pitchService.findById(id).orElse(null);
        if (pitch == null) {
            return "redirect:/";
        }
        model.addAttribute("pitch", pitch);
        return "pitch-detail";
    }
}
