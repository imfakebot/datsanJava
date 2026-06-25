package com.tanh.datsan.controller;

import com.tanh.datsan.entity.Booking;
import com.tanh.datsan.entity.Pitch;
import com.tanh.datsan.repository.BookingRepository;
import com.tanh.datsan.repository.PitchRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.tanh.datsan.service.FileStorageService;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;
import java.time.DayOfWeek;
import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private PitchRepository pitchRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private com.tanh.datsan.repository.AccountRepository accountRepository;

    @Autowired
    private com.tanh.datsan.service.BookingService bookingService;

    @Autowired
    private com.tanh.datsan.repository.TimeSlotRepository timeSlotRepository;

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("pitches", pitchRepository.findAll());
        java.util.List<Booking> bookings = bookingRepository.findAll();
        model.addAttribute("bookings", bookings);
        
        double totalRevenue = bookings.stream()
                .filter(b -> b.getStatus() == com.tanh.datsan.constant.BookingStatus.CONFIRMED || b.getStatus() == com.tanh.datsan.constant.BookingStatus.CHECKED_IN)
                .mapToDouble(Booking::getTotalAmount)
                .sum();
        model.addAttribute("totalRevenue", totalRevenue);
        return "admin-dashboard";
    }

    @GetMapping("/customers")
    public String customers(Model model) {
        model.addAttribute("accounts", accountRepository.findAll());
        return "admin-customers";
    }

    @PostMapping("/customer/{id}/toggle-lock")
    public String toggleCustomerLock(@PathVariable Long id) {
        com.tanh.datsan.entity.Account account = accountRepository.findById(id).orElse(null);
        if (account != null && account.getRole() != com.tanh.datsan.constant.Role.ADMIN) {
            account.setLocked(!account.isLocked());
            accountRepository.save(account);
        }
        return "redirect:/admin/customers";
    }



    @PostMapping("/booking/{id}/checkin")
    public String checkInBooking(@PathVariable Long id) {
        Booking booking = bookingRepository.findById(id).orElse(null);
        if (booking != null && booking.getStatus() == com.tanh.datsan.constant.BookingStatus.CONFIRMED) {
            booking.setStatus(com.tanh.datsan.constant.BookingStatus.CHECKED_IN);
            bookingRepository.save(booking);
        }
        return "redirect:/admin/dashboard?checkedin";
    }

    @PostMapping("/booking/offline")
    public String createOfflineBooking(
            @RequestParam("pitchId") Long pitchId,
            @RequestParam("bookingDate") LocalDate bookingDate,
            @RequestParam("startTime") LocalTime startTime,
            @RequestParam("durationMinutes") Integer durationMinutes,
            @RequestParam("totalAmount") Double totalAmount,
            Principal principal) {
        
        Pitch pitch = pitchRepository.findById(pitchId).orElseThrow(() -> new RuntimeException("Sân không tồn tại"));
        com.tanh.datsan.entity.Account account = accountRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new RuntimeException("Tài khoản không tồn tại"));
                
        LocalDateTime startDT = LocalDateTime.of(bookingDate, startTime);
        LocalDateTime endDT = startDT.plusMinutes(durationMinutes);
        
        Booking booking = new Booking();
        booking.setPitch(pitch);
        booking.setAccount(account);
        booking.setBookingDate(bookingDate);
        booking.setStartTime(startDT);
        booking.setEndTime(endDT);
        booking.setDurationMinutes(durationMinutes);
        booking.setTotalAmount(totalAmount);
        
        bookingService.createOfflineBooking(booking);
        
        return "redirect:/admin/dashboard?offline_booked";
    }

    @GetMapping("/pitches/create")
    public String showCreatePitchForm(Model model) {
        model.addAttribute("pitch", new Pitch());
        return "admin-pitch-form";
    }

    @PostMapping("/pitches/create")
    public String createPitch(@ModelAttribute Pitch pitch) {
        pitch.setStatus("Available");
        pitchRepository.save(pitch);
        return "redirect:/admin/dashboard";
    }

    @GetMapping("/pitches/{id}/edit")
    public String showEditPitchForm(@PathVariable Long id, Model model) {
        Pitch pitch = pitchRepository.findById(id).orElse(null);
        if (pitch == null) return "redirect:/admin/dashboard";
        model.addAttribute("pitch", pitch);
        return "admin-pitch-form";
    }

    @PostMapping("/pitches/{id}/edit")
    public String editPitch(@PathVariable Long id, @ModelAttribute Pitch pitchDetails) {
        Pitch pitch = pitchRepository.findById(id).orElse(null);
        if (pitch != null) {
            pitch.setName(pitchDetails.getName());
            pitch.setLocation(pitchDetails.getLocation());
            pitch.setPitchType(pitchDetails.getPitchType());
            pitch.setImageUrl(pitchDetails.getImageUrl());
            pitch.setStatus(pitchDetails.getStatus());
            pitchRepository.save(pitch);
        }
        return "redirect:/admin/dashboard";
    }

    @PostMapping("/pitches/{id}/delete")
    public String deletePitch(@PathVariable Long id) {
        pitchRepository.deleteById(id);
        return "redirect:/admin/dashboard";
    }

    @GetMapping("/pitches/{id}/timeslots")
    public String manageTimeSlots(@PathVariable Long id, Model model) {
        Pitch pitch = pitchRepository.findById(id).orElse(null);
        if (pitch == null) return "redirect:/admin/dashboard";
        model.addAttribute("pitch", pitch);
        model.addAttribute("timeSlots", timeSlotRepository.findByPitchIdOrderByDayOfWeekAscStartTimeAsc(id));
        model.addAttribute("newTimeSlot", new com.tanh.datsan.entity.TimeSlot());
        return "admin-timeslots";
    }

    @PostMapping("/pitches/{id}/timeslots/add")
    public String addTimeSlot(@PathVariable Long id, @ModelAttribute com.tanh.datsan.entity.TimeSlot timeSlot) {
        Pitch pitch = pitchRepository.findById(id).orElse(null);
        if (pitch != null) {
            timeSlot.setPitch(pitch);
            timeSlotRepository.save(timeSlot);
        }
        return "redirect:/admin/pitches/" + id + "/timeslots";
    }

    @PostMapping("/pitches/{pitchId}/timeslots/{slotId}/delete")
    public String deleteTimeSlot(@PathVariable Long pitchId, @PathVariable Long slotId) {
        timeSlotRepository.deleteById(slotId);
        return "redirect:/admin/pitches/" + pitchId + "/timeslots";
    }
}
