package com.tanh.datsan.service;

import com.tanh.datsan.entity.Pitch;
import java.util.List;

public interface PitchService {
    List<Pitch> findHomePitches(String search, String filterLocation, Double userLat, Double userLng);
    List<String> findDistinctLocations();
    List<Pitch> findAll();
    java.util.Optional<Pitch> findById(Long id);
    Pitch save(Pitch pitch);
    void deleteById(Long id);
}
