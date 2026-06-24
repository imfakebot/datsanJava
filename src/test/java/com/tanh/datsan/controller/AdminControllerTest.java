package com.tanh.datsan.controller;

import com.tanh.datsan.constant.BookingStatus;
import com.tanh.datsan.entity.Account;
import com.tanh.datsan.entity.Booking;
import com.tanh.datsan.entity.Pitch;
import com.tanh.datsan.repository.AccountRepository;
import com.tanh.datsan.repository.BookingRepository;
import com.tanh.datsan.repository.PitchRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Transactional
public class AdminControllerTest {

    @Autowired
    private AdminController adminController;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private PitchRepository pitchRepository;

    @Test
    public void testCheckInBooking_Success() throws Exception {
        // Arrange: Create user, pitch, and booking
        Account user = new Account();
        user.setUsername("testuser_checkin");
        user.setEmail("checkin@test.com");
        user.setPassword("password123");
        accountRepository.save(user);

        Pitch pitch = new Pitch();
        pitch.setName("Test Pitch");
        pitch.setPitchType("5");
        pitchRepository.save(pitch);

        Booking booking = new Booking();
        booking.setAccount(user);
        booking.setPitch(pitch);
        booking.setBookingCode("CHK12345");
        booking.setBookingDate(LocalDate.now());
        booking.setStartTime(LocalDateTime.now().plusHours(1));
        booking.setEndTime(LocalDateTime.now().plusHours(2));
        booking.setDurationMinutes(60);
        booking.setTotalAmount(100.0);
        booking.setStatus(BookingStatus.CONFIRMED); // Must be confirmed to be checked in
        bookingRepository.save(booking);

        // Act: Perform Check In directly
        String result = adminController.checkInBooking(booking.getId());

        // Assert: Verify the status is now CHECKED_IN
        assertEquals("redirect:/admin/dashboard?checkedin", result);
        Booking updatedBooking = bookingRepository.findById(booking.getId()).orElseThrow();
        assertEquals(BookingStatus.CHECKED_IN, updatedBooking.getStatus());
    }

    @Test
    public void testCheckInBooking_NotConfirmed() throws Exception {
        // Arrange
        Account user = new Account();
        user.setUsername("testuser_checkin2");
        user.setEmail("checkin2@test.com");
        user.setPassword("password123");
        accountRepository.save(user);

        Pitch pitch = new Pitch();
        pitch.setName("Test Pitch 2");
        pitch.setPitchType("7");
        pitchRepository.save(pitch);

        Booking booking = new Booking();
        booking.setAccount(user);
        booking.setPitch(pitch);
        booking.setBookingCode("CHK12346");
        booking.setBookingDate(LocalDate.now());
        booking.setStartTime(LocalDateTime.now().plusHours(1));
        booking.setEndTime(LocalDateTime.now().plusHours(2));
        booking.setDurationMinutes(60);
        booking.setTotalAmount(100.0);
        booking.setStatus(BookingStatus.PENDING_PAYMENT); // Not confirmed!
        bookingRepository.save(booking);

        // Act
        String result = adminController.checkInBooking(booking.getId());

        // Assert: Status should remain PENDING_PAYMENT
        assertEquals("redirect:/admin/dashboard?checkedin", result);
        Booking updatedBooking = bookingRepository.findById(booking.getId()).orElseThrow();
        assertEquals(BookingStatus.PENDING_PAYMENT, updatedBooking.getStatus());
    }
}
