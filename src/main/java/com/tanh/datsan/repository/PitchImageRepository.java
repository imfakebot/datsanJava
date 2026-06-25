package com.tanh.datsan.repository;

import com.tanh.datsan.entity.PitchImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PitchImageRepository extends JpaRepository<PitchImage, Long> {
}
