package com.tanh.datsan.repository;

import com.tanh.datsan.entity.Pitch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PitchRepository extends JpaRepository<Pitch, Long> {
    java.util.List<Pitch> findByNameContainingIgnoreCaseOrLocationContainingIgnoreCase(String name, String location);

    @org.springframework.data.jpa.repository.Query("SELECT DISTINCT p.location FROM Pitch p WHERE p.location IS NOT NULL")
    java.util.List<String> findDistinctLocations();
}
