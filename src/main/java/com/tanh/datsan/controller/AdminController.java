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

    @PostMapping("/pitch/add")
    public String addPitch(
            @RequestParam("name") String name,
            @RequestParam("location") String location,
            @RequestParam("pitchType") String pitchType,
            @RequestParam("file") MultipartFile file) {
        
        Pitch pitch = new Pitch();
        pitch.setName(name);
        pitch.setLocation(location);
        pitch.setPitchType(pitchType);
        pitch.setStatus("Available");
        // Coordinates can be set to default or parsed
        pitch.setLatitude(0.0);
        pitch.setLongitude(0.0);

        if (!file.isEmpty()) {
            String fileName = fileStorageService.storeFile(file);
            pitch.setImageUrl("/uploads/" + fileName);
        }

        pitchRepository.save(pitch);
        return "redirect:/admin/dashboard?added";
    }

    @GetMapping("/pitch/{id}")
    public String viewPitchAdmin(@PathVariable Long id, Model model) {
        Pitch pitch = pitchRepository.findById(id).orElse(null);
        if (pitch == null) return "redirect:/admin/dashboard";

        List<Booking> activeBookings = bookingRepository.findAll().stream()
                .filter(b -> b.getPitch().getId().equals(id)
                        && b.getStatus() == com.tanh.datsan.constant.BookingStatus.CONFIRMED
                        && b.getStartTime().isBefore(LocalDateTime.now())
                        && b.getEndTime().isAfter(LocalDateTime.now()))
                .toList();

        boolean isCurrentlyPlayed = !activeBookings.isEmpty();
        
        List<Booking> pitchBookings = bookingRepository.findAll().stream()
                .filter(b -> b.getPitch().getId().equals(id))
                .toList();
                
        List<com.tanh.datsan.entity.TimeSlot> timeSlots = timeSlotRepository.findByPitchId(id);

        model.addAttribute("pitch", pitch);
        model.addAttribute("isCurrentlyPlayed", isCurrentlyPlayed);
        model.addAttribute("activeBooking", isCurrentlyPlayed ? activeBookings.get(0) : null);
        model.addAttribute("pitchBookings", pitchBookings);
        model.addAttribute("timeSlots", timeSlots);

        return "admin-pitch-detail";
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

    @PostMapping("/pitch/{id}/timeslot/add")
    public String addTimeSlot(
            @PathVariable Long id,
            @RequestParam("startTime") LocalTime startTime,
            @RequestParam("endTime") LocalTime endTime,
            @RequestParam("dayOfWeek") String dayOfWeekStr,
            @RequestParam("price") Double price,
            @RequestParam(value = "isGoldenHour", defaultValue = "false") boolean isGoldenHour) {
        
        Pitch pitch = pitchRepository.findById(id).orElseThrow();
        com.tanh.datsan.entity.TimeSlot slot = new com.tanh.datsan.entity.TimeSlot();
        slot.setPitch(pitch);
        slot.setStartTime(startTime);
        slot.setEndTime(endTime);
        slot.setDayOfWeek(DayOfWeek.valueOf(dayOfWeekStr));
        slot.setPrice(price);
        slot.setGoldenHour(isGoldenHour);
        
        timeSlotRepository.save(slot);
        return "redirect:/admin/pitch/" + id + "?added_slot";
    }

    @PostMapping("/pitch/{pitchId}/timeslot/{slotId}/delete")
    public String deleteTimeSlot(@PathVariable Long pitchId, @PathVariable Long slotId) {
        timeSlotRepository.deleteById(slotId);
        return "redirect:/admin/pitch/" + pitchId + "?deleted_slot";
    }
}
