package com.tanh.datsan.service;

import com.tanh.datsan.dto.PitchCreateRequest;
import com.tanh.datsan.dto.PitchDTO;
import com.tanh.datsan.entity.Pitch;
import com.tanh.datsan.exception.AppException;
import com.tanh.datsan.exception.ErrorCode;
import com.tanh.datsan.repository.PitchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PitchService {

    private final PitchRepository pitchRepository;

    public PitchDTO createPitch(PitchCreateRequest request) {
        if (pitchRepository.findByName(request.getName()).isPresent()) {
            throw new AppException(ErrorCode.PITCH_ALREADY_EXISTS);
        }

        Pitch pitch = Pitch.builder()
            .name(request.getName())
            .location(request.getLocation())
            .latitude(request.getLatitude())
            .longitude(request.getLongitude())
            .pitchType(request.getPitchType())
            .status("ACTIVE")
            .build();

        Pitch savedPitch = pitchRepository.save(pitch);
        return convertToDTO(savedPitch);
    }

    public PitchDTO getPitchById(Long id) {
        Pitch pitch = pitchRepository.findById(id)
            .orElseThrow(() -> new AppException(ErrorCode.PITCH_NOT_FOUND));
        return convertToDTO(pitch);
    }

    public List<PitchDTO> getAllPitches() {
        return pitchRepository.findAll().stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }

    public List<PitchDTO> getActivePitches() {
        return pitchRepository.findByStatus("ACTIVE").stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }

    public PitchDTO updatePitch(Long id, PitchCreateRequest request) {
        Pitch pitch = pitchRepository.findById(id)
            .orElseThrow(() -> new AppException(ErrorCode.PITCH_NOT_FOUND));

        pitch.setName(request.getName());
        pitch.setLocation(request.getLocation());
        pitch.setLatitude(request.getLatitude());
        pitch.setLongitude(request.getLongitude());
        pitch.setPitchType(request.getPitchType());

        Pitch updatedPitch = pitchRepository.save(pitch);
        return convertToDTO(updatedPitch);
    }

    public void deletePitch(Long id) {
        Pitch pitch = pitchRepository.findById(id)
            .orElseThrow(() -> new AppException(ErrorCode.PITCH_NOT_FOUND));

        pitch.setStatus("DELETED");
        pitchRepository.save(pitch);
    }

    public void activatePitch(Long id) {
        Pitch pitch = pitchRepository.findById(id)
            .orElseThrow(() -> new AppException(ErrorCode.PITCH_NOT_FOUND));

        pitch.setStatus("ACTIVE");
        pitchRepository.save(pitch);
    }

    public void deactivatePitch(Long id) {
        Pitch pitch = pitchRepository.findById(id)
            .orElseThrow(() -> new AppException(ErrorCode.PITCH_NOT_FOUND));

        pitch.setStatus("INACTIVE");
        pitchRepository.save(pitch);
    }

    private PitchDTO convertToDTO(Pitch pitch) {
        return PitchDTO.builder()
            .id(pitch.getId())
            .name(pitch.getName())
            .location(pitch.getLocation())
            .latitude(pitch.getLatitude())
            .longitude(pitch.getLongitude())
            .pitchType(pitch.getPitchType())
            .status(pitch.getStatus())
            .build();
    }
}
