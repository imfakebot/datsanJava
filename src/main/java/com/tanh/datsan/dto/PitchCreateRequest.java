package com.tanh.datsan.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PitchCreateRequest {
    private String name;
    private String location;
    private Double latitude;
    private Double longitude;
    private String pitchType;
}
