package com.tanh.datsan.controller;

import com.tanh.datsan.constant.BookingStatus;
import com.tanh.datsan.constant.PaymentMethod;
import com.tanh.datsan.entity.Account;
import com.tanh.datsan.entity.Booking;
import com.tanh.datsan.entity.Payment;
import com.tanh.datsan.entity.Pitch;
import com.tanh.datsan.repository.PaymentRepository;
import com.tanh.datsan.entity.TimeSlot;
import com.tanh.datsan.service.AccountService;
import com.tanh.datsan.service.BookingService;
import com.tanh.datsan.service.PaymentService;
import com.tanh.datsan.service.PitchService;
import com.tanh.datsan.service.TimeSlotService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;
import java.time.DayOfWeek;
import java.util.List;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Controller
@RequestMapping("/booking")
public class BookingController {

    @Autowired
    private PitchService pitchService;

    @Autowired
    private AccountService accountService;

    @Autowired
    private BookingService bookingService;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private TimeSlotService timeSlotService;

    @PostMapping("/init")
    public String initBooking(
            @RequestParam("pitchId") Long pitchId,
            @RequestParam("bookingDate") String bookingDateStr,
            @RequestParam("startTime") String startTimeStr,
            @RequestParam("duration") Double duration,
            Authentication authentication,
            HttpServletRequest request
    ) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }

        String username = ((UserDetails) authentication.getPrincipal()).getUsername();
        Account account = accountService.findByUsername(username).orElse(null);
        if (account == null) {
            return "redirect:/login";
        }

        Pitch pitch = pitchService.findById(pitchId).orElse(null);
        if (pitch == null) {
            return "redirect:/";
        }

        if ("Maintenance".equalsIgnoreCase(pitch.getStatus())) {
            return "redirect:/pitch/" + pitchId + "?error=maintenance";
        }

        Booking booking = bookingService.processOnlineBooking(account, pitch, bookingDateStr, startTimeStr, duration);
        
        String ipAddr = request.getRemoteAddr();
        String vnpayUrl = paymentService.createPaymentAndGetVnPayUrl(booking, ipAddr);

        return "redirect:" + vnpayUrl;
    }

    @PostMapping("/{id}/cancel")
    public String cancelBooking(@PathVariable Long id, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }
        
        String username = ((UserDetails) authentication.getPrincipal()).getUsername();
        Account account = accountService.findByUsername(username).orElse(null);
        if (account == null) {
            return "redirect:/login";
        }
        
        Booking booking = bookingService.findById(id).orElse(null);
        if (booking != null && booking.getAccount().getId().equals(account.getId())) {
            if (booking.getStatus() == BookingStatus.PENDING_PAYMENT) {
                booking.setStatus(BookingStatus.CANCELLED);
                bookingService.save(booking);
            }
        }
        
        return "redirect:/dashboard";
    }
}
