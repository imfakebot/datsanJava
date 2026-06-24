package com.tanh.datsan.controller;

import com.tanh.datsan.constant.BookingStatus;
import com.tanh.datsan.entity.Booking;
import com.tanh.datsan.entity.Payment;
import com.tanh.datsan.repository.BookingRepository;
import com.tanh.datsan.repository.PaymentRepository;
import com.tanh.datsan.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.Map;

@Controller
@RequestMapping("/api/payment")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @GetMapping("/vnpay-return")
    public String vnpayReturn(@RequestParam Map<String, String> params, Model model) {
        boolean isValid = paymentService.verifyReturnUrl(params);
        if (!isValid) {
            model.addAttribute("message", "Invalid VNPAY signature.");
            return "payment-failed";
        }

        String orderId = params.get("vnp_TxnRef");
        String responseCode = params.get("vnp_ResponseCode");
        String transactionNo = params.get("vnp_TransactionNo");
        String bankCode = params.get("vnp_BankCode");

        try {
            Long bookingId = Long.parseLong(orderId);
            Booking booking = bookingRepository.findById(bookingId).orElse(null);
            if (booking == null) {
                model.addAttribute("message", "Booking not found.");
                return "payment-failed";
            }

            Payment payment = paymentRepository.findByBookingId(bookingId).orElse(null);
            if (payment == null) {
                model.addAttribute("message", "Payment record not found.");
                return "payment-failed";
            }

            if ("00".equals(responseCode)) {
                if (payment.getStatus() == com.tanh.datsan.constant.PaymentStatus.PENDING) {
                    payment.setStatus(com.tanh.datsan.constant.PaymentStatus.SUCCESS);
                    payment.setTransactionNo(transactionNo);
                    payment.setBankCode(bankCode);
                    payment.setVnpResponseCode(responseCode);
                    payment.setPaymentTime(LocalDateTime.now());
                    paymentRepository.save(payment);

                    booking.setStatus(BookingStatus.CONFIRMED);
                    bookingRepository.save(booking);
                }
                model.addAttribute("booking", booking);
                return "payment-success";
            } else {
                if (payment.getStatus() == com.tanh.datsan.constant.PaymentStatus.PENDING) {
                    payment.setStatus(com.tanh.datsan.constant.PaymentStatus.FAILED);
                    payment.setVnpResponseCode(responseCode);
                    paymentRepository.save(payment);

                    booking.setStatus(BookingStatus.CANCELLED);
                    bookingRepository.save(booking);
                }
                model.addAttribute("message", "Payment failed or was cancelled.");
                return "payment-failed";
            }
        } catch (Exception e) {
            model.addAttribute("message", "System error: " + e.getMessage());
            return "payment-failed";
        }
    }
}
