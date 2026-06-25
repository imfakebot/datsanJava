package com.tanh.datsan.dto;

import lombok.*;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RevenueDTO {
    private Long id;
    private LocalDate date;
    private Double dailyTotal;
    private Integer completedBookings;
}
