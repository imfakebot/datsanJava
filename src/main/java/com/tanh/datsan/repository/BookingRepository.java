package com.tanh.datsan.repository;

import com.tanh.datsan.constant.BookingStatus;
import com.tanh.datsan.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    @Query("SELECT COUNT(b) > 0 FROM Booking b WHERE b.pitch.id = :pitchId "
            + "AND b.status IN :statuses "
            + "AND b.startTime < :endTime "
            + "AND b.endTime > :startTime")
    boolean existsOverlappingBooking(
            @Param("pitchId") Long pitchId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime,
            @Param("statuses") List<BookingStatus> statuses
    );

    @Query("SELECT b FROM Booking b WHERE b.pitch.id = :pitchId "
            + "AND b.bookingDate = :date "
            + "AND b.status IN :statuses "
            + "ORDER BY b.startTime ASC")
    List<Booking> findBookedTimeRanges(
            @Param("pitchId") Long pitchId,
            @Param("date") LocalDate date,
            @Param("statuses") List<BookingStatus> statuses
    );

    Optional<Booking> findByBookingCode(String bookingCode);

    List<Booking> findByAccountIdOrderByCreatedAtDesc(Long accountId);

    List<Booking> findByStatusAndCreatedAtBefore(BookingStatus status, LocalDateTime time);

    List<Booking> findByStatusAndStartTimeBefore(BookingStatus status, LocalDateTime time);
}
