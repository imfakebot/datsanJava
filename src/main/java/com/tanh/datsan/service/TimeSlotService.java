package com.tanh.datsan.service;

import com.tanh.datsan.entity.TimeSlot;

import java.time.DayOfWeek;
import java.util.List;

public interface TimeSlotService {
    List<TimeSlot> findByPitchIdAndDayOfWeek(Long pitchId, DayOfWeek dayOfWeek);
    List<TimeSlot> findByPitchIdOrderByDayOfWeekAscStartTimeAsc(Long pitchId);
    TimeSlot findById(Long id);
    TimeSlot save(TimeSlot timeSlot);
    void deleteById(Long id);
    double calculatePrice(Long pitchId, java.time.LocalDateTime startDT, int durationMinutes);
}
