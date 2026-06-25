package com.tanh.datsan.repository;

import com.tanh.datsan.constant.PaymentStatus;
import com.tanh.datsan.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByBookingId(Long bookingId);
    List<Payment> findByStatus(PaymentStatus status);

    @Query("SELECT p FROM Payment p JOIN FETCH p.booking b JOIN FETCH b.pitch WHERE p.status = com.tanh.datsan.constant.PaymentStatus.SUCCESS AND b.bookingDate >= :startDate AND b.bookingDate <= :endDate")
    List<Payment> findSuccessfulPaymentsWithBookingInDateRange(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM Payment p JOIN p.booking b WHERE p.status = com.tanh.datsan.constant.PaymentStatus.SUCCESS AND YEAR(b.bookingDate) = :year")
    Double getTotalRevenueByYear(@Param("year") int year);

    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM Payment p JOIN p.booking b WHERE p.status = com.tanh.datsan.constant.PaymentStatus.SUCCESS AND YEAR(b.bookingDate) = :year AND MONTH(b.bookingDate) = :month")
    Double getTotalRevenueByYearMonth(@Param("year") int year, @Param("month") int month);
}
