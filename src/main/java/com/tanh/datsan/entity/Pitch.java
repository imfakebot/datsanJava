package com.tanh.datsan.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Entity
@Table(name = "pitches")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Pitch {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String location;
    private Double latitude;
    private Double longitude;
    
    @Column(nullable = false)
    private String pitchType;
    
    @Column(nullable = false)
    private String status = "ACTIVE";

    @OneToMany(mappedBy = "pitch", cascade = CascadeType.ALL)
    private List<TimeSlot> timeSlots;

    @OneToMany(mappedBy = "pitch", cascade = CascadeType.ALL)
    private List<Booking> bookings;
}
