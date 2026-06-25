package com.tanh.datsan.service;

import com.tanh.datsan.constant.PaymentStatus;
import com.tanh.datsan.dto.RevenueDTO;
import com.tanh.datsan.dto.RevenueSummaryDTO;
import com.tanh.datsan.entity.Booking;
import com.tanh.datsan.entity.Payment;
import com.tanh.datsan.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RevenueService {

    private final PaymentRepository paymentRepository;

    public RevenueSummaryDTO getRevenueSummary(LocalDate startDate, LocalDate endDate) {
        List<Payment> payments = paymentRepository.findSuccessfulPaymentsWithBookingInDateRange(startDate, endDate);

        double totalRevenue = 0;
        int totalBookings = 0;

        Map<String, Double> dailyRevenue = new HashMap<>();
        Map<String, Double> monthlyRevenue = new HashMap<>();
        Map<String, Double> yearlyRevenue = new HashMap<>();
        Map<String, Integer> pitchRevenue = new HashMap<>();

        for (Payment payment : payments) {
            Booking booking = payment.getBooking();
            LocalDate date = booking.getBookingDate();
            double amount = payment.getAmount();

            totalRevenue += amount;
            totalBookings++;

            // Daily
            dailyRevenue.merge(date.toString(), amount, Double::sum);

            // Monthly
            monthlyRevenue.merge(YearMonth.from(date).toString(), amount, Double::sum);

            // Yearly
            yearlyRevenue.merge(String.valueOf(date.getYear()), amount, Double::sum);

            // Pitch
            pitchRevenue.merge(booking.getPitch().getName(), 1, Integer::sum);
        }

        double averagePerBooking = totalBookings > 0 ? totalRevenue / totalBookings : 0;

        return RevenueSummaryDTO.builder()
            .totalRevenue(totalRevenue)
            .totalBookings(totalBookings)
            .averagePerBooking(averagePerBooking)
            .dailyRevenue(dailyRevenue)
            .monthlyRevenue(monthlyRevenue)
            .yearlyRevenue(yearlyRevenue)
            .pitchRevenue(pitchRevenue)
            .build();
    }

    public Double getYearlyRevenue(int year) {
        return paymentRepository.getTotalRevenueByYear(year);
    }

    public Double getMonthlyRevenue(int year, int month) {
        return paymentRepository.getTotalRevenueByYearMonth(year, month);
    }

    public List<RevenueDTO> getDailyRevenue(LocalDate startDate, LocalDate endDate) {
        List<Payment> payments = paymentRepository.findSuccessfulPaymentsWithBookingInDateRange(startDate, endDate);
        
        Map<LocalDate, RevenueDTO> dailyMap = new HashMap<>();
        
        for (Payment payment : payments) {
            LocalDate date = payment.getBooking().getBookingDate();
            RevenueDTO dto = dailyMap.computeIfAbsent(date, d -> RevenueDTO.builder()
                .date(d)
                .dailyTotal(0.0)
                .completedBookings(0)
                .build());
                
            dto.setDailyTotal(dto.getDailyTotal() + payment.getAmount());
            dto.setCompletedBookings(dto.getCompletedBookings() + 1);
        }
        
        return dailyMap.values().stream()
            .sorted(Comparator.comparing(RevenueDTO::getDate))
            .collect(Collectors.toList());
    }
}
