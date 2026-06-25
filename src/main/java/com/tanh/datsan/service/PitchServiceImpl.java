package com.tanh.datsan.service;

import com.tanh.datsan.entity.Pitch;
import com.tanh.datsan.repository.PitchRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PitchServiceImpl implements PitchService {

    private final PitchRepository pitchRepository;

    public PitchServiceImpl(PitchRepository pitchRepository) {
        this.pitchRepository = pitchRepository;
    }

    @Override
    public List<Pitch> findHomePitches(String search, String filterLocation, Double userLat, Double userLng) {
        List<Pitch> pitches;
        if (search != null && !search.trim().isEmpty()) {
            pitches = pitchRepository.findByNameContainingIgnoreCaseOrLocationContainingIgnoreCase(search.trim(), search.trim());
        } else {
            pitches = pitchRepository.findAll();
        }

        if (filterLocation != null && !filterLocation.trim().isEmpty()) {
            pitches = pitches.stream()
                    .filter(p -> p.getLocation() != null && p.getLocation().toLowerCase().contains(filterLocation.trim().toLowerCase()))
                    .collect(Collectors.toList());
        }

        if (userLat != null && userLng != null) {
            for (Pitch p : pitches) {
                if (p.getLatitude() != null && p.getLongitude() != null) {
                    double distance = calculateHaversineDistance(userLat, userLng, p.getLatitude(), p.getLongitude());
                    p.setDistanceToUser(distance);
                }
            }
            pitches.sort((p1, p2) -> {
                if (p1.getDistanceToUser() == null && p2.getDistanceToUser() == null) return 0;
                if (p1.getDistanceToUser() == null) return 1;
                if (p2.getDistanceToUser() == null) return -1;
                return Double.compare(p1.getDistanceToUser(), p2.getDistanceToUser());
            });
        }

        return pitches;
    }

    @Override
    public List<String> findDistinctLocations() {
        return pitchRepository.findDistinctLocations();
    }

    private double calculateHaversineDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // Bán kính Trái đất bằng km
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }

    @Override
    public List<Pitch> findAll() {
        return pitchRepository.findAll();
    }

    @Override
    public java.util.Optional<Pitch> findById(Long id) {
        return pitchRepository.findById(id);
    }

    @Override
    public Pitch save(Pitch pitch) {
        return pitchRepository.save(pitch);
    }

    @Override
    public void deleteById(Long id) {
        pitchRepository.deleteById(id);
    }
}
