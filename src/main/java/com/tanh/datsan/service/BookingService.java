
package com.tanh.datsan.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.tanh.datsan.constant.BookingStatus;
import com.tanh.datsan.entity.Account;
import com.tanh.datsan.entity.Booking;
import com.tanh.datsan.entity.Pitch;
import com.tanh.datsan.exception.AppException;
import com.tanh.datsan.exception.ErrorCode;
import com.tanh.datsan.repository.BookingRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final TimeSlotService timeSlotService;

    public List<Booking> findAll() {
        return bookingRepository.findAll();
    }

    public List<Booking> findByAccountIdOrderByCreatedAtDesc(Long accountId) {
        return bookingRepository.findByAccountIdOrderByCreatedAtDesc(accountId);
    }

    public Optional<Booking> findById(Long id) {
        return bookingRepository.findById(id);
    }

    public Booking save(Booking booking) {
        return bookingRepository.save(booking);
    }

    public Booking processOnlineBooking(Account account, Pitch pitch, String bookingDateStr, String startTimeStr, double duration) {
        LocalDate bookingDate = LocalDate.parse(bookingDateStr, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        LocalTime time = LocalTime.parse(startTimeStr, DateTimeFormatter.ofPattern("HH:mm"));
        LocalDateTime startTime = LocalDateTime.of(bookingDate, time);
        LocalDateTime endTime = startTime.plusMinutes((long) (duration * 60));

        Booking booking = new Booking();
        booking.setAccount(account);
        booking.setPitch(pitch);
        booking.setBookingDate(bookingDate);
        booking.setStartTime(startTime);
        booking.setEndTime(endTime);
        booking.setDurationMinutes((int) (duration * 60));
        
        return createBooking(booking);
    }

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
        
        if (booking.getTotalAmount() == null || booking.getTotalAmount() == 0) {
            double totalAmount = timeSlotService.calculatePrice(booking.getPitch().getId(), booking.getStartTime(), booking.getDurationMinutes());
            booking.setTotalAmount(totalAmount);
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
        
        if (booking.getTotalAmount() == null || booking.getTotalAmount() == 0) {
            double totalAmount = timeSlotService.calculatePrice(booking.getPitch().getId(), booking.getStartTime(), booking.getDurationMinutes());
            booking.setTotalAmount(totalAmount);
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
        String datePart = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String randomPart = UUID.randomUUID().toString().substring(0, 4).toUpperCase();
        return datePart + "-" + randomPart;
    }

}
