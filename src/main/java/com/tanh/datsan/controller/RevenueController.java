package com.tanh.datsan.controller;

import com.tanh.datsan.dto.RevenueDTO;
import com.tanh.datsan.dto.RevenueSummaryDTO;
import com.tanh.datsan.service.RevenueService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.*;

@RestController
@RequestMapping("/api/admin/revenue")
@RequiredArgsConstructor
public class RevenueController {

    private final RevenueService revenueService;

    @GetMapping("/summary")
    public ResponseEntity<RevenueSummaryDTO> getRevenueSummary(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        if (startDate == null) {
            startDate = LocalDate.now().minusMonths(1);
        }
        if (endDate == null) {
            endDate = LocalDate.now();
        }

        try {
            RevenueSummaryDTO summary = revenueService.getRevenueSummary(startDate, endDate);
            return ResponseEntity.ok(summary);
        } catch (Exception e) {
            System.err.println("ERROR loading revenue summary: " + e.getMessage());
            e.printStackTrace();
            // Return empty summary instead of error
            RevenueSummaryDTO empty = RevenueSummaryDTO.builder()
                .totalRevenue(0.0)
                .totalBookings(0)
                .averagePerBooking(0.0)
                .dailyRevenue(new HashMap<>())
                .monthlyRevenue(new HashMap<>())
                .yearlyRevenue(new HashMap<>())
                .pitchRevenue(new HashMap<>())
                .build();
            return ResponseEntity.ok(empty);
        }
    }

    @GetMapping("/daily")
    public ResponseEntity<List<RevenueDTO>> getDailyRevenue(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        if (startDate == null) {
            startDate = LocalDate.now().minusDays(30);
        }
        if (endDate == null) {
            endDate = LocalDate.now();
        }

        try {
            List<RevenueDTO> dtos = revenueService.getDailyRevenue(startDate, endDate);
            return ResponseEntity.ok(dtos);
        } catch (Exception e) {
            System.err.println("ERROR loading daily revenue: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.ok(new ArrayList<>());
        }
    }

    @GetMapping("/yearly/{year}")
    public ResponseEntity<Double> getYearlyRevenue(@PathVariable int year) {
        try {
            Double totalRevenue = revenueService.getYearlyRevenue(year);
            return ResponseEntity.ok(totalRevenue != null ? totalRevenue : 0.0);
        } catch (Exception e) {
            System.err.println("ERROR loading yearly revenue: " + e.getMessage());
            return ResponseEntity.ok(0.0);
        }
    }

    @GetMapping("/monthly/{year}/{month}")
    public ResponseEntity<Double> getMonthlyRevenue(@PathVariable int year, @PathVariable int month) {
        try {
            Double totalRevenue = revenueService.getMonthlyRevenue(year, month);
            return ResponseEntity.ok(totalRevenue != null ? totalRevenue : 0.0);
        } catch (Exception e) {
            System.err.println("ERROR loading monthly revenue: " + e.getMessage());
            return ResponseEntity.ok(0.0);
        }
    }
}
