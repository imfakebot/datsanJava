package com.tanh.datsan.service;

import com.tanh.datsan.constant.PaymentMethod;
import com.tanh.datsan.constant.PaymentStatus;
import com.tanh.datsan.entity.Booking;
import com.tanh.datsan.entity.Payment;
import com.tanh.datsan.exception.AppException;
import com.tanh.datsan.exception.ErrorCode;
import com.tanh.datsan.repository.BookingRepository;
import com.tanh.datsan.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final BookingRepository bookingRepository;
    private final RevenueService revenueService;

    public Payment createPayment(Booking booking, PaymentMethod method) {
        Payment payment = Payment.builder()
            .booking(booking)
            .amount(booking.getTotalAmount())
            .method(method)
            .status(PaymentStatus.PENDING)
            .build();

        return paymentRepository.save(payment);
    }

    public Payment confirmPayment(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
            .orElseThrow(() -> new AppException(ErrorCode.PAYMENT_NOT_FOUND));

        payment.setStatus(PaymentStatus.SUCCESS);
        payment.setPaymentTime(LocalDateTime.now());
        Payment savedPayment = paymentRepository.save(payment);

        return savedPayment;
    }

    public Payment failPayment(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
            .orElseThrow(() -> new AppException(ErrorCode.PAYMENT_NOT_FOUND));

        payment.setStatus(PaymentStatus.FAILED);
        return paymentRepository.save(payment);
    }

    public Payment getPaymentById(Long paymentId) {
        return paymentRepository.findById(paymentId)
            .orElseThrow(() -> new AppException(ErrorCode.PAYMENT_NOT_FOUND));
    }

    public Optional<Payment> getPaymentByBookingId(Long bookingId) {
        return paymentRepository.findByBookingId(bookingId);
    }

    public List<Payment> getPaymentsByStatus(PaymentStatus status) {
        return paymentRepository.findByStatus(status);
    }

    public Payment updatePayment(Long paymentId, String transactionNo, String bankCode, String orderInfo, String vnpResponseCode) {
        Payment payment = paymentRepository.findById(paymentId)
            .orElseThrow(() -> new AppException(ErrorCode.PAYMENT_NOT_FOUND));

        if (transactionNo != null) payment.setTransactionNo(transactionNo);
        if (bankCode != null) payment.setBankCode(bankCode);
        if (orderInfo != null) payment.setOrderInfo(orderInfo);
        if (vnpResponseCode != null) payment.setVnpResponseCode(vnpResponseCode);

        return paymentRepository.save(payment);
    }
}
