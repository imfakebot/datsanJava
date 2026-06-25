package com.tanh.datsan.controller;

import com.tanh.datsan.entity.Booking;
import com.tanh.datsan.entity.Pitch;
import com.tanh.datsan.entity.Account;
import com.tanh.datsan.entity.TimeSlot;
import com.tanh.datsan.constant.BookingStatus;
import com.tanh.datsan.constant.Role;
import com.tanh.datsan.service.AccountService;
import com.tanh.datsan.service.BookingService;
import com.tanh.datsan.service.FileStorageService;
import com.tanh.datsan.service.PitchService;
import com.tanh.datsan.service.TimeSlotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
    private PitchService pitchService;

    @Autowired
    private BookingService bookingService;

    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private AccountService accountService;

    @Autowired
    private TimeSlotService timeSlotService;

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("pitches", pitchService.findAll());
        List<Booking> bookings = bookingService.findAll();
        model.addAttribute("bookings", bookings);
        
        double totalRevenue = bookings.stream()
                .filter(b -> b.getStatus() == BookingStatus.CONFIRMED || b.getStatus() == BookingStatus.CHECKED_IN)
                .mapToDouble(Booking::getTotalAmount)
                .sum();
        model.addAttribute("totalRevenue", totalRevenue);
        return "admin-dashboard";
    }

    @GetMapping("/customers")
    public String customers(Model model) {
        model.addAttribute("accounts", accountService.findAll());
        return "admin-customers";
    }

    @PostMapping("/customer/{id}/toggle-lock")
    public String toggleCustomerLock(@PathVariable Long id) {
        Account account = accountService.findById(id).orElse(null);
        if (account != null && account.getRole() != Role.ADMIN) {
            account.setLocked(!account.isLocked());
            accountService.save(account);
        }
        return "redirect:/admin/customers";
    }



    @PostMapping("/booking/{id}/checkin")
    public String checkInBooking(@PathVariable Long id) {
        Booking booking = bookingService.findById(id).orElse(null);
        if (booking != null && booking.getStatus() == BookingStatus.CONFIRMED) {
            booking.setStatus(BookingStatus.CHECKED_IN);
            bookingService.save(booking);
        }
        return "redirect:/admin/dashboard?checkedin";
    }

    @PostMapping("/booking/offline")
    public String createOfflineBooking(
            @RequestParam("pitchId") Long pitchId,
            @RequestParam("bookingDate") LocalDate bookingDate,
            @RequestParam("startTime") LocalTime startTime,
            @RequestParam("durationMinutes") Integer durationMinutes,
            Principal principal) {
        
        Pitch pitch = pitchService.findById(pitchId).orElseThrow(() -> new RuntimeException("Sân không tồn tại"));
        Account account = accountService.findByUsername(principal.getName())
                .orElseThrow(() -> new RuntimeException("Tài khoản không tồn tại"));
                
        LocalDateTime startDT = LocalDateTime.of(bookingDate, startTime);
        LocalDateTime endDT = startDT.plusMinutes(durationMinutes);
        
        // Calculate price dynamically based on TimeSlot configuration, minute by minute
        double totalAmount = timeSlotService.calculatePrice(pitchId, startDT, durationMinutes);
        
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

    @GetMapping("/booking/calculate")
    @ResponseBody
    public double calculateBookingAmount(
            @RequestParam("pitchId") Long pitchId,
            @RequestParam("bookingDate") LocalDate bookingDate,
            @RequestParam("startTime") LocalTime startTime,
            @RequestParam("durationMinutes") Integer durationMinutes) {
        
        LocalDateTime startDT = LocalDateTime.of(bookingDate, startTime);
        return timeSlotService.calculatePrice(pitchId, startDT, durationMinutes);
    }

    @GetMapping("/pitches/create")
    public String showCreatePitchForm(Model model) {
        model.addAttribute("pitch", new Pitch());
        return "admin-pitch-form";
    }

    @PostMapping("/pitches/create")
    public String createPitch(@ModelAttribute Pitch pitch, 
                              @RequestParam(value = "imageFiles", required = false) MultipartFile[] imageFiles) {
        processUploadedImages(pitch, imageFiles, false);
        pitch.setStatus("Available");
        pitchService.save(pitch);
        return "redirect:/admin/dashboard";
    }

    @GetMapping("/pitches/{id}/edit")
    public String showEditPitchForm(@PathVariable Long id, Model model) {
        Pitch pitch = pitchService.findById(id).orElse(null);
        if (pitch == null) return "redirect:/admin/dashboard";
        
        model.addAttribute("pitch", pitch);
        return "admin-pitch-form";
    }

    @PostMapping("/pitches/{id}/edit")
    public String editPitch(@PathVariable Long id, 
                            @ModelAttribute Pitch pitchDetails,
                            @RequestParam(value = "imageFiles", required = false) MultipartFile[] imageFiles,
                            @RequestParam(value = "deleteAllImages", defaultValue = "false") boolean deleteAllImages) {
        Pitch pitch = pitchService.findById(id).orElse(null);
        if (pitch != null) {
            pitch.setName(pitchDetails.getName());
            pitch.setLocation(pitchDetails.getLocation());
            pitch.setLatitude(pitchDetails.getLatitude());
            pitch.setLongitude(pitchDetails.getLongitude());
            pitch.setPitchType(pitchDetails.getPitchType());
            pitch.setStatus(pitchDetails.getStatus());
            pitch.setDescription(pitchDetails.getDescription());
            pitch.setAmenities(pitchDetails.getAmenities());
            
            processUploadedImages(pitch, imageFiles, deleteAllImages);
            
            pitchService.save(pitch);
        }
        return "redirect:/admin/dashboard";
    }

    private void processUploadedImages(Pitch pitch, MultipartFile[] imageFiles, boolean deleteAllImages) {
        if (pitch.getImages() == null) {
            pitch.setImages(new java.util.ArrayList<>());
        }
        
        if (deleteAllImages) {
            pitch.getImages().clear();
        }
        
        int order = pitch.getImages().size() + 1;
        
        if (imageFiles != null) {
            for (MultipartFile file : imageFiles) {
                if (!file.isEmpty()) {
                    String fileUrl = fileStorageService.storeFile(file);
                    pitch.getImages().add(com.tanh.datsan.entity.PitchImage.builder()
                        .imageUrl(fileUrl)
                        .displayOrder(order++)
                        .pitch(pitch)
                        .build());
                }
            }
        }
    }

    @PostMapping("/pitches/{id}/delete")
    public String deletePitch(@PathVariable Long id) {
        pitchService.deleteById(id);
        return "redirect:/admin/dashboard";
    }

    @GetMapping("/pitches/{id}/timeslots")
    public String manageTimeSlots(@PathVariable Long id, Model model) {
        Pitch pitch = pitchService.findById(id).orElse(null);
        if (pitch == null) return "redirect:/admin/dashboard";
        model.addAttribute("pitch", pitch);
        model.addAttribute("timeSlots", timeSlotService.findByPitchIdOrderByDayOfWeekAscStartTimeAsc(id));
        model.addAttribute("newTimeSlot", new TimeSlot());
        return "admin-timeslots";
    }

    @PostMapping("/pitches/{id}/timeslots/add")
    public String addTimeSlot(@PathVariable Long id, @ModelAttribute TimeSlot timeSlot) {
        Pitch pitch = pitchService.findById(id).orElse(null);
        if (pitch != null) {
            timeSlot.setPitch(pitch);
            timeSlotService.save(timeSlot);
        }
        return "redirect:/admin/pitches/" + id + "/timeslots";
    }

    @PostMapping("/pitches/{pitchId}/timeslots/{slotId}/delete")
    public String deleteTimeSlot(@PathVariable Long pitchId, @PathVariable Long slotId) {
        timeSlotService.deleteById(slotId);
        return "redirect:/admin/pitches/" + pitchId + "/timeslots";
    }
}
