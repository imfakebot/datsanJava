package com.tanh.datsan.service;

import com.tanh.datsan.entity.TimeSlot;
import com.tanh.datsan.repository.TimeSlotRepository;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.util.List;

@Service
public class TimeSlotServiceImpl implements TimeSlotService {

    private final TimeSlotRepository timeSlotRepository;

    public TimeSlotServiceImpl(TimeSlotRepository timeSlotRepository) {
        this.timeSlotRepository = timeSlotRepository;
    }

    @Override
    public List<TimeSlot> findByPitchIdAndDayOfWeek(Long pitchId, DayOfWeek dayOfWeek) {
        return timeSlotRepository.findByPitchIdAndDayOfWeek(pitchId, dayOfWeek);
    }

    @Override
    public List<TimeSlot> findByPitchIdOrderByDayOfWeekAscStartTimeAsc(Long pitchId) {
        return timeSlotRepository.findByPitchIdOrderByDayOfWeekAscStartTimeAsc(pitchId);
    }

    @Override
    public TimeSlot save(TimeSlot timeSlot) {
        return timeSlotRepository.save(timeSlot);
    }

    @Override
    public void deleteById(Long id) {
        if (timeSlotRepository.existsById(id)) {
            timeSlotRepository.deleteById(id);
        }
    }

    @Override
    public double calculatePrice(Long pitchId, java.time.LocalDateTime startDT, int durationMinutes) {
        double totalAmount = 0.0;
        java.time.LocalDateTime currentDT = startDT;
        java.time.LocalDateTime endDT = startDT.plusMinutes(durationMinutes);
        
        while (currentDT.isBefore(endDT)) {
            java.time.DayOfWeek dayOfWeek = currentDT.getDayOfWeek();
            java.time.LocalTime time = currentDT.toLocalTime();
            
            List<TimeSlot> slots = timeSlotRepository.findByPitchIdAndDayOfWeek(pitchId, dayOfWeek);
            double currentMinuteRate = 300000.0 / 60.0; // default 300k/hr
            
            for (TimeSlot slot : slots) {
                if (!time.isBefore(slot.getStartTime()) && time.isBefore(slot.getEndTime())) {
                    currentMinuteRate = slot.getPrice() / 60.0;
                    break;
                }
            }
            
            totalAmount += currentMinuteRate;
            currentDT = currentDT.plusMinutes(1);
        }
        
        return Math.round(totalAmount);
    }
}
