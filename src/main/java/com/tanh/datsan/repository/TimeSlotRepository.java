package com.tanh.datsan.repository;

import com.tanh.datsan.entity.TimeSlot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.DayOfWeek;
import java.util.List;

@Repository
public interface TimeSlotRepository extends JpaRepository<TimeSlot, Long> {
    List<TimeSlot> findByPitchId(Long pitchId);
    List<TimeSlot> findByPitchIdAndDayOfWeek(Long pitchId, DayOfWeek dayOfWeek);
    List<TimeSlot> findByPitchIdOrderByDayOfWeekAscStartTimeAsc(Long pitchId);
}
