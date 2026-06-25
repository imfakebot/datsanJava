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
        model.addAttribute("bookings", bookingRepository.findAll());
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


}
