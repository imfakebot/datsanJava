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

        LocalDate bookingDate = LocalDate.parse(bookingDateStr, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        LocalTime time = LocalTime.parse(startTimeStr, DateTimeFormatter.ofPattern("HH:mm"));
        LocalDateTime startTime = LocalDateTime.of(bookingDate, time);
        LocalDateTime endTime = startTime.plusMinutes((long) (duration * 60));

        // Calculate price dynamically based on TimeSlot configuration, minute by minute
        double totalAmount = timeSlotService.calculatePrice(pitchId, startTime, (int) (duration * 60));

        Booking booking = new Booking();
        booking.setBookingCode(UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        booking.setAccount(account);
        booking.setPitch(pitch);
        booking.setBookingDate(bookingDate);
        booking.setStartTime(startTime);
        booking.setEndTime(endTime);
        booking.setDurationMinutes((int) (duration * 60));
        booking.setTotalAmount(totalAmount);
        booking.setStatus(BookingStatus.PENDING_PAYMENT);

        booking = bookingService.save(booking);

        Payment payment = new Payment();
        payment.setBooking(booking);
        payment.setAmount(totalAmount);
        payment.setMethod(PaymentMethod.VNPAY);
        payment.setStatus(com.tanh.datsan.constant.PaymentStatus.PENDING);
        paymentService.save(payment);

        String ipAddr = request.getRemoteAddr();
        String vnpayUrl = paymentService.createVnPayUrl(totalAmount, booking.getId().toString(), ipAddr);

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
