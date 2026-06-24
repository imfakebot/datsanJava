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
    public String showHomePage(Model model) {
        model.addAttribute("account", new Account());
        model.addAttribute("pitches", pitchRepository.findAll());
        return "index";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("account", new Account());
        return "register";
    }

    @org.springframework.web.bind.annotation.PostMapping("/register")
    public String processRegister(Account account) {
        account.setPassword(passwordEncoder.encode(account.getPassword()));
        account.setRole(com.tanh.datsan.constant.Role.CUSTOMER);
        accountRepository.save(account);
        return "redirect:/login?registered";
    }
}
