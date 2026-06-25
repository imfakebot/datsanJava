package com.tanh.datsan.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RevenueDetailDTO {
    private Long id;
    private Long paymentId;
    private String pitchName;
    private Double amount;
}
