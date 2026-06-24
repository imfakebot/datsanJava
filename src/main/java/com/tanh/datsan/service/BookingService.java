package com.tanh.datsan.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.tanh.datsan.constant.BookingStatus;
import com.tanh.datsan.entity.Booking;
import com.tanh.datsan.exception.AppException;
import com.tanh.datsan.exception.ErrorCode;
import com.tanh.datsan.repository.BookingRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;

    public boolean checkAvailability(Long pitchId, LocalDateTime startTime, LocalDateTime endTime) {
        List<BookingStatus> conflictingStatuses = Arrays.asList(
                BookingStatus.PENDING_PAYMENT,
                BookingStatus.CONFIRMED,
                BookingStatus.CHECKED_IN
        );
        return !bookingRepository.existsOverlappingBooking(pitchId, startTime, endTime, conflictingStatuses);
    }

    public List<Booking> getBookedTimeRanges(Long pitchId, LocalDate date) {
        List<BookingStatus> conflictingStatuses = Arrays.asList(
                BookingStatus.PENDING_PAYMENT,
                BookingStatus.CONFIRMED,
                BookingStatus.CHECKED_IN
        );
        return bookingRepository.findBookedTimeRanges(pitchId, date, conflictingStatuses);
    }

    public List<Booking> getBookingHistory(Long accountId) {
        return bookingRepository.findByAccountIdOrderByCreatedAtDesc(accountId);
    }

    public Booking createBooking(Booking booking) {
        boolean isAvailable = checkAvailability(booking.getPitch().getId(), booking.getStartTime(), booking.getEndTime());
        if (!isAvailable) {
            throw new AppException(ErrorCode.BOOKING_OVERLAP);
        }
        
        if (booking.getBookingCode() == null || booking.getBookingCode().isEmpty()) {
            booking.setBookingCode(generateBookingCode());
        }
        
        booking.setStatus(BookingStatus.PENDING_PAYMENT);
        return bookingRepository.save(booking);
    }

    public Booking createOfflineBooking(Booking booking) {
        boolean isAvailable = checkAvailability(booking.getPitch().getId(), booking.getStartTime(), booking.getEndTime());
        if (!isAvailable) {
            throw new AppException(ErrorCode.BOOKING_OVERLAP);
        }
        
        if (booking.getBookingCode() == null || booking.getBookingCode().isEmpty()) {
            booking.setBookingCode(generateBookingCode());
        }
        
        booking.setStatus(BookingStatus.CONFIRMED);
        return bookingRepository.save(booking);
    }

    public boolean cancelBooking(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new AppException(ErrorCode.BOOKING_NOT_FOUND));
        
        booking.setStatus(BookingStatus.CANCELLED);
        bookingRepository.save(booking);
        return true;
    }

    public void checkIn(String bookingCode) {
        Booking booking = bookingRepository.findByBookingCode(bookingCode)
                .orElseThrow(() -> new AppException(ErrorCode.BOOKING_NOT_FOUND));
        
        booking.setStatus(BookingStatus.CHECKED_IN);
        bookingRepository.save(booking);
    }

    @Scheduled(fixedRate = 60000) // Runs every 1 minute
    public void autoCancelPendingBookings() {
        LocalDateTime tenMinutesAgo = LocalDateTime.now().minusMinutes(10);
        List<Booking> pendingBookings = bookingRepository.findByStatusAndCreatedAtBefore(BookingStatus.PENDING_PAYMENT, tenMinutesAgo);
        
        for (Booking booking : pendingBookings) {
            booking.setStatus(BookingStatus.CANCELLED);
        }
        if (!pendingBookings.isEmpty()) {
            bookingRepository.saveAll(pendingBookings);
        }
    }

    @Scheduled(fixedRate = 60000) // Runs every 1 minute
    public void autoMarkNoShow() {
        LocalDateTime fifteenMinutesAgo = LocalDateTime.now().minusMinutes(15);
        List<Booking> confirmedBookings = bookingRepository.findByStatusAndStartTimeBefore(BookingStatus.CONFIRMED, fifteenMinutesAgo);
        
        for (Booking booking : confirmedBookings) {
            booking.setStatus(BookingStatus.NO_SHOW);
        }
        if (!confirmedBookings.isEmpty()) {
            bookingRepository.saveAll(confirmedBookings);
        }
    }

    private String generateBookingCode() {
        String datePart = LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd"));
        String randomPart = UUID.randomUUID().toString().substring(0, 4).toUpperCase();
        return datePart + "-" + randomPart;
    }

}
