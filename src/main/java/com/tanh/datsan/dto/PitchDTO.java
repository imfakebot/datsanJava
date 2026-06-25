package com.tanh.datsan.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PitchDTO {
    private Long id;
    private String name;
    private String location;
    private Double latitude;
    private Double longitude;
    private String pitchType;
    private String status;
}
