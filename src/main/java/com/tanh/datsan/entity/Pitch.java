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
    
    private String pitchType;
    private String status;
    
    @OneToMany(mappedBy = "pitch", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PitchImage> images = new java.util.ArrayList<>();

    @Transient
    private String rawImageUrls;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Column(columnDefinition = "TEXT")
    private String amenities;

    @Transient
    private Double distanceToUser;

    @OneToMany(mappedBy = "pitch", cascade = CascadeType.ALL)
    private List<TimeSlot> timeSlots;

    @OneToMany(mappedBy = "pitch", cascade = CascadeType.ALL)
    private List<Booking> bookings;
}
