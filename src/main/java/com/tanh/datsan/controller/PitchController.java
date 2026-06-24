package com.tanh.datsan.controller;

import com.tanh.datsan.entity.Pitch;
import com.tanh.datsan.repository.PitchRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/pitch")
public class PitchController {

    @Autowired
    private PitchRepository pitchRepository;

    @GetMapping("/{id}")
    public String showPitchDetail(@PathVariable Long id, Model model) {
        Pitch pitch = pitchRepository.findById(id).orElse(null);
        if (pitch == null) {
            return "redirect:/";
        }
        model.addAttribute("pitch", pitch);
        return "pitch-detail";
    }
}
