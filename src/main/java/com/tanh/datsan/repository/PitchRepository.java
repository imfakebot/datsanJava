package com.tanh.datsan.repository;

import com.tanh.datsan.entity.Pitch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PitchRepository extends JpaRepository<Pitch, Long> {
    Optional<Pitch> findByName(String name);
    List<Pitch> findByStatus(String status);
}
