package com.tanh.datsan.dto;

import lombok.*;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RevenueSummaryDTO {
    private Double totalRevenue;
    private Integer totalBookings;
    private Double averagePerBooking;
    private Map<String, Double> dailyRevenue;
    private Map<String, Double> monthlyRevenue;
    private Map<String, Double> yearlyRevenue;
    private Map<String, Integer> pitchRevenue;
}
