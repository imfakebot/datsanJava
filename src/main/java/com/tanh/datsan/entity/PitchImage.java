package com.tanh.datsan.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "pitch_images")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PitchImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String imageUrl;

    @Column(name = "display_order")
    private Integer displayOrder;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pitch_id", nullable = false)
    private Pitch pitch;
}
